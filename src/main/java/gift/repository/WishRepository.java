package gift.repository;

import gift.entity.Wish;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishRepository extends JpaRepository<Wish, Long> {

    List<Wish> findAllByUserId(Long userId);

    Page<Wish> findAllByUserId(Long userId, Pageable pageable);

    Optional<Wish> findByUserIdAndProductId(Long userId, Long productId);

    Boolean existsByUserIdAndProductId(Long userId, Long productId);
}
