package gift.dto;

public record WishRequestDto(Long id, Long productId, Long quantity) {
    public WishRequestDto(Long productId, Long quantity) {
        this(null, productId, quantity);
    }
}
