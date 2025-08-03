package gift.Repository;

import gift.entity.Member;
import gift.entity.Product;
import gift.entity.Wishlist;
import gift.repository.MemberRepository;
import gift.repository.ProductRepository;
import gift.repository.WishlistRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
@TestPropertySource(properties = "spring.sql.init.mode=never")
class WishlistRepositoryTest {

    @Autowired
    private WishlistRepository wishlists;

    @Autowired
    private MemberRepository members;

    @Autowired
    private ProductRepository products;

    @Autowired
    private EntityManager em;

    private Member savedMember;
    private Product savedProduct;

    @BeforeEach
    void setup() {
        savedMember = members.save(new Member("test@example.com", "1234"));
        savedProduct = products.save(new Product("초콜릿", 1000, "img"));
    }

    @Test
    void 저장기능테스트() {
        Wishlist expected = new Wishlist(savedMember, savedProduct);
        Wishlist actual = wishlists.save(expected);


        assertAll(
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.getMember().getEmail()).isEqualTo("test@example.com"),
                () -> assertThat(actual.getProduct().getName()).isEqualTo("초콜릿"),
                () -> assertThat(actual.getQuantity()).isEqualTo(1)
        );
    }

    @Test
    void 멤버로위시리스트조회하기() {
        Wishlist wishlist = new Wishlist(savedMember, savedProduct);

        wishlists.save(wishlist);

        em.flush();
        em.clear();

        List<Wishlist> result = wishlists.findByMember(savedMember);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getProduct().getName()).isEqualTo("초콜릿");

    }
}
