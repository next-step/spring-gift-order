package gift.dto;

import java.util.List;

public class WishListResponseDto {
    private List<WishResponseDto> wishes;
    private int totalCount;

    public WishListResponseDto(List<WishResponseDto> wishes, int totalCount) {
        this.wishes = wishes;
        this.totalCount = totalCount;
    }

    public List<WishResponseDto> getWishes() { return wishes; }
    public int getTotalCount() { return totalCount; }
}