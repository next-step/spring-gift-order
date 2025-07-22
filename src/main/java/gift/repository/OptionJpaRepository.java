package gift.repository;

import gift.domain.Option;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OptionJpaRepository extends JpaRepository<Option, Long> {

    List<Option> findByProductId(Long productId);

    Page<Option> findByProductId(Long productId, Pageable pageable);
}
