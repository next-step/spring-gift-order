package gift.repository;
import gift.entity.Option;
import gift.entity.Product;
import gift.entity.ProductOption;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
public class ProductOptionRepositoryTest {
    @Autowired
    private ProductOptionRepository productOptionRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OptionRepository optionRepository;

    @Autowired
    private TestEntityManager tem;

    @Test
    public void findByProductId() {
        Product expactProduct = productRepository.save(new Product("과자", 1000L, "http://snack"));
        Option expactOption = optionRepository.save(new Option("할인율"));
        ProductOption expactProductOption = productOptionRepository.save(new ProductOption(expactProduct, expactOption, 30L));

        tem.flush();
        tem.clear();

        Product actualProduct = productRepository.findById(expactProduct.getId()).orElse(null);
        assertThat(actualProduct.getOptions().getFirst().getOption().getName())
                .isSameAs(expactProductOption.getOption().getName());
    }

    @Test
    public void 옵션이름50글자제한() {
        assertThrows(ConstraintViolationException.class, () -> {
            Option expactOption = optionRepository.save(new Option("이글자는오십글자인가이글자는오십글자인가이글자는오십글자인가이글자는오십글자인가이글자는오십글자인가인가"));
        });
    }

    @Test
    public void 옵션이름특수문자제한() {
        assertThrows(ConstraintViolationException.class, () -> {
            Option expactOption = optionRepository.save(new Option("!@#$%^&*()"));
        });
    }

    @Test
    public void duplicateProductOption() {
        Product expactProduct = productRepository.save(new Product("과자", 1000L, "http://snack"));
        Option expactOption = optionRepository.save(new Option("할인율"));
        ProductOption po1 = productOptionRepository.save(new ProductOption(expactProduct, expactOption, 30L));

        assertThrows(DataIntegrityViolationException.class, () -> {
            productOptionRepository.save(new ProductOption(expactProduct, expactOption, 30L));
        });

    }
}
