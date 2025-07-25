package gift.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoUserInfoResponse {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    public Long getId() { return id; }
    public String getNickname() {
        return (kakaoAccount != null && kakaoAccount.profile != null) ? kakaoAccount.profile.nickname : null;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class KakaoAccount {
        @JsonProperty("profile")
        private Profile profile;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Profile {
        @JsonProperty("nickname")
        private String nickname;
    }
}