package gift.dto;
import jakarta.validation.constraints.*;

public record ProductRequestDto(
        @NotNull(message = "이름은 필수입니다.")
        @Size(max=15, message = "상품 이름은 최대 15자까지 입력 가능합니다.")
        @Pattern(regexp = "^[a-zA-Z0-9가-힣 ()\\[\\]+\\-&/_]*$", message = "상품 이름의 특수문자는 ( ), [ ], +, -, &, /, _ 이외에는 허용되지 않습니다.")
        String name,
        @NotNull(message = "가격은 필수입니다.") Long price,
        @NotNull(message = "이미지링크는 필수입니다.") String imageUrl
) {}