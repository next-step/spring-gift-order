package gift.dto.auth;

public record AuthUser(
    Long providerId,
    String email,
    String nickname,
    String profileImage,
    String accessToken,
    String refreshToken
) {

    public static AuthUser fromKakao(KaKaoUserInfo userInfo, KaKaoTokenInfo tokenInfo) {
        return new AuthUser(
            userInfo.id(),
            userInfo.kakaoAccount().email(),
            userInfo.kakaoAccount().profile().nickname(),
            userInfo.kakaoAccount().profile().profileImageUrl(),
            tokenInfo.accessToken(),
            tokenInfo.refreshToken()
        );
    }
}
