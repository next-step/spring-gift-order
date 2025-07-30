package gift.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class OrderRequestDTO {

    @NotNull(message = "상품 ID는 필수입니다.")
    private Long productId;

    @NotNull(message = "옵션 ID는 필수입니다.")
    private Long optionId;

    @NotNull(message = "수량은 필수입니다.")
    @Min(value = 1, message = "수량은 최소 1개 이상이어야 합니다.")
    private Integer quantity;

    @Size(max = 500, message = "메시지는 최대 500자까지 입력할 수 있습니다.")
    private String message;

    public OrderRequestDTO() {
    }

    public OrderRequestDTO(Long productId, Long optionId, Integer quantity, String message) {
        this.productId = productId;
        this.optionId = optionId;
        this.quantity = quantity;
        this.message = message;
    }

    public Long getProductId() {
        return productId;
    }

    public Long getOptionId() {
        return optionId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public String getMessage() {
        return message;
    }
}
