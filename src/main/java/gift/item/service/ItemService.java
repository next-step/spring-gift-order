package gift.item.service;

import gift.common.dto.PageResponseDto;
import gift.common.vo.PageIndex;
import gift.common.vo.PageSize;
import gift.common.vo.SortDirection;
import gift.item.ItemEntity;
import gift.item.ItemSortBy;
import gift.item.OptionEntity;
import gift.item.dto.ItemCreateDto;
import gift.item.dto.ItemDetailResponseDto;
import gift.item.dto.ItemResponseDto;
import gift.item.dto.ItemUpdateDto;
import gift.item.dto.OptionResponseDto;
import gift.item.exception.ItemNotFoundException;
import gift.item.repository.ItemRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public ItemResponseDto findItem(Long itemId) {
        ItemEntity itemEntity = itemRepository.findById(itemId)
            .orElseThrow(() -> new ItemNotFoundException(itemId));

        return new ItemResponseDto(
            itemEntity.getId(),
            itemEntity.getName(),
            itemEntity.getPrice(),
            itemEntity.getImageUrl()
        );
    }

    public PageResponseDto<ItemResponseDto> findAll(
        PageIndex page,
        PageSize size,
        ItemSortBy sortBy,
        SortDirection direction
    ) {

        Sort sort = Sort.by(
            direction.toSortDir(),
            sortBy.property()
        );

        Pageable pageable = PageRequest.of(page.toZeroBased(), size.toValue(), sort);

        Page<ItemEntity> itemEntities = itemRepository.findAll(pageable);
        Page<ItemResponseDto> pagedDtos = itemEntities.map(entity -> new ItemResponseDto(
            entity.getId(),
            entity.getName(),
            entity.getPrice(),
            entity.getImageUrl()
        ));

        return PageResponseDto.from(pagedDtos);

    }

    @Transactional
    public ItemDetailResponseDto createItem(ItemCreateDto itemCreateDto) {
        ItemEntity newItemEntity = new ItemEntity(
            itemCreateDto.name(),
            itemCreateDto.price(),
            itemCreateDto.imageUrl()
        );

        List<OptionEntity> optionEntities = itemCreateDto.options().stream()
            .map(optionDto -> new OptionEntity(
                optionDto.name(),
                optionDto.quantity(),
                newItemEntity
            )).toList();

        newItemEntity.getOptions().addAll(optionEntities);

        ItemEntity savedItemEntity = itemRepository.save(newItemEntity);

        return new ItemDetailResponseDto(
            savedItemEntity.getId(),
            savedItemEntity.getName(),
            savedItemEntity.getPrice(),
            savedItemEntity.getImageUrl(),
            savedItemEntity.getOptions().stream().map(
                optionEntity -> new OptionResponseDto(
                    optionEntity.getId(),
                    optionEntity.getName(),
                    optionEntity.getQuantity()
                )
            ).toList()
        );
    }

    @Transactional
    public ItemResponseDto updateItem(Long itemId, ItemUpdateDto itemUpdateDto) {
        ItemEntity oldItemEntity = itemRepository.findById(itemId)
            .orElseThrow(() -> new ItemNotFoundException(itemId));

        oldItemEntity.setName(itemUpdateDto.name());
        oldItemEntity.setPrice(itemUpdateDto.price());
        oldItemEntity.setImageUrl(itemUpdateDto.imageUrl());

        ItemEntity updatedItemEntity = itemRepository.save(oldItemEntity);

        return new ItemResponseDto(
            updatedItemEntity.getId(),
            updatedItemEntity.getName(),
            updatedItemEntity.getPrice(),
            updatedItemEntity.getImageUrl()
        );
    }

    @Transactional
    public void deleteItem(Long itemId) {
        ItemEntity itemEntity = itemRepository.findById(itemId)
            .orElseThrow(() -> new ItemNotFoundException(itemId));

        itemRepository.deleteById(itemEntity.getId());
    }
}
