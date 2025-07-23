package gift;

import static org.assertj.core.api.Assertions.assertThat;
import gift.domain.Member;
import gift.domain.Product;
import gift.domain.WishList;
import gift.repository.member.MemberJpaRepository;
import gift.repository.product.ProductJpaRepository;
import gift.repository.wishlist.WishListJpaRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@DataJpaTest
@DisplayName("WishListJpaRepository 테스트")
class WishListJpaRepositoryTest {

    @Autowired
    private WishListJpaRepository wishListRepository;

    @Autowired
    private MemberJpaRepository memberRepository;

    @Autowired
    private ProductJpaRepository productRepository;

    @Autowired
    private TestEntityManager entityManager;

    private PasswordEncoder passwordEncoder;
    private Member testMember1;
    private Member testMember2;
    private Product testProduct1;
    private Product testProduct2;
    private Product testProduct3;


    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();

        testMember1 = Member.of("member1@test", "pass1", passwordEncoder);
        testMember2 = Member.of("member2@test", "pass2", passwordEncoder);
        entityManager.persist(testMember1);
        entityManager.persist(testMember2);

        testProduct1 = Product.of("p1", 10000, "url1");
        testProduct2 = Product.of("p2", 20000, "url2");
        testProduct3 = Product.of("p3", 30000, "url3");
        entityManager.persist(testProduct1);
        entityManager.persist(testProduct2);
        entityManager.persist(testProduct3);

        entityManager.flush();
        entityManager.clear();

        testMember1 = memberRepository.findById(testMember1.getId()).orElseThrow();
        testMember2 = memberRepository.findById(testMember2.getId()).orElseThrow();
        testProduct1 = productRepository.findById(testProduct1.getId()).orElseThrow();
        testProduct2 = productRepository.findById(testProduct2.getId()).orElseThrow();
        testProduct3 = productRepository.findById(testProduct3.getId()).orElseThrow();
    }

    @Test
    void 위시리스트_저장() {
        WishList newWishList = WishList.of(testMember1, testProduct1, 5);

        WishList savedWishList = wishListRepository.save(newWishList);
        entityManager.flush();
        entityManager.clear();

        assertThat(savedWishList).isNotNull();
        assertThat(savedWishList.getId()).isNotNull();
        assertThat(savedWishList.getMember().getId()).isEqualTo(testMember1.getId());
        assertThat(savedWishList.getProduct().getId()).isEqualTo(testProduct1.getId());
        assertThat(savedWishList.getQuantity()).isEqualTo(5);

        WishList foundWishList = wishListRepository.findById(savedWishList.getId()).orElse(null);
        assertThat(foundWishList).isNotNull();
        assertThat(foundWishList.getMember().getEmail()).isEqualTo(testMember1.getEmail());
        assertThat(foundWishList.getProduct().getName()).isEqualTo(testProduct1.getName());
    }

    @Test
    void 회원ID로_위시리스트_전체_조회() {
        WishList wl1 = WishList.of(testMember1, testProduct1, 1);
        WishList wl2 = WishList.of(testMember1, testProduct2, 2);
        WishList wl3 = WishList.of(testMember1, testProduct3, 3);
        wishListRepository.save(wl1);
        wishListRepository.save(wl2);
        wishListRepository.save(wl3);
        entityManager.flush();
        entityManager.clear();

        List<WishList> wishLists = wishListRepository.findAllByMemberId(testMember1.getId());

        assertThat(wishLists).hasSize(3);
    }

    @Test
    void 회원ID와_상품ID로_위시리스트_단건_조회() {
        WishList wishList = WishList.of(testMember1, testProduct1, 5);
        wishListRepository.save(wishList);
        entityManager.flush();
        entityManager.clear();

        Optional<WishList> foundWishList = wishListRepository.findByMemberIdAndProductId(testMember1.getId(), testProduct1.getId());

        assertThat(foundWishList).isPresent();
        assertThat(foundWishList.get().getQuantity()).isEqualTo(5);
    }

    @Test
    void 위시리스트_수량_업데이트() {
        WishList wishList = WishList.of(testMember1, testProduct1, 5);
        wishListRepository.save(wishList);
        entityManager.flush();
        entityManager.clear();

        WishList foundWishList = wishListRepository.findByMemberIdAndProductId(testMember1.getId(), testProduct1.getId()).orElseThrow();
        foundWishList.update(3);
        wishListRepository.save(foundWishList);
        entityManager.flush();
        entityManager.clear();

        WishList updatedWishList = wishListRepository.findByMemberIdAndProductId(testMember1.getId(), testProduct1.getId()).orElseThrow();
        assertThat(updatedWishList.getQuantity()).isEqualTo(8);

        updatedWishList.update(-5);
        wishListRepository.save(updatedWishList);
        entityManager.flush();
        entityManager.clear();

        WishList finalWishList = wishListRepository.findByMemberIdAndProductId(testMember1.getId(), testProduct1.getId()).orElseThrow();
        assertThat(finalWishList.getQuantity()).isEqualTo(3);
    }

    @Test
    void 위시리스트_삭제() {
        WishList wishListToDelete = WishList.of(testMember1, testProduct1, 10);
        wishListRepository.save(wishListToDelete);
        entityManager.flush();

        Long wishListId = wishListToDelete.getId();

        wishListRepository.deleteById(wishListId);
        entityManager.flush();

        Optional<WishList> foundAfterDelete = wishListRepository.findById(wishListId);
        assertThat(foundAfterDelete).isEmpty();
    }
}