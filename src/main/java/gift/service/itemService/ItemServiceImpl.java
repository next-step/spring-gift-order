package gift.service.itemService;

import gift.dto.itemDto.ItemCreateDto;
import gift.entity.Item;
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
    public Item saveItem(ItemCreateDto itemCreateDto) {
        Item item = itemCreateDto.dtoToItem();

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
        Item targetItem = itemRepository.findByName(name);
        itemRepository.delete(targetItem);
    }

    @Override
    @Transactional
    public Item updateItem(Long id, Item item) {
        Optional<Item> findItem = findItemById(id);

        Item targetItem = findItem.get();

        Item updatedItem = targetItem.update(item);

        return save(updatedItem);

    }

    @Override
    public Optional<Item> findById(Long id) {
        return itemRepository.findById(id);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        itemRepository.deleteById(id);
    }

    @Override
    public Page<Item> getAllItems(Pageable pageable) {
        return itemRepository.findAll(pageable);
    }

    @Override
    public Optional<Item> findItemByName(String name) {
        return Optional.ofNullable(itemRepository.findByName(name));
    }

    @Override
    public Optional<Item> findItemById(Long itemId) {
        return itemRepository.findById(itemId);
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
