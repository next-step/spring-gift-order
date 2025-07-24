package gift.repository.optionRepository;

import gift.entity.Item;
import gift.entity.ItemOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface OptionRepository extends JpaRepository<ItemOption, Long> {
    Optional<ItemOption> findByItem(Item item);
}
