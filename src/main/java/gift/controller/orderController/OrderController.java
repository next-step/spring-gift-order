package gift.controller.orderController;


import gift.config.Interceptor.LoginUser;
import gift.config.Interceptor.UserOnly;
import gift.dto.orderDto.OrderRequestDto;
import gift.dto.orderDto.OrderResponseDto;
import gift.entity.Order;
import gift.event.OrderEvent;
import gift.service.order.OrderService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final ApplicationEventPublisher eventPublisher;

    public OrderController(OrderService orderService, ApplicationEventPublisher eventPublisher) {
        this.orderService = orderService;
        this.eventPublisher = eventPublisher;
    }


    @UserOnly
    @PostMapping
    public ResponseEntity<OrderResponseDto> order(@LoginUser String userEmail, @RequestBody OrderRequestDto orderRequestDto) {
        Long optionId = orderRequestDto.optionId();
        String message = orderRequestDto.message();
        Integer quantity = orderRequestDto.quantity();

        Order order = orderService.order(optionId, userEmail, message, quantity);
        eventPublisher.publishEvent(new OrderEvent(userEmail, message));

        return new ResponseEntity<>(OrderResponseDto.from(order), HttpStatus.CREATED);
    }
}
