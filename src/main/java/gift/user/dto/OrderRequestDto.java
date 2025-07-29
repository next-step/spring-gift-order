package gift.user.dto;

public record OrderRequestDto(
        Long productId,
        Long optionId,
        Long quantity,
        String message) {

}
