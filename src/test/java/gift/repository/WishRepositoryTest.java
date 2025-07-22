package gift.repository;

import static org.assertj.core.api.Assertions.assertThat;

import gift.entity.Member;
import gift.entity.Product;
import gift.entity.Wish;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@DataJpaTest
class WishRepositoryTest {

    @Autowired
    private WishRepository wishRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

    private Member member;
    private Product product;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(new Member("test@example.com", "password"));
        product = productRepository.save(new Product("테스트 상품", 10000, "test.jpg"));
    }

    @Test
    @DisplayName("위시리스트에 상품을 추가하고 회원을 통해 조회한다")
    void addWishAndFindByMember() {
        // given
        Wish wish = new Wish(member, product);

        // when
        wishRepository.save(wish);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Wish> wishPage = wishRepository.findByMember(member, pageable);
        List<Wish> wishes = wishPage.getContent();

        // then
        assertThat(wishes).hasSize(1); // 위시리스트에 1개의 상품이 있는지 확인
        assertThat(wishes.get(0).getMember().getEmail()).isEqualTo("test@example.com");
        assertThat(wishes.get(0).getProduct().getName()).isEqualTo("테스트 상품");
    }
}