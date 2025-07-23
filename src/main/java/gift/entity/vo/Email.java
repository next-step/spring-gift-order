package gift.entity.vo;

import gift.exception.InvalidRegisterParameterException;
import jakarta.persistence.Embeddable;

import java.util.Objects;

@Embeddable
public class Email {

    private String value;

    protected Email() {
    }

    public Email(String value) {
        check(value);
        this.value = value;
    }

    private void check(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new InvalidRegisterParameterException("이메일은 필수입니다.");
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new InvalidRegisterParameterException("올바른 이메일 형식이 아닙니다.");
        }
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return Objects.equals(value, email.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
