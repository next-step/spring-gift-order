package gift.controller;

import gift.dto.LoginMember;
import gift.dto.OrderRequest;
import gift.dto.OrderResponse;
import gift.entity.Member;
import gift.jwt.Authenticated;
import gift.service.MemberService;
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
    private final MemberService memberService;

    public OrderController(OrderService orderService, MemberService memberService) {
        this.orderService = orderService;
        this.memberService = memberService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Authenticated LoginMember loginMember, @RequestBody OrderRequest request) {

        Member member = memberService.findByEmail(loginMember.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일이 유효하지 않습니다."));

        OrderResponse response = orderService.createOrder(request, member);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
