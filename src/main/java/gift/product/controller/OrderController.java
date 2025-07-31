package gift.product.controller;

import gift.product.dto.request.OrderRequest;
import gift.product.dto.response.OrderResponse;
import gift.product.service.OrderService;
import gift.shared.annotation.KakaoUser;
import gift.shared.auth.service.KakaoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    private final KakaoService kakaoService;

    public OrderController(OrderService orderService, KakaoService kakaoService) {
        this.orderService = orderService;
        this.kakaoService = kakaoService;
    }

    @PostMapping()
    public ResponseEntity<OrderResponse> orderProduct(@KakaoUser String accessToken, @RequestBody OrderRequest orderRequest){
        kakaoService.sendMessageToMe(accessToken, orderRequest.message());
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.order(orderRequest));
    }
}
