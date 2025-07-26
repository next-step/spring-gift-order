package gift.repository;

import gift.domain.Member;
import gift.domain.Product;
import gift.domain.Wish;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WishJpaRepository extends JpaRepository<Wish, Long> {
    
    @Query("SELECT w FROM Wish w JOIN FETCH w.product WHERE w.member.id = :memberId")
    List<Wish> findByMemberIdWithProduct(@Param("memberId") Long memberId);
    
    @Query("SELECT w FROM Wish w JOIN FETCH w.product WHERE w.member.id = :memberId ORDER BY w.id DESC")
    Page<Wish> findByMemberIdWithProduct(@Param("memberId") Long memberId, Pageable pageable);
    
    @Query("SELECT w FROM Wish w JOIN FETCH w.product WHERE w.member.id = :memberId ORDER BY w.id DESC")
    Slice<Wish> findByMemberIdWithProductSlice(@Param("memberId") Long memberId, Pageable pageable);
    
    Optional<Wish> findByMemberAndProduct(Member member, Product product);
    
    @Modifying
    @Query("DELETE FROM Wish w WHERE w.id = :id AND w.member.id = :memberId")
    int deleteByIdAndMemberId(@Param("id") Long id, @Param("memberId") Long memberId);
}
