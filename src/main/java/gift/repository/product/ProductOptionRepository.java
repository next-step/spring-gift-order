package gift.repository.product;

import gift.entity.ProductOption;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {

    List<ProductOption> findAllByProductId(Long productId);

    @Transactional
    int deleteProductOptionById(Long id);
}
