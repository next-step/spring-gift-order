package gift.wish.dto;

import gift.product.dto.ProductResponseDto;
import gift.product.entity.Product;
import gift.wish.entity.Wish;
import org.springframework.stereotype.Component;

@Component
public class WishMapper {
    public WishResponse toWishResponse(Wish wish) {
        if (wish == null) {
            return null;
        }

        return new WishResponse(
            wish.getId(),
            wish.getOption().getId(),
            toProductDto(wish.getProduct()),
            wish.getOption().getName(),
            wish.getQuantity()
        );
    }

    private ProductResponseDto toProductDto(Product product) {
        if (product == null) {
            return null;
        }

        return new ProductResponseDto(
            product.getId(),
            product.getName(),
            product.getPrice(),
            product.getImageUrl()
        );
    }
}
