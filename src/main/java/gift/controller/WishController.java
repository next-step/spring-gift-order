package gift.controller;


import gift.dto.request.WishAddRequestDto;
import gift.dto.request.WishDeleteRequestDto;
import gift.dto.request.WishUpdateRequestDto;
import gift.dto.response.WishIdResponseDto;
import gift.dto.response.WishResponseDto;
import gift.service.WishService;
import gift.wishPreProcess.LoginMember;
import jakarta.validation.Valid;
import java.util.List;
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

@RestController
@RequestMapping("/api/wishes")
public class WishController {

    private final WishService wishService;

    public WishController(WishService wishService) {
        this.wishService = wishService;
    }

    @PostMapping("")
    public ResponseEntity<WishIdResponseDto> addToWish(
        @RequestBody @Valid WishAddRequestDto wishAddRequestDto,
        @LoginMember String userEmail
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(wishService.addProduct(wishAddRequestDto, userEmail));
    }

    @GetMapping("")
    public ResponseEntity<List<WishResponseDto>> getWishItem(
        @LoginMember String userEmail,
        @RequestParam(required = false, defaultValue = "0", value = "page") int pageNo,
        @RequestParam(required = false, defaultValue = "id", value = "sortBy") String sortBy
    ) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(wishService.getWishList(userEmail, pageNo, sortBy));
    }

    @DeleteMapping("/{wishId}")
    public ResponseEntity<Void> deleteWish(
        @RequestBody @Valid WishDeleteRequestDto productName,
        @LoginMember String userEmail,
        @PathVariable Long wishId) {

        wishService.deleteProduct(userEmail, wishId, productName);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/{wishId}")
    public ResponseEntity<Void> updateWish(
        @RequestBody @Valid WishUpdateRequestDto wishUpdateRequestDto,
        @LoginMember String userEmail,
        @PathVariable Long wishId
    ) {
        wishService.updateProduct(wishId, userEmail, wishUpdateRequestDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
