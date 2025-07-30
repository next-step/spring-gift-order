package gift.service;

import gift.dto.KakaoTokenResponse;
import gift.dto.KakaoUserInfo;
import gift.entity.Member;
import gift.repository.MemberRepository;
import gift.util.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;

@Service
public class KakaoOAuthService {
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    KakaoOAuthService(MemberRepository memberRepository, JwtUtil jwtUtil) {
        this.memberRepository = memberRepository;
        this.jwtUtil = jwtUtil;
    }

    private final RestClient restClient = RestClient.create();

    @Value("${kakao.oauth.client-id}")
    private String clientId;

    @Value("${kakao.oauth.redirect-uri}")
    private String redirectUri;

    private final String authUrl = "https://kauth.kakao.com/oauth/authorize";
    private final String tokenUrl = "https://kauth.kakao.com/oauth/token";
    private final String UserInfoUrl = "https://kapi.kakao.com/v2/user/me";

    public URI buildKakaoAuthUri() {
        return UriComponentsBuilder.fromUriString(authUrl)
                .queryParam("scope", "talk_message")
                .queryParam("response_type", "code")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .build()
                .toUri();
    }

    public KakaoTokenResponse exchangeCodeForToken(String code) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);

        return restClient.post()
                .uri(tokenUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .body(KakaoTokenResponse.class);
    }

    public KakaoUserInfo getUserInfo(String accessToken) {
        return restClient.get()
                .uri(UserInfoUrl)
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
                .retrieve()
                .body(KakaoUserInfo.class);
    }

    public String loginWithKakao(String code) {
        KakaoTokenResponse tokenResponse = exchangeCodeForToken(code);
        KakaoUserInfo kakaoUserInfo = getUserInfo(tokenResponse.accessToken());
        String kakaoId = String.valueOf(kakaoUserInfo.id());
        LocalDateTime expiry = LocalDateTime.now().plusSeconds(tokenResponse.expiresIn());
        String emailCandidate = "kakao" + kakaoId + "@example.com";

        Member member = memberRepository.findByKakaoId(kakaoId)
                .map(existing -> {
                    existing.setKakaoAccessToken(tokenResponse.accessToken());
                    existing.setKakaoRefreshToken(tokenResponse.refreshToken());
                    existing.setKakaoTokenExpiry(expiry);
                    return memberRepository.save(existing);
                })
                .orElseGet(() -> {
                    Member newMember = Member.fromKakao(kakaoId);
                    newMember.setEmail(emailCandidate);
                    newMember.setKakaoAccessToken(tokenResponse.accessToken());
                    newMember.setKakaoRefreshToken(tokenResponse.refreshToken());
                    newMember.setKakaoTokenExpiry(expiry);
                    return memberRepository.save(newMember);
                });

        return jwtUtil.generateTokenForKakao(member);
    }

}
