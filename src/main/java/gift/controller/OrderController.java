package gift.controller;

import gift.annotation.LoginMember;
import gift.dto.MemberRequest;
import gift.dto.MemberResponse;
import gift.dto.OrderRequest;
import gift.dto.OrderResponse;
import gift.dto.common.PageResponse;
import gift.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> order(
            @Valid @RequestBody OrderRequest request,
            @RequestHeader("kakao-access-token") String kakaoAccessToken,
            @LoginMember MemberRequest member) {

        OrderResponse response = orderService.order(request, member, kakaoAccessToken);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<PageResponse<OrderResponse>> getOrders(
            @LoginMember MemberRequest member,
            Pageable pageable) {

        PageResponse<OrderResponse> response = orderService.getOrdersByMember(member.id(), pageable);
        return ResponseEntity.ok(response);
    }

}