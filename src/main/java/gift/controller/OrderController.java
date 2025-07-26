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

        // 1. JWT에서 memberId 추출
        String token = bearerToken.replace("Bearer ", "");
        Long memberId = jwtProvider.getId(token);

        // 2. 사용자 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("회원이 존재하지 않습니다."));

        // 3. 주문 처리 (수량 차감 + 위시리스트 삭제 + 저장)
        Order order = orderService.placeOrder(member, dto);

        // 4. 응답 반환
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new OrderResponseDto(order));
    }
}
