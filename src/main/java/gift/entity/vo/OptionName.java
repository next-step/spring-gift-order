package gift.entity.vo;

import gift.exception.InvalidOptionCreateException;
import jakarta.persistence.Embeddable;

@Embeddable
public class OptionName {

    private String value;

    protected OptionName() {
    }

    public OptionName(String value) {
        check(value);
        this.value = value;
    }

    private void check(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidOptionCreateException("옵션 이름은 필수입니다.");
        }
        if (value.length() > 50) {
            throw new InvalidOptionCreateException("옵션 이름은 공백을 포함하여 최대 50자까지 입력할 수 있습니다.");
        }
        if (!value.matches("^[a-zA-Z0-9가-힣ㄱ-ㆎ\\s()\\[\\]+&/_-]+$")) {
            throw new InvalidOptionCreateException("특수 문자 ( ), [ ], +, -, &, /, _ 외는 사용 불가합니다.");
        }

    }

    public String value() {
        return value;
    }
}
