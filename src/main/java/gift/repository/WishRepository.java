package gift.repository;

import gift.domain.Wish;
import gift.domain.Member;
import gift.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface WishRepository extends JpaRepository<Wish, Long> {
    List<Wish> findByMember(Member member);
    Optional<Wish> findByMemberAndProduct(Member member, Product product);
    Page<Wish> findByMember(Member member, Pageable pageable);
    @Transactional
    @Modifying
    @Query("DELETE FROM Wish w WHERE w.option.id = :optionId")
    void deleteByOptionId(Long optionId);
}
