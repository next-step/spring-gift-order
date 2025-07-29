package gift.kakao.service;

import gift.kakao.KakaoTokenEntity;
import gift.kakao.MessageTemplate;
import gift.kakao.dto.KakaoMessageResponse;
import gift.kakao.dto.KakaoTokenResponseDto;
import gift.kakao.exception.KakaoServerException;
import gift.kakao.exception.TokenExpiredException;
import gift.member.MemberEntity;
import gift.member.exception.MemberNotFoundException;
import gift.member.repository.MemberRepository;
import gift.order.OrderEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Service
public class KakaoService {

    private final String clientId;
    private final String redirectUri;

    private final MemberRepository memberRepository;

    private final RestClient client = RestClient.builder().build();

    public KakaoService(
        @Value("${kakao.app.key}") String clientId,
        @Value("${kakao.redirect_uri}") String redirectUri,
        MemberRepository memberRepository
    ) {
        this.clientId = clientId;
        this.redirectUri = redirectUri;
        this.memberRepository = memberRepository;
    }

    public void fetchAndSaveToken(String code, Long memberId) {

        MemberEntity memberEntity = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberNotFoundException(memberId));

        String requestBody = String.format(
            "grant_type=authorization_code&client_id=%s&redirect_uri=%s&code=%s",
            clientId, redirectUri, code
        );

        KakaoTokenResponseDto response = client.post()
            .uri("https://kauth.kakao.com/oauth/token")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            .body(requestBody)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError,
                (httpRequest, httpResponse) -> {
                    throw new IllegalArgumentException("파라미터 오류");
                })
            .onStatus(HttpStatusCode::is5xxServerError,
                (httpRequest, httpResponse) -> {
                    throw new KakaoServerException();
                })
            .body(KakaoTokenResponseDto.class);

        if (response == null) {
            throw new KakaoServerException();
        }

        KakaoTokenEntity kakaoToken = KakaoTokenEntity.from(response);
        memberEntity.setKakaoToken(kakaoToken);
        memberRepository.save(memberEntity);
    }

    @Transactional
    public void sendOrderMessageToMe(OrderEntity orderEntity, Long memberId) {

        MemberEntity memberEntity = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberNotFoundException(memberId));

        if (memberEntity.getKakaoToken().isAccessTokenExpired()) {
            refreshToken(memberId);
        }

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("template_object", MessageTemplate.ofOrder(orderEntity));

        KakaoMessageResponse response = client.post()
            .uri("https://kapi.kakao.com/v2/api/talk/memo/default/send")
            .headers(httpHeaders -> {
                httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                httpHeaders.setBearerAuth(memberEntity.getKakaoToken().getAccessToken());
            })
            .body(body)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError,
                (httpRequest, httpResponse) -> {
                    throw new IllegalArgumentException("파라미터 오류");
                })
            .onStatus(HttpStatusCode::is5xxServerError,
                (httpRequest, httpResponse) -> {
                    throw new KakaoServerException();
                })
            .body(KakaoMessageResponse.class);

        if (response == null || response.resultCode() != 0) {
            throw new KakaoServerException();
        }
    }

    @Transactional
    public void refreshToken(Long memberId) {
        MemberEntity memberEntity = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberNotFoundException(memberId));

        KakaoTokenEntity token = memberEntity.getKakaoToken();

        if (token == null) {
            throw new TokenExpiredException("카카오 토큰이 없습니다. 로그인 해주세요.");
        }

        if (token.isRefreshTokenExpired()) {
            throw new TokenExpiredException("카카오 리프레시 토큰이 만료되었습니다. 재로그인 해주세요.");
        }

        String requestBody = String.format(
            "grant_type=refresh_token&client_id=%s&refresh_token=%s",
            clientId, token.getRefreshToken()
        );

        KakaoTokenResponseDto response = client.post()
            .uri("https://kauth.kakao.com/oauth/token")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            .body(requestBody)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError,
                (httpRequest, httpResponse) -> {
                    throw new IllegalArgumentException("파라미터 오류");
                })
            .onStatus(HttpStatusCode::is5xxServerError,
                (httpRequest, httpResponse) -> {
                    throw new KakaoServerException();
                })
            .body(KakaoTokenResponseDto.class);

        if (response == null) {
            throw new KakaoServerException();
        }

        if (response.refreshToken() != null) {
            token.updateRefreshToken(response.refreshToken(), response.refreshTokenExpiresIn());
        }

        token.updateAccessToken(response.accessToken(), response.expiresIn());

        memberEntity.setKakaoToken(token);
        memberRepository.save(memberEntity);

    }

}
