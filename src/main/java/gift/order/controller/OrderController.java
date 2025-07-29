package gift.order.controller;

import gift.common.security.AuthenticatedMember;
import gift.common.security.LoginMember;
import gift.order.dto.OrderRequestDto;
import gift.order.dto.OrderResponseDto;
import gift.order.service.OrderService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
        @RequestBody @Valid OrderRequestDto orderRequestDto,
        @LoginMember AuthenticatedMember member
    ) {
        OrderResponseDto orderResponseDto = orderService.create(member.id(), orderRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderResponseDto);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getMyOrders(
        @LoginMember AuthenticatedMember member
    ) {
        List<OrderResponseDto> orderResponseDtos = orderService.getAll(member.id());
        return ResponseEntity.ok(orderResponseDtos);
    }

}
