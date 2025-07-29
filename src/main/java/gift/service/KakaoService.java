package gift.service;

import gift.auth.JwtUtil;
import gift.dto.KakaoAccessTokenDTO;
import gift.dto.KakaoTokenResponseDTO;
import gift.dto.TokenResponseDTO;
import gift.entity.LoginType;
import gift.entity.Member;
import gift.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;

@Service
public class KakaoService {
    private final String CLIENT_ID;
    private final String REDIRECT_URI;
    private final RestClient restClient;
    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;

    public KakaoService(@Value("${kakao.client-id}") String clientId,
        @Value("${kakao.redirect-uri}") String redirectUri, JwtUtil jwtUtil, MemberRepository memberRepository) {
        this.CLIENT_ID = clientId;
        this.REDIRECT_URI = redirectUri;
        this.restClient = RestClient.create();
        this.jwtUtil = jwtUtil;
        this.memberRepository = memberRepository;
    }

    private KakaoTokenResponseDTO requestKakaoToken(String authorizationCode) {
        final String url = "https://kauth.kakao.com/oauth/token";
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);

        var body = new LinkedMultiValueMap<String, String>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", CLIENT_ID);
        body.add("redirect_uri", REDIRECT_URI);
        body.add("code", authorizationCode);

        return restClient.post()
            .uri(url)
            .headers(httpHeaders -> httpHeaders.addAll(headers))
            .body(body)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                throw new IllegalArgumentException("인가 코드가 유효하지 않습니다.");
            })
            .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                throw new IllegalStateException("카카오 서버에 문제가 발생했습니다.");
            })
            .body(KakaoTokenResponseDTO.class);
    }

    private KakaoAccessTokenDTO requestKakaoAccessToken(String accessToken) {
        final String url = "https://kapi.kakao.com/v1/user/access_token_info";
        var kakaoAccessTokenDTO = restClient.get()
            .uri(url)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .retrieve()
            .body(KakaoAccessTokenDTO.class);
        if (kakaoAccessTokenDTO == null || kakaoAccessTokenDTO.id() == null) {
            throw new IllegalArgumentException("카카오 액세스 토큰 정보가 유효하지 않습니다.");
        }
        return kakaoAccessTokenDTO;
    }

    public TokenResponseDTO loginWithKakao(String authorizationCode) {
        KakaoTokenResponseDTO kakaoToken = requestKakaoToken(authorizationCode);
        KakaoAccessTokenDTO kakaoAccessTokenDTO = requestKakaoAccessToken(kakaoToken.accessToken());
        if (!memberRepository.existsByTypeId(kakaoAccessTokenDTO.id().toString())) {
            final String tempEmail = "kakao_" + kakaoAccessTokenDTO.id() + "@kakao.com";
            Member member = new Member(tempEmail, LoginType.KAKAO, kakaoAccessTokenDTO.id().toString());
            memberRepository.save(member);
        }
        Member member = memberRepository.findByLoginTypeAndTypeId(LoginType.KAKAO, kakaoAccessTokenDTO.id().toString())
            .orElseThrow(() -> new IllegalArgumentException("카카오 사용자 정보를 찾을 수 없습니다."));

        Map<String, Object> tokenInfo = new HashMap<>();
        tokenInfo.put("kakaoAccessToken", kakaoToken.accessToken());
        tokenInfo.put("kakaoRefreshToken", kakaoToken.refreshToken());
        tokenInfo.put("kakaoExpiresIn", kakaoToken.expiresIn());
        tokenInfo.put("kakaoTokenType", kakaoToken.tokenType());
        tokenInfo.put("kakaoScope", kakaoToken.scope());

        Map<String, Object> claims = new HashMap<>();
        claims.put("token", tokenInfo);

        String jwtToken = jwtUtil.createToken(member.getEmail(), claims);
        return new TokenResponseDTO(jwtToken);
    }
}
