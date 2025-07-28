package gift.controller.orderController;


import gift.config.LoginUser;
import gift.dto.orderDto.OrderRequestDto;
import gift.dto.orderDto.OrderResponseDto;
import gift.entity.Order;
import gift.service.order.OrderService;
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
    public ResponseEntity<OrderResponseDto> order(@LoginUser String userEmail, @RequestBody OrderRequestDto orderRequestDto) {
        Long optionId = orderRequestDto.optionId();
        String message = orderRequestDto.message();
        Integer quantity = orderRequestDto.quantity();

        Order order = orderService.order(optionId, userEmail, message, quantity);

        return new ResponseEntity<>(OrderResponseDto.from(order), HttpStatus.CREATED);
    }
}
