package gift.controller;

import gift.auth.LoginMember;
import gift.dto.ProductResponse;
import gift.dto.WishRequest;
import gift.entity.Member;
import gift.service.WishService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wishes")
public class WishController {

    private final WishService wishService;

    public WishController(WishService wishService) {
        this.wishService = wishService;
    }

    @PostMapping
    public ResponseEntity<Void> addWish(
            @LoginMember Member member,
            @Valid @RequestBody WishRequest request
    ) {
        wishService.addWish(member.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getWishes(
            @LoginMember Member member,
            Pageable pageable
    ) {
        Page<ProductResponse> products = wishService.getWishes(member.getId(), pageable);
        return ResponseEntity.ok(products);
    }

    @DeleteMapping("/products/{productId}")
    public ResponseEntity<Void> deleteWish(
            @LoginMember Member member,
            @PathVariable Long productId
    ) {
        wishService.deleteWish(member.getId(), productId);
        return ResponseEntity.noContent().build();
    }
}
