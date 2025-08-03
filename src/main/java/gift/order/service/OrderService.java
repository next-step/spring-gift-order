package gift.order.service;

import gift.order.dto.OrderRequestDto;
import gift.order.dto.OrderResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderResponseDto createOrder(Long memberId, OrderRequestDto orderRequestDto, String accessToken);
    Page<OrderResponseDto> listOrders(Long memberId, Pageable pageable);
}
