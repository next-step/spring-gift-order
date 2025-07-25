package gift.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gift.Jwt.JwtUtil;
import gift.Entity.Member;
import gift.properties.KakaoProperties;
import gift.repository.MemberRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Service
public class KakaoLoginService {

    private final KakaoProperties kakaoProperties;
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public KakaoLoginService(KakaoProperties kakaoProperties, MemberRepository memberRepository, JwtUtil jwtUtil) {
        this.kakaoProperties = kakaoProperties;
        this.memberRepository = memberRepository;
        this.jwtUtil = jwtUtil;
    }

    public void kakaoLogin(String code, HttpServletResponse response) {
        try {
            String accessToken = getAccessToken(code);
            JsonNode userInfo = getUserInfo(accessToken);

            String kakaoId = String.valueOf(userInfo.get("id").asLong());
            String nickname = getNullableField(userInfo, "properties", "nickname");
            String email = getNullableField(userInfo, "kakao_account", "email");

            Member member = registerIfAbsent(kakaoId, nickname, email);

            String token = jwtUtil.createToken(member);
            setTokenAsCookie(response, token);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("카카오 로그인 처리 중 오류 발생: " + e.getMessage());
        }
    }

    private String getAccessToken(String code) throws Exception {
        String tokenUrl = "https://kauth.kakao.com/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoProperties.getClientId());
        body.add("redirect_uri", kakaoProperties.getRedirectUri());
        body.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(URI.create(tokenUrl), request, String.class);

        System.out.println("🔑 카카오 토큰 응답: " + response.getBody());

        JsonNode node = objectMapper.readTree(response.getBody());
        return node.get("access_token").asText();
    }

    private JsonNode getUserInfo(String accessToken) throws Exception {
        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(userInfoUrl, HttpMethod.GET, request, String.class);

        System.out.println("👤 사용자 정보 응답: " + response.getBody());

        return objectMapper.readTree(response.getBody());
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
            Member member = new Member();
            member.setNickname(kakaoId); // 카카오 ID를 사용자 ID(nickname)로 사용
            member.setEmail(email);
            member.setName(nickname);
            member.setAddress("카카오 로그인 사용자");
            member.setPassword("123456789");  // 실제로 사용되지 않음
            member.setRole("USER");
            return memberRepository.save(member);
        });
    }


    private void setTokenAsCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("Authorization", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60);  // 1시간
        response.addCookie(cookie);
    }
}
