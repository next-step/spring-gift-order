package gift.service.itemService;

import gift.dto.itemDto.ItemCreateDto;
import gift.entity.Item;
import gift.exception.itemException.ItemNotFoundException;
import gift.repository.itemRepository.ItemRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    public ItemServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    @Transactional
    public Item saveItem(Item item) {

        return itemRepository.save(item);
    }

    /***
     * 메서드 분리의 방향성을 잘 잡혀서, 일단 no usages 여도 임시 keep
     */
    @Override
    public Page<Item> getItems(String name, Integer price, Pageable pageable) {

        if (name == null) {
            return findItemsByPrice(price, pageable);
        }
        if (price == null) {
            return findItemsByName(name, pageable);
        }
        return findItemsByNameAndPrice(name, price, pageable);
    }

    @Override
    @Transactional
    public void delete(String name) {
        Item targetItem = findItemByName(name);
        itemRepository.delete(targetItem);
    }

    @Override
    @Transactional
    public Item updateItem(Long id, Item item) {
        Item findItem = findItemById(id);

        Item targetItem = findItem;

        Item updatedItem = findItem.update(item);

        return save(updatedItem);

    }

    @Override
    public Item findById(Long id) {
        return itemRepository.findById(id).orElseThrow(ItemNotFoundException::new);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Item targetItem = findItemById(id);
        itemRepository.deleteById(targetItem.getId());
    }

    @Override
    @Transactional
    public void deleteByItem(Item item) {
        Item targetItem = findItemById(item.getId());
        itemRepository.deleteById(targetItem.getId());

    }

    @Override
    public Page<Item> getAllItems(Pageable pageable) {
        return itemRepository.findAll(pageable);
    }

    @Override
    public Item findItemByName(String name) {
        return itemRepository.findByName(name).orElseThrow(ItemNotFoundException::new);
    }

    @Override
    public Item findItemById(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(ItemNotFoundException::new);
    }

    @Override
    public Page<Item> findItemsByName(String name, Pageable pageable) {
        return itemRepository.findByNameContaining(name, pageable);
    }

    @Override
    public Page<Item> findItemsByPrice(Integer price, Pageable pageable) {
        return itemRepository.findByPrice(price, pageable);
    }

    @Override
    public Page<Item> findItemsByNameAndPrice(String name, Integer price, Pageable pageable) {
        return itemRepository.findByNameContainingAndPrice(name, price, pageable);
    }

    @Override
    public Item save(Item updatedItem) {
        return itemRepository.save(updatedItem);
    }


}
