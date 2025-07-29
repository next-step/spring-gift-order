package gift.repository;

import gift.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
public class WishRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WishRepository wishRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OptionRepository optionRepository;
    @Autowired
    private ProductOptionRepository productOptionRepository;

    @Test
    void save() {
        User expectUser = new User("kakao@kakao.com", "1234");
        Product expectProduct = productRepository.save(new Product("과자", 1000L, "http://snack"));
        Option expectOption = optionRepository.save(new Option("할인율"));
        ProductOption expectProductOption = productOptionRepository.save(new ProductOption(expectProduct, expectOption, 30L));
        Wish expected = new Wish(expectUser, expectProductOption, 30L);

        Wish actual = wishRepository.save(expected);
        assertAll(
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.getUser()).isEqualTo(expected.getUser()),
                () -> assertThat(actual.getProductOption()).isEqualTo(expected.getProductOption()),
                () -> assertThat(actual.getQuantity()).isEqualTo(expected.getQuantity())
        );
    }

    @Test
    void findById() {
        User expectUser = new User("kakao@kakao.com", "1234");
        Product expectProduct = productRepository.save(new Product("과자", 1000L, "http://snack"));
        Option expectOption = optionRepository.save(new Option("할인율"));
        ProductOption expectProductOption = productOptionRepository.save(new ProductOption(expectProduct, expectOption, 30L));
        Wish expected = wishRepository.save(new Wish(expectUser, expectProductOption, 30L));
        
        Wish actual = wishRepository.findById(expected.getId()).orElse(null);
        assertThat(expected).isEqualTo(actual);
    }

    @Test
    void existsByProductOptionId() {
        User expectUser = userRepository.save(new User("kakao@kakao.com", "1234"));
        Product expectProduct = productRepository.save(new Product("과자", 1000L, "http://snack"));
        Option expectOption = optionRepository.save(new Option("할인율"));
        ProductOption expectProductOption = productOptionRepository.save(new ProductOption(expectProduct, expectOption, 30L));
        Wish expected = wishRepository.save(new Wish(expectUser, expectProductOption, 30L));
        
        boolean flag = wishRepository.existsByProductOptionId(expected.getProductOption().getId());
        assertThat(flag).isTrue();
    }

    @Test
    void sortByUserIdAsc() {
        User expectUser1 = userRepository.save(new User("kakao@kakao.com", "1234"));
        User expectUser2 = userRepository.save(new User("kakao2@kakao.com", "1234"));
        User expectUser3 = userRepository.save(new User("kakao3@kakao.com", "1234"));
        Product expectProduct = productRepository.save(new Product("과자", 1000L, "http://snack"));
        Option expectOption1 = optionRepository.save(new Option("할인율"));
        Option expectOption2 = optionRepository.save(new Option("땅콩맛"));
        Option expectOption3 = optionRepository.save(new Option("우유맛"));
        ProductOption expectProductOption1 = productOptionRepository.save(new ProductOption(expectProduct, expectOption1, 30L));
        ProductOption expectProductOption2 = productOptionRepository.save(new ProductOption(expectProduct, expectOption2, 30L));
        ProductOption expectProductOption3 = productOptionRepository.save(new ProductOption(expectProduct, expectOption3, 30L));
        
        wishRepository.save(new Wish(expectUser3, expectProductOption1, 3L));
        wishRepository.save(new Wish(expectUser1, expectProductOption2, 3L));
        wishRepository.save(new Wish(expectUser2, expectProductOption3, 3L));
        Page<Wish> page = wishRepository.findAll(
                PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "userId"))
        );

        List<Wish> wishes = page.getContent();
        assertThat(wishes.stream().map(Wish::getUser).toList())
                .containsExactly(expectUser1, expectUser2 , expectUser3);
    }
}
