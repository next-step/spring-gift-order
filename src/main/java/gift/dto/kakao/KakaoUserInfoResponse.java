package gift.dto.kakao;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.Optional;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record KakaoUserInfoResponse(
        Long id,
        String connectedAt,
        KakaoAccount kakaoAccount
) {

    public Optional<String> getEmail() {
        return Optional.ofNullable(kakaoAccount)
                .map(KakaoAccount::email);
    }

    public Optional<String> getNickname() {
        return Optional.ofNullable(kakaoAccount)
                .map(KakaoAccount::profile)
                .map(Profile::nickname);
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
