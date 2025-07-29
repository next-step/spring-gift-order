package gift.dto.order;

public record OrderRequest(
    Long memberId,
    Long optionId,
    int quantity,
    String message
) {}
