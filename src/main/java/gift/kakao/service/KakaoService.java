package gift.kakao.service;

import gift.kakao.KakaoTokenEntity;
import gift.kakao.dto.KakaoTokenResponseDto;
import gift.kakao.exception.KakaoServerException;
import gift.member.MemberEntity;
import gift.member.exception.MemberNotFoundException;
import gift.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
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

}
