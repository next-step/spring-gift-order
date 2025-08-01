package gift.product.dto;


import java.time.LocalDateTime;


public record SendOrderRequest(
	Long id,
	String itemName,
	String optionName,
	int quantity,
	String message,
	LocalDateTime orderDateTime
) {
}
