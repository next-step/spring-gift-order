package gift.wishlist.repository;

import gift.wishlist.Wishlist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    List<Wishlist> findAllByMemberId(Long memberId);
    Optional<Wishlist> findByProductIdAndMemberId(Long productId, Long memberId);

    Page<Wishlist> findAllByMemberId(Long memberId, Pageable pageable);

}
