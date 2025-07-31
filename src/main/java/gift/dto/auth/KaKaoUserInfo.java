package gift.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KaKaoUserInfo(
    Long id,
    @JsonProperty("kakao_account")
    KaKaoAccount kakaoAccount
) {

    public record KaKaoAccount(
        String email,
        Profile profile
    ) {

    }

    public record Profile(
        String nickname,
        @JsonProperty("profile_image_url")
        String profileImageUrl
    ) {

    }
}
