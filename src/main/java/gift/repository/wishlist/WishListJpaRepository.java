package gift.repository.wishlist;

import gift.domain.WishList;
import gift.repository.BaseRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WishListJpaRepository extends BaseRepository<WishList, Long> {

    List<WishList> findAllByMemberId(Long memberId);

    Optional<WishList> findByMemberIdAndProductId(Long memberId, Long productId);

    Page<WishList> findAllPageByMemberId(Long memberId, Pageable pageable);
}
