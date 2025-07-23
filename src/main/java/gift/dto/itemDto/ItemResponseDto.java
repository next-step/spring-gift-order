package gift.dto.itemDto;

import gift.entity.Item;

import java.util.ArrayList;
import java.util.List;


public record ItemResponseDto(Long id, String name, Integer price, String imageUrl) {

    public static ItemResponseDto from(Item item) {
        return new ItemResponseDto(item.getId(), item.getName(), item.getPrice(), item.getImageUrl());
    }

    public static List<ItemResponseDto> from(List<Item> items) {
        List<ItemResponseDto> result = new ArrayList<>();
        for (Item item : items) {
            result.add(from(item));
        }
        return result;
    }
}
