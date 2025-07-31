package gift.dto.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record OrderRequest(

    @NotNull(message = "옵션 ID는 필수입니다.")
    Long optionId,

    @Min(value = 1, message = "수량은 1 이상이어야 합니다.")
    int quantity,

    @NotBlank(message = "메시지는 필수입니다.")
    @Size(max = 500, message = "메시지는 500자 이내로 입력해 주세요.")
    String message
) {

}
