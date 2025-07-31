package gift.product.repository;

import gift.product.entity.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OptionRepository extends JpaRepository<Option, Long> {
    @Query("SELECT o FROM Option o WHERE o.product.id=:productId")
    List<Option> findAllByProductId(@Param("productId") Long productId);

    @Query("SELECT o FROM Option o WHERE o.id=:optionId")
    Optional<Option> findByOptionId(@Param("optionId") Long optionId);
}
