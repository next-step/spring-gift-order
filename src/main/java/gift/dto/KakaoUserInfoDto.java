package gift.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoUserInfoDto(Long id, @JsonProperty("kakao_account") KakaoAccount kakaoAccount) {
    public String getEmail() {
        return kakaoAccount.email();
    }

    public record KakaoAccount(String email) {

    }
}