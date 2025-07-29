package gift.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import gift.entity.Member;
import gift.entity.Option;
import gift.entity.Order;
import gift.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class OrderRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OptionRepository optionRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void 주문_저장_성공() {
        // given
        Member member = Member.of("aran@email.com", "1234");
        memberRepository.save(member);

        Product product = Product.of("사과", "apple.com", 508L);
        productRepository.save(product);

        Option option = Option.of("빨강사과", 2, product);
        optionRepository.save(option);

        // when
        Order order = Order.of(member, product, option, 1, "");

        // then
        assertThat(order.getId()).isNull();

        var actual = orderRepository.save(order);

        assertThat(actual.getId()).isNotNull();
    }
}
