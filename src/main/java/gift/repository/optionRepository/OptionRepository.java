package gift.repository.optionRepository;

import gift.entity.Item;
import gift.entity.ItemOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface OptionRepository extends JpaRepository<ItemOption, Long> {
    ItemOption findByItem(Item item);
}
