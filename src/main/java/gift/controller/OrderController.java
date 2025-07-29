package gift.controller;

import gift.auth.LoginMember;
import gift.domain.Member;
import gift.domain.Order;
import gift.dto.OrderRequest;
import gift.dto.OrderResponse;
import gift.service.OrderService;
import gift.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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
    public ResponseEntity<OrderResponse> placeOrder(
        @LoginMember Member member,
        @RequestBody OrderRequest orderRequest,
        @RequestHeader("Authorization") String authorizationHeader
    ) {
        String accessToken = JwtUtil.getAccessTokenFromHeader(authorizationHeader);
        Order order = orderService.placeOrder(member, orderRequest, accessToken);
        return ResponseEntity.status(HttpStatus.CREATED).body(OrderResponse.from(order));
    }
}
