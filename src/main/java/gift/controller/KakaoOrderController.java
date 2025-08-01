package gift.controller;

import gift.annotation.UserValid;
import gift.dto.KakaoOrderRequestDto;
import gift.dto.UserInfoDto;
import gift.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/kakao-order")
public class KakaoOrderController {
    private final OrderService orderService;
    public KakaoOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Void> order(@UserValid UserInfoDto userInfoDto, @RequestBody KakaoOrderRequestDto kakaoOrderRequestDto) {
        orderService.orderProduct(userInfoDto, kakaoOrderRequestDto);
        return ResponseEntity.noContent().build();
    }


}
