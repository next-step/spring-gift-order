package gift.dto.wishListDto;


import gift.entity.WishItem;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

public record ResponseWishItem(List<ResponseWishItemDto> items, int currentPage, int totalPages, long totalElements,
                               boolean isLast) {
    public static ResponseWishItem from(Page<WishItem> page) {
        List<ResponseWishItemDto> dtoList = new ArrayList<>();
        for (WishItem wishItem : page) {
            dtoList.add(ResponseWishItemDto.from(wishItem));
        }
        return new ResponseWishItem(dtoList, page.getNumber(), page.getTotalPages(), page.getTotalElements(), page.isLast());
    }
}