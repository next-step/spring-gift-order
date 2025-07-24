package gift.repository.itemRepository;

import gift.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    Optional<Item> findByName(String name);

    Page<Item> findByPrice(Integer price, Pageable pageable);

    Page<Item> findByNameContaining(String name, Pageable pageable);

    Page<Item> findByNameContainingAndPrice(String name, Integer price, Pageable pageable);
}
