package gift.controller;

import gift.annotation.UserValid;
import gift.dto.UserInfoDto;
import gift.dto.WishRequestDto;
import gift.dto.WishResponseDto;
import gift.service.WishService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wish")
public class WishController {

    private final WishService wishService;

    public WishController(WishService wishService) {
        this.wishService = wishService;
    }

    @GetMapping()
    public ResponseEntity<Page<WishResponseDto>> findUserWishes(
            @UserValid UserInfoDto userInfoDto,
            @SortDefault(sort = "id")
            Pageable pageable) {
        return ResponseEntity.ok(wishService.findUserWishes(userInfoDto, pageable));
    }

    @PostMapping()
    public ResponseEntity<WishResponseDto> add(@UserValid UserInfoDto userInfoDto, @RequestBody WishRequestDto wishRequestDto) {
        return new ResponseEntity<>(wishService.addWish(userInfoDto, wishRequestDto), HttpStatus.CREATED);
    }

    @PatchMapping()
    public ResponseEntity<Void> update(@UserValid UserInfoDto userInfoDto, @RequestBody WishRequestDto wishrequestDto) {
        wishService.updateWish(wishrequestDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping()
    public ResponseEntity<Void> delete(@UserValid UserInfoDto userInfoDto, @RequestBody WishRequestDto wishRequestDto) {
        wishService.deleteWish(wishRequestDto);
        return ResponseEntity.noContent().build();
    }
}
