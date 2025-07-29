package gift.service;

import gift.dto.PageResponse;
import gift.dto.Pagination;
import gift.dto.WishRequest;
import gift.dto.WishResponse;
import gift.entity.Member;
import gift.entity.ProductOption;

public interface WishService {

    WishResponse addWish(Long memberId, WishRequest request);

    void deleteWish(Long memberId, Long productId);

    void deleteWishIfExists(Member member, ProductOption option);

    PageResponse<WishResponse> getWishes(Long memberId, Pagination pagination);
}