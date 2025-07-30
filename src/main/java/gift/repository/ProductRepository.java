package gift.repository;

import gift.domain.product.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("select p from Product p where p.id < :cursor order by p.id desc")
    List<Product> findAllWithCursor(Long cursor, Pageable pageable);

    @Query("select p from Product p join fetch p.options o where o.id=:id")
    Optional<Product> findProductByOptionId(Long id);

}
