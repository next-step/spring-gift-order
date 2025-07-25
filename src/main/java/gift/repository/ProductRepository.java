package gift.repository;

import gift.Entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query(value = "SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.options",
            countQuery = "SELECT COUNT(p) FROM Product p")
    Page<Product> findAll(Pageable pageable);
}


