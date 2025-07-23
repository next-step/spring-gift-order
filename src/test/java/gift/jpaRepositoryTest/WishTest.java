package gift.jpaRepositoryTest;


import static org.junit.jupiter.api.Assertions.assertThrows;

import gift.constant.MemberRole;
import gift.entity.Member;
import gift.entity.Product;
import gift.entity.Wish;
import gift.repository.MemberRepository;
import gift.repository.ProductRepository;
import gift.repository.WishRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;

@DataJpaTest
public class WishTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private WishRepository wishRepository;

    @PersistenceContext
    private EntityManager em;

    @BeforeEach
    public void setUp() {
        Product product = new Product();
        product.setName("테스트1");
        product.setPrice(123);
        product.setImageURL("https://imamamger.com");
        productRepository.saveAndFlush(product);

        Member member = new Member();
        member.setMemberRole(MemberRole.valueOf("user"));
        member.setEmail("test1@gmail.com");
        member.setPassword("testpassword");

        memberRepository.saveAndFlush(member);
    }

    @AfterEach
    public void tearDown() {
        em.clear();
        productRepository.deleteAll();
    }

    @Test
    @DisplayName("존재하지 않는 상품 위시 추가 시도 실패")
    public void 존재하지_않는_상품_위시추가_실패() {
        Wish wish = new Wish();
        Product product = new Product();
        product.setName("테스트2");
        product.setPrice(1243);
        product.setImageURL("https://imamdamger.com");
        Member member = memberRepository.findById(1L).get();
        wish.setProduct(product);
        wish.setMember(member);
        wish.setQuantity(1);

        assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            wishRepository.saveAndFlush(wish);
        });
    }

    @Test
    @DisplayName("잘못된 상품 수량")
    public void 잘못된_상품_수량() {
        Wish wish = new Wish();
        wish.setProduct(productRepository.findById(1L).get());
        wish.setMember(memberRepository.findById(1L).get());
        wish.setQuantity(-1);

        assertThrows(ConstraintViolationException.class, () -> {
            wishRepository.saveAndFlush(wish);
        });
    }

}
