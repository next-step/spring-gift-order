package gift.controller;

import gift.dto.order.OrderRequestDto;
import gift.dto.order.OrderResponseDto;
import gift.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(
            @RequestAttribute("userEmail") String userEmail,
            @Valid @RequestBody OrderRequestDto requestDto) {
        OrderResponseDto response = orderService.createOrder(userEmail, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}