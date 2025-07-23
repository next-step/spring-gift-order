package gift.jpaRepositoryTest;

import static org.junit.jupiter.api.Assertions.assertThrows;

import gift.entity.Option;
import gift.entity.Product;
import gift.repository.OptionRepository;
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
public class OptionTest {

    @Autowired
    private OptionRepository optionRepository;
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
        optionRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    @DisplayName("잘못된 옵션명")
    public void 잘못된_옵션명() {
        Option option = new Option();
        Product product = productRepository.findById(1L).get();
        option.setName("이런 옵션 안옵션!@#$^&$%^%*^&*(_)__*)&%^$%#$");
        option.setProduct(product);
        option.setQuantity(12);
        assertThrows(ConstraintViolationException.class, () -> {
            optionRepository.saveAndFlush(option);
        });
    }

    @Test
    @DisplayName("잘못된 수량")
    public void 잘못된_수량() {
        Option option = new Option();
        Product product = productRepository.findById(1L).get();
        option.setName("옵션명1");
        option.setProduct(product);
        option.setQuantity(0);
        assertThrows(ConstraintViolationException.class, () -> {
            optionRepository.saveAndFlush(option);
        });
    }
}
