package gift.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gift.auth.JwtProvider;
import gift.domain.Member;
import gift.repository.MemberRepository;
import gift.service.KakaoLoginService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.Optional;

@Controller
public class KakaoLoginController {

    private final KakaoLoginService kakaoLoginService;
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;

    public KakaoLoginController(KakaoLoginService kakaoLoginService,
                                MemberRepository memberRepository,
                                JwtProvider jwtProvider) {
        this.kakaoLoginService = kakaoLoginService;
        this.memberRepository = memberRepository;
        this.jwtProvider = jwtProvider;
    }

    @GetMapping("kakao/form")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("kakao/login")
    public String redirectToKakaoAuth(@RequestParam String clientId, HttpSession session) {

        session.setAttribute("clientId", clientId);
        session.setAttribute("redirectUri", "http://localhost:8080");

        String kakaoAuthUrl = UriComponentsBuilder
                .fromHttpUrl("https://kauth.kakao.com/oauth/authorize")
                .queryParam("response_type", "code")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", "http://localhost:8080")
                .queryParam("scope", "talk_message")
                .build()
                .toUriString();

        return "redirect:" + kakaoAuthUrl;
    }

    @GetMapping
    public String redirectToKakaoLogin(@RequestParam String code, HttpSession session) {
        try {
            String accessTokenJson = kakaoLoginService.getAccessToken(code, session);

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> tokenMap = objectMapper.readValue(accessTokenJson, Map.class);

            String kakaoAccessToken = (String) tokenMap.get("access_token");
            session.setAttribute("kakaoAccessToken", kakaoAccessToken);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "login";
        }

        String email = "user@email.com";
        String password = "1234";

        Member member = memberRepository.findByEmail(email)
                .orElseGet(() -> memberRepository.save(new Member(email, password)));

        String jwt = jwtProvider.createToken(member.getId());
        session.setAttribute("jwtAccessToken", jwt);
        return "redirect:/message";
    }



}
