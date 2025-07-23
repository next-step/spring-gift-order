package gift.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import gift.dto.CreateWishRequest;
import gift.entity.Member;
import gift.entity.Product;
import gift.jwt.JwtTokenProvider;
import gift.repository.MemberRepository;
import gift.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class WishRestControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Autowired private ProductRepository productRepository;
    @Autowired private MemberRepository memberRepository;
    @Autowired private JwtTokenProvider jwtTokenProvider;

    private Member member;
    private String token;
    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(Member.of("aran@email.com", "1234"));

        token = jwtTokenProvider.createToken(member.getEmail());

        product1 = productRepository.save(Product.of("쿠키", "cookie.com", 100L));
        product2 = productRepository.save(Product.of("식빵", "eatbread.com", 200L));
    }

    @Test
    @DisplayName("위시 추가 성공")
    void addWishSuccess() throws Exception {
        CreateWishRequest request = new CreateWishRequest(product1.getId());

        mockMvc.perform(post("/api/wishes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("위시 삭제 성공")
    void deleteWishSuccess() throws Exception {
        CreateWishRequest request = new CreateWishRequest(product2.getId());

        mockMvc.perform(post("/api/wishes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/api/wishes")
                        .header("Authorization", "Bearer " + token)
                        .param("productId", product2.getId().toString()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("위시 조회 성공")
    void getWishListSuccess() throws Exception {
        mockMvc.perform(post("/api/wishes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateWishRequest(product1.getId()))))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/wishes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateWishRequest(product2.getId()))))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/wishes")
                        .header("Authorization", "Bearer " + token)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }
}
