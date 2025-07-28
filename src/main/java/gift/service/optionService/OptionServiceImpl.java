package gift.service.optionService;

import gift.entity.Item;
import gift.entity.ItemOption;
import gift.exception.itemException.OptionNotFoundException;
import gift.repository.itemRepository.ItemRepository;
import gift.repository.optionRepository.OptionRepository;
import gift.service.itemService.ItemService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OptionServiceImpl implements OptionService {

    private final OptionRepository optionRepository;
    private final ItemService itemService;

    public OptionServiceImpl(OptionRepository optionRepository, ItemRepository itemRepository, ItemService itemService) {
        this.optionRepository = optionRepository;
        this.itemService = itemService;
    }

    @Override
    public ItemOption save(ItemOption itemOption, Long itemId) {
        Item item = itemService.findById(itemId);
        item.checkDuplicatedItem(itemOption.getOptionName());

        ItemOption savedItemOption = new ItemOption(item, itemOption.getOptionName(), itemOption.getQuantity());

        return optionRepository.save(savedItemOption);
    }

    @Override
    public List<ItemOption> getOptions(Long itemId) {
        Item item = itemService.findById(itemId);

        return item.getOptions();
    }

    @Transactional
    @Override
    public ItemOption quantityControl(ItemOption targetOption, Long itemId) {
        Item item = itemService.findById(itemId);

        ItemOption itemOption = getByItem(item);
        ItemOption changedOption = itemOption.quantityControl(targetOption.getQuantity());

        return optionRepository.save(changedOption);
    }

    private ItemOption getByItem(Item item) {
        return optionRepository.findByItem(item).orElseThrow(OptionNotFoundException::new);
    }


}
