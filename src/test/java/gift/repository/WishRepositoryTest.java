package gift.repository;

import gift.domain.Member;
import gift.domain.Product;
import gift.domain.Wish;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
class WishRepositoryTest {

    @Autowired
    private WishRepository wishRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

    private Member testMember;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testMember = new Member("test@example.com", "password");
        memberRepository.save(testMember);

        testProduct = new Product("Test Product", 10000, "test_image.jpg");
        productRepository.save(testProduct);
    }

    @Test
    void save() {
        Wish expected = new Wish(testMember, testProduct, 1);
        Wish actual = wishRepository.save(expected);

        assertAll(
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.getMember().getId()).isEqualTo(testMember.getId()),
                () -> assertThat(actual.getProduct().getId()).isEqualTo(testProduct.getId())
        );
    }

    @Test
    void findByMemberId() {
        Wish wish1 = new Wish(testMember, testProduct, 1);
        wishRepository.save(wish1);

        Product anotherProduct = new Product("Another Product", 20000, "another_image.jpg");
        productRepository.save(anotherProduct);
        Wish wish2 = new Wish(testMember, anotherProduct, 1);
        wishRepository.save(wish2);

        Pageable pageable = PageRequest.of(0, 10);
        assertThat(wishRepository.findByMemberId(testMember.getId(), pageable).getContent()).hasSize(2);
    }

    @Test
    void deleteByMemberIdAndProductId() {
        Wish wish = new Wish(testMember, testProduct, 1);
        wishRepository.save(wish);

        wishRepository.deleteByMemberIdAndProductId(testMember.getId(), testProduct.getId());

        Pageable pageable = PageRequest.of(0, 10);
        assertThat(wishRepository.findByMemberId(testMember.getId(), pageable).getContent()).isEmpty();
    }
}
