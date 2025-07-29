package gift.service;

import gift.dto.KakaoTokenDto;
import gift.dto.KakaoUserInfoDto;
import gift.dto.UserInfoDto;
import gift.entity.KakaoToken;
import gift.entity.User;
import gift.exception.NotFoundException;
import gift.infrastructure.KakaoAuthClient;
import gift.repository.KakaoTokenRepository;
import gift.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class KakaoAuthService {
    private final KakaoAuthClient kakaoAuthClient;
    private final KakaoTokenRepository kakaoTokenRepository;
    private final UserRepository userRepository;

    public KakaoAuthService(KakaoAuthClient kakaoAuthClient,
                            KakaoTokenRepository kakaoTokenRepository,
                            UserRepository userRepository) {
        this.kakaoAuthClient = kakaoAuthClient;
        this.kakaoTokenRepository = kakaoTokenRepository;
        this.userRepository = userRepository;
    }

    public KakaoTokenDto accessKakaoToken(UserInfoDto userInfoDto, String code) {
        KakaoTokenDto kakaoTokenDto = kakaoAuthClient.getKakaoToken(code);
        LocalDateTime expiresDate = LocalDateTime.now().plusSeconds(kakaoTokenDto.expiresIn());
        LocalDateTime refreshTokenExpiresDate = LocalDateTime.now().plusSeconds(kakaoTokenDto.refreshTokenExpiresIn());

        KakaoToken kakaoToken = new KakaoToken(kakaoTokenDto.tokenType(),
                kakaoTokenDto.accessToken(),
                kakaoTokenDto.idToken(),
                expiresDate,
                kakaoTokenDto.refreshToken(),
                refreshTokenExpiresDate,
                kakaoTokenDto.scope());

        kakaoToken = kakaoTokenRepository.save(kakaoToken);

        User user = userRepository.findById(userInfoDto.id()).orElseThrow(() -> new NotFoundException("User", userInfoDto.id()));
        user.updateKakaoToken(kakaoToken);

        return kakaoTokenDto;
    }

    public KakaoUserInfoDto getKakaoUserInfo(KakaoTokenDto token) {
        return kakaoAuthClient.getKakaoUserInfo(token);
    }
}
