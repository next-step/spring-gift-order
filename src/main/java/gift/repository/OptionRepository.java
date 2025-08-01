package gift.repository;

import gift.entity.Option;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OptionRepository extends JpaRepository<Option, Long> {

    @EntityGraph(attributePaths = {"product"})
    List<Option> findAllByProductId(Long productId);
    void deleteAllByProductId(Long productId);
    boolean existsByProductIdAndName(Long productId, String name);
}