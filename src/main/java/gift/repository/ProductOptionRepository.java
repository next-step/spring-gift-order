package gift.repository;

import gift.entity.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {
    List<ProductOption> findByProductId(Long productId);
    Optional<ProductOption> findByIdAndProductId(Long optionId, Long productId);
    boolean existsByProductIdAndName(Long productId, String name);
}
