package gift.controller.kakao;

import gift.dto.KakaoUserInfoResponse;
import gift.entity.Member;
import gift.security.JwtTokenProvider;
import gift.service.KakaoAuthService;
import gift.service.MemberService;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Member> kakaoCallback(@RequestParam("code") String authorizeCode) {
        String accessToken = kakaoAuthService.getAccessToken(authorizeCode);
        KakaoUserInfoResponse kakaoUserInfoResponse = kakaoAuthService.getUserInfo(accessToken);
        Member member = memberService.processKakaoLogin(kakaoUserInfoResponse, accessToken);
        
        return ResponseEntity.ok(member);
    }
}
