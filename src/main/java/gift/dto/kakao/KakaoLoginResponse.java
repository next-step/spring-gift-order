package gift.dto.kakao;

import gift.dto.jwt.JwtTokenResponse;

public record KakaoLoginResponse(String kakaoAccessToken, String jwtAccessToken) {

    public static KakaoLoginResponse of(KakaoTokenResponse kakaoTokenResponse, JwtTokenResponse jwtTokenResponse) {
        return new KakaoLoginResponse(kakaoTokenResponse.accessToken(), jwtTokenResponse.accessToken());
    }
}
