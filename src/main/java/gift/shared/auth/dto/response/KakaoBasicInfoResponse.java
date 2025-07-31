package gift.shared.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import gift.shared.exception.user.NoUserException;

import static gift.shared.token.status.TokenStatus.*;

public class KakaoBasicInfoResponse {
    private Long id;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    public KakaoBasicInfoResponse() {
    }

    public String getKakaoAccountEmail() {
        if(kakaoAccount == null || kakaoAccount.getEmail() == null) {
            throw new NoUserException(NO_KAKAO_TOKEN.getMessage());
        }
        return kakaoAccount.getEmail();
    }

    public static class KakaoAccount{
        private String email;

        public KakaoAccount() {}

        public KakaoAccount(String email) {
            this.email = email;
        }

        public String getEmail() { return email; }
    }

    public Long getId() {
        return id;
    }
}
