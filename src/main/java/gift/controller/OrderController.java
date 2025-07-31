package gift.controller;

import gift.dto.OrderRequest;
import gift.dto.OrderResponse;
import gift.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
        @RequestBody
        OrderRequest request,
        @RequestHeader("Authorization")
        String authorizationHeader
    ) {
        String accessToken = authorizationHeader.replaceFirst("Bearer ", "");
        OrderResponse response = orderService.createOrder(request, accessToken);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
