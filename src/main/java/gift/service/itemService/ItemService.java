package gift.service.itemService;

import gift.dto.itemDto.ItemCreateDto;
import gift.entity.Item;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ItemService {
    Item saveItem(ItemCreateDto itemCreateDto);

    Page<Item> getItems(String name, Integer price, Pageable pageable);

    void delete(String name);

    Item updateItem(Long id, Item item);

    Optional<Item> findById(Long id);

    void deleteById(Long id);

    Page<Item> getAllItems(Pageable pageable);

    Optional<Item> findItemByName(@NotNull String name);

    Optional<Item> findItemById(Long itemId);

    Page<Item> findItemsByName(String name, Pageable pageable);

    Page<Item> findItemsByPrice(Integer price, Pageable pageable);

    Page<Item> findItemsByNameAndPrice(String name, Integer price, Pageable pageable);

    Item save(Item updatedItem);
}
