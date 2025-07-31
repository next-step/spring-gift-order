package gift.controller;

import gift.auth.LoginMember;
import gift.dto.OrderRequest;
import gift.dto.OrderResponse;
import gift.entity.Member;
import gift.service.OrderService;
import jakarta.validation.Valid;
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
    public ResponseEntity<OrderResponse> createOrder(
            @LoginMember Member member,
            @Valid @RequestBody OrderRequest orderRequest,
            @RequestHeader("X-Kakao-Token") String kakaoAccessToken
    ) {
        OrderResponse response = orderService.createOrder(member.getId(), orderRequest,
                kakaoAccessToken);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}