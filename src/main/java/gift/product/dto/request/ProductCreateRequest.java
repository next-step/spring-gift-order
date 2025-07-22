package gift.product.dto.request;

import gift.product.entity.Product;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProductCreateRequest(
        Long giftId,
        @Pattern(
                regexp = "^[a-zA-Z0-9가-힣 ()\\[\\]+\\-&/_]+$",
                message = "특수 문자는 ( ), [ ], +, -, &, /, _ 만 가능합니다."
        )
        @Size(min = 1, max = 15, message = "상품 명은 공백포함 15자 이하여야 합니다.")
        String giftName,
        Integer giftPrice,
        String giftPhotoUrl
) {
}
