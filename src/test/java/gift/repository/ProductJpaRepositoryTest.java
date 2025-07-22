package gift.repository;

import static org.assertj.core.api.Assertions.assertThat;

import gift.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@DataJpaTest
class ProductJpaRepositoryTest {

    @Autowired
    private ProductJpaRepository repository;

    @Test
    @DisplayName("상품 저장")
    void save() {
        //when
        Product product = new Product(null, "product1", 1000, "url.com");
        //given
        Product savedProduct = repository.save(product);
        //then
        assertThat(savedProduct.getId()).isNotNull();
    }

    @Test
    @DisplayName("ID로 상품 조회")
    void findById() {
        //when
        Product product = new Product(null, "product1", 1000, "url.com");
        //given
        Product savedProduct = repository.save(product);
        //then
        assertThat(repository.findById(savedProduct.getId()).get()).isEqualTo(savedProduct);
    }

    @Test
    @DisplayName("상품 페이지네이션 조회")
    void findAll() {
        //when
        Product product1 = new Product(null, "product1", 1000, "url.com");
        Product product2 = new Product(null, "product2", 1000, "url.com");
        //given
        repository.save(product1);
        repository.save(product2);
        Pageable pageable = PageRequest.of(0, 2);
        //then
        assertThat(repository.findAll(pageable).getContent().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("상품 수정")
    void update() {
        //when
        Product product = new Product(null, "product1", 1000, "url.com");
        Product savedProduct = repository.save(product);
        //given
        savedProduct.update("updateName", 1000, "update.com");
        //then
        assertThat(savedProduct.getName()).isEqualTo("updateName");
    }

    @Test
    @DisplayName("상품 삭제")
    void delete() {
        //when
        Product product = new Product(null, "product1", 1000, "url.com");
        Product savedProduct = repository.save(product);
        //given
        repository.deleteById(savedProduct.getId());
        //then
        assertThat(repository.findAll()).size().isEqualTo(0);
    }
}