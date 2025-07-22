package gift.jpa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import gift.entity.Product;
import gift.entity.ProductOption;
import gift.repository.product.ProductRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    void save() {
        ProductOption option = new ProductOption("option1", 1);
        Product expected = new Product("test", 1, "test", List.of(option));

        Product actual = productRepository.save(expected);
        assertAll(
            () -> assertThat(actual.getName()).isEqualTo(expected.getName()),
            () -> assertThat(actual.getPrice()).isEqualTo(expected.getPrice()),
            () -> assertThat(actual.getImageUrl()).isEqualTo(expected.getImageUrl())
        );
    }

    @Test
    void findById() {
        ProductOption option = new ProductOption("option1", 1);
        Product expected = new Product("example1", 4700, "https://www.starbucks.co.kr/index.do",
            List.of(option));

        Product actual = productRepository.findById(1L).get();
        assertAll(
            () -> assertThat(actual.getName()).isEqualTo(expected.getName()),
            () -> assertThat(actual.getPrice()).isEqualTo(expected.getPrice()),
            () -> assertThat(actual.getImageUrl()).isEqualTo(expected.getImageUrl())
        );
    }

    @Test
    void update() {
        ProductOption option = new ProductOption("option1", 1);

        Product origin = new Product("test", 1, "test", List.of(option));
        Product expected = new Product("test2", 2, "test2", List.of(option));

        Product beforeProduct = productRepository.save(origin);
        beforeProduct.change(expected.getName(), expected.getPrice(), expected.getImageUrl());

        Product actual = productRepository.findById(4L).get();
        assertAll(
            () -> assertThat(actual.getName()).isEqualTo(expected.getName()),
            () -> assertThat(actual.getPrice()).isEqualTo(expected.getPrice()),
            () -> assertThat(actual.getImageUrl()).isEqualTo(expected.getImageUrl())
        );
    }

    @Test
    void existsById_success() {
        Boolean expected = productRepository.existsById(1L);
        assertThat(expected).isEqualTo(true);
    }

    @Test
    void existsById_fail() {
        Boolean expected = productRepository.existsById(999L);
        assertThat(expected).isEqualTo(false);
    }

    @Test
    void deleteById() {
        ProductOption option = new ProductOption("option1", 1);
        Product expected = new Product("test", 1, "test", List.of(option));

        Product actual = productRepository.save(expected);
        productRepository.deleteById(actual.getId());
        assertThat(productRepository.existsById(actual.getId())).isEqualTo(false);
    }
}
