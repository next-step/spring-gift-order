package gift.controller;

import gift.annotation.LoginMember;
import gift.dto.WishRequestDto;
import gift.dto.WishResponseDto;
import gift.entity.Member;
import gift.service.WishService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/wishes")
public class WishController {

    private final WishService wishService;

    public WishController(WishService wishService) {
        this.wishService = wishService;
    }

    @PostMapping
    public ResponseEntity<Void> addWish(
            @Valid @RequestBody WishRequestDto request,
            @LoginMember Member member) {
        wishService.addWish(member.getId(), request.getProductId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<Page<WishResponseDto>> getWishList(
            @LoginMember Member member,
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable) {

        Page<WishResponseDto> wishPage = wishService.getWishList(member, pageable);
        return ResponseEntity.ok(wishPage);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> removeWish(
            @PathVariable Long productId,
            @LoginMember Member member) {
        wishService.removeWish(member, productId);
        return ResponseEntity.noContent().build();
    }
}