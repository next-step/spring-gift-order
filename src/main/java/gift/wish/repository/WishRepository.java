package gift.wish.repository;

import gift.wish.entity.Wish;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishRepository extends JpaRepository<Wish, Long> {
    Optional<Wish> findByMemberIdAndId(Long memberId, Long wishId);

    Optional<Wish> findByMemberIdAndOptionId(Long memberId, Long optionId);

    List<Wish> findAllByMemberId(Long memberId);

    Page<Wish> findAllByMemberId(Long memberId, Pageable pageable);

    void deleteByIdAndMemberId(Long wishId, Long memberId);
}