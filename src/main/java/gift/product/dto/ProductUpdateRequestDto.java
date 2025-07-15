package gift.product.dto;

import gift.product.ValidationPatterns;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ProductUpdateRequestDto(
        Long id,
        @NotBlank(message = "상품명은 필수로 입력해야합니다.")
        @Size(max = 15, message = "상품명은 15자 이내로 입력해야합니다.")
        @Pattern(
                regexp = ValidationPatterns.ALLOWED_NAME_PATTERN,
                message = "상품명에 허용되지 않는 특수 문자가 포함되어 있습니다."
        )
        @Pattern(
                regexp = ValidationPatterns.KAKAO_PATTERN,
                message = "\"카카오\"가 포함된 상품명은 MD 협의 후 사용할 수 있습니다."
        )
        String name,
        Long price,
        String url,
        List<ProductOptionAddRequestDto> options
) {
    public ProductUpdateRequestDto() {
        this(null, null, null, null, null);
    }
}
