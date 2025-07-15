package gift.member;

import com.fasterxml.jackson.annotation.JsonCreator;
import gift.member.exception.InvalidMemberException;

public enum Role {
    ADMIN,
    USER;

    @JsonCreator
    public static Role from(String value) {
        for (Role role : values()) {
            if (role.name().equalsIgnoreCase(value)) {
                return role;
            }
        }
        throw new InvalidMemberException("잘못된 등급입니다.","roleError");
    }

}
