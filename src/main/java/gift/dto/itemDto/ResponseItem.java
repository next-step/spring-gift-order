package gift.dto.itemDto;

import gift.entity.Item;

public record ResponseItem(ItemResponseDto item) {
    public static ResponseItem from(Item item) {
        return new ResponseItem(ItemResponseDto.from(item));
    }
}