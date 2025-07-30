package gift.dto;

public record KakaoOrderRequestDto(Long optionId,
                                   Long quantity,
                                   String message) { }
