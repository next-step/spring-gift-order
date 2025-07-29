package gift.dto;

public record WishRequestDto(Long id, Long productOptionId, Long quantity) {
    public WishRequestDto(Long productOptionId, Long quantity) {
        this(null, productOptionId, quantity);
    }
}
