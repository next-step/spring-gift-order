package gift.controller;

import gift.annotation.LoginUser;
import gift.dto.OrderRequestDTO;
import gift.dto.OrderResponseDTO;
import gift.jwt.JwtTokenProvider;
import gift.jwt.JwtUtils;
import gift.model.User;
import gift.service.KakaoOAuthService;
import gift.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtUtils jwtUtils;
    private final KakaoOAuthService kakaoOAuthService;

    public OrderController(KakaoOAuthService kakaoOAuthService, OrderService orderService, JwtTokenProvider jwtTokenProvider, JwtUtils jwtUtils) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.orderService = orderService;
        this.jwtUtils = jwtUtils;
        this.kakaoOAuthService = kakaoOAuthService;
    }

    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@RequestBody OrderRequestDTO request,
                                                        @LoginUser User user,
                                                        @RequestHeader(value = "Authorization", required = false) String jwtToken) {
        String accessToken = null;
        String pureToken = jwtUtils.extractPureToken(jwtToken);
        if (pureToken != null) {
            accessToken = kakaoOAuthService.getKakaoAccessTokenFromPureToken(pureToken);
        } else {
            return ResponseEntity.badRequest().body(null);
        }

        if (accessToken == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        OrderResponseDTO response = orderService.createOrder(request, user, accessToken);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);


    }
}