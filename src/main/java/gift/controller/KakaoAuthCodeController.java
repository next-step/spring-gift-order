package gift.controller;

import gift.auth.JwtAuth;
import gift.dto.KakaoTokenResponseDto;
import gift.dto.KakaoTokenWithJwtResponseDto;
import gift.entity.Member;
import gift.service.KakaoApiService;
import gift.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class KakaoAuthCodeController {

    private final KakaoApiService kakaoApiService;
    private final MemberService memberService;
    private final JwtAuth jwtAuth;

    public KakaoAuthCodeController(KakaoApiService kakaoApiService, MemberService memberService, JwtAuth jwtAuth) {
        this.kakaoApiService = kakaoApiService;
        this.memberService = memberService;
        this.jwtAuth = jwtAuth;
    }

    @GetMapping
    public ResponseEntity<KakaoTokenWithJwtResponseDto> getAccessToken(@RequestParam("code") String code) {
        KakaoTokenResponseDto kakaoTokenResponseDto = kakaoApiService.getKakaoLoginResponse(code);
        String accessToken = kakaoTokenResponseDto.accessToken();
        String email = kakaoApiService.getUserEmail(accessToken);
        Member member = new Member(email, "12345678", true);
        if(!memberService.isEmailExists(email)) {
            memberService.add(member);
        }
        String jwtToken = jwtAuth.createJwtToken(member, accessToken);
        KakaoTokenWithJwtResponseDto responseDto = new KakaoTokenWithJwtResponseDto(kakaoTokenResponseDto, jwtToken);
        return ResponseEntity.ok(responseDto);
    }
}
