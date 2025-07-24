package gift.dto.option;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// 이후 Option 객체 생성 등에 사용할 dto
public record OptionRequest(
    @NotBlank(message = "옵션 이름은 필수입니다.")
    @Size(max = 50, message = "옵션 이름은 최대 50자까지 가능합니다.")
    @Pattern(regexp = "^[a-zA-Z0-9가-힣()\\[\\]+\\-&/_\\s]+$", message="사용가능한 특수문자: (), [], +, -, &, /, _")
    String name,
    Long productId,

    @Min(value = 1, message = "옵션 수량은 최소 1개 이상이어야 합니다.")
    @Max(value = 99999999, message = "옵션 수량은 1억 개 미만이어야 합니다.")
    int quantity
) {}
