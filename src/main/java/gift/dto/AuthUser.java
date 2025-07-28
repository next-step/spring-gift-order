package gift.dto;

public record AuthUser(
    Long providerId,
    String email,
    String nickname,
    String profileImage
) {

    public static AuthUser fromKakao(KaKaoUserInfo kaKaoUserInfo) {
        Long providerId = kaKaoUserInfo.id();
        String email = kaKaoUserInfo.kakaoAccount().email();
        String nickname = kaKaoUserInfo.kakaoAccount().profile().nickname();
        String profileImage = kaKaoUserInfo.kakaoAccount().profile().profileImageUrl();

        return new AuthUser(providerId, email, nickname, profileImage);
    }
}
