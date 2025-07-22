package gift.controller.wishlist;

import gift.dto.product.ProductResponseDto;
import gift.dto.wishlist.WishListResponseDto;
import gift.entity.LoginMember;
import gift.entity.Member;
import gift.service.wishlist.WishListService;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wishes")
public class WishListController {

    private final WishListService wishListService;

    public WishListController(WishListService wishListService) {
        this.wishListService = wishListService;
    }

    @PostMapping("/{productId}")
    public ResponseEntity<WishListResponseDto> create(
        @PathVariable Long productId,
        @LoginMember Member member
    ) {
        WishListResponseDto responseDto = wishListService.create(productId, member.getId());

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> findAll(
        @LoginMember Member member,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        Page<ProductResponseDto> productPage = wishListService.findAll(member.getId(), page, size);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Page-Number", String.valueOf(productPage.getNumber()));
        headers.add("X-Page-Size", String.valueOf(productPage.getSize()));

        return ResponseEntity.ok()
            .headers(headers)
            .body(productPage.getContent());
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> delete(
        @PathVariable Long productId,
        @LoginMember Member member
    ) {
        wishListService.delete(productId, member.getId());

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}