package gift.service.itemService;

import gift.entity.Item;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemService {
    Item saveItem(Item item);

    Page<Item> getItems(String name, Integer price, Pageable pageable);

    void delete(String name);

    Item updateItem(Long id, Item item);

    Item findById(Long id);

    void deleteById(Long id);

    void deleteByItem(Item item);

    Page<Item> getAllItems(Pageable pageable);

    Item findItemByName(@NotNull String name);

    Item findItemById(Long itemId);

    Page<Item> findItemsByName(String name, Pageable pageable);

    Page<Item> findItemsByPrice(Integer price, Pageable pageable);

    Page<Item> findItemsByNameAndPrice(String name, Integer price, Pageable pageable);

    Item save(Item updatedItem);
}
