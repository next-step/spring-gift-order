package gift.api.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import gift.api.member.domain.Member;
import gift.api.member.domain.MemberRole;
import gift.api.member.repository.MemberRepository;
import gift.api.option.domain.Option;
import gift.api.option.repository.OptionRepository;
import gift.api.order.domain.Order;
import gift.api.order.dto.OrderRequestDto;
import gift.api.order.dto.OrderResponseDto;
import gift.api.order.event.OrderCompletedEvent;
import gift.api.order.repository.OrderRepository;
import gift.api.product.domain.Product;
import gift.exception.notfound.MemberNotFoundException;
import gift.exception.option.InvalidOptionQuantityException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private OptionRepository optionRepository;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    private Member member;
    private Product product;
    private Option option;

    @BeforeEach
    void setUp() {
        member = new Member("user@test.com", "password", MemberRole.USER);
        ReflectionTestUtils.setField(member, "id", 1L);

        product = new Product("테스트 상품", 10000L, "image.jpg");
        ReflectionTestUtils.setField(product, "id", 101L);

        option = new Option("테스트 옵션", 10, product);
        ReflectionTestUtils.setField(option, "id", 1001L);
    }

    @Test
    @DisplayName("주문 생성 성공")
    void createOrder_success() {
        // given
        OrderRequestDto requestDto = new OrderRequestDto(1001L, 5, "감사합니다.");
        Order savedOrder = new Order(member, option, requestDto.quantity(), requestDto.message());
        ReflectionTestUtils.setField(savedOrder, "id", 1L);

        given(memberRepository.findByEmail("user@test.com")).willReturn(Optional.of(member));
        given(optionRepository.getOptionById(1001L)).willReturn(Optional.of(option));
        given(orderRepository.save(any(Order.class))).willReturn(savedOrder);

        // when
        OrderResponseDto response = orderService.createOrder("user@test.com", requestDto);

        // then
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.optionId()).isEqualTo(1001L);
        assertThat(response.quantity()).isEqualTo(5);

        // 재고가 정상적으로 차감되었는지 확인
        assertThat(option.getQuantity()).isEqualTo(5);

        // 이벤트가 정상적으로 발행되었는지 확인
        ArgumentCaptor<OrderCompletedEvent> eventCaptor = ArgumentCaptor.forClass(
                OrderCompletedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        Order completedOrder = eventCaptor.getValue().getOrder();
        assertThat(completedOrder.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("주문 생성 실패 - 재고 부족")
    void createOrder_fail_insufficientStock() {
        // given
        OrderRequestDto requestDto = new OrderRequestDto(1001L, 15, "감사합니다."); // 재고(10)보다 많은 수량

        given(memberRepository.findByEmail("user@test.com")).willReturn(Optional.of(member));
        given(optionRepository.getOptionById(1001L)).willReturn(Optional.of(option));

        // when & then
        assertThatThrownBy(() -> orderService.createOrder("user@test.com", requestDto))
                .isInstanceOf(InvalidOptionQuantityException.class)
                .hasMessage("재고가 부족합니다.");
    }

    @Test
    @DisplayName("주문 생성 실패 - 존재하지 않는 회원")
    void createOrder_fail_memberNotFound() {
        // given
        OrderRequestDto requestDto = new OrderRequestDto(1001L, 5, "감사합니다.");
        given(memberRepository.findByEmail("no@test.com")).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> orderService.createOrder("no@test.com", requestDto))
                .isInstanceOf(MemberNotFoundException.class);
    }
}