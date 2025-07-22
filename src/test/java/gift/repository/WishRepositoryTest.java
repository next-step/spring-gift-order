package gift.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

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
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class WishRepositoryTest {

    @Autowired
    private WishRepository wishRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

    private Member testMember;
    private Product testProduct;
    private Product testProduct2;

    @BeforeEach
    void setUp() {
        testMember = memberRepository.save(new Member("test@domain.com", "pw"));
        testProduct = productRepository.save(new Product("테스트 상품", 4500, "https://test.jpg"));
        testProduct2 = productRepository.save(
            new Product("또다른 상품", 3000, "https://another.jpg"));
    }

    @Test
    @DisplayName("위시 저장 테스트")
    void save() {
        Wish wish = new Wish(testMember, testProduct, 1);
        Wish savedWish = wishRepository.save(wish);

        Wish testWish = wishRepository.findById(savedWish.getId()).orElseThrow();

        assertAll("유저 필드 검증",
            () -> assertThat(testWish.getMember().getId()).isNotNull(),
            () -> assertThat(testWish.getMember().getEmail()).isEqualTo("test@domain.com")
        );

        assertAll("상품 필드 검증",
            () -> assertThat(testWish.getProduct().getId()).isNotNull(),
            () -> assertThat(testWish.getProduct().getName()).isEqualTo("테스트 상품"),
            () -> assertThat(testWish.getProduct().getPrice()).isEqualTo(4500),
            () -> assertThat(testWish.getProduct().getImageUrl()).isEqualTo("https://test.jpg"),
            () -> assertThat(testWish.getQuantity()).isEqualTo(1)
        );
    }

    @Test
    @DisplayName("유저와 상품으로 위시 존재 여부 확인")
    void existsByMemberAndProduct() {
        wishRepository.save(new Wish(testMember, testProduct, 1));

        boolean exists = wishRepository.existsByMemberAndProduct(testMember, testProduct);

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("유저와 상품으로 위시 삭제")
    void deleteByMemberAndProduct() {
        Wish wish = wishRepository.save(new Wish(testMember, testProduct, 1));

        wishRepository.deleteByMemberAndProduct(testMember, testProduct);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Wish> wishesPage = wishRepository.findAllByMember(testMember, pageable);

        List<Wish> wishes = wishesPage.getContent();

        assertThat(wishes).doesNotContain(wish);
    }

    @Test
    @DisplayName("유저로 모든 위시 조회")
    void findAllByMember() {
        Wish wish1 = wishRepository.save(new Wish(testMember, testProduct, 1));
        Wish wish2 = wishRepository.save(new Wish(testMember, testProduct2, 2));

        Pageable pageable = PageRequest.of(0, 10);
        Page<Wish> wishesPage = wishRepository.findAllByMember(testMember, pageable);

        List<Wish> wishes = wishesPage.getContent();

        assertThat(wishes).hasSize(2);
        assertThat(wishes).contains(wish1, wish2);
    }
}
