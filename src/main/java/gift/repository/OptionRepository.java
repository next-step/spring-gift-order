package gift.repository;

import gift.entity.Option;
import gift.entity.Product;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OptionRepository extends JpaRepository<Option, Long> {

    List<Option> findAllByProduct(Product product);

    boolean existsByNameAndProduct(String name, Product product);
}
