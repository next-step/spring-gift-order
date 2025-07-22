package gift.controller;

import gift.domain.Member;
import gift.domain.Product;
import gift.domain.Wish;
import gift.repository.MemberRepository;
import gift.repository.ProductRepository;
import gift.repository.WishRepository;
import gift.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.IntStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class WishPaginationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private MemberRepository memberRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private WishRepository wishRepository;
    @Autowired private JwtUtil jwtUtil;

    private String jwtToken;
    private Long memberId;

    @BeforeEach
    void setUp() {
        wishRepository.deleteAll();
        productRepository.deleteAll();
        memberRepository.deleteAll();

        Member member = new Member("testuser", "testuser@example.com");
        memberRepository.save(member);
        memberId = member.getId();

        jwtToken = jwtUtil.createToken(member);

        IntStream.rangeClosed(1, 10).forEach(i -> {
            Product product = new Product("상품" + i, 1000 * i, "image" + i + ".jpg");
            productRepository.save(product);

            Wish wish = new Wish(member, product, i);
            wishRepository.save(wish);
        });
    }

    @Test
    void 위시리스트_페이지네이션_조회() throws Exception {
        mockMvc.perform(get("/api/wishes/member")
                        .header("Authorization", "Bearer " + jwtToken)
                        .param("page", "0")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(5))
                .andExpect(jsonPath("$.totalElements").value(10))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.currentPage").value(0));
    }
}
