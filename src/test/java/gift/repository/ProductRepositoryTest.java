package gift.repository;

import gift.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.assertj.core.api.Assertions.tuple;

@DataJpaTest
public class ProductRepositoryTest {
    @Autowired
    private ProductRepository productRepository;

    @Test
    void save() {
        Product expected = new Product("과자", 1000L, "http://snack");
        Product actual = productRepository.save(expected);
        assertAll(
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.getName()).isEqualTo(expected.getName()),
                () -> assertThat(actual.getPrice()).isEqualTo(expected.getPrice()),
                () -> assertThat(actual.getImageUrl()).isEqualTo(expected.getImageUrl())
        );
    }

    @Test
    void findById() {
        Product product1 = productRepository.save(new Product("과자", 1000L, "http://snack"));
        Product product2 = productRepository.findById(product1.getId()).orElse(null);
        assertThat(product1).isEqualTo(product2);
    }

    @Test
    void sortByIdAsc() {
        Product a = productRepository.save(new Product("B", 2000L, "u2"));
        Product b = productRepository.save(new Product("C", 3000L, "u1"));
        Product c = productRepository.save(new Product("A", 1000L, "u3"));
        Page<Product> page = productRepository.findAll(
                PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "id"))
        );

        List<Product> products = page.getContent();
        assertThat(products.stream().map(Product::getId).toList())
                .containsExactly(a.getId(),b.getId(),c.getId());
    }

    @Test
    void sortByPriceDescThenNameAsc() {
        productRepository.save(new Product("B", 2000L, "u2"));
        productRepository.save(new Product("A", 3000L, "u1"));
        productRepository.save(new Product("A", 1000L, "u3"));

        Page<Product> page = productRepository.findAll(
                PageRequest.of(0, 20,
                        Sort.by(Sort.Order.desc("price"), Sort.Order.asc("name")))
        );

        assertThat(page.getContent())
                .extracting(Product::getPrice, Product::getName)
                .containsExactly(
                        tuple(3000L, "A"),
                        tuple(2000L, "B"),
                        tuple(1000L, "A")
                );
    }
}
