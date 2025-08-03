package gift.order.service;

import gift.option.entity.Option;
import gift.option.exception.OptionNotFoundException;
import gift.option.repository.OptionRepository;
import gift.order.dto.OrderRequestDto;
import gift.order.dto.OrderResponseDto;
import gift.order.entity.Order;
import gift.order.event.OrderCreatedEvent;
import gift.order.repository.OrderRepository;
import gift.wish.repository.WishRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceImplTest {

    @Mock
    private OptionRepository optionRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private WishRepository wishRepository;
    @Mock
    private ApplicationEventPublisher publisher;

    @InjectMocks
    private OrderServiceImpl service;

    @Captor
    private ArgumentCaptor<OrderCreatedEvent> eventCaptor;
    @Captor
    private ArgumentCaptor<Order> orderCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("정상 주문 시 재고 차감, 저장, 위시 삭제, 이벤트 발행, DTO가 반환된다. ")
    void createOrder_success() {
        // given
        Long memberId = 1L;
        Long optionId = 2L;
        String accessToken = "Bearer test-access-token";
        OrderRequestDto dto = new OrderRequestDto(optionId, 3, "테스트 주문 완료!");

        Option opt = new Option(null, "낱개", 10);
        when(optionRepository.findById(optionId)).thenReturn(Optional.of(opt));

        // when
        OrderResponseDto resp = service.createOrder(memberId, dto, accessToken);

        // then
        assertEquals(7, opt.getQuantity());

        verify(orderRepository).save(orderCaptor.capture());
        Order saved = orderCaptor.getValue();
        assertEquals(3, saved.getQuantity());
        assertEquals("테스트 주문 완료!", saved.getMessage());
        assertSame(opt, saved.getOption());

        verify(wishRepository).deleteByOption(opt);

        // and: 이벤트 발행 검증
        verify(publisher).publishEvent(eventCaptor.capture());
        OrderCreatedEvent evt = eventCaptor.getValue();
        assertEquals(memberId, evt.memberId());
        assertSame(saved, evt.order());

        assertEquals(saved.getQuantity(), resp.quantity());
        assertEquals(saved.getMessage(), resp.message());
    }

    @Test
    @DisplayName("createOrder: 옵션이 없을 경우, OptionNotFoundException 발생 및 이벤트 호출하지 않는다. ")
    void createOrder_optionNotFound() {
        // given
        when(optionRepository.findById(anyLong())).thenReturn(Optional.empty());
        OrderRequestDto dto = new OrderRequestDto(99L, 1, "테스트 주문 완료!");
        String accessToken = "Bearer test-access-token";

        // when & then
        assertThrows(OptionNotFoundException.class,
                () -> service.createOrder(1L, dto, accessToken));

        verify(orderRepository, never()).save(any());
        verify(publisher, never()).publishEvent(any());
    }
}
