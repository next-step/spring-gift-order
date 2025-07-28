package gift.service;

import gift.dto.KakaoTokenDto;
import gift.dto.KakaoUserInfoDto;
import gift.infrastructure.KakaoAuthClient;
import org.springframework.stereotype.Service;

@Service
public class KakaoAuthService {
    private final KakaoAuthClient kakaoAuthClient;

    public KakaoAuthService(KakaoAuthClient kakaoAuthClient) {
        this.kakaoAuthClient = kakaoAuthClient;
    }

    public KakaoTokenDto getKakaoToken(String code) {
        return kakaoAuthClient.getKakaoToken(code);
    }

    public KakaoUserInfoDto getKakaoUserInfo(KakaoTokenDto token) {
        return kakaoAuthClient.getKakaoUserInfo(token);
    }
}
