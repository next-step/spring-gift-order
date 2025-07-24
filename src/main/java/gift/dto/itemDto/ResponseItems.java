package gift.dto.itemDto;

import gift.entity.Item;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

public record ResponseItems(List<ItemResponseDto> items, int currentPage, int totalPages, long totalElements,
                            boolean isLast) {
    public static ResponseItems from(Page<Item> page) {
        List<ItemResponseDto> dtoList = new ArrayList<>();
        for (Item item : page.getContent()) {
            dtoList.add(ItemResponseDto.from(item));
        }

        return new ResponseItems(dtoList, page.getNumber(), page.getTotalPages(), page.getTotalElements(), page.isLast());
    }
}