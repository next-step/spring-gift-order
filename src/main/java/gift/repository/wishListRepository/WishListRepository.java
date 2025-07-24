package gift.repository.wishListRepository;

import gift.entity.Item;
import gift.entity.User;
import gift.entity.WishItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface WishListRepository extends JpaRepository<WishItem, Long> {
    Page<WishItem> findAllByUser(User user, Pageable pageable);

    Optional<WishItem> findByUserAndItem(User user, Item item);

    boolean existsByItem(Item item);

    Page<WishItem> findByUserAndItemNameContainingAndItemPrice(User user, String name, Integer price, Pageable pageable);

    WishItem findByUserEmailAndItemName(String userEmail, String itemName);

    Page<WishItem> findByUserAndItemIn(User user, Collection<Item> items, Pageable pageable);

    WishItem findByUserEmailAndItem(String userEmail, Optional<Item> item);

    Page<WishItem> findByUserAndItem_NameContainingAndItem_Price(User user, String itemName, Integer itemPrice, Pageable pageable);

    Page<WishItem> findByUserAndItem_NameContaining(User user, String itemName, Pageable pageable);

    Page<WishItem> findByUserAndItem_Price(User user, Integer itemPrice, Pageable pageable);

    Page<WishItem> findByUser(User user, Pageable pageable);
}
