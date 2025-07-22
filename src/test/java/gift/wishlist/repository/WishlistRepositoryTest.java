package gift.wishlist.repository;

import gift.product.entity.Product;
import gift.product.repository.ProductRepository;
import gift.user.entity.User;
import gift.user.repository.UserRepository;
import gift.wishlist.entity.Wishlist;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class WishlistRepositoryTest {

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void save(){
        Long giftId = 1L;
        String giftName = "test";
        Integer giftPrice = 10000;
        String giftPhotoUrl = "test";
        Product product = productRepository.save(new Product(giftId,giftName,giftPrice,giftPhotoUrl));

        String email = "test@gmail.com";
        String password = "test";
        User user = userRepository.save(new User(email,password));

        Wishlist wishlist = wishlistRepository.save(new Wishlist(user, product));
        assertEquals(product,wishlist.getProduct());
        assertEquals(user, wishlist.getUser());
    }

    @Test
    void findAllByUserId(){
        Product product = productRepository.save(new Product(1L, "test", 10000,"test"));
        Product product2 = productRepository.save(new Product(2L, "test2", 30000,"testtest"));

        String email = "test@gmail.com";
        String password = "test";
        User user = userRepository.save(new User(email,password));

        Pageable pageable = PageRequest.of(0, 1);

        wishlistRepository.save(new Wishlist(user, product));
        wishlistRepository.save(new Wishlist(user, product2));
        Page<Wishlist> wishlist = wishlistRepository.findAllByUserId(user.getId(), pageable);

        assertNotNull(wishlist);
        assertEquals(2, wishlist.getTotalElements());
    }

    @Test
    void deleteById(){
        Product product = productRepository.save(new Product(1L, "test", 10000,"test"));
        Product product2 = productRepository.save(new Product(2L, "test2", 30000,"testtest"));

        String email = "test@gmail.com";
        String password = "test";
        User user = userRepository.save(new User(email,password));

        Pageable pageable = PageRequest.of(0, 1);

        wishlistRepository.save(new Wishlist(user, product));
        wishlistRepository.save(new Wishlist(user, product2));
        wishlistRepository.deleteById(1L);
        Page<Wishlist> wishlist = wishlistRepository.findAllByUserId(user.getId(), pageable);
        assertNotNull(wishlist);
        assertEquals(1, wishlist.getTotalElements());
    }
}