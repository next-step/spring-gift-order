package gift.item.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.regex.Pattern;

@Embeddable
public class OptionName {

    private static final Pattern PERMITTED_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9가-힣 ()\\[\\]+\\-&/_]*$");

    @Column(name = "name", nullable = false, length = 50)
    private String optionName;

    protected OptionName() {
    }

    public OptionName(String optionName) {
        if (optionName == null) {
            throw new IllegalArgumentException("optionName은 null일 수 없습니다.");
        }

        if (optionName.length() > 50) {
            throw new IllegalArgumentException("옵션명은 공백포함 최대 50자까지 입력할 수 있습니다.");
        }

        if (!PERMITTED_PATTERN.matcher(optionName).matches()) {
            throw new IllegalArgumentException("옵션명에 허용되지 않는 문자가 포함되어 있습니다.");
        }

        this.optionName = optionName;
    }

    public String toValue() {
        return optionName;
    }
}
