package gift.product.dto;


import jakarta.validation.constraints.*;


public record CreateOptionRequest(
	@NotBlank
	@Size(max = 50)
	@Pattern(
		regexp = "^[가-힣a-zA-Z0-9\\s()\\[\\]+\\-&/_]*$",
		message = "허용되지 않는 특수문자가 포함됐습니다."
	)
	String optionName,

	@NotNull
	@Min(1)
	@Max(99999999)
	int quantity
) {
}
