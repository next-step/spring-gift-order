package gift.dto;

import gift.validator.NoKakao;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;

public record ProductRequest(
        Long id,

        @NotBlank(message = "상품 이름은 필수입니다.")
        @Size(max = 15, message = "상품 이름은 공백 포함 최대 15자까지 입력할 수 있습니다.")
        @Pattern(
                regexp = "^[a-zA-Z0-9가-힣\\s()\\[\\]+\\-&/_]*$",
                message = "상품 이름에 허용되지 않는 특수문자가 포함되어 있습니다."
        )
        @NoKakao
        String name,
        int price,
        String imageUrl,

        @NotEmpty(message = "상품에는 최소 1개 이상의 옵션이 필요합니다.")
        @Valid
        List<OptionRequest> options) {

}
