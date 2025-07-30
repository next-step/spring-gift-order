package gift.controller;

import gift.auth.LoginMember;
import gift.dto.OrderRequestDTO;
import gift.dto.OrderResponseDTO;
import gift.entity.Member;
import gift.service.OrderService;
import jakarta.validation.Valid;
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
    public ResponseEntity<OrderResponseDTO> createOrder(
        @Valid @RequestBody OrderRequestDTO orderRequestDTO,
        @LoginMember Member member,
        @RequestHeader("Authorization") String authorizationHeader
    ) {
        String jwtToken = authorizationHeader.replace("Bearer ", "");
        OrderResponseDTO response = orderService.createOrder(orderRequestDTO, member.getId(), jwtToken);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
