package gift.controller;

import gift.config.JwtProvider;
import gift.dto.request.OrderRequestDto;
import gift.dto.response.OrderResponseDto;
import gift.entity.Member;
import gift.entity.Order;
import gift.repository.MemberRepository;
import gift.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;

    public OrderController(OrderService orderService,
                           JwtProvider jwtProvider,
                           MemberRepository memberRepository) {
        this.orderService = orderService;
        this.jwtProvider = jwtProvider;
        this.memberRepository = memberRepository;
    }

    @PostMapping
    public ResponseEntity<OrderResponseDto> placeOrder(
            @RequestHeader("Authorization") String bearerToken,
            @RequestBody OrderRequestDto dto) {

        String token = bearerToken.replace("Bearer ", "");
        Long memberId = jwtProvider.getId(token);


        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("회원이 존재하지 않습니다."));

        Order order = orderService.placeOrder(member, dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new OrderResponseDto(order));
    }
}
