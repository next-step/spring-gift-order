package gift.repository;


import gift.entity.Product;
import gift.entity.ProductOption;
import gift.fixture.ProductFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ProductOptionRepositoryTest {

    @Autowired
    private ProductRepository productRepository;


    @Test
    @DisplayName("옵션을 추가한다")
    void save_test() {
        Product testProduct =ProductFixture.createProduct2();
        testProduct.addOption("테스트 옵션",9999);
        Product savedProduct=productRepository.save(testProduct);

        Product product = productRepository.findById(savedProduct.getId()).get();
        assertThat(product).isNotNull();

        List<ProductOption> foundOptions = product.getOptions();

        assertThat(foundOptions.getFirst().getName()).isEqualTo("테스트 옵션");


    }

    @Test
    @DisplayName("상품의 옵션을 조회한다")
    void find_test() {
        Product testProduct =ProductFixture.createProduct2();
        testProduct.addOption("테스트 옵션",9999);
        Product savedProduct=productRepository.save(testProduct);

        Product product = productRepository.findById(savedProduct.getId()).get();
        assertThat(product).isNotNull();

        List<ProductOption> foundOptions = product.getOptions();


        assertThat(foundOptions).hasSize(1);

    }
}
