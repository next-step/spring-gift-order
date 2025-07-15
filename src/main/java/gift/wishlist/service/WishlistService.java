package gift.wishlist.service;

import gift.wishlist.dto.WishlistItemRequestDto;
import gift.wishlist.dto.WishlistItemResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface WishlistService {
    void addWishlistItem(Long memberId, WishlistItemRequestDto requestDto);

    void deleteWishlistItemById(Long itemId);

    List<WishlistItemResponseDto> findAllWishlistItemsByMemberId(Long memberId);

    Page<WishlistItemResponseDto> findAllWishlistItemsByMemberIdWithPageable(Long memberId, Pageable pageable);

    void updateWishlistItemById(Long itemId, Long quantity);
}
