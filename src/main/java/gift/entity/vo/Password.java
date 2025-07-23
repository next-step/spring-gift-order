package gift.entity.vo;

import gift.exception.InvalidRegisterParameterException;
import jakarta.persistence.Embeddable;

@Embeddable
public class Password {

    private String value;

    protected Password() {
    }

    public Password(String value) {
        check(value);
        this.value = value;
    }

    private void check(String password) {
        if (password == null || password.length() < 8) {
            throw new InvalidRegisterParameterException("비밀번호는 8자 이상이어야 합니다.");
        }
    }

    public boolean matches(Password password) {
        return password.value().equals(this.value);
    }

    public String value() {
        return value;
    }
}
