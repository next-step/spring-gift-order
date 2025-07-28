package gift.controller.orderController;


import gift.dto.orderDto.OrderRequestDto;
import gift.dto.orderDto.OrderResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @PostMapping
    public ResponseEntity<OrderResponseDto> post(@RequestBody OrderRequestDto orderRequestDto) {

    }
}
