package gift.dto;

import java.util.Map;

public record AuthUser(
    Long providerId,
    String email,
    String nickname,
    String profileImage
) {

    public static AuthUser fromKakao(Map<String, Object> kakaoResponse) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) kakaoResponse.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        Long providerId = ((Number) kakaoResponse.get("id")).longValue();
        String email = (String) kakaoAccount.get("email");
        String nickname = (String) profile.get("nickname");
        String profileImage = (String) profile.get("profile_image_url");

        return new AuthUser(providerId, email, nickname, profileImage);
    }
}
