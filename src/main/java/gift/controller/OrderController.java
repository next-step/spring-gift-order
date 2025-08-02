package gift.controller;

import gift.annotation.UserValid;
import gift.dto.KakaoOrderRequestDto;
import gift.dto.UserInfoDto;
import gift.exception.OutOfStockException;
import gift.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 카카오 주문 요청 메서드
     * @param userInfoDto 유저 정보
     * @param kakaoOrderRequestDto 카카오 주문용 객체
     * @return 카카오 메시지 API 응답 코드
     */
    @PostMapping
    public ResponseEntity<String> orderKakao(@UserValid UserInfoDto userInfoDto, @RequestBody KakaoOrderRequestDto kakaoOrderRequestDto) {
        return orderService.orderProduct(userInfoDto, kakaoOrderRequestDto);
    }

    // 재고 없을 경우 발생하는 예외 핸들러
    @ExceptionHandler(OutOfStockException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String handleUnprocessableEntity(OutOfStockException e) { return e.getMessage(); }
}
