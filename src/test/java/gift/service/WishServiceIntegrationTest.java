package gift.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import gift.domain.Member;
import gift.domain.Product;
import gift.domain.Wish;
import gift.dto.WishRequest;
import gift.dto.WishResponse;
import gift.exception.BusinessException;
import gift.exception.ErrorCode;
import gift.repository.MemberJpaRepository;
import gift.repository.ProductJpaRepository;
import gift.repository.WishJpaRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@DisplayName("위시 서비스 통합 테스트")
class WishServiceIntegrationTest {

    @Autowired
    private WishService wishService;

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private ProductJpaRepository productJpaRepository;

    @Autowired
    private WishJpaRepository wishJpaRepository;

    @Test
    @DisplayName("위시 생성 및 조회 테스트")
    void createAndGetWishTest() {
        // Setup
        Member member = Member.of("wish@example.com", "password");
        Member savedMember = memberJpaRepository.save(member);

        Product product = Product.of("테스트상품", 10000, "http://example.com/test.jpg");
        Product savedProduct = productJpaRepository.save(product);

        // Create wish
        WishRequest request = new WishRequest(savedProduct.id());
        Wish createdWish = wishService.createOrUpdate(savedMember.id(), request);

        assertAll(
                () -> assertThat(createdWish.getId()).isNotNull(),
                () -> assertThat(createdWish.getMember().id()).isEqualTo(savedMember.id()),
                () -> assertThat(createdWish.getProduct().id()).isEqualTo(savedProduct.id())
        );

        // Get wishes
        List<WishResponse> wishes = wishService.getAllByMemberId(savedMember.id());
        assertThat(wishes).hasSize(1);
        
        WishResponse wishResponse = wishes.get(0);
        assertAll(
                () -> assertThat(wishResponse.id()).isEqualTo(createdWish.getId()),
                () -> assertThat(wishResponse.name()).isEqualTo("테스트상품"),
                () -> assertThat(wishResponse.price()).isEqualTo(10000),
                () -> assertThat(wishResponse.imageUrl()).isEqualTo("http://example.com/test.jpg")
        );
    }

    @Test
    @DisplayName("중복 위시 생성 테스트")
    void duplicateWishTest() {
        // Setup
        Member member = Member.of("duplicate@example.com", "password");
        Member savedMember = memberJpaRepository.save(member);

        Product product = Product.of("중복상품", 15000, "http://example.com/duplicate.jpg");
        Product savedProduct = productJpaRepository.save(product);

        // Create first wish
        WishRequest request = new WishRequest(savedProduct.id());
        Wish firstWish = wishService.createOrUpdate(savedMember.id(), request);

        // Try to create duplicate wish
        Wish secondWish = wishService.createOrUpdate(savedMember.id(), request);

        // Should return the same wish (not create a new one)
        assertThat(firstWish.getId()).isEqualTo(secondWish.getId());

        // Verify only one wish exists
        List<WishResponse> wishes = wishService.getAllByMemberId(savedMember.id());
        assertThat(wishes).hasSize(1);
    }

    @Test
    @DisplayName("위시 삭제 테스트")
    void deleteWishTest() {
        // Setup
        Member member = Member.of("delete@example.com", "password");
        Member savedMember = memberJpaRepository.save(member);

        Product product = Product.of("삭제상품", 20000, "http://example.com/delete.jpg");
        Product savedProduct = productJpaRepository.save(product);

        Wish wish = Wish.of(savedMember, savedProduct);
        Wish savedWish = wishJpaRepository.save(wish);

        // Delete wish
        wishService.delete(savedMember.id(), savedWish.getId());

        // Verify deletion
        List<WishResponse> wishes = wishService.getAllByMemberId(savedMember.id());
        assertThat(wishes).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 회원으로 위시 생성 시 예외 발생")
    void createWishWithNonExistentMemberTest() {
        // Setup
        Product product = Product.of("예외상품", 25000, "http://example.com/exception.jpg");
        Product savedProduct = productJpaRepository.save(product);

        WishRequest request = new WishRequest(savedProduct.id());

        // Should throw exception
        assertThatThrownBy(() -> wishService.createOrUpdate(999L, request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("존재하지 않는 상품으로 위시 생성 시 예외 발생")
    void createWishWithNonExistentProductTest() {
        // Setup
        Member member = Member.of("exception@example.com", "password");
        Member savedMember = memberJpaRepository.save(member);

        WishRequest request = new WishRequest(999L);

        // Should throw exception
        assertThatThrownBy(() -> wishService.createOrUpdate(savedMember.id(), request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PRODUCT_NOT_FOUND);
    }

    @Test
    @DisplayName("다른 회원의 위시 삭제 시 예외 발생")
    void deleteOtherMemberWishTest() {
        // Setup
        Member member1 = Member.of("member1@example.com", "password");
        Member member2 = Member.of("member2@example.com", "password");
        Member savedMember1 = memberJpaRepository.save(member1);
        Member savedMember2 = memberJpaRepository.save(member2);

        Product product = Product.of("권한상품", 30000, "http://example.com/auth.jpg");
        Product savedProduct = productJpaRepository.save(product);

        Wish wish = Wish.of(savedMember1, savedProduct);
        Wish savedWish = wishJpaRepository.save(wish);

        // Try to delete other member's wish
        assertThatThrownBy(() -> wishService.delete(savedMember2.id(), savedWish.getId()))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.UNAUTHORIZED_ACCESS);
    }
}
