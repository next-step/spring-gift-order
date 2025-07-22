package gift.wishlist.controller;

import gift.shared.annotation.AuthUser;
import gift.user.dto.response.UserResponse;
import gift.wishlist.dto.response.WishlistResponse;
import gift.wishlist.service.WishlistService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {
    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @GetMapping()
    public ResponseEntity<List<WishlistResponse>> getWishLists(@AuthUser UserResponse user, Pageable pageable) {
        return ResponseEntity.ok()
                .body(wishlistService.getWishlists(user.id(), pageable.getPageNumber(), pageable.getPageSize()));
    }

    @PostMapping("/{giftId}")
    public ResponseEntity<String> addWishList(
            @AuthUser UserResponse user,
            @PathVariable Long giftId
    ) {
        wishlistService.addWishList(giftId, user.id());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{wishlistId}")
    public ResponseEntity<String> deleteWishList(
            @AuthUser UserResponse user,
            @PathVariable Long wishlistId
    ) {
        wishlistService.deleteById(wishlistId);
        return ResponseEntity.noContent().build();
    }
}
