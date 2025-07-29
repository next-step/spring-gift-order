package gift.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record OrderRequest(
        @NotNull(message = "옵션 ID는 필수입니다.")
        Long optionId,

        @Min(value = 1, message = "주문 수량은 1개 이상이어야 합니다.")
        int quantity,

        @NotBlank(message = "메시지는 비워둘 수 없습니다.")
        @Size(max = 100, message = "메시지는 최대 100자까지 입력할 수 있습니다.")
        String message
) {

}