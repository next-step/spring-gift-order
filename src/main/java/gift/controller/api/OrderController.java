package gift.controller.api;

import gift.auth.resolver.CurrentUser;
import gift.common.code.CustomResponseCode;
import gift.common.dto.CustomResponseBody;
import gift.dto.Order.OrderRequest;
import gift.dto.Order.OrderResponse;
import gift.entity.Member.Member;
import gift.service.Order.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    public ResponseEntity<CustomResponseBody<OrderResponse>> createOrder(
        @CurrentUser Member member,
        @Valid @RequestBody OrderRequest request
    ) {
        OrderResponse response = orderService.create(member, request);

        return ResponseEntity
            .status(CustomResponseCode.CREATED.getHttpStatus())
            .body(CustomResponseBody.of(CustomResponseCode.CREATED, response));
    }
}
