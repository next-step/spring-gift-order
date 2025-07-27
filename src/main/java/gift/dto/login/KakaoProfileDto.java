package gift.dto.login;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoProfileDto(
    @JsonProperty long id,
    @JsonProperty(value = "kakao_account") KakaoAccountDto kakaoAccountDto
) {
    public record KakaoAccountDto(
        @JsonProperty String email
    ) {}
}
