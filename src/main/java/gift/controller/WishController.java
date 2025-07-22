package gift.controller;

import gift.common.annotation.CurrentUser;
import gift.common.code.CustomResponseCode;
import gift.common.dto.CustomResponseBody;
import gift.dto.PageResponse;
import gift.dto.Pagination;
import gift.dto.WishRequest;
import gift.dto.WishResponse;
import gift.entity.Member;
import gift.service.WishService;
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
        WishResponse response = wishService.addWish(member.getId(), request);
        return ResponseEntity
            .status(CustomResponseCode.CREATED.getHttpStatus())
            .body(CustomResponseBody.of(CustomResponseCode.CREATED, response));
    }

    @GetMapping
    public ResponseEntity<CustomResponseBody<PageResponse<WishResponse>>> getWishes(
        @Valid @ModelAttribute Pagination pagination,
        @CurrentUser Member member
    ) {
        PageResponse<WishResponse> wishes = wishService.getWishes(member.getId(), pagination);

        return ResponseEntity
            .ok(CustomResponseBody.of(CustomResponseCode.RETRIEVED, wishes));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<CustomResponseBody<Void>> deleteWish(
        @PathVariable Long productId,
        @CurrentUser Member member
    ) {
        wishService.deleteWish(member.getId(), productId);
        return ResponseEntity
            .status(CustomResponseCode.DELETED.getHttpStatus())
            .body(CustomResponseBody.of(CustomResponseCode.DELETED));
    }
}
