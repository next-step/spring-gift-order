package gift.controller;

import gift.annotation.LoginMember;
import gift.dto.OrderRequestDto;
import gift.dto.OrderResponseDto;
import gift.entity.Member;
import gift.service.KakaoMessageService;
import gift.service.OrderService;
import jakarta.validation.Valid;
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
    private final KakaoMessageService kakaoMessageService;

    public OrderController(OrderService orderService, KakaoMessageService kakaoMessageService) {
        this.orderService = orderService;
        this.kakaoMessageService = kakaoMessageService;

    }

    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(@Valid @RequestBody OrderRequestDto dto, @LoginMember Member member) {
        OrderResponseDto response = orderService.createOrder(member.getId(), dto);
        String accessToken = member.getKakaoAccessToken();
        kakaoMessageService.sendKakaoMessage(accessToken, response);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
