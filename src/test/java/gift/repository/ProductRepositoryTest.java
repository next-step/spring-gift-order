package gift.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import gift.model.Product;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest
@Transactional
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    void save() {
        // given
        Product expected = new Product("coffee", 2500, "https://coffee.jpg", false);

        // when
        Product actual = productRepository.save(expected);

        // then
        assertAll(
            () -> assertThat(actual.getId()).isNotNull(),
            () -> assertThat(actual.getName()).isEqualTo(expected.getName()),
            () -> assertThat(actual.getPrice()).isEqualTo(expected.getPrice()),
            () -> assertThat(actual.getImageUrl()).isEqualTo(expected.getImageUrl())
        );
    }

    @Test
    void findById() {
        // given
        Product expected = new Product("coffee", 2500, "https://coffee.jpg", false);
        Product saved = productRepository.save(expected);

        // when
        Product actual = productRepository.findById(saved.getId())
            .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        // then
        assertAll(
            () -> assertThat(actual.getName()).isEqualTo(expected.getName()),
            () -> assertThat(actual.getPrice()).isEqualTo(expected.getPrice()),
            () -> assertThat(actual.getImageUrl()).isEqualTo(expected.getImageUrl())
        );
    }

    @Test
    void findAll() {
        // given
        long before = productRepository.count();

        Product product1 = new Product("test_coffee", 2500, "https://test_coffee.jpg", false);
        Product product2 = new Product("test_tea", 2000, "https://test_tea.jpg", false);
        productRepository.save(product1);
        productRepository.save(product2);

        // when
        List<Product> members = productRepository.findAll();

        // then
        assertAll(
            () -> assertThat(members).hasSize((int) (2 + before)),
            () -> assertThat(members)
                .extracting(Product::getName)
                .contains("test_coffee", "test_tea")
        );
    }

    @Test
    void update() {
        // given
        Product product = new Product("test_coffee", 2500, "https://test_coffee.jpg", false);
        Product savedProduct = productRepository.save(product);

        // when
        savedProduct.update("test_tea", 3500, "https://test_tea.jpg", false);

        // then
        Product foundProduct = productRepository.findById(savedProduct.getId())
            .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        assertAll(
            () -> assertThat(foundProduct.getName()).isEqualTo("test_tea"),
            () -> assertThat(foundProduct.getPrice()).isEqualTo(3500),
            () -> assertThat(foundProduct.getImageUrl()).isEqualTo("https://test_tea.jpg")
        );
    }


    @Test
    void deleteById() {
        // given
        Product product = new Product("test_coffee", 2500, "https://test_coffee.jpg", false);
        Product savedProduct = productRepository.save(product);

        // when
        productRepository.deleteById(savedProduct.getId());

        // then
        Optional<Product> deletedProduct = productRepository.findById(savedProduct.getId());
        assertThat(deletedProduct).isEmpty();
    }

}
