package gift.service;

import gift.common.exception.ProductNotFoundException;
import gift.domain.product.Product;
import gift.dto.product.CreateProductOptionRequest;
import gift.dto.product.CreateProductRequest;
import gift.dto.product.ProductResponse;
import gift.dto.product.UpdateProductRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class ProductServiceTest {

    @Autowired
    ProductService productService;

    Product product;

    @BeforeEach
    void before() {
        CreateProductRequest createProductRequest = new CreateProductRequest("칫솔", "image", List.of(new CreateProductOptionRequest("튼튼한 샤프", 10000, 10)));
        product = productService.saveProduct(createProductRequest);
    }

    @Test
    @DisplayName("사용자는 상품을 저장할 수 있다.")
    void test1() {
        CreateProductRequest createProductRequest = new CreateProductRequest("칫솔", "image", List.of(new CreateProductOptionRequest("튼튼한 샤프", 10000, 10)));

        Product product = productService.saveProduct(createProductRequest);

        assertThat(product).isNotNull();
        assertThat(product.getId()).isNotNull();
        assertThat(product.getName()).isEqualTo("칫솔");
        assertThat(product.getImageUrl()).isEqualTo("image");

    }

    @Test
    @DisplayName("사용자는 상품을 수정할 수 있다.")
    void test2() {
        UpdateProductRequest updateProductRequest = new UpdateProductRequest("칫솔2", "image2");
        Product update = productService.updateProduct(product.getId(), updateProductRequest);

        assertThat(update.getId()).isEqualTo(product.getId());
        assertThat(update.getName()).isEqualTo("칫솔2");
        assertThat(update.getImageUrl()).isEqualTo("image2");
    }

    @Test
    @DisplayName("사용자는 상품 목록을 조회할 수 있다.")
    void test3() {
        CreateProductRequest createProductRequest = new CreateProductRequest("칫솔", "image", List.of(new CreateProductOptionRequest("튼튼한 샤프", 10000, 10)));
        productService.saveProduct(createProductRequest);

        //beforeEach에서 생성한 것 까지 총 2건의 데이터 있음
        List<ProductResponse> products = productService.getAllProducts(null, 10);
        assertThat(products).isNotEmpty();
        assertThat(products.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("사용자는 상품 단건을 조회할 수 있다.")
    void test4() {
        Product getProduct = productService.getProduct(this.product.getId());

        assertThat(getProduct.getId()).isEqualTo(product.getId());
        assertThat(getProduct.getName()).isEqualTo("칫솔");
        assertThat(getProduct.getImageUrl()).isEqualTo("image");
    }

    @Test
    @DisplayName("사용자는 상품을 삭제할 수 있다.")
    void test5() {
        productService.deleteProduct(product.getId());

        assertThatThrownBy(() -> productService.getProduct(product.getId())).isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    @DisplayName("getProducts() 메서드 페이지네이션 테스트 1 - 커서를 기준으로 다음 데이터를 불러올 수 있다.")
    void test6_1() {
        Product product1 = productService.saveProduct(new CreateProductRequest("칫솔1", "image", List.of(new CreateProductOptionRequest("튼튼한 샤프", 10000, 10))));
        productService.saveProduct(new CreateProductRequest("칫솔2", "image", List.of(new CreateProductOptionRequest("튼튼한 샤프", 10000, 10))));
        productService.saveProduct(new CreateProductRequest("칫솔3", "image", List.of(new CreateProductOptionRequest("튼튼한 샤프", 10000, 10))));
        productService.saveProduct(new CreateProductRequest("칫솔4", "image", List.of(new CreateProductOptionRequest("튼튼한 샤프", 10000, 10))));
        productService.saveProduct(new CreateProductRequest("칫솔5", "image", List.of(new CreateProductOptionRequest("튼튼한 샤프", 10000, 10))));
        productService.saveProduct(new CreateProductRequest("칫솔6", "image", List.of(new CreateProductOptionRequest("튼튼한 샤프", 10000, 10))));
        productService.saveProduct(new CreateProductRequest("칫솔7", "image", List.of(new CreateProductOptionRequest("튼튼한 샤프", 10000, 10))));
        productService.saveProduct(new CreateProductRequest("칫솔8", "image", List.of(new CreateProductOptionRequest("튼튼한 샤프", 10000, 10))));
        productService.saveProduct(new CreateProductRequest("칫솔9", "image", List.of(new CreateProductOptionRequest("튼튼한 샤프", 10000, 10))));
        productService.saveProduct(new CreateProductRequest("칫솔10", "image", List.of(new CreateProductOptionRequest("튼튼한 샤프", 10000, 10))));

        List<ProductResponse> products = productService.getAllProducts(product1.getId(), 10);

        assertThat(products.size()).isEqualTo(1);
        assertThat(products.get(0).name()).isEqualTo("칫솔");
    }
}