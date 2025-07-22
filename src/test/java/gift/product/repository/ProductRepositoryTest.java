package gift.product.repository;

import gift.product.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ProductRepositoryTest {
    @Autowired
    private ProductRepository productRepository;

    @Test
    void save(){
        Long giftId = 1L;
        String giftName = "test";
        Integer giftPrice = 10000;
        String giftPhotoUrl = "test";
        Product product = productRepository.save(new Product(giftId,giftName,giftPrice,giftPhotoUrl));
        assertEquals(giftId,product.getGiftId());
        assertEquals(giftName, product.getGiftName());
        assertEquals(giftPrice, product.getGiftPrice());
        assertEquals(giftPhotoUrl, product.getGiftPhotoUrl());
    }

    @Test
    void findById(){
        Long giftId = 1L;
        String giftName = "test";
        Integer giftPrice = 10000;
        String giftPhotoUrl = "test";
        productRepository.save(new Product(giftId,giftName,giftPrice,giftPhotoUrl));
        Product product = productRepository.findById(giftId).orElse(null);
        assertNotNull(product);
        assertEquals(giftId,product.getGiftId());
        assertEquals(giftName,product.getGiftName());
        assertEquals(giftPrice, product.getGiftPrice());
        assertEquals(giftPhotoUrl, product.getGiftPhotoUrl());
    }

    @Test
    void findAll(){
        productRepository.save(new Product(0L,"test",1000, "test"));
        productRepository.save(new Product(1L,"test2",2000, "test2"));
        List<Product> products = productRepository.findAll();
        assertNotNull(products);
        assertEquals(2,products.size());
    }

    @Test
    void deleteById(){
        Long giftId = 1L;
        String giftName = "test";
        Integer giftPrice = 10000;
        String giftPhotoUrl = "test";
        productRepository.save(new Product(giftId,giftName,giftPrice,giftPhotoUrl));
        productRepository.deleteById(giftId);
        List<Product> products = productRepository.findAll();
        assertEquals(0,products.size());
    }
}