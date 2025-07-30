package gift.api.order.controller;

import gift.api.order.dto.OrderRequestDto;
import gift.api.order.dto.OrderResponseDto;
import gift.api.order.service.OrderService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(
            @RequestAttribute("userEmail") String email,
            @Valid @RequestBody OrderRequestDto orderRequestDto
    ) {
        OrderResponseDto orderResponseDto = orderService.createOrder(email, orderRequestDto);

        URI locaotion = URI.create("/api/orders" + orderResponseDto.id());

        return ResponseEntity.created(locaotion).body(orderResponseDto);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getAllOrders(
            @RequestAttribute("userEmail") String email,
            @PageableDefault(size = 5, sort = "orderDateTime", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        List<OrderResponseDto> orders = orderService.getOrders(email, pageable)
                .getContent();

        return ResponseEntity.ok(orders);
    }
}
