package gift.controller.itemController;


import gift.dto.itemDto.*;
import gift.entity.Item;
import gift.service.itemService.ItemService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/products")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ResponseEntity<ResponseItem> addItem(@RequestBody @Valid ItemCreateDto dto) {
        Item item = itemService.saveItem(dto);
        ItemResponseDto responseDto = ItemResponseDto.from(item);

        return new ResponseEntity<>(new ResponseItem(responseDto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ResponseItems> getItems(@RequestParam(required = false) String name, @RequestParam(required = false) Integer price, Pageable pageable) {
        Page<Item> items;

        if (name == null && price == null) {
            items = itemService.getAllItems(pageable);
        } else if (name != null && price == null) {
            items = itemService.findItemsByName(name, pageable);
        } else if (name == null && price != null) {
            items = itemService.findItemsByPrice(price, pageable);
        } else {
            items = itemService.findItemsByNameAndPrice(name, price, pageable);
        }

        return ResponseEntity.ok(ResponseItems.from(items));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteItem(@RequestParam(required = false) String name) {
        itemService.delete(name);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseItem> updateItem(@PathVariable Long id, @RequestBody @Valid ItemUpdateDto dto) {
        Item item = Item.from(dto);
        Item updatedItem = itemService.updateItem(id, item);
        ItemResponseDto responseDto = ItemResponseDto.from(updatedItem);

        return ResponseEntity.ok(new ResponseItem(responseDto));
    }
}
