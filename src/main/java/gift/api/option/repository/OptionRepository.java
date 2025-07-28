package gift.api.option.repository;

import gift.api.option.domain.Option;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OptionRepository extends JpaRepository<Option, Long> {

    Optional<Option> getOptionById(Long id);
}
