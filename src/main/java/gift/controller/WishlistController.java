package gift.controller;

import gift.dto.wishlist.WishlistRequestDto;
import gift.dto.wishlist.WishlistResponseDto;
import gift.service.WishlistService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wishlists") // 경로 수정
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @GetMapping
    public ResponseEntity<Page<WishlistResponseDto>> getWishlists(
            @RequestAttribute("userEmail") String userEmail, Pageable pageable) {
        Page<WishlistResponseDto> wishlists = wishlistService.getWishlists(userEmail, pageable);
        return ResponseEntity.ok(wishlists);
    }

    @PostMapping
    public ResponseEntity<Void> addWishlist(@RequestAttribute("userEmail") String userEmail,
            @Valid @RequestBody WishlistRequestDto request) {
        wishlistService.addWishlist(userEmail, request.productId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWishlist(@RequestAttribute("userEmail") String userEmail,
            @PathVariable("id") Long wishlistId) {
        wishlistService.deleteWishlist(userEmail, wishlistId);
        return ResponseEntity.noContent().build();
    }
}