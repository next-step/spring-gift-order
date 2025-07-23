package gift.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateOptionRequest (
    @NotNull
    @Size(max = 50, message = "공백 포함 최대 50자까지만 입력 가능합니다.")
    @Pattern(regexp = "^[a-zA-Z0-9가-힣 ()\\[\\]+\\-&/_]*$", message = "특수문자는 (), [], +, -, &, /, _ 만 가능합니다.")
    String name,

    @NotNull
    int quantity,

    Long productId
) {}
