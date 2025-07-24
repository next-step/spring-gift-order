package gift.oauth.service;

import gift.api.member.domain.Member;
import gift.api.member.domain.MemberRole;
import gift.api.member.repository.MemberRepository;
import gift.oauth.dto.KakaoTokenResponseDto;
import gift.oauth.dto.KakaoUserInfoResponseDto;
import gift.util.JwtUtil;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Transactional(readOnly = true)
public class KakaoService {

    private final MemberRepository memberRepository;
    private final WebClient webClient;
    private final JwtUtil jwtUtil;

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.client-secret}")
    private String clientSecret;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    public KakaoService(MemberRepository memberRepository, WebClient.Builder webClientBuilder,
            JwtUtil jwtUtil) {
        this.memberRepository = memberRepository;
        this.webClient = webClientBuilder.build();
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public String login(String code) {
        String accessToken = getAccessToken(code);
        KakaoUserInfoResponseDto userInfo = getUserInfo(accessToken);
        Member member = registerOrLoginUser(userInfo);

        return jwtUtil.createToken(member.getEmail(), member.getRole());
    }

    private String getAccessToken(String code) {
        String uri = "https://kauth.kakao.com/oauth/token";

        KakaoTokenResponseDto kakaoTokenResponseDto = webClient.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue("grant_type=authorization_code&client_id=" + clientId +
                        "&redirect_uri=" + redirectUri + "&code=" + code +
                        "&client_secret=" + clientSecret)
                .retrieve()
                .bodyToMono(KakaoTokenResponseDto.class)
                .block();

        if (kakaoTokenResponseDto == null) {
            throw new RuntimeException("카카오 토큰을 발급받는데 실패했습니다.");
        }

        return kakaoTokenResponseDto.accessToken();
    }

    private KakaoUserInfoResponseDto getUserInfo(String accessToken) {
        String uri = "https://kapi.kakao.com/v2/user/me";

        KakaoUserInfoResponseDto userInfo = webClient.get()
                .uri(uri)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(KakaoUserInfoResponseDto.class)
                .block();

        if (userInfo == null) {
            throw new RuntimeException("카카오 사용자 정보를 가져오는데 실패했습니다.");
        }

        return userInfo;
    }

    private Member registerOrLoginUser(KakaoUserInfoResponseDto userInfo) {
        String nickname = userInfo.kakaoAccount().profile().nickname();
        String email = nickname + "@kakao";

        return memberRepository.findByEmail(email)
                .orElseGet(() -> {
                    String password = BCrypt.hashpw("password", BCrypt.gensalt());

                    Member newMember = new Member(
                            email,
                            password,
                            MemberRole.USER
                    );

                    return memberRepository.save(newMember);
                });
    }
}
