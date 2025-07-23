package gift.dto;

public record WishResponseDTO (
    Long memberId,
    ProductResponseDTO product,
    int quantity
) { }
