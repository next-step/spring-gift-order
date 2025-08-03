package gift.order.controller;

import gift.member.dto.AuthenticatedMemberDto;
import gift.order.dto.OrderRequestDto;
import gift.order.dto.OrderResponseDto;
import gift.order.service.OrderService;
import gift.security.annotation.LoginMember;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
            @LoginMember AuthenticatedMemberDto loginMember,
            @CookieValue("kakaoAccessToken") String kakaoAccessToken,
            @RequestBody @Valid OrderRequestDto orderRequestDto
    ) {
        OrderResponseDto response = orderService.createOrder(loginMember.id(), orderRequestDto, kakaoAccessToken);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public Page<OrderResponseDto> listOrders(
            @LoginMember AuthenticatedMemberDto loginMember,
            @PageableDefault(page = 0, size = 10, sort = "orderDateTime", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return orderService.listOrders(loginMember.id(), pageable);
    }
}

