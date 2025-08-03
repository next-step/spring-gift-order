package gift.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import gift.dto.LoginMemberDto;
import gift.dto.RegisterRequest;
import gift.dto.TokenResponse;
import gift.dto.WishRequest;
import gift.dto.WishResponse;
import gift.exception.MemberNotFoundException;
import gift.model.Member;
import gift.model.Product;
import gift.repository.MemberRepository;
import gift.repository.ProductRepository;
import gift.repository.WishlistRepository;
import gift.service.MemberService;
import gift.service.WishlistService;
import jakarta.transaction.Transactional;
import java.util.Optional;
import jdk.jshell.spi.ExecutionControlProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class WishlistControllerTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private WishlistService wishlistService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Product saveProduct() {
        Product product = new Product("test_coffee", 2500, "https://test_coffee.jpg", false);
        return productRepository.save(product);
    }

    @Test
    void 위시리스트_추가() throws Exception {
        TokenResponse tokenResponse = memberService.save(
            new RegisterRequest("abc@naver.com", "1234"));
        Long memberId = memberRepository.findByEmail("abc@naver.com").get().getId();
        Product product = saveProduct();

        WishRequest wishRequest = new WishRequest(product.getId(), 3);
        String json = objectMapper.writeValueAsString(wishRequest);

        mockMvc.perform(post("/api/wishes").contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", tokenResponse.getToken()).content(json))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.memberId").value(memberId))
            .andExpect(jsonPath("$.productId").value(product.getId()))
            .andExpect(jsonPath("$.quantity").value(3));
    }

    @Test
    void 위시리스트_없는_상품_추가() throws Exception {
        TokenResponse tokenResponse = memberService.save(
            new RegisterRequest("abc@naver.com", "1234"));
        Long memberId = memberRepository.findByEmail("abc@naver.com").get().getId();

        WishRequest wishRequest = new WishRequest(999L, 3);
        String json = objectMapper.writeValueAsString(wishRequest);

        mockMvc.perform(post("/api/wishes").contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", tokenResponse.getToken()).content(json))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("id: 999. 해당 ID의 상품이 존재하지 않습니다."));
    }

    @Test
    void 토큰_없이_위시리스트_추가_요청() throws Exception {
        TokenResponse tokenResponse = memberService.save(
            new RegisterRequest("abc@naver.com", "1234"));

        WishRequest wishRequest = new WishRequest(1L, 3);
        String json = objectMapper.writeValueAsString(wishRequest);

        mockMvc.perform(post("/api/wishes").contentType(MediaType.APPLICATION_JSON).content(json))
            .andExpect(status().isUnauthorized()).andExpect(content().string("Token is missing"));
    }

    @Test
    void 잘못된_토큰_위시리스트_추가_요청() throws Exception {
        TokenResponse tokenResponse = new TokenResponse("Invalid Token");

        WishRequest wishRequest = new WishRequest(1L, 3);
        String json = objectMapper.writeValueAsString(wishRequest);

        mockMvc.perform(post("/api/wishes").contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", tokenResponse.getToken()).content(json))
            .andExpect(status().isUnauthorized()).andExpect(content().string("Invalid Token"));
    }

    @Test
    void 위시리스트_추가_수량이_음수() throws Exception {
        TokenResponse tokenResponse = memberService.save(
            new RegisterRequest("abc@naver.com", "1234"));
        Long memberId = memberRepository.findByEmail("abc@naver.com").get().getId();

        WishRequest wishRequest = new WishRequest(1L, -3);
        String json = objectMapper.writeValueAsString(wishRequest);

        mockMvc.perform(post("/api/wishes").contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", tokenResponse.getToken()).content(json))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.messages[0]").value("수량은 음수가 될 수 없습니다."));
    }

    @Test
    void 위시리스트_전체_조회() throws Exception {
        TokenResponse tokenResponse = memberService.save(
            new RegisterRequest("abc@naver.com", "1234"));
        Product saved1 = saveProduct();
        Product saved2 = saveProduct();

        wishlistService.create(new WishRequest(saved1.getId(), 3),
            new LoginMemberDto("abc@naver.com"));
        wishlistService.create(new WishRequest(saved2.getId(), 10),
            new LoginMemberDto("abc@naver.com"));

        Long memberId = memberRepository.findByEmail("abc@naver.com").get().getId();

        mockMvc.perform(get("/api/wishes").contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", tokenResponse.getToken())).andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    void 위시리스트_삭제() throws Exception {
        TokenResponse tokenResponse = memberService.save(
            new RegisterRequest("abc@naver.com", "1234"));
        Product product = saveProduct();

        wishlistService.create(new WishRequest(product.getId(), 3),
            new LoginMemberDto("abc@naver.com"));

        mockMvc.perform(
                delete("/api/wishes/" + product.getId()).contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", tokenResponse.getToken()))
            .andExpect(status().isNoContent());
    }

    @Test
    void pagination() throws Exception {
        // given
        TokenResponse tokenResponse = memberService.save(
            new RegisterRequest("abc@naver.com", "1234"));
        for (int i = 0; i < 15; i++) {
            Product product = saveProduct();
            wishlistService.create(new WishRequest(product.getId(), i),
                new LoginMemberDto("abc@naver.com"));
        }

        // then
        // 0번 페이지 (최대 10개)
        mockMvc.perform(get("/api/wishes?page=0&size=10")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", tokenResponse.getToken()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(10));

        // 1번 페이지 (나머지 5개)
        mockMvc.perform(get("/api/wishes?page=1&size=10")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", tokenResponse.getToken()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(5));
    }
}
