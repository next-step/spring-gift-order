package gift.wishlist.repository;

import gift.wishlist.WishlistEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WishlistRepository extends JpaRepository<WishlistEntity, Long> {

    List<WishlistEntity> findByMemberIdOrderByCreatedAtDesc(Long memberId);

    Optional<WishlistEntity> findByIdAndMemberId(Long id, Long memberId);

    Page<WishlistEntity> findByMemberId(Long memberId, Pageable pageable);
}