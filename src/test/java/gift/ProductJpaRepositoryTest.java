package gift;

import static org.assertj.core.api.Assertions.assertThat;
import gift.domain.Product;
import gift.repository.product.ProductJpaRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
@DisplayName("ProductJpaRepository 테스트")
class ProductJpaRepositoryTest {

    @Autowired
    private ProductJpaRepository productRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void 상품_저장() {
        Product newProduct = Product.of("p", 10000, "save url");

        Product savedProduct = productRepository.save(newProduct);
        entityManager.flush();
        entityManager.clear();

        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getId()).isNotNull();
        assertThat(savedProduct.getName()).isEqualTo("p");
        assertThat(savedProduct.getPrice()).isEqualTo(10000);
        assertThat(savedProduct.getImageUrl()).isEqualTo("save url");

        Product foundProduct = productRepository.findById(savedProduct.getId()).orElse(null);
        assertThat(foundProduct).isNotNull();
    }

    @Test
    void ID로_상품_조회() {
        Product product = Product.of("p", 20000, "get url");
        entityManager.persist(product);
        entityManager.flush();
        entityManager.clear();

        Product foundProduct = productRepository.findById(product.getId()).orElse(null);

        assertThat(foundProduct).isNotNull();
        assertThat(foundProduct.getName()).isEqualTo("p");
    }

    @Test
    void 존재하지않는_ID로_상품_조회() {
        Optional<Product> foundProduct = productRepository.findById(999L);

        assertThat(foundProduct).isEmpty();
    }

    @Test
    void 상품_정보_수정() {
        Product productToUpdate = Product.of("original", 30000, "origin url");
        entityManager.persist(productToUpdate);
        entityManager.flush();
        entityManager.clear();

        Product foundProduct = productRepository.findById(productToUpdate.getId()).orElseThrow();
        foundProduct.update("updated", 35000, "update url");
        productRepository.save(foundProduct);
        entityManager.flush();
        entityManager.clear();

        Product verifiedProduct = productRepository.findById(foundProduct.getId()).orElse(null);
        assertThat(verifiedProduct).isNotNull();
        assertThat(verifiedProduct.getName()).isEqualTo("updated");
        assertThat(verifiedProduct.getPrice()).isEqualTo(35000);
        assertThat(verifiedProduct.getImageUrl()).isEqualTo("update url");
    }

    @Test
    void 상품_삭제() {
        Product productToDelete = Product.of("삭제할 상품", 40000, "delete url");
        entityManager.persist(productToDelete);
        entityManager.flush();

        Long productId = productToDelete.getId();

        productRepository.deleteById(productId);
        entityManager.flush();

        Optional<Product> foundAfterDelete = productRepository.findById(productId);
        assertThat(foundAfterDelete).isEmpty();
    }
}