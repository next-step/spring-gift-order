package gift.item.service;

import gift.item.ItemEntity;
import gift.item.OptionEntity;
import gift.item.dto.OptionCreateDto;
import gift.item.dto.OptionResponseDto;
import gift.item.dto.OptionUpdateDto;
import gift.item.exception.ItemNotFoundException;
import gift.item.exception.LastOptionCannotBeDeletedException;
import gift.item.exception.OptionNotFoundException;
import gift.item.repository.ItemRepository;
import gift.item.repository.OptionRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OptionService {

    private final OptionRepository optionRepository;
    private final ItemRepository itemRepository;

    public OptionService(OptionRepository optionRepository, ItemRepository itemRepository) {
        this.optionRepository = optionRepository;
        this.itemRepository = itemRepository;
    }

    public List<OptionResponseDto> findOptionsByItemId(Long itemId) {
        itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(itemId));

        List<OptionEntity> optionEntities = optionRepository.findByItemId(itemId);

        return optionEntities.stream().map(optionEntity ->
            new OptionResponseDto(
                optionEntity.getId(),
                optionEntity.getName(),
                optionEntity.getQuantity()
            )
        ).toList();
    }

    @Transactional
    public OptionResponseDto createOption(Long itemId, OptionCreateDto optionCreateDto) {
        ItemEntity itemEntity = itemRepository.findById(itemId)
            .orElseThrow(() -> new ItemNotFoundException(itemId));

        OptionEntity newOptionEntity = new OptionEntity(
            optionCreateDto.name(),
            optionCreateDto.quantity(),
            itemEntity
        );

        itemEntity.getOptions().add(newOptionEntity);

        itemRepository.save(itemEntity);

        return new OptionResponseDto(
            newOptionEntity.getId(),
            newOptionEntity.getName(),
            newOptionEntity.getQuantity()
        );
    }

    @Transactional
    public OptionResponseDto updateOption(Long optionId, OptionUpdateDto optionUpdateDto) {
        OptionEntity oldOptionEntity = optionRepository.findById(optionId)
            .orElseThrow(() -> new OptionNotFoundException(optionId));

        oldOptionEntity.setName(optionUpdateDto.name());
        oldOptionEntity.setQuantity(optionUpdateDto.quantity());

        OptionEntity updatedOptionEntity = optionRepository.save(oldOptionEntity);
        return new OptionResponseDto(
            updatedOptionEntity.getId(),
            updatedOptionEntity.getName(),
            updatedOptionEntity.getQuantity()
        );
    }

    @Transactional
    public void deleteOption(Long optionId) {
        OptionEntity oldOptionEntity = optionRepository.findById(optionId)
            .orElseThrow(() -> new OptionNotFoundException(optionId));

        Long itemId = oldOptionEntity.getItem().getId();
        List<OptionEntity> itemOptions = optionRepository.findByItemId(itemId);

        if (itemOptions.size() <= 1) {
            throw new LastOptionCannotBeDeletedException(itemId);
        }

        optionRepository.delete(oldOptionEntity);
    }

    @Transactional
    public OptionResponseDto decreaseQuantity(Long optionId, Integer decrement) {
        OptionEntity optionEntity = optionRepository.findById(optionId)
            .orElseThrow(() -> new OptionNotFoundException(optionId));

        if (decrement <= 0) {
            throw new IllegalArgumentException("감소량(decrement)은 양수여야 합니다.");
        }

        if (optionEntity.getQuantity() < decrement) {
            throw new IllegalArgumentException("현재 수량을 초과하는 decrement 입니다.");
        }

        if (optionEntity.getQuantity() == decrement) {
            Long itemId = optionEntity.getItem().getId();
            List<OptionEntity> itemOptions = optionRepository.findByItemId(itemId);
            if (itemOptions.size() <= 1) {
                throw new LastOptionCannotBeDeletedException(itemId);
            }
        }
        optionEntity.setQuantity(optionEntity.getQuantity() - decrement);
        OptionEntity updatedOptionEntity = optionRepository.save(optionEntity);
        return new OptionResponseDto(
            updatedOptionEntity.getId(),
            updatedOptionEntity.getName(),
            updatedOptionEntity.getQuantity()
        );

    }

}
