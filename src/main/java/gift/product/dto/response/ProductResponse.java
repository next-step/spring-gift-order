package gift.product.dto.response;

import gift.product.entity.Product;

public record ProductResponse(
        Long id,
        Long giftId,
        String giftName,
        Integer giftPrice,
        String giftPhotoUrl
) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getGiftId(),
                product.getGiftName(),
                product.getGiftPrice(),
                product.getGiftPhotoUrl()
        );
    }
}
