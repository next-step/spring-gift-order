package gift.wish.service;

import gift.member.entity.Member;
import gift.wish.dto.CreateWishRequest;
import gift.wish.dto.CreateWishResponse;
import gift.wish.dto.WishResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WishService {
    CreateWishResponse create(Member member, CreateWishRequest request);

    List<WishResponse> findAllWishes(Long memberId);

    Page<WishResponse> findAll(Long memberId, Pageable pageable);

    WishResponse findWish(Long memberId, Long id);

    void deleteWish(Long wishId, Long memberId);
}
