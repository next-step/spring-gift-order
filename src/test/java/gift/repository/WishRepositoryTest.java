package gift.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import gift.entity.Member;
import gift.entity.Product;
import gift.entity.Wish;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class WishRepositoryTest {

    @Autowired
    private ProductRepository products;

    @Autowired
    private MemberRepository members;

    @Autowired
    private WishRepository wishes;

    @Test
    @DisplayName("위시 저장 테스트")
    void save() {
        // given
        Product product = Product.of("고구마", "goguma.com", 100L);
        products.save(product);

        Member member = Member.of("aran@email.com", "1234");
        members.save(member);

        // when
        Wish wish = Wish.of(member, product);

        // then
        assertThat(wish.getId()).isNull();

        Wish actual = wishes.save(wish);
        assertThat(actual.getId()).isNotNull();
    }
}
