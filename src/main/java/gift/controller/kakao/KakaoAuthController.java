package gift.controller.kakao;

import gift.dto.KakaoUserInfoResponseDto;
import gift.dto.MemberResponseDto;
import gift.entity.Member;
import gift.security.JwtTokenProvider;
import gift.service.KakaoAuthService;
import gift.service.MemberService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KakaoAuthController {
    private final KakaoAuthService kakaoAuthService;
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    public KakaoAuthController (
            KakaoAuthService kakaoAuthService,
            MemberService memberService,
            JwtTokenProvider jwtTokenProvider
    ) {
        this.kakaoAuthService = kakaoAuthService;
        this.memberService = memberService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("/callback")
    public MemberResponseDto kakaoCallback(@RequestParam("code") String authorizeCode) {
        String accessToken = kakaoAuthService.getAccessToken(authorizeCode);
        KakaoUserInfoResponseDto kakaoUserInfoResponseDto = kakaoAuthService.getUserInfo(accessToken);
        Member member = memberService.processKakaoLogin(kakaoUserInfoResponseDto, accessToken);
        return new MemberResponseDto(jwtTokenProvider.generateToken(member));
    }
}
