package gift.dto.product;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.ArrayList;
import java.util.List;

public record CreateProductRequest(
        @NotBlank(message = "이름은 필수 입력 값입니다.")
        @Size(max = 15, message = "상품은 공백을 포함하여 15자까지 입력할 수 있습니다.")
        @Pattern(regexp = "^[a-zA-Z0-9ㄱ-ㅎㅏ-ㅣ가-힣()\\[\\]+\\-&/_\\s]*$", message = "특수문자는 ( ), [ ], +, -, &, /, _ 만 허용됩니다.")
        String name,

        @NotBlank
        String imageUrl,

        @NotEmpty(message = "옵션은 1개 이상 필요합니다.")
        @Valid
        List<CreateProductOptionRequest> options
) {

    private static final CreateProductRequest EMPTY = new CreateProductRequest(null, null, new ArrayList<>());

    public static CreateProductRequest empty() {
        return EMPTY;
    }
}
