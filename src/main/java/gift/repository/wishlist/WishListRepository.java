package gift.repository.wishlist;

import gift.entity.Wish;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishListRepository extends JpaRepository<Wish, Long> {

    Optional<Wish> findByProductIdAndMemberId(Long productId, Long memberId);

    @EntityGraph(attributePaths = {"product", "member"})
    List<Wish> findAllByMemberId(Long memberId);
}