package gift.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import gift.dto.OptionRequest;
import gift.dto.ProductRequest;
import gift.dto.ProductResponse;
import gift.entity.Product;
import gift.repository.ProductRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ProductServiceIntegrationTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("상품과 옵션을 함께 저장한다")
    void addProductWithOptions() {
        // given
        List<OptionRequest> options = List.of(
                new OptionRequest("옵션1", 100),
                new OptionRequest("옵션2", 200)
        );
        ProductRequest request = new ProductRequest(null, "테스트 상품", 10000, "test.jpg", options);

        // when
        ProductResponse response = productService.addProduct(request);
        Product actual = productRepository.findById(response.getId()).get();

        // then
        assertThat(actual.getName()).isEqualTo("테스트 상품");
        assertThat(actual.getOptions()).hasSize(2);
        assertThat(actual.getOptions().get(0).getName()).isEqualTo("옵션1");
    }

    @Test
    @DisplayName("중복된 이름의 옵션을 포함하여 상품을 저장하면 예외가 발생한다")
    void addProductWithDuplicateOptionNames() {
        // given
        List<OptionRequest> options = List.of(
                new OptionRequest("중복 옵션", 100),
                new OptionRequest("중복 옵션", 200)
        );
        ProductRequest request = new ProductRequest(null, "테스트 상품", 10000, "test.jpg", options);

        // when & then
        assertThatThrownBy(() -> productService.addProduct(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("옵션 이름은 중복될 수 없습니다: 중복 옵션");
    }

    @Test
    @DisplayName("상품과 옵션을 함께 수정한다")
    void updateProductWithOptions() {
        // given
        // 1. 초기 상품과 옵션 저장
        ProductRequest initialRequest = new ProductRequest(null, "초기 상품", 10000, "initial.jpg",
                List.of(new OptionRequest("초기 옵션", 10))
        );
        ProductResponse initialResponse = productService.addProduct(initialRequest);
        Long productId = initialResponse.getId();

        // 2. 수정할 내용 정의
        List<OptionRequest> updatedOptions = List.of(
                new OptionRequest("수정된 옵션1", 100),
                new OptionRequest("수정된 옵션2", 200)
        );
        ProductRequest updateRequest = new ProductRequest(productId, "수정된 상품", 15000, "updated.jpg",
                updatedOptions);

        // when
        productService.updateProduct(productId, updateRequest);
        Product actual = productRepository.findById(productId).get();

        // then
        assertThat(actual.getName()).isEqualTo("수정된 상품");
        assertThat(actual.getPrice()).isEqualTo(15000);
        assertThat(actual.getOptions()).hasSize(2);
        assertThat(actual.getOptions().get(0).getName()).isEqualTo("수정된 옵션1");
        assertThat(actual.getOptions().get(0).getQuantity()).isEqualTo(100);
    }
}