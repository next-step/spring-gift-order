package gift.repository;

import gift.entity.Product;
import gift.entity.User;
import gift.entity.Wish;
import gift.entity.vo.Email;
import gift.entity.vo.Password;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class WishRepositoryTest {

    @Autowired
    WishRepository wishRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    void save() {
        Product product = new Product("test", 1000, "www.image.com");
        productRepository.save(product);
        User user = new User(new Email("test@email.com"), new Password("12345678"));
        userRepository.save(user);
        Wish expected = new Wish(user, product, 100);
        Wish actual = wishRepository.save(expected);
        assertAll(
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.getProduct()).isEqualTo(expected.getProduct())
        );
    }

    @Test
    void findByUserIdAndProductId() {
        Product product = new Product("test", 1000, "www.image.com");
        productRepository.save(product);
        User user = new User(new Email("test@email.com"), new Password("12345678"));
        userRepository.save(user);
        Wish expected = new Wish(user, product, 100);
        wishRepository.save(expected);
        Wish actual = wishRepository.findByUserIdAndProductId(user.getId(), product.getId()).get();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void existsByUserIdAndProductId() {
        Product product = new Product("test", 1000, "www.image.com");
        productRepository.save(product);
        User user = new User(new Email("test@email.com"), new Password("12345678"));
        userRepository.save(user);
        Wish wish = new Wish(user, product, 100);
        wishRepository.save(wish);
        assertTrue(wishRepository.existsByUserIdAndProductId(user.getId(), product.getId()));
    }
}