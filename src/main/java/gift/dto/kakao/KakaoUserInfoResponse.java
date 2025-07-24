package gift.dto.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoUserInfoResponse(
        Long id,

        @JsonProperty("connected_at")
        String connectedAt,

        @JsonProperty("kakao_account")
        KakaoAccount kakaoAccount
) {

    public String getEmail() {
        if (this.kakaoAccount == null) {
            return null;
        }
        return this.kakaoAccount.email();
    }

    public String getNickname() {
        if (this.kakaoAccount == null || this.kakaoAccount.profile() == null) {
            return null;
        }
        return this.kakaoAccount.profile().nickname();
    }

    public record KakaoAccount(
            String email,
            Profile profile
    ) {

    }

    public record Profile(
            String nickname,

            @JsonProperty("thumbnail_image_url")
            String thumbnailImageUrl,

            @JsonProperty("profile_image_url")
            String profileImageUrl
    ) {

    }
}
