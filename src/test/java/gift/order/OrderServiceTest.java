package gift.order;

import gift.member.entity.Member;
import gift.member.entity.Role;
import gift.order.dto.OrderRequest;
import gift.order.dto.OrderResponse;
import gift.order.entity.Order;
import gift.order.repository.OrderRepository;
import gift.order.service.OrderServiceImpl;
import gift.product.entity.Option;
import gift.product.entity.Product;
import gift.product.repository.OptionRepository;
import gift.wish.entity.Wish;
import gift.wish.repository.WishRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @InjectMocks
    private OrderServiceImpl orderService;
    @Mock
    private OptionRepository optionRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private WishRepository wishRepository;

    private Member member;
    private Product product;
    private Option option;
    private Wish wish;

    @BeforeEach
    void setUp() {
        member = new Member(1L, "test@kakao.com", "password", Role.USER);
        product = new Product(1L, "상품1", 10000, "https://test.jpg");
        option = new Option(1L, "테스트 옵션", 100);
        option.setProduct(product);
        wish = new Wish(1L, member, option, 1);
    }

    @Test
    @DisplayName("주문 성공 테스트")
    void doOrder_success() {
        var orderRequest = new OrderRequest(option.getId(), 10, "배송 전 연락바랍니다.");
        var order = new Order(1L, option.getId(), orderRequest.quantity(), LocalDateTime.now(), orderRequest.message());

        given(optionRepository.findById(option.getId())).willReturn(Optional.of(option));
        given(orderRepository.save(any(Order.class))).willReturn(order);
        given(wishRepository.findByMemberIdAndOptionId(member.getId(), option.getId())).willReturn(Optional.of(wish));

        OrderResponse orderResponse = orderService.doOrder(member, orderRequest);

        assertThat(orderResponse.quantity()).isEqualTo(orderRequest.quantity());
        assertThat(option.getQuantity()).isEqualTo(90);

        verify(wishRepository, Mockito.times(1)).findByMemberIdAndOptionId(member.getId(), option.getId());
        verify(wishRepository, Mockito.times(1)).deleteById(wish.getId());
    }

    @Test
    @DisplayName("주문 실패 - 존재하지 않는 옵션")
    void doOrder_fail1() {
        var orderRequest = new OrderRequest(100L, 10, "존재하지 않는 옵션");
        given(optionRepository.findById(100L)).willReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> orderService.doOrder(member, orderRequest));

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("주문 실패 - 재고 부족")
    void doOrder_fail2() {
        var orderRequest = new OrderRequest(option.getId(), 101, "재고보다 많이 주문");
        given(optionRepository.findById(option.getId())).willReturn(Optional.of(option));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.doOrder(member, orderRequest);
        });
        assertThat(exception.getMessage()).isEqualTo("재고가 부족합니다.");

        verify(orderRepository, never()).save(any(Order.class));
    }
}