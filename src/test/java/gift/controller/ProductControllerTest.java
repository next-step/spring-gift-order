package gift.controller;

import gift.auth.JwtTokenProvider;
import gift.entity.Member;
import gift.entity.Product;
import gift.repository.MemberRepository;
import gift.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String token;

    @BeforeEach
    void setUp() {
        Member member = memberRepository.save(new Member("test@example.com", "password"));
        token = jwtTokenProvider.createToken(member.getEmail());
        for (int i = 1; i <= 25; i++) {
            productRepository.save(new Product("상품 " + i, 1000L * i, "image" + i + ".jpg"));
        }
    }

    @Test
    @DisplayName("상품 목록 페이지네이션 조회 테스트")
    void getProductsWithPagination() throws Exception {
        mockMvc.perform(get("/api/products")
                        .header("Authorization", "Bearer " + token)
                        // ---------------------------
                        .param("page", "1")
                        .param("size", "10")
                        .param("sort", "price,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(10))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.totalElements").value(25))
                .andExpect(jsonPath("$.number").value(1));
    }
}