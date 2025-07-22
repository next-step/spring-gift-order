package gift.service;

import gift.dto.PageResponse;
import gift.dto.Pagination;
import gift.dto.WishRequest;
import gift.dto.WishResponse;

public interface WishService {

    WishResponse addWish(Long memberId, WishRequest request);

    void deleteWish(Long memberId, Long productId);

    PageResponse<WishResponse> getWishes(Long memberId, Pagination pagination);
}