package gift;

import gift.Entity.Member;
import gift.Entity.Option;
import gift.Entity.Product;
import gift.Entity.Wish;
import gift.repository.MemberRepository;
import gift.repository.OptionRepository;
import gift.repository.ProductRepository;
import gift.repository.WishRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DataJpaTest
public class WishRepositoryTest {

    @Autowired
    private WishRepository wishRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OptionRepository optionRepository;

    private Member createMember(String nickname) {
        return memberRepository.save(new Member(
                nickname,
                "test@kakao.com",
                "123456789",
                "테스터",
                "주소",
                "USER"
        ));
    }

    private Product createProduct(String name, int price) {
        return productRepository.save(new Product(null, name, price, "https://img.com/"));
    }

    private Option createOption(String name, int quantity, Product product) {
        return optionRepository.save(new Option(name, quantity, product));
    }

    @Test
    void testDeleteWish() {
        Member member = createMember("deleteUser");
        Product product = createProduct("라떼", 3000);
        Option option = createOption("HOT", 5, product);
        Wish wish = wishRepository.save(new Wish(member, product, option));

        wishRepository.delete(wish);

        List<Wish> result = wishRepository.findAll();
        assertThat(result).isEmpty();
    }

    @Test
    void testSaveWishWithOption() {
        Member member = createMember("testId");
        Product product = createProduct("아메리카노", 2000);
        Option option = createOption("ICE", 10, product);
        Wish wish = new Wish(member, product, option);

        wishRepository.save(wish);

        List<Wish> result = wishRepository.findAll();
        assertThat(result).hasSize(1);
        Wish saved = result.get(0);
        assertThat(saved.getMember().getNickname()).isEqualTo("testId");
        assertThat(saved.getProduct().getName()).isEqualTo("아메리카노");
        assertThat(saved.getOption().getName()).isEqualTo("ICE");
        assertThat(saved.getOption().getQuantity()).isEqualTo(10);
    }

}
