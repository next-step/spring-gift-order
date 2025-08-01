package gift.repository.product;

import gift.entity.ProductOption;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {

    List<ProductOption> findAllByProductId(Long productId);

    @Transactional
    int deleteProductOptionById(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<ProductOption> findById(Long id);
}
