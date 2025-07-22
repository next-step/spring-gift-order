package gift.repository;
import gift.entity.Product;
import gift.fixture.ProductFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ProductRepositoryTest {
    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("상품을 저장한다")
    void save_test() {
        Product test = ProductFixture.createProduct2();
        Product actual=productRepository.save(test);

        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getName()).isEqualTo(test.getName());
    }

    @Test
    @DisplayName("전체 상품 목록을 조회할 수 있다")
    void findAll_test() {
        productRepository.save(new Product(null,"사탕", 500, "http://img.com/3"));
        productRepository.save(new Product(null,"껌", 300, "http://img.com/4"));

        List<Product> products = productRepository.findAll();
        assertThat(products).hasSize(2);
    }

    @Test
    @DisplayName("ID값으로 상품을 찾는다")
    void findById_test() {
        Product test = productRepository.save(ProductFixture.createProduct2());
        Long id = test.getId();


        Product found = productRepository.findById(id)
                .orElseThrow(() -> new AssertionError("상품이 존재하지 않습니다: id = " + id));


        assertThat(found.getId()).isEqualTo(id);
        assertThat(found.getName()).isEqualTo(test.getName());
    }

    @Test
    @DisplayName("상품을 삭제할 수 있다")
    void deleteById_test() {

        Product saved = productRepository.save(ProductFixture.createProduct2());
        Long id = saved.getId();


        productRepository.deleteById(id);


        boolean exists = productRepository.existsById(id);
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("변경 감지를 사용하여 업데이트 한다")
    void update() {
        Product saved = productRepository.save(ProductFixture.createProduct2());
        saved.setName("변경");
        Product station2 = productRepository.findByName("변경").get();
        assertThat(station2).isNotNull();

    }




}
