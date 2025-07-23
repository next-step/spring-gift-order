package gift.controller;

import gift.annotation.LoginUser;
import gift.dto.WishRequest;
import gift.dto.WishResponse;
import gift.entity.User;
import gift.enums.WishSortKey;
import gift.service.WishService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishes")
public class WishController {

    private final WishService wishService;

    WishController(WishService wishService) {
        this.wishService = wishService;
    }

    @GetMapping
    public ResponseEntity<List<WishResponse>> getWishPage(@LoginUser User user,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int size,
                                                          @RequestParam(defaultValue = "name-asc") String sortKey)
    {
        List<WishResponse> wishPage = wishService.getWishPage(user.getId(), page, size, WishSortKey.from(sortKey));

        return new ResponseEntity<>(wishPage, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<WishResponse> postWish(
            @RequestBody WishRequest wishRequest,
            @LoginUser User user) {
        return new ResponseEntity<>(wishService.createWish(user.getId(), wishRequest), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<WishResponse> updateWish(
            @RequestBody WishRequest wishRequest,
            @LoginUser User user) {
        return new ResponseEntity<>(wishService.updateWish(user.getId(), wishRequest), HttpStatus.OK);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteWish(
            @PathVariable Long productId,
            @LoginUser User user) {
        wishService.deleteWish(user.getId(), productId);
        return ResponseEntity.noContent().build();
    }
}
