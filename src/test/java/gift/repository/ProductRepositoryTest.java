package gift.repository;

import gift.common.exception.ProductOptionException;
import gift.domain.product.Product;
import gift.domain.product.ProductOption;
import gift.dto.product.CreateProductOptionRequest;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static gift.repository.ProductRepositoryTest.ProductRepositoryTestFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
public class ProductRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductOptionRepository optionRepository;

    @Test
    @DisplayName("상품 저장")
    void test1_1() {
        Product product = new Product("감자칩", "image", OPTION);
        Product save = productRepository.save(product);

        assertThat(save.getId()).isNotNull();
        assertThat(save.getName()).isEqualTo("감자칩");
        assertThat(save.getImageUrl()).isEqualTo("image");
    }

    @Test
    @DisplayName("상품 저장 시 반드시 1개의 옵션을 포함")
    void test1_2() {
        Product product = new Product("감자칩", "image", OPTION);
        Product save = productRepository.save(product);

        em.flush();
        em.clear();

        Product getProduct = productRepository.findById(save.getId()).get();

        List<ProductOption> options = getProduct.getOptions();

        assertThat(options.get(0).getId()).isNotNull();
        assertThat(options.get(0).getProduct()).isNotNull();
        assertThat(options.get(0).getName()).isEqualTo("양파맛");
        assertThat(options.get(0).getPrice()).isEqualTo(1000);
        assertThat(options.get(0).getQuantity()).isEqualTo(10);
    }

    @Test
    @DisplayName("상품 수정")
    void test2() {
        Product product = new Product("감자칩", "image", OPTION);
        Product save = productRepository.save(product);

        em.flush();

        Product getProduct = productRepository.findById(save.getId()).get();
        getProduct.update("고구마칩", "image2");

        em.flush();
        em.clear();

        Product expected = productRepository.findById(save.getId()).get();

        assertThat(expected.getId()).isNotNull();
        assertThat(expected.getName()).isEqualTo("고구마칩");
        assertThat(expected.getImageUrl()).isEqualTo("image2");
    }

    @Test
    @DisplayName("상품 조회")
    void test3() {
        Product product = new Product("감자칩", "image", OPTION);
        productRepository.save(product);

        em.flush();
        em.clear();

        Product getProduct = productRepository.findById(product.getId()).get();
        assertThat(getProduct.getId()).isNotNull();
        assertThat(getProduct.getName()).isEqualTo("감자칩");
        assertThat(getProduct.getImageUrl()).isEqualTo("image");
    }

    @Test
    @DisplayName("상품 삭제")
    void test4_1() {
        Product product = new Product("감자칩", "image", OPTION);
        productRepository.save(product);

        em.flush();
        em.clear();

        productRepository.delete(product);

        em.flush();

        Optional<Product> getProduct = productRepository.findById(product.getId());
        assertThat(getProduct).isEmpty();
    }

    @Test
    @DisplayName("상품 삭제시 옵션도 같이 삭제")
    void test4_2() {
        Product product = new Product("감자칩", "image", OPTION);
        productRepository.save(product);
        Long optionId = product.getOptions().get(0).getId();

        em.flush();
        em.clear();

        productRepository.delete(product);

        em.flush();

        Optional<ProductOption> byId = optionRepository.findById(optionId);
        assertThat(byId).isEmpty();
    }

    @Test
    @DisplayName("상품에서 관리하는 옵션 리스트의 데이터를 제거할 경우 옵션은 삭제")
    void test4_3() {
        Product product = new Product("감자칩", "image", List.of(new CreateProductOptionRequest("양파맛", 1000, 10), new CreateProductOptionRequest("감자맛", 2000, 10)));
        Product save = productRepository.save(product);

        save.removeOption(save.getOptions().get(0));

        em.flush();
        em.clear();

        Product getProduct = productRepository.findById(save.getId()).get();

        assertThat(getProduct.getOptions().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("상품에 옵션 하나는 필수로 존재해야 하므로 옵션이 1개밖에 없다면 그 옵션은 삭제 불가")
    void test4_4() {
        Product product = new Product("감자칩", "image", OPTION);
        Product save = productRepository.save(product);

        assertThatThrownBy(() -> save.removeOption(save.getOptions().get(0))).isInstanceOf(ProductOptionException.class);
    }



    static class ProductRepositoryTestFixture {
        public static final List<CreateProductOptionRequest> OPTION = List.of(new CreateProductOptionRequest("양파맛", 1000, 10));
    }

}
