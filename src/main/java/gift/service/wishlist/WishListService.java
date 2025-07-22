package gift.service.wishlist;

import gift.dto.product.ProductResponseDto;
import gift.dto.wishlist.WishListResponseDto;
import org.springframework.data.domain.Page;

public interface WishListService {

    WishListResponseDto create(Long productId, Long memberId);

    Page<ProductResponseDto> findAll(Long memberId, int page, int size);

    void delete(Long productId, Long memberId);
}