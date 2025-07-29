package gift.controller;

import gift.config.JwtProvider;
import gift.config.LoginUser;
import gift.dto.kakao.LoginUserInfo;
import gift.dto.request.OrderRequestDto;
import gift.dto.response.OrderResponseDto;
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


    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponseDto> placeOrder(
            @LoginUser LoginUserInfo loginUser,
            @RequestBody OrderRequestDto dto) {

        Order order = orderService.placeOrder(loginUser, dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new OrderResponseDto(order));
    }
}
