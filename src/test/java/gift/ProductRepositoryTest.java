package gift;

import gift.entity.Product;
import gift.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("ID 오름차순으로 모든 상품을 조회")
    void findAllByOrderByIdAsc() {
        Product product1 = new Product("A", 1000L, "url1");
        Product product2 = new Product("B", 2000L, "url2");

        productRepository.save(product1);
        productRepository.save(product2);

        List<Product> products = productRepository.findAllByOrderByIdAsc();

        assertThat(products).hasSize(2);
        assertThat(products.get(0).getName()).isEqualTo("A");
        assertThat(products.get(1).getName()).isEqualTo("B");
    }
}
