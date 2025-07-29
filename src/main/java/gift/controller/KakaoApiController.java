package gift.controller;

import gift.common.argumentResolver.LoginUser;
import gift.dto.jwt.JwtTokenResponse;
import gift.dto.kakao.*;
import gift.dto.login.KakaoLoginRequest;
import gift.dto.user.UserInfo;
import gift.service.api.KakaoLoginApi;
import gift.service.OrderService;
import gift.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class KakaoApiController {

    private final KakaoLoginApi kakaoLoginApi;
    private final UserService userService;
    private final OrderService orderService;

    public KakaoApiController(KakaoLoginApi kakaoLoginApi, UserService userService, OrderService orderService) {
        this.kakaoLoginApi = kakaoLoginApi;
        this.userService = userService;
        this.orderService = orderService;
    }

    @GetMapping("/kakao/callback")
    public ResponseEntity<KakaoLoginResponse> kakaoLogin(@RequestParam String code) {
        String kakaoAccessToken = kakaoLoginApi.getAccessToken(code);
        KakaoUserIdResponse kakaoUserId = kakaoLoginApi.getUserInfo(kakaoAccessToken);
        JwtTokenResponse jwtTokenResponse = userService.login(new KakaoLoginRequest(kakaoUserId.id(), kakaoAccessToken));
        return ResponseEntity.ok(KakaoLoginResponse.of(kakaoAccessToken, jwtTokenResponse));
    }

    @PostMapping("/api/orders")
    public ResponseEntity<KakaoOrderResponse> order(@LoginUser UserInfo userInfo, @RequestBody @Valid KakaoOrderRequest orderRequest) {
        KakaoOrderResponse orderResponse = orderService.order(userInfo, orderRequest);
        return new ResponseEntity<>(orderResponse, HttpStatus.CREATED);
    }
}
