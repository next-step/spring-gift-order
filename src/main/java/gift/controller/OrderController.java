package gift.controller;

import gift.domain.Member;
import gift.dto.OrderRequest;
import gift.dto.OrderResponse;
import gift.resolver.LoginMember;
import gift.service.OrderService;
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
    public ResponseEntity<OrderResponse> createOrder(
            @LoginMember Member member,
            @RequestBody OrderRequest request
    ) {
        OrderResponse response = orderService.createOrder(member, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

