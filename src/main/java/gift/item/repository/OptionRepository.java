package gift.item.repository;

import gift.item.OptionEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OptionRepository extends JpaRepository<OptionEntity, Long> {

    List<OptionEntity> findByItemId(Long itemId);

}
