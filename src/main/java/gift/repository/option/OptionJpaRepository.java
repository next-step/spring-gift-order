package gift.repository.option;

import gift.domain.Option;
import gift.repository.BaseRepository;

public interface OptionJpaRepository extends BaseRepository<Option, Long> {

    boolean existsByProductIdAndName(Long productId, String name);

}
