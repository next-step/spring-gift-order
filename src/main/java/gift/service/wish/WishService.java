package gift.service.wish;

import gift.dto.pagination.PageResponse;
import gift.dto.pagination.Pagination;
import gift.dto.wish.WishRequest;
import gift.dto.wish.WishResponse;
import gift.entity.member.Member;
import gift.entity.product.option.ProductOption;

public interface WishService {

    WishResponse addWish(Member member, WishRequest request);

    void deleteWish(Member member, Long productId);

    void deleteWishIfExists(Member member, ProductOption option);

    PageResponse<WishResponse> getWishes(Member member, Pagination pagination);
}