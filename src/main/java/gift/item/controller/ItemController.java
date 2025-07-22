package gift.item.controller;

import gift.common.dto.PageResponseDto;
import gift.common.vo.PageIndex;
import gift.common.vo.PageSize;
import gift.common.vo.SortDirection;
import gift.item.ItemSortBy;
import gift.item.dto.ItemCreateDto;
import gift.item.dto.ItemDetailResponseDto;
import gift.item.dto.ItemResponseDto;
import gift.item.dto.ItemUpdateDto;
import gift.item.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemResponseDto> findItem(@PathVariable Long itemId) {
        ItemResponseDto dto = itemService.findItem(itemId);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<PageResponseDto<ItemResponseDto>> findAll(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "id") String sortBy,
        @RequestParam(defaultValue = "desc") String direction
    ) {

        PageResponseDto<ItemResponseDto> pagedDtos = itemService.findAll(
            new PageIndex(page),
            new PageSize(size),
            ItemSortBy.from(sortBy),
            SortDirection.from(direction)
        );
        return ResponseEntity.ok(pagedDtos);
    }

    @PostMapping
    public ResponseEntity<ItemDetailResponseDto> createItem(
        @RequestBody @Valid ItemCreateDto itemCreateDto) {
        ItemDetailResponseDto dto = itemService.createItem(itemCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<ItemResponseDto> updateItem(
        @PathVariable Long itemId,
        @RequestBody @Valid ItemUpdateDto itemUpdateDto
    ) {
        ItemResponseDto dto = itemService.updateItem(itemId, itemUpdateDto);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long itemId) {
        itemService.deleteItem(itemId);
        return ResponseEntity.noContent().build();
    }

}
