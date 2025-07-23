package gift.service.wishListService;

import gift.dto.wishListDto.CreateWishItemRequestDto;
import gift.dto.wishListDto.UpdateWishItemDto;
import gift.entity.WishItem;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WishListService {

    WishItem addWishItem(@Valid CreateWishItemRequestDto createWishItemRequestDto, String userEmail);

    Page<WishItem> getItemList(String name, Integer price, String userEmail, Pageable pageable);

    WishItem deleteWishItem(String name, String userEmail);

    WishItem updateWishItem(UpdateWishItemDto updateWishItemDto, String userEmail);

    WishItem controlWishItemQuantity(String itemName, String userEmail, Integer quantity);
}
