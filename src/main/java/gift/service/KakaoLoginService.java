package gift.service;

import gift.dto.KakaoLoginResponse;
import gift.entity.Member;
import gift.repository.MemberRepository;
import java.net.URI;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class KakaoLoginService {

    @Value("${kakao.client_id}")
    private String clientId;

    @Value("${kakao.redirect_uri}")
    private String redirectUri;

    private final RestTemplate restTemplate;
    private final MemberRepository memberRepository;

    public KakaoLoginService(RestTemplate restTemplate, MemberRepository memberRepository) {
        this.restTemplate = restTemplate;
        this.memberRepository = memberRepository;
    }

    public String getAccessToken(String code) {
        var url = "https://kauth.kakao.com/oauth/token";

        var headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);

        var body = new LinkedMultiValueMap<String, String>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);

        var request = new RequestEntity<>(body, headers, HttpMethod.POST, URI.create(url));
        try {
            ResponseEntity<KakaoLoginResponse> response = restTemplate.postForEntity(url, request,
                    KakaoLoginResponse.class
            );

            String accessToken = response.getBody().accessToken();

            Member member = memberRepository.findById(1L)
                    .orElseThrow(() -> new RuntimeException("memberId=1 회원이 없습니다."));
            member.updateKakaoAccessToken(accessToken);
            memberRepository.save(member);

            // 이메일로 회원 정보 가져와 그 회원에게 액세스 토큰 부여 ( 카카오로부터 이메일 받는 권한이 없어서 현재는 작동 X)

//            String email = getKakaoEmail(accessToken);

//            String email = getKakaoEmail(accessToken);
//
//            Member member = memberRepository.findByEmail(email)
//                    .orElseThrow(() -> new RuntimeException("해당 이메일로 가입된 회원이 없습니다."));
//            member.updateKakaoAccessToken(accessToken);
//            memberRepository.save(member);

            return accessToken;
        } catch(HttpClientErrorException e) {
            throw new IllegalArgumentException("잘못된 요청입니다." + e.getResponseBodyAsString());
        } catch(HttpServerErrorException e) {
            throw new RuntimeException("카카오 서버에 문제가 발생하였습니다." + e.getResponseBodyAsString());
        }
    }

    private String getKakaoEmail(String accessToken) {
        var url = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                url, HttpMethod.GET, request, Map.class
        );

        Map<String, Object> kakaoAccount = (Map<String, Object>) response.getBody().get("kakao_account");

        if (kakaoAccount == null || !Boolean.TRUE.equals(kakaoAccount.get("has_email"))) {
            throw new RuntimeException("카카오 계정에 이메일 정보가 없습니다.");
        }

        return (String) kakaoAccount.get("email");
    }

}
