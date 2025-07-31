package gift.controller.order;

import gift.dto.order.OrderRequest;
import gift.dto.order.OrderResponse;
import gift.global.util.RequestAttributes;
import gift.service.order.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/order")
public class OrderApiController {
    private final OrderService orderService;

    public OrderApiController(OrderService orderService) {
        this.orderService = orderService;
    }
    
    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(
        @RequestBody OrderRequest orderRequest,
        // 카카오 로그인 시에 받은 액세스 토큰을 해당 헤더에 추가
        @RequestHeader("kakao-access-token") String kakaoAccessToken,
        // /login 경로로 로그인 요청시 받은 토큰값을 Authorization에 넣으면 알아서 memberId 추출
        @RequestAttribute(name = RequestAttributes.MEMBER_ID) Long memberId
    ) {
        OrderResponse orderResponse = orderService.createOrder(orderRequest, memberId, kakaoAccessToken);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderResponse);
    }
}
