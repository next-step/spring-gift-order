package gift.service.Wish;

import gift.dto.Pagination.PageResponse;
import gift.dto.Pagination.Pagination;
import gift.dto.Wish.WishRequest;
import gift.dto.Wish.WishResponse;
import gift.entity.Member;
import gift.entity.ProductOption;

public interface WishService {

    WishResponse addWish(Member member, WishRequest request);

    void deleteWish(Member member, Long productId);

    void deleteWishIfExists(Member member, ProductOption option);

    PageResponse<WishResponse> getWishes(Member member, Pagination pagination);
}