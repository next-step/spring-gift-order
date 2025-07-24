package gift.controller;

import gift.auth.LoginMember;
import gift.domain.Member;
import gift.dto.WishRequest;
import gift.dto.WishResponse;
import gift.service.WishService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wishes")
public class WishController {

    private final WishService wishService;

    public WishController(WishService wishService) {
        this.wishService = wishService;
    }

    @GetMapping
    public Page<WishResponse> getMyWishes(@LoginMember Member member, Pageable pageable) {
        return wishService.getWishList(member.getId(), pageable);
    }

    @PostMapping
    public ResponseEntity<Void> addWish(@RequestBody WishRequest request, @LoginMember Member member) {
        wishService.addWish(member.getId(), request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> removeWish(@PathVariable Long productId, @LoginMember Member member) {
        if (wishService.removeWish(member.getId(), productId)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/{productId}")
    public ResponseEntity<Void> updateWishQuantity(@PathVariable Long productId, @RequestBody WishRequest request, @LoginMember Member member) {
        wishService.updateWishQuantity(member.getId(), productId, request.getQuantity());
        return ResponseEntity.ok().build();
    }
}
