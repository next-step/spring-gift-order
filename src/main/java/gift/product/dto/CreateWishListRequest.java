package gift.product.dto;


import jakarta.validation.constraints.NotNull;


public record CreateWishListRequest(
	@NotNull
	Long itemId) {
}
