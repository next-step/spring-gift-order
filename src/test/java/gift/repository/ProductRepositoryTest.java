package gift.repository;

import static org.assertj.core.api.Assertions.assertThat;

import gift.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("상품을 저장하고 ID로 조회한다")
    void saveAndFindById() {
        // given
        Product product = new Product("테스트 상품", 10000, "test.jpg");

        // when
        Product savedProduct = productRepository.save(product);
        Product foundProduct = productRepository.findById(savedProduct.getId())
                .orElseThrow(() -> new AssertionError("저장된 상품을 찾을 수 없습니다."));

        // then
        assertThat(foundProduct.getId()).isNotNull(); // ID가 생성되었는지 확인
        assertThat(foundProduct.getName()).isEqualTo(product.getName());
    }
}