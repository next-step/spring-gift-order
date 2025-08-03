package gift.controller;

import gift.dto.KakaoUserInfoResponseDto;
import gift.dto.SocialInfoDto;
import gift.dto.TokenResponse;
import gift.service.KakaoService;
import gift.service.MemberService;
import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SocialLoginController {

    private final KakaoService kakaoService;
    private final MemberService memberService;

    public SocialLoginController(KakaoService kakaoService, MemberService memberService) {
        this.kakaoService = kakaoService;
        this.memberService = memberService;
    }

    @GetMapping
    public ResponseEntity<TokenResponse> callback(@RequestParam("code") String code) {
        String socialAccessToken = kakaoService.getAccessTokenFromKakao(code);

        KakaoUserInfoResponseDto userInfo = kakaoService.getUserInfo(socialAccessToken);
        Long socialId = userInfo.getId();

        if (!memberService.isSocialIdExists(socialId)) {
            //회원가입
            URI redirectUri = URI.create(
                "/admin/members/new?socialId=" + socialId + "&providerType=kakao&"
                    + "socialAccessToken=" + socialAccessToken);
            return ResponseEntity.status(HttpStatus.FOUND).location(redirectUri).build();
        }

        //로그인
        TokenResponse jwtAccessToken = memberService.findBySocialId(socialId);
        SocialInfoDto socialInfoDto = new SocialInfoDto(socialId, "kakao", socialAccessToken);
        memberService.updateSocialInfo(socialInfoDto);

        return ResponseEntity.status(HttpStatus.OK).body(jwtAccessToken);
    }


}
