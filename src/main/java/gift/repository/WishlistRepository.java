package gift.repository;

import gift.domain.Wishlist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    Optional<Wishlist> findByProductId(Long productId);

    Page<Wishlist> findAllByUserId(Long userId, Pageable pageable);
}
