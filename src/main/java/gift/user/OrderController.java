package gift.user;

import gift.authorization.service.JwtProvider;
import gift.member.Member;
import gift.resolver.LoginMember;
import gift.user.dto.OrderRequestDto;
import gift.user.dto.OrderResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponseDto> placeHolder(
            @LoginMember Member member,
            @RequestBody @Valid OrderRequestDto requestDto
    ) {
        OrderResponseDto responseDto = orderService.placeOrder(member,requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
