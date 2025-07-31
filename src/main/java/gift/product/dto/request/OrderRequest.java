package gift.product.dto.request;

public record OrderRequest(
        Long optionId,
        Integer quantity,
        String message
) {
}
