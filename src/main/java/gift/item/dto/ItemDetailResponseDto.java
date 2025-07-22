package gift.item.dto;

import java.util.List;

public record ItemDetailResponseDto(
    Long id,
    String name,
    Integer price,
    String imageUrl,
    List<OptionResponseDto> options
) {

}
