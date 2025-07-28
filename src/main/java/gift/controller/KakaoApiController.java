package gift.controller;

import gift.common.argumentResolver.LoginUser;
import gift.dto.jwt.JwtTokenResponse;
import gift.dto.kakao.*;
import gift.dto.user.UserInfo;
import gift.service.KakaoLoginService;
import gift.service.OrderService;
import gift.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class KakaoApiController {

    private final KakaoLoginService kakaoLoginService;
    private final UserService userService;
    private final OrderService orderService;

    public KakaoApiController(KakaoLoginService kakaoLoginService, UserService userService, OrderService orderService) {
        this.kakaoLoginService = kakaoLoginService;
        this.userService = userService;
        this.orderService = orderService;
    }

    @GetMapping("/kakao/callback")
    public ResponseEntity<KakaoLoginResponse> kakaoLogin(@RequestParam String code) {
        KakaoTokenResponse token = kakaoLoginService.getAccessToken(code);
        KakaoUserIdResponse kakaoUserId = kakaoLoginService.getUserInfo(token.accessToken());
        JwtTokenResponse jwtTokenResponse = userService.kakaoLogin(kakaoUserId.id(), token.accessToken());
        return ResponseEntity.ok(KakaoLoginResponse.of(token, jwtTokenResponse));
    }

    @PostMapping("/api/orders")
    public ResponseEntity<KakaoOrderResponse> order(@LoginUser UserInfo userInfo, @RequestBody @Valid KakaoOrderRequest orderRequest) {
        KakaoOrderResponse orderResponse = orderService.order(userInfo, orderRequest);
        return new ResponseEntity<>(orderResponse, HttpStatus.CREATED);
    }
}
