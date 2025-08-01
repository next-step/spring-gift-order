package gift.controller;

import gift.dto.order.OrderRequestDto;
import gift.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
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
    public ResponseEntity<String> createOrder(
            @Valid @RequestBody OrderRequestDto orderRequestDto,
            HttpServletRequest request
    ) {
        String userEmail = (String) request.getAttribute("userEmail");

        orderService.createOrder(userEmail, orderRequestDto);

        return ResponseEntity.ok("주문이 성공적으로 완료되었습니다.");
    }
}