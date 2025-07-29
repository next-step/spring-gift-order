package gift.service;

import com.fasterxml.jackson.databind.JsonNode;
import gift.Jwt.JwtUtil;
import gift.Entity.Member;
import gift.repository.MemberRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class KakaoLoginService {

    private final KakaoOauthClient kakaoOauthClient;
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    private static final Logger log = LoggerFactory.getLogger(KakaoLoginService.class);

    public KakaoLoginService(KakaoOauthClient kakaoOauthClient,
                             MemberRepository memberRepository,
                             JwtUtil jwtUtil) {
        this.kakaoOauthClient = kakaoOauthClient;
        this.memberRepository = memberRepository;
        this.jwtUtil = jwtUtil;
    }

    public String kakaoLogin(String code, HttpServletResponse response) {
        try {
            String accessToken = kakaoOauthClient.getAccessToken(code);
            JsonNode userInfo = kakaoOauthClient.getUserInfo(accessToken);

            String kakaoId = String.valueOf(userInfo.get("id").asLong());
            String nickname = getNullableField(userInfo, "properties", "nickname");
            String email = getNullableField(userInfo, "kakao_account", "email");

            Member member = registerIfAbsent(kakaoId, nickname, email);
            String token = jwtUtil.createToken(member);
            setTokenAsCookie(response, token, accessToken);

            return accessToken;
        } catch (Exception e) {
            log.error("카카오 로그인 중 오류 발생", e);
            throw new RuntimeException("카카오 로그인 처리 중 오류 발생: " + e.getMessage());
        }
    }

    private String getNullableField(JsonNode root, String parent, String field) {
        JsonNode parentNode = root.get(parent);
        if (parentNode != null && parentNode.has(field)) {
            return parentNode.get(field).asText();
        }
        return null;
    }

    private Member registerIfAbsent(String kakaoId, String nickname, String email) {
        return memberRepository.findByNickname(kakaoId).orElseGet(() -> {
            Member member = new Member(
                    kakaoId,
                    email,
                    "123456789",
                    nickname,
                    "카카오 로그인 사용자",
                    "USER"
            );
            return memberRepository.save(member);
        });
    }


    private void setTokenAsCookie(HttpServletResponse response, String token, String accessToken) {
        Cookie cookie = new Cookie("Authorization", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60);  // 1시간
        response.addCookie(cookie);

        Cookie kakaoCookie = new Cookie("KakaoAccessToken", accessToken);
        kakaoCookie.setHttpOnly(true);
        kakaoCookie.setPath("/");
        kakaoCookie.setMaxAge(60 * 60);
        response.addCookie(kakaoCookie);
    }
}
