package gift.controller;

import gift.Entity.Member;
import gift.annotation.LoginMember;
import gift.request.OrderRequest;
import gift.response.OrderResponse;
import gift.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderRestController {

    private final OrderService orderService;

    public OrderRestController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(@LoginMember Member member,
                                                    @RequestBody OrderRequest request) {
        if (member == null) {
            return ResponseEntity.status(401).build();
        }

        OrderResponse response = orderService.placeOrder(member, request, null);
        return ResponseEntity.status(201).body(response);
    }
}

