package gift.repository;

import gift.entity.Option;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OptionRepository extends JpaRepository<Option, Long> {

    List<Option> findAllByProduct_Id(Long productId);

    boolean existsByProduct_IdAndName(Long productId, String name);

    boolean existsByProduct_IdAndNameAndIdNot(Long productId, String name, Long optionId);
}
