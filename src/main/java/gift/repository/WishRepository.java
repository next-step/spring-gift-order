package gift.repository;

import gift.domain.Wish;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishRepository extends JpaRepository<Wish, Long> {

    List<Wish> findAllByMemberId(Long memberId);

    Optional<Wish> findByMemberIdAndProductOptionId(Long memberId, Long optionId);

    Page<Wish> findAllByMemberId(Long memberId, Pageable pageable);

    void deleteByMemberIdAndProductOptionId(Long memberId, Long optionId);

}

