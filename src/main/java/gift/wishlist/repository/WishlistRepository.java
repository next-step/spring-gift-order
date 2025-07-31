package gift.wishlist.repository;

import gift.wishlist.entity.Wishlist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long>, PagingAndSortingRepository<Wishlist, Long> {
    @Query("SELECT w FROM Wishlist w WHERE w.user.id=:userId")
    Page<Wishlist> findAllByUserId(@Param("userId") Long userId, Pageable pageable);

    @Modifying
    @Transactional
    @Query("DELETE FROM Wishlist w WHERE w.product.id=:productId")
    void deleteByProductId(@Param("productId") Long productId);
}
