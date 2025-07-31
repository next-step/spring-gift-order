package gift.controller.api;

import gift.auth.resolver.CurrentUser;
import gift.common.code.CustomResponseCode;
import gift.common.dto.CustomResponseBody;
import gift.dto.Pagination.PageResponse;
import gift.dto.Pagination.Pagination;
import gift.dto.Wish.WishRequest;
import gift.dto.Wish.WishResponse;
import gift.entity.Member.Member;
import gift.service.Wish.WishService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
    public ResponseEntity<CustomResponseBody<WishResponse>> addWish(
        @Valid @RequestBody WishRequest request,
        @CurrentUser Member member
    ) {
        WishResponse response = wishService.addWish(member, request);
        return ResponseEntity
            .status(201)
            .body(CustomResponseBody.of(CustomResponseCode.CREATED, response));
    }

    @GetMapping
    public ResponseEntity<CustomResponseBody<PageResponse<WishResponse>>> getWishes(
        @Valid @ModelAttribute Pagination pagination,
        @CurrentUser Member member
    ) {
        PageResponse<WishResponse> wishes = wishService.getWishes(member, pagination);

        return ResponseEntity
            .status(200)
            .body(CustomResponseBody.of(CustomResponseCode.RETRIEVED, wishes));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<CustomResponseBody<Void>> deleteWish(
        @PathVariable Long productId,
        @CurrentUser Member member
    ) {
        wishService.deleteWish(member, productId);
        return ResponseEntity
            .status(204)
            .body(CustomResponseBody.of(CustomResponseCode.DELETED));
    }
}
