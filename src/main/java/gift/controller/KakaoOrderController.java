package gift.controller;

import gift.annotation.UserValid;
import gift.dto.KakaoOrderRequestDto;
import gift.dto.UserInfoDto;
import gift.service.KakaoOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/kakao-order")
public class KakaoOrderController {
    private final KakaoOrderService kakaoOrderService;
    public KakaoOrderController(KakaoOrderService kakaoOrderService) {
        this.kakaoOrderService = kakaoOrderService;
    }

    @PostMapping
    public ResponseEntity<Void> order(@UserValid UserInfoDto userInfoDto, @RequestBody KakaoOrderRequestDto kakaoOrderRequestDto) {
        kakaoOrderService.orderProduct(userInfoDto, kakaoOrderRequestDto);
        return ResponseEntity.noContent().build();
    }


}
