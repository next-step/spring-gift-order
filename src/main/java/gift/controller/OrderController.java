package gift.controller;

import gift.domain.Member;
import gift.domain.OrderRequest;
import gift.domain.OrderResponse;
import gift.resolver.LoginMember;
import gift.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<OrderResponse> create(@RequestBody OrderRequest request,
                                                @LoginMember Member member) {
        OrderResponse response = orderService.createOrder(member, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
