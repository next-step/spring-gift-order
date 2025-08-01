package gift.service;

import gift.auth.JwtTokenProvider;
import gift.client.KakaoApiClient;
import gift.dto.kakao.KakaoTokenRequest;
import gift.dto.kakao.KakaoUserInfoResponse;
import gift.dto.kakao.LoginResponse;
import gift.entity.Member;
import gift.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class KakaoService {
    private final JwtTokenProvider jwtTokenProvider;
    private final KakaoApiClient kakaoApiClient;
    private final MemberRepository memberRepository;

    @Value("${kakao.client.id}") private String clientId;
    @Value("${kakao.redirect-uri}") private String redirectUri;

    public KakaoService(JwtTokenProvider jwtTokenProvider, KakaoApiClient kakaoApiClient, MemberRepository memberRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.kakaoApiClient = kakaoApiClient;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public LoginResponse processKakaoLogin(String code) {
        KakaoTokenRequest tokenRequest = KakaoTokenRequest.builder()
                .clientId(clientId).redirectUri(redirectUri).code(code).build();

        String accessToken = kakaoApiClient.fetchAccessToken(tokenRequest);
        KakaoUserInfoResponse userInfo = kakaoApiClient.fetchUserInfo(accessToken);

        String userIdentifier = "kakao_" + userInfo.getId();

        Member member = memberRepository.findByEmail(userIdentifier)
                .orElseGet(() -> new Member(userIdentifier, "social_login"));

        member.setAccessToken(accessToken);
        memberRepository.save(member);

        String serviceToken = jwtTokenProvider.createToken(userIdentifier);
        return new LoginResponse(userInfo.getId(), userInfo.getNickname(), serviceToken);
    }
}