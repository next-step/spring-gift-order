package gift.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.loader.internal.AliasConstantsHelper.get;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import gift.dto.OrderRequest;
import gift.entity.Member;
import gift.entity.Option;
import gift.entity.Order;
import gift.entity.Product;
import gift.entity.Wish;
import gift.repository.MemberRepository;
import gift.repository.OptionRepository;
import gift.repository.OrderRepository;
import gift.repository.ProductRepository;
import gift.repository.WishRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OptionRepository optionRepository;

    @Autowired
    private WishRepository wishRepository;

    @Autowired
    private OrderRepository orderRepository;

    @MockBean
    private KakaoMessageService kakaoMessageService;

    @Test
    void 주문_성공() {
        Member member = memberRepository.save(Member.of("aran@email.com", "1234"));
        Product product = productRepository.save(Product.of("기픈물", "deepwater.com", 4000L));
        Option option = optionRepository.save(Option.of("500mL", 30, product));
        Wish wish = wishRepository.save(Wish.of(member, product));

        OrderRequest request = new OrderRequest(product.getId(), option.getId(), 2, "추카해");

        // when
        orderService.createOrder(request, member);

        // then
        Option updated = optionRepository.findById(option.getId()).get();
        assertThat(updated.getQuantity()).isEqualTo(28);

        Page<Wish> remain = wishRepository.findAllByMember(member, Pageable.ofSize(10));
        assertThat(remain.getContent()).isEmpty();

        Order savedOrder = orderRepository.findAll().get(0);
        verify(kakaoMessageService).sendMessage(any(), any());
    }
}

