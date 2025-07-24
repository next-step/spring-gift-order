package gift.repository;

import gift.domain.Wish;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface WishRepository extends JpaRepository<Wish, Long> {
    @Query("SELECT w FROM Wish w LEFT JOIN FETCH w.product WHERE w.member.id = :memberId")
    Page<Wish> findByMemberIdWithProduct(Long memberId, Pageable pageable);

    Page<Wish> findByMemberId(Long memberId, Pageable pageable);
    Optional<Wish> findByMemberIdAndProductId(Long memberId, Long productId);
    void deleteByMemberIdAndProductId(Long memberId, Long productId);
}
