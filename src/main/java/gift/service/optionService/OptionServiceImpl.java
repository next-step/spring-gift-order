package gift.service.optionService;

import gift.dto.optionDto.OptionRequestDto;
import gift.entity.Item;
import gift.entity.ItemOption;
import gift.exception.itemException.ItemNotFoundException;
import gift.exception.itemException.OptionDuplicatedException;
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
    public ItemOption save(OptionRequestDto optionRequestDto, Long itemId) {
        Item item = itemService.findById(itemId)
                .orElseThrow(ItemNotFoundException::new);

        for (ItemOption option : item.getOptions()) {
            if (option.getOptionName().equals(optionRequestDto.optionName())) {
                throw new OptionDuplicatedException();
            }
        }

        ItemOption itemOption = new ItemOption(item, optionRequestDto.optionName(), optionRequestDto.quantity());

        return optionRepository.save(itemOption);
    }

    @Override
    public List<ItemOption> getOptions(Long itemId) {
        Item item = itemService.findById(itemId)
                .orElseThrow(ItemNotFoundException::new);

        return item.getOptions();
    }

    @Transactional
    @Override
    public ItemOption quantityControl(OptionRequestDto optionRequestDto, Long itemId) {
        Item item = itemService.findById(itemId)
                .orElseThrow(ItemNotFoundException::new);

        ItemOption itemOption = optionRepository.findByItem(item);
        ItemOption changedOption = itemOption.quantityControl(optionRequestDto.quantity());

        return optionRepository.save(changedOption);
    }


}
