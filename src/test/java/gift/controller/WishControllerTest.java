package gift.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gift.config.JwtProvider;
import gift.dto.request.QuantityUpdateRequestDto;
import gift.dto.request.WishRequestDto;
import gift.entity.Member;
import gift.entity.Product;
import gift.entity.Wish;
import gift.repository.MemberRepository;
import gift.repository.ProductRepository;
import gift.repository.WishRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class WishControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Autowired private MemberRepository memberRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private WishRepository wishRepository;
    @Autowired private JwtProvider jwtProvider;

    private Long memberId;
    private Long productId;
    private Long wishId;
    private String token;

    @BeforeEach
    void setUp() {
        // 사용자 저장
        Member member = new Member(null, "test@example.com", "1234", "USER");
        member = memberRepository.save(member);
        memberId = member.getId();

        // 상품 저장
        Product product = new Product(null, "테스트상품", 1000, "image.jpg");
        product = productRepository.save(product);
        productId = product.getId();

        // 위시 저장
       wishId = wishRepository.save(new Wish(member, product, 3)).getId();

        // JWT 발급
        token = jwtProvider.generateToken(member);
    }

    @Test
    @DisplayName("위시 등록")
    void addWishTest() throws Exception {
        WishRequestDto dto = new WishRequestDto(productId, 2);

        mockMvc.perform(post("/wishes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("위시 목록 조회")
    void getWishesTest() throws Exception {
        mockMvc.perform(get("/wishes")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("위시 수량 수정")
    void updateQuantityTest() throws Exception {
        QuantityUpdateRequestDto dto = new QuantityUpdateRequestDto(5);

        mockMvc.perform(patch("/wishes/" + wishId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("위시 삭제")
    void deleteWishTest() throws Exception {
        mockMvc.perform(delete("/wishes/" + wishId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }
}
