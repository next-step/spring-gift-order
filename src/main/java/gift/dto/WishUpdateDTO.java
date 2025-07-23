package gift.dto;

import jakarta.validation.constraints.Min;

public record WishUpdateDTO(
    @Min(value = 0, message = "수량은 0 이상이어야 합니다.")
    int quantity
) { }
