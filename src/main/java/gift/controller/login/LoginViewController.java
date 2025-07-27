package gift.controller.login;

import gift.config.KakaoProperties;
import gift.dto.member.MemberRequestDto;
import gift.dto.member.MemberResponseDto;
import gift.entity.LoginType;
import gift.service.member.MemberService;
import gift.service.member.OauthService;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginViewController {

    private final KakaoProperties kakaoProperties;
    private final OauthService oauthService;
    private final MemberService memberService;

    public LoginViewController(KakaoProperties kakaoProperties, OauthService oauthService,
        MemberService memberService) {
        this.kakaoProperties = kakaoProperties;
        this.oauthService = oauthService;
        this.memberService = memberService;
    }

    @GetMapping("/login-success")
    public String viewLoginSuccess() {
        return "login-success";
    }

    @GetMapping("/view/login")
    public String loginPage(Model model) {
        model.addAttribute("kakaoRestApiKey", kakaoProperties.restApiKey());
        model.addAttribute("redirectUri", kakaoProperties.redirectUri());

        return "login";
    }

    @GetMapping("/oauth/kakao")
    public void kakaoLogin(
        @RequestParam String code,
        HttpServletResponse response
    ) throws IOException {
        String kakaoAccessToken = oauthService.fetchKakaoToken(code);
        String email = oauthService.extractEmailFromKakao(kakaoAccessToken);

        String jwtToken = memberService.createOrLoginForKakao(
            new MemberRequestDto(email, null, LoginType.KAKAO)).token();

        ResponseCookie cookie = ResponseCookie.from("token", jwtToken)
            .httpOnly(true)
            .secure(false)
            .path("/")
            .maxAge(Duration.ofHours(1))
            .sameSite("Lax")
            .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        response.sendRedirect("/login-success");
    }
}
