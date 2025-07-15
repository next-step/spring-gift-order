package gift.repository;

import gift.product.Product;
import gift.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void 상품추가_그리고_조회(){
        Product product = new Product("과자", 200L, "snack.png");
        productRepository.save(product);

        Optional<Product> foundProduct = productRepository.findById(product.getId());

        assertThat(foundProduct).isPresent();
        assertThat(foundProduct.get().getName()).isEqualTo("과자");
    }

    @Test
    public void 상품_삭제(){
        Product product = new Product("과자", 200L, "snack.png");
        productRepository.save(product);

        Product foundProduct = productRepository.findById(product.getId()).get();
        productRepository.deleteById(foundProduct.getId());
        Optional<Product> deletedProduct = productRepository.findById(product.getId());

        assertThat(deletedProduct).isNotPresent();
    }

    @Test
    public void 상품_페이지네이션_조회() {
        for (int i = 1; i <= 30; i++) {
            productRepository.save(new Product("상품 " + i, (long) i * 100, "url" + i));
        }

        Pageable pageable = PageRequest.of(0, 10);

        Page<Product> page = productRepository.findAll(pageable);

        assertThat(page.getContent().size()).isEqualTo(10);
        assertThat(page.getTotalElements()).isEqualTo(34);
        assertThat(page.getTotalPages()).isEqualTo(4);
        assertThat(page.getNumber()).isEqualTo(0);
    }

}
