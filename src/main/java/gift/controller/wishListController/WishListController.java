package gift.controller.wishListController;

import gift.config.LoginUser;
import gift.dto.wishListDto.*;
import gift.entity.WishItem;
import gift.service.wishListService.WishListService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/wish")
public class WishListController {

    private final WishListService wishListService;

    public WishListController(WishListService wishListService) {
        this.wishListService = wishListService;
    }

    @PostMapping
    public ResponseEntity<ResponseWishItemDto> addItem(@RequestBody @Valid CreateWishItemRequestDto dto, @LoginUser String userEmail) {

        WishItem addedWishItem = wishListService.addWishItem(dto, userEmail);

        return new ResponseEntity<>(ResponseWishItemDto.from(addedWishItem), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ResponseWishItem> getWishItemList(@LoginUser String userEmail, @RequestParam(required = false) String name, @RequestParam(required = false) Integer price, Pageable pageable) {
        Page<WishItem> wishItemList = wishListService.getItemList(name, price, userEmail, pageable);

        List<ResponseWishItemDto> wishItemDtoList = new ArrayList<>();
        for (WishItem wishItem : wishItemList) {
            wishItemDtoList.add(ResponseWishItemDto.from(wishItem));
        }

        return ResponseEntity.ok(ResponseWishItem.from(wishItemList));
    }

    @PostMapping("/option")
    public ResponseEntity<QuantityWishItemDto> controlWishItemQuantity(@LoginUser String userEmail, @RequestParam String itemName, @RequestParam Integer quantity) {

        WishItem wishItem = wishListService.controlWishItemQuantity(itemName, userEmail, quantity);
        String updatedItemName = wishItem.getItem().getName();

        QuantityWishItemDto quantityWishItemDto = new QuantityWishItemDto(wishItem.getId(), updatedItemName, quantity);

        return new ResponseEntity<>(quantityWishItemDto, HttpStatus.ACCEPTED);
    }


    @DeleteMapping
    public ResponseEntity<ResponseWishItemDto> deleteWishItem(@LoginUser String userEmail, @RequestParam String name) {

        WishItem targetWishItem = wishListService.deleteWishItem(name, userEmail);

        return new ResponseEntity<>(ResponseWishItemDto.from(targetWishItem), HttpStatus.NO_CONTENT);
    }

    @PutMapping("/option")
    public ResponseEntity<ResponseWishItemDto> updateWishItem(@LoginUser String userEmail, @RequestBody UpdateWishItemDto updateWishItemDto) {

        WishItem updatedWishItem = wishListService.updateWishItem(updateWishItemDto, userEmail);

        return new ResponseEntity<>(ResponseWishItemDto.from(updatedWishItem), HttpStatus.ACCEPTED);
    }

}
