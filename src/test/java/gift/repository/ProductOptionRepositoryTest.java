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
    private ProductOptionRepository productOptionRepository;

    @Autowired
    private ProductRepository productRepository;


    @Test
    @DisplayName("옵션을 추가한다")
    void save_test() {
        Product test_product = productRepository.save(ProductFixture.createProduct2());

        ProductOption option = new ProductOption("테스트 옵션", 9999,test_product);
        ProductOption savedOption = productOptionRepository.save(option);


        assertThat(savedOption.getId()).isNotNull();
        assertThat(savedOption.getName()).isEqualTo("테스트 옵션");
        assertThat(savedOption.getQuantity()).isEqualTo(9999);
        assertThat(savedOption.getProduct().getId()).isEqualTo(test_product.getId());

    }

    @Test
    @DisplayName("상품의 옵션을 조회한다")
    void find_test() {
        Product test_product = productRepository.save(ProductFixture.createProduct2());

        ProductOption option = new ProductOption("테스트 옵션", 9999,test_product);
        ProductOption savedOption = productOptionRepository.save(option);

        List<ProductOption> foundOptions = productOptionRepository.findByProductId(savedOption.getId());


        assertThat(foundOptions).hasSize(1);

    }
}
