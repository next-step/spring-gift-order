package gift.product.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;


public record CreateUserRequest(
	@Email(message = "올바르지 않는 이메일 형식입니다.")
	String email,

	@NotBlank
	String password,

	@NotBlank
	String nickName
) {
}
