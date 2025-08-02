package gift.service;

import gift.dto.KakaoLoginResponse;
import gift.entity.Member;
import gift.jwt.JwtTokenProvider;
import gift.repository.MemberRepository;
import java.net.URI;
import java.util.Map;
import java.util.UUID;
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
    private final JwtTokenProvider jwtTokenProvider;

    public KakaoLoginService(RestTemplate restTemplate, MemberRepository memberRepository, JwtTokenProvider jwtTokenProvider) {
        this.restTemplate = restTemplate;
        this.memberRepository = memberRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public String getJwtToken(String code) {
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

            var kakaoId = getKakaoId(accessToken);

            Member member = memberRepository.findByKakaoId(kakaoId)
                            .orElseGet(() -> registerNewKakaoMember(kakaoId));

            member.updateKakaoAccessToken(accessToken);
            memberRepository.save(member);

            return jwtTokenProvider.createToken(member.getEmail());

        } catch(HttpClientErrorException e) {
            throw new IllegalArgumentException("잘못된 요청입니다." + e.getResponseBodyAsString());
        } catch(HttpServerErrorException e) {
            throw new RuntimeException("카카오 서버에 문제가 발생하였습니다." + e.getResponseBodyAsString());
        }
    }

    private Long getKakaoId(String accessToken) {
        var url = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                url, HttpMethod.GET, request, Map.class
        );

        Long id = (Long) response.getBody().get("id");

        if(id == null) {
            throw new RuntimeException("카카오 사용자 Id를 조회하지 못했습니다.");
        }

        return id;
    }

    private Member registerNewKakaoMember(Long kakaoId) {
        String email = kakaoId+ "@kakao.com";
        String password = UUID.randomUUID().toString();

        Member newMember = Member.of(email, password);
        newMember.updateKakaoId(kakaoId);

        return memberRepository.save(newMember);
    }
}
