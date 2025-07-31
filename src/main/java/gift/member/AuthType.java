package gift.member;

import com.fasterxml.jackson.annotation.JsonCreator;
import gift.member.exception.InvalidMemberException;

public enum AuthType{
    EMAIL,
    KAKAO;

    @JsonCreator
    public static AuthType from(String value) {
        for (AuthType authtype : values()) {
            if (authtype.name().equalsIgnoreCase(value)) {
                return authtype;
            }
        }
        throw new InvalidMemberException("잘못된 가입 유형입니다.","authTypeError");
    }

}
