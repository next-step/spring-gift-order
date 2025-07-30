package gift.controller;

import gift.annotation.LoginUser;
import gift.dto.OrderRequest;
import gift.dto.OrderResponse;
import gift.entity.Order;
import gift.entity.User;
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
    public ResponseEntity<OrderResponse> postOrder(@LoginUser User user,
                                                   @RequestBody OrderRequest orderRequest,
                                                   @RequestHeader(value = "Kakao-Access-Token", required = false) String accessToken) {
        Order order = orderService.createOrder(user.getId(), orderRequest);

        if (accessToken != null) {
            orderService.sendOrderKakaoMessage(order, accessToken);
        }

        return new ResponseEntity<>(OrderResponse.of(order), HttpStatus.CREATED);
    }
}
