package gift.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import gift.model.Member;
import gift.model.Product;
import gift.model.Wishlist;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest
@Transactional
class WishlistRepositoryTest {

    @Autowired
    WishlistRepository wishlistRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ProductRepository productRepository;

    private Member saveMember() {
        Member member = new Member("kim@naver.com", "1234");
        return memberRepository.save(member);
    }

    private Product saveProduct() {
        Product product = new Product("test_coffee", 2500, "https://test_coffee.jpg", false);
        return productRepository.save(product);
    }

    @Test
    void save() {
        // given
        Member member = saveMember();
        Product product = saveProduct();
        Wishlist expected = new Wishlist(member, product, 3);

        // when
        Wishlist actual = wishlistRepository.save(expected);

        // then
        assertAll(
            () -> assertThat(actual.getId()).isNotNull(),
            () -> assertThat(actual.getMember().getId()).isEqualTo(expected.getMember().getId()),
            () -> assertThat(actual.getProduct().getId()).isEqualTo(expected.getProduct().getId()),
            () -> assertThat(actual.getQuantity()).isEqualTo(expected.getQuantity())
        );
    }

    @Test
    void findByMemberAndProduct() {
        // given
        Member member = saveMember();
        Product product = saveProduct();
        Wishlist expected = new Wishlist(member, product, 3);
        Wishlist saved = wishlistRepository.save(expected);

        // when
        Wishlist actual = wishlistRepository.findByMemberAndProduct(member, product)
            .orElseThrow(() -> new IllegalArgumentException("Wishlist not found"));

        // then
        assertAll(
            () -> assertThat(actual.getId()).isNotNull(),
            () -> assertThat(actual.getMember().getId()).isEqualTo(expected.getMember().getId()),
            () -> assertThat(actual.getProduct().getId()).isEqualTo(expected.getProduct().getId()),
            () -> assertThat(actual.getQuantity()).isEqualTo(expected.getQuantity())
        );
    }

    @Test
    void findAllByMember() {
        // given
        Member member = saveMember();
        Product product1 = saveProduct();
        Product product2 = saveProduct();

        wishlistRepository.save(new Wishlist(member, product1, 3));
        wishlistRepository.save(new Wishlist(member, product2, 1));

        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.asc("id"));
        Pageable pageable = PageRequest.of(0, 10, Sort.by(sorts));

        // when
        Page<Wishlist> wishlists = wishlistRepository.findAllByMember(pageable, member);

        // then
        assertThat(wishlists).hasSize(2);
    }

    @Test
    void deleteByMemberAndProduct() {
        // given
        Member member = saveMember();
        Product product = saveProduct();
        Wishlist wishlist = new Wishlist(member, product, 3);
        wishlistRepository.save(wishlist);

        // when
        wishlistRepository.deleteByMemberAndProduct(member, product);

        // then
        Optional<Wishlist> afterDelete = wishlistRepository.findByMemberAndProduct(member, product);
        assertThat(afterDelete).isEmpty();
    }

    @Test
    void 없는_위시리스트_삭제시_deleteByMemberAndProduct() {
        // given
        Member member = saveMember();
        Product product = saveProduct();

        // when
        wishlistRepository.deleteByMemberAndProduct(member, product);

        // then
        Optional<Wishlist> afterDelete = wishlistRepository.findByMemberAndProduct(member, product);
        assertThat(afterDelete).isEmpty();
    }
}

