package gift.product.dto;


import jakarta.validation.constraints.NotBlank;


public record LoginResponse(

	@NotBlank
	String token
) {
}
