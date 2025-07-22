package gift.repository;


import static org.assertj.core.api.Assertions.assertThat;

import gift.domain.Member;
import gift.domain.Product;
import gift.domain.Wish;
import gift.util.ShaUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@DataJpaTest
class WishJpaRepositoryTest {

    @Autowired
    private WishJpaRepository wishRepository;
    @Autowired
    private MemberJpaRepository memberRepository;
    @Autowired
    private ProductJpaRepository productRepository;

    @Test
    @DisplayName("위시에 상품 저장")
    void 위시_상품저장() {
        //when
        String password = "123";
        String salt = ShaUtil.getSalt();
        String encryptPassword = ShaUtil.encrypt(password, salt);
        Member member = memberRepository.save(
                new Member(null, "ex@email.com", encryptPassword, salt));

        Product savedProduct = productRepository.save(
                new Product(null, "product1", 1000, "url.com"));

        //given
        Wish savedWish = wishRepository.save(new Wish(null, member, savedProduct, 1));

        //then
        assertThat(savedWish.getId()).isNotNull();
    }

    @Test
    @DisplayName("멤버별로 상품 조회")
    void getWishListByMember() {
        //when
        String password = "123";
        String salt = ShaUtil.getSalt();
        String encryptPassword = ShaUtil.encrypt(password, salt);
        Member member = memberRepository.save(
                new Member(null, "ex@email.com", encryptPassword, salt));

        Product product1 = productRepository.save(
                new Product(null, "product1", 1000, "url.com"));

        Product product2 = productRepository.save(
                new Product(null, "product2", 1000, "url.com"));

        //given
        wishRepository.save(new Wish(null, member, product1, 1));
        wishRepository.save(new Wish(null, member, product2, 1));
        Pageable pageable = PageRequest.of(0, 10);
        Page<Wish> wishes = wishRepository.findByMemberId(member.getId(), pageable);

        //then
        assertThat(wishes.getContent().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("페이지네이션 테스트")
    void 멤버별_상품조회_페이지네이션() {
        //when
        String password = "123";
        String salt = ShaUtil.getSalt();
        String encryptPassword = ShaUtil.encrypt(password, salt);
        Member member = memberRepository.save(
                new Member(null, "ex@email.com", encryptPassword, salt));

        Product product1 = productRepository.save(
                new Product(null, "product1", 1000, "url.com"));

        Product product2 = productRepository.save(
                new Product(null, "product2", 1000, "url.com"));

        Product product3 = productRepository.save(
                new Product(null, "product3", 1000, "url.com"));

        //given
        wishRepository.save(new Wish(null, member, product1, 1));
        wishRepository.save(new Wish(null, member, product2, 1));
        wishRepository.save(new Wish(null, member, product3, 1));
        Pageable pageable = PageRequest.of(1, 2);
        Page<Wish> wishes = wishRepository.findByMemberId(member.getId(), pageable);

        //then
        assertThat(wishes.getContent().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("수량 변경")
    void updateQuantity() {
        //when
        String password = "123";
        String salt = ShaUtil.getSalt();
        String encryptPassword = ShaUtil.encrypt(password, salt);
        Member member = memberRepository.save(
                new Member(null, "ex@email.com", encryptPassword, salt));

        Product savedProduct = productRepository.save(
                new Product(null, "product1", 1000, "url.com"));

        Wish savedWish = wishRepository.save(new Wish(null, member, savedProduct, 1));

        //given
        savedWish.update(3);

        //then
        assertThat(savedWish.getQuantity()).isEqualTo(3);
    }

    @Test
    @DisplayName("위시 상품 삭제")
    void delete() {
        //when
        String password = "123";
        String salt = ShaUtil.getSalt();
        String encryptPassword = ShaUtil.encrypt(password, salt);
        Member member = memberRepository.save(
                new Member(null, "ex@email.com", encryptPassword, salt));

        Product product1 = productRepository.save(
                new Product(null, "product1", 1000, "url.com"));

        Product product2 = productRepository.save(
                new Product(null, "product2", 1000, "url.com"));

        Wish wish1 = new Wish(null, member, product1, 1);
        Wish wish2 = new Wish(null, member, product2, 1);

        //given
        wishRepository.save(wish1);
        wishRepository.save(wish2);
        wishRepository.deleteById(wish1.getId());
        Pageable pagable = PageRequest.of(0, 10);

        //then
        assertThat(wishRepository.findByMemberId(member.getId(), pagable).getContent().size()).isEqualTo(1);
    }
}