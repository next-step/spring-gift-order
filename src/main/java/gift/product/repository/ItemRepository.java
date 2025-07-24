package gift.product.repository;


import gift.product.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface ItemRepository extends JpaRepository<Item, Long> {
	Page<Item> findAll(Pageable pageable);
}
