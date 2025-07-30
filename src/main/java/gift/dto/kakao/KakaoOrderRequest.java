package gift.dto.kakao;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record KakaoOrderRequest(

        @NotNull(message = "옵션 아이디는 필수 입력 값입니다.")
        Long optionId,

        @NotNull(message = "상품 수량은 필수 입력 값입니다.")
        @Min(0)
        Integer quantity,

        @NotBlank(message = "전송 메시지는 필수 입력 값입니다.")
        String message

) {

}
