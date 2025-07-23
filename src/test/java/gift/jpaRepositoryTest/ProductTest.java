package gift.jpaRepositoryTest;


import static org.junit.jupiter.api.Assertions.assertThrows;

import gift.entity.Product;
import gift.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class ProductTest {

    @Autowired
    private ProductRepository productRepository;

    @PersistenceContext
    private EntityManager em;

    @BeforeEach
    public void setUp() {
        Product product = new Product();
        product.setName("테스트1");
        product.setPrice(123);
        product.setImageURL("https://imamamger.com");
        productRepository.saveAndFlush(product);
    }

    @AfterEach
    public void tearDown() {
        em.clear();
        productRepository.deleteAll();
    }

    @Test
    @DisplayName("잘못된 상품명")
    public void 잘못된_상품명() {
        Product product = new Product();
        product.setName("너무긴상품명은조금아닌것같은데요");
        product.setPrice(123);
        product.setImageURL("https://imamamger.com");
        assertThrows(ConstraintViolationException.class, () -> {
            productRepository.saveAndFlush(product);
        });
    }

    @Test
    @DisplayName("잘못된 가격")
    public void 잘못된_가격() {
        Product product = new Product();
        product.setName("테스트상품1");
        product.setPrice(0);
        product.setImageURL("https://imamamger.com");
        assertThrows(ConstraintViolationException.class, () -> {
            productRepository.saveAndFlush(product);
        });
    }

    @Test
    @DisplayName("잘못된 이미지 URL")
    public void 잘못된_이미지_URL() {
        Product product = new Product();
        product.setName("테스트상품1");
        product.setPrice(123);
        product.setImageURL("picture");
        assertThrows(ConstraintViolationException.class, () -> {
            productRepository.saveAndFlush(product);
        });
    }
}
