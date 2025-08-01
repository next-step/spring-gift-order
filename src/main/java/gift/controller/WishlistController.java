package gift.controller;

import gift.dto.wishlist.WishlistRequestDto;
import gift.dto.wishlist.WishlistResponseDto;
import gift.service.WishlistService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wishes")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @PostMapping
    public ResponseEntity<Void> addWish(
            @RequestAttribute("userEmail") String userEmail,
            @RequestBody WishlistRequestDto requestDto
    ) {
        wishlistService.addWish(userEmail, requestDto.productId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<Page<WishlistResponseDto>> getWishes(
            @RequestAttribute("userEmail") String userEmail,
            Pageable pageable
    ) {
        Page<WishlistResponseDto> wishes = wishlistService.getWishes(userEmail, pageable);
        return ResponseEntity.ok(wishes);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> removeWish(
            @RequestAttribute("userEmail") String userEmail,
            @PathVariable Long productId
    ) {
        wishlistService.removeWish(userEmail, productId);
        return ResponseEntity.noContent().build();
    }
}