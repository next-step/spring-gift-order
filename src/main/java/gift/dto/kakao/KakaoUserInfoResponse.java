package gift.dto.kakao;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record KakaoUserInfoResponse(
        Long id,
        String connectedAt,
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

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record Profile(
            String nickname,
            String thumbnailImageUrl,
            String profileImageUrl
    ) {

    }
}
