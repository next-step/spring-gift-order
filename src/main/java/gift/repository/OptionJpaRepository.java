package gift.repository;

import gift.domain.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OptionJpaRepository extends JpaRepository<Option, Long> {
    
    List<Option> findByProductId(Long productId);
    
    @Query("SELECT o FROM Option o WHERE o.product.id = :productId AND o.name = :name")
    Optional<Option> findByProductIdAndName(@Param("productId") Long productId, @Param("name") String name);
    
    boolean existsByProductIdAndName(Long productId, String name);
}
