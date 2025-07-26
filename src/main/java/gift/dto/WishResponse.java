package gift.dto;

import gift.domain.Wish;
import java.util.List;

public record WishResponse(
        Long id,
        String name,
        Integer price,
        String imageUrl) {

    public static WishResponse from(Wish wish) {
        return new WishResponse(
                wish.getId(),
                wish.getProduct().name(),
                wish.getProduct().price(),
                wish.getProduct().imageUrl()
        );
    }

    public static List<WishResponse> fromList(List<Wish> wishes) {
        return wishes.stream()
                .map(WishResponse::from)
                .toList();
    }
}
