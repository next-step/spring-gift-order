package gift.controller;

import gift.auth.JwtTokenProvider;
import gift.entity.Member;
import gift.entity.Product;
import gift.entity.Wishlist;
import gift.repository.MemberRepository;
import gift.repository.ProductRepository;
import gift.repository.WishlistRepository;
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
public class WishlistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String token;
    private Member member;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(new Member("test@example.com", "password"));
        token = jwtTokenProvider.createToken(member.getEmail());

        for (int i = 1; i <= 15; i++) {
            Product product = productRepository.save(new Product("위시 상품 " + i, 100L * i, "wish" + i + ".jpg"));
            wishlistRepository.save(new Wishlist(member, product));
        }
    }

    @Test
    @DisplayName("위시리스트 목록 페이지네이션 조회 테스트")
    void getWishlistsWithPagination() throws Exception {
        mockMvc.perform(get("/api/wishlists")
                        .header("Authorization", "Bearer " + token)
                        .param("page", "2")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(5))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.totalElements").value(15))
                .andExpect(jsonPath("$.number").value(2));
    }
}