package gift.repository;

import gift.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Modifying(clearAutomatically = true)
    @Query("update Product p set p.name = :name, p.price = :price, p.imageUrl = :imageUrl where p.id = :id")
    int updateProduct(@Param("id") Long id,
        @Param("name") String name,
        @Param("price") int price,
        @Param("imageUrl") String imageUrl);
}
