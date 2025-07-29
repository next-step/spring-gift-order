package gift.dto;

import gift.entity.Wish;

public record WishResponseDto(Long id, Long productOptionId, Long quantity) {
    public WishResponseDto(Wish wish) {
        this(wish.getId(), wish.getProductOption().getId(), wish.getQuantity());
    }
}
