package gift.dto.kakao;

import gift.dto.jwt.JwtTokenResponse;

public record KakaoLoginResponse(String kakaoAccessToken, String jwtAccessToken) {

    public static KakaoLoginResponse of(String kakaoAccessToken, JwtTokenResponse jwtTokenResponse) {
        return new KakaoLoginResponse(kakaoAccessToken, jwtTokenResponse.accessToken());
    }
}
