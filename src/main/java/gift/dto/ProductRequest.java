package gift.dto;

import gift.exception.BusinessException;
import gift.exception.ErrorCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigInteger;

public record ProductRequest(

        @Size(max = 15, message = "상품 이름은 공백 포함 15자입니다.")
        @Pattern(
                regexp = "^[가-힣a-zA-Z0-9 ()\\[\\]+\\-\\&/_]*$",
                message = "상품 이름에는 한글, 영어, 숫자, 공백, (), [], +, -, &, /, _ 만 사용할 수 있습니다."
        )
        @Pattern(regexp = "^(?!.*카카오).*$", message = "상품 이름에 '카카오'가 포함되어 있습니다. 담당 MD와 협의가 필요합니다.")
        String name,
        @NotBlank(message = "가격은 필수입니다.")
        @Pattern(regexp = "\\d+", message = "가격은 숫자만 가능합니다.")
        String price,
        @NotBlank(message = "이미지 URL은 필수입니다.")
        @Pattern(
                regexp = "^(https?://).+",
                message = "이미지 URL은 http 또는 https로 시작해야 합니다."
        )
        String imageUrl
) {

    public Integer validatedPrice() {
        try {
            var parsedPrice = new BigInteger(price);
            if (parsedPrice.compareTo(BigInteger.ZERO) < 0 ||
                    parsedPrice.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0) {
                throw new BusinessException(ErrorCode.PRICE_OUT_OF_RANGE);
            }
            return parsedPrice.intValue();
        } catch (NumberFormatException e) {
            throw new BusinessException(ErrorCode.INVALID_PRICE_FORMAT);
        }
    }
}
