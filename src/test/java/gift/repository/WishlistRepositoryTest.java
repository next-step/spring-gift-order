package gift.repository;

import static org.assertj.core.api.Assertions.assertThat;

import gift.entity.Member;
import gift.entity.Product;
import gift.entity.Wishlist;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class WishlistRepositoryTest {

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void saveAndFindWishlist() {
        // given - 테스트 준비 과정
        // 테스트용 회원과 상품 객체를 만들어서 H2 데이터베이스에 저장
        Member member = memberRepository.save(new Member("test@example.com", "password"));
        Product product = productRepository.save(new Product("테스트 상품", 10000L, "test.jpg"));
        Wishlist wishlist = new Wishlist(member, product);

        // when - 실제 테스트 대상 실행
        wishlistRepository.save(wishlist);

        // then - 테스트 결과 검증
        Wishlist foundWishlist = wishlistRepository.findById(wishlist.getId()).orElse(null);

        assertThat(foundWishlist).isNotNull();
        assertThat(foundWishlist.getMember().getEmail()).isEqualTo("test@example.com");
        assertThat(foundWishlist.getProduct().getName()).isEqualTo("테스트 상품");
    }
}