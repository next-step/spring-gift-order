package gift.controller;

import gift.config.JwtProvider;
import gift.dto.kakao.KakaoSignupRequest;
import gift.dto.kakao.KakaoSignupRequest2;
import gift.dto.kakao.KakaoUserInfo;
import gift.dto.kakao.TokenResponse;
import gift.entity.Member;
import gift.repository.MemberRepository;
import gift.service.KakaoAuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/kakao")
public class KakaoJoinController {

    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final KakaoAuthService kakaoAuthService;

    public KakaoJoinController(MemberRepository memberRepository, JwtProvider jwtProvider, KakaoAuthService kakaoAuthService) {
        this.memberRepository = memberRepository;
        this.jwtProvider = jwtProvider;
        this.kakaoAuthService = kakaoAuthService;
    }


    @PostMapping("/signup/email")
    public ResponseEntity<TokenResponse> kakaoSignup(@RequestBody KakaoSignupRequest2 request) {
        KakaoUserInfo userInfo = kakaoAuthService.parseIdToken(request.idToken());

        Member member = memberRepository.findByEmail(userInfo.email())
                .orElseGet(() -> {
                    Member newMember = new Member(userInfo.email(), "kakao-user");
                    return memberRepository.save(newMember);
                });


        String jwt = jwtProvider.createToken(member, request.accessToken());
        return ResponseEntity.ok(new TokenResponse(jwt));
    }
}
