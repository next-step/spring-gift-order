package gift.wishlist.controller;

import gift.member.Member;
import gift.resolver.LoginMember;
import gift.wishlist.Wishlist;
import gift.wishlist.dto.WishlistItemRequestDto;
import gift.wishlist.dto.WishlistItemResponseDto;
import gift.wishlist.service.WishlistService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @GetMapping
    public ResponseEntity<Page<WishlistItemResponseDto>> findAllWishlistItemsByMemberIdWithPageable(
            @LoginMember Member member,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<WishlistItemResponseDto> items = wishlistService.findAllWishlistItemsByMemberIdWithPageable(member.getId(), pageable);
        return new ResponseEntity<>(items, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Void> addWishlistItem(
            @LoginMember Member member,
            @Valid @RequestBody WishlistItemRequestDto requestDto
    ) {
        wishlistService.addWishlistItem(member.getId(), requestDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Void> updateWishlistItemById(
            @PathVariable Long itemId,
            @RequestParam Long quantity
    ) {
        wishlistService.updateWishlistItemById(itemId, quantity);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteWishlistItemById(
            @PathVariable Long itemId
    ) {
        wishlistService.deleteWishlistItemById(itemId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
