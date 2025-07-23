package gift.controller;

import gift.auth.LoginMember;
import gift.dto.WishRequestDTO;
import gift.dto.WishResponseDTO;
import gift.dto.WishUpdateDTO;
import gift.entity.Member;
import gift.service.WishService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishes")
public class WishController {

    private final WishService wishService;

    public WishController(WishService wishService) {
        this.wishService = wishService;
    }

    @PostMapping
    public ResponseEntity<WishResponseDTO> addWish(
        @Valid @RequestBody WishRequestDTO wishRequestDTO,
        @LoginMember Member member
    ) {
        WishResponseDTO response = wishService.addWish(wishRequestDTO, member);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<WishResponseDTO>> getWishes(
        @LoginMember Member member,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "name,asc") String sort
    ) {
        List<WishResponseDTO> wishes = wishService.getWishes(member, page, size, sort);
        return ResponseEntity.ok(wishes);
    }

    @PatchMapping("/{productId}")
    public ResponseEntity<Void> updateWishQuantity(
        @PathVariable Long productId,
        @Valid @RequestBody WishUpdateDTO wishUpdateDTO,
        @LoginMember Member member
    ) {
        wishService.updateWishQuantity(productId, wishUpdateDTO, member);

        if (wishUpdateDTO.quantity() == 0) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteWish(
        @PathVariable Long productId,
        @LoginMember Member member
    ) {
        wishService.deleteWish(productId, member);
        return ResponseEntity.noContent().build();
    }
}
