package gift.authorization.oauth;

import gift.authorization.oauth.dto.KakaoTokenResponseDto;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class KakaoTokenService {

    private final KakaoTokenRepository tokenRepository;
    private final KakaoClient kakaoClient;
    private final KakaoOAuthProperties kakaoProps;

    public KakaoTokenService(KakaoTokenRepository tokenRepository, KakaoClient kakaoClient, KakaoOAuthProperties kakaoProps) {
        this.tokenRepository = tokenRepository;
        this.kakaoClient = kakaoClient;
        this.kakaoProps = kakaoProps;
    }

    public String getValidAccessToken(String clientId) {
        KakaoToken token = tokenRepository.findById(clientId)
                .orElseThrow(() -> new IllegalStateException("해당 유저의 토큰 정보가 없습니다."));

        if (token.isExpired()) {
            KakaoTokenResponseDto newToken = kakaoClient.refreshAccessToken(
                    kakaoProps.getTokenUri(),
                    kakaoProps.getClientId(),
                    token.getRefreshToken()
            );

            LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(newToken.expiresIn());
            token.update(newToken.accessToken(), newToken.refreshToken(), expiresAt);
            tokenRepository.save(token);
        }

        return token.getAccessToken();
    }

    public void addToken(String clientId, KakaoTokenResponseDto dto) {
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(dto.expiresIn());
        KakaoToken token = new KakaoToken(clientId, dto.accessToken(), dto.refreshToken(), expiresAt);
        tokenRepository.save(token);
    }
}
