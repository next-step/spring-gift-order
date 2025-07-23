package gift.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import gift.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository products;

    @Test
    @DisplayName("상품 저장 테스트")
    void save() {
        var product = Product.of("감자2", "gamja.com", 1800L);

        // 저장 전 id가 null인지 검증 ( == 객체가 없다)
        assertThat(product.getId()).isNull();

        // 저장 후 잘 저장됐는지 검증(id 존재, 이름 일치 확인)
        Product actual = products.save(product);
        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getName()).isEqualTo("감자2");
    }
}
