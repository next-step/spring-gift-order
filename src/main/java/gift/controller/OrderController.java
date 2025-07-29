package gift.controller;

import gift.config.JwtProvider;
import gift.config.LoginMember;
import gift.dto.request.OrderRequestDto;
import gift.dto.response.OrderResponseDto;
import gift.entity.Member;
import gift.entity.Order;
import gift.service.KakaoMessageService;
import gift.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;
    private final JwtProvider jwtProvider;
    private final KakaoMessageService kakaoMessageService;

    public OrderController(OrderService orderService,
                           JwtProvider jwtProvider,
                           KakaoMessageService kakaoMessageService) {
        this.orderService = orderService;
        this.jwtProvider = jwtProvider;
        this.kakaoMessageService = kakaoMessageService;
    }

    @PostMapping
    public ResponseEntity<OrderResponseDto> placeOrder(
            @LoginMember Member member,
            @RequestHeader("Authorization") String bearerToken,
            @RequestBody OrderRequestDto dto) {

        String token = bearerToken.replace("Bearer ", "");
        Order order = orderService.placeOrder(member, dto);
        String kakaoAccessToken = jwtProvider.getKakaoAccessToken(token);

        kakaoMessageService.sendMessageToMe(
                kakaoAccessToken,
                order.getOption().getProduct().getName(),
                order.getOption().getName(),
                order.getQuantity(),
                order.getMessage(),
                order.getOrderDateTime()
        );


        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new OrderResponseDto(order));
    }
}
