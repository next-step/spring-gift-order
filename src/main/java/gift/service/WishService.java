package gift.service;

import gift.dto.WishResponseDto;
import gift.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WishService {
    void addWish(Long memberId, Long productId);
    Page<WishResponseDto> getWishList(Member member, Pageable pageable);
    void removeWish(Member member, Long productId);
}
