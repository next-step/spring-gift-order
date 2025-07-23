package gift.repository;

import gift.entity.Option;
import gift.entity.vo.OptionName;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OptionRepository extends JpaRepository<Option, Long> {

    List<Option> findAllByProductId(Long productId);

    boolean existsByProductIdAndName(Long productId, OptionName name);

    Page<Option> findAllByProductId(Long productId, Pageable pageable);
}
