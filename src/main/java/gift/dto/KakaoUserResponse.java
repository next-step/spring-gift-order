package gift.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoUserResponse(
        Long id,
        @JsonProperty("connected_at") String connectedAt,
        KakaoAccount kakaoAccount
) {
    public record KakaoAccount(
            @JsonProperty("profile_nickname_needs_agreement") boolean nicknameNeedsAgreement,
            Profile profile,
            @JsonProperty("has_email") boolean hasEmail,
            @JsonProperty("email_needs_agreement") boolean emailNeedsAgreement,
            String email
    ) {}

    public record Profile(
            String nickname
    ) {}
}

