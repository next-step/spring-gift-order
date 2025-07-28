package gift.shared.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import gift.shared.exception.user.NoUserException;

import static gift.shared.token.status.TokenStatus.*;

public class KakaoBasicInfoResponse {
    private Long id;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    // 해당 부분은 protected 로 작성 안해도 될까요?
    // 이렇게 작성한 이유는 Domain 이라면 protected 를 사용했지만,
    // DTO 로 사용해서 따로 protected 를 설정하지 않았습니다.
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

    public String makeEmailById(){
        return this.id + "@kakao.com";
    }
}
