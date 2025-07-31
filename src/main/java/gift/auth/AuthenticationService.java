package gift.auth;

import gift.dto.KakaoTokenRefreshResponse;
import gift.entity.Member;
import gift.repository.MemberRepository;
import gift.service.KakaoOAuthService;
import gift.util.JwtUtil;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationService {
    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;
    private final KakaoOAuthService kakaoOAuthService;

    public AuthenticationService(JwtUtil jwtUtil, MemberRepository memberRepository, KakaoOAuthService kakaoOAuthService) {
        this.jwtUtil = jwtUtil;
        this.memberRepository = memberRepository;
        this.kakaoOAuthService = kakaoOAuthService;
    }

    public AuthenticationResult authenticate(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return AuthenticationResult.failure("Authorization header is missing or invalid");
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.isTokenValid(token)) {
            Long memberId = jwtUtil.extractMemberId(token);
            Member member = memberRepository.findById(memberId).orElseThrow();

            if(kakaoOAuthService.isAccessTokenValid(member.getKakaoAccessToken())){
                String newJwtToken = jwtUtil.generateToken(
                        member.getEmail().getValue(),
                        member.getId(),
                        member.getRole().name()
                );
                return AuthenticationResult.success(
                        member.getId(),
                        member.getEmail().getValue(),
                        member.getRole().name(),
                        newJwtToken
                );
            }

            try {
                KakaoTokenRefreshResponse newAccessTokens = kakaoOAuthService.refreshKakaoAccessToken(
                            member.getKakaoRefreshToken(),
                            member.getKakaoId()
                    );
                member.setKakaoAccessToken(newAccessTokens.accessToken());
                String newJwtToken = jwtUtil.generateToken(
                        member.getEmail().getValue(),
                        member.getId(),
                        member.getRole().name()
                );
                return AuthenticationResult.success(
                        member.getId(),
                        member.getEmail().getValue(),
                        member.getRole().name(),
                        newJwtToken
                );
            } catch (Exception e) {
                return AuthenticationResult.failure("Refresh token expired or invalid");
            }
        }
        return AuthenticationResult.success(
                jwtUtil.extractMemberId(token),
                jwtUtil.extractEmail(token),
                jwtUtil.extractRole(token)
        );
    }
}
