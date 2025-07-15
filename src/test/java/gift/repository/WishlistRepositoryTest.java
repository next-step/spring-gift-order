package gift.repository;

import gift.member.Member;
import gift.member.Role;
import gift.member.repository.MemberRepository;
import gift.product.Product;
import gift.product.repository.ProductRepository;
import gift.wishlist.Wishlist;
import gift.wishlist.repository.WishlistRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
public class WishlistRepositoryTest {

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void 위시리스트에_아이템을_저장한다() {
        Member member = memberRepository.save(new Member("test@email.com", "password", "홍길동", Role.USER));
        Product product = productRepository.save(new Product("초콜릿", 1500L, "http://image"));

        Wishlist wishlist = new Wishlist(member, product, 2L);
        Wishlist saved = wishlistRepository.save(wishlist);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getQuantity()).isEqualTo(2L);
    }

    @Test
    void 위시리스트를_ID로_조회한다() {
        Member member = memberRepository.save(new Member("kim@email.com", "pass", "김철수", Role.USER));
        Product product = productRepository.save(new Product("사탕", 1000L, "http://image2"));

        Wishlist wishlist = new Wishlist(member, product, 3L);
        Wishlist saved = wishlistRepository.save(wishlist);

        Optional<Wishlist> found = wishlistRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getQuantity()).isEqualTo(3L);
    }

    @Test
    void 위시리스트의_수량을_변경한다() {
        Member member = memberRepository.save(new Member("lee@email.com", "pw", "이영희", Role.USER));
        Product product = productRepository.save(new Product("커피", 3000L, "http://image3"));

        Wishlist wishlist = new Wishlist(member, product, 1L);
        Wishlist saved = wishlistRepository.save(wishlist);

        saved.updateQuantity(5L);
        Wishlist updated = wishlistRepository.save(saved);

        assertThat(updated.getQuantity()).isEqualTo(5L);
    }

    @Test
    void 페이지네이션으로_위시리스트_조회() {
        Member member = memberRepository.save(new Member("email@email.com", "pw", "이름", Role.USER));

        for (int i = 1; i <= 25; i++) {
            Product product = productRepository.save(new Product("상품" + i, 1000L * i, "http://url.com/" + i));
            wishlistRepository.save(new Wishlist(member, product, (long) i));
        }

        PageRequest pageRequest = PageRequest.of(0, 10); // 첫 페이지, 10개
        Page<Wishlist> wishlistPage = wishlistRepository.findAllByMemberId(member.getId(), pageRequest);

        assertThat(wishlistPage.getContent().size()).isEqualTo(10);
        assertThat(wishlistPage.getTotalElements()).isEqualTo(25);
        assertThat(wishlistPage.getTotalPages()).isEqualTo(3);
    }
}
