package gift.controller;

import gift.annotation.LoginMember;
import gift.domain.Member;
import gift.domain.Product;
import gift.dto.ProductResponse;
import gift.dto.WishRequest;
import gift.service.ProductService;
import gift.service.WishService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wishes")
public class WishController {

    private final WishService wishService;
    private final ProductService productService;

    public WishController(WishService wishService,  ProductService productService) {
        this.productService = productService;
        this.wishService = wishService;
    }

    @GetMapping
    public Page<ProductResponse> getWishes(@LoginMember Member member, Pageable pageable) {
        return wishService.getWishProducts(member.getId(), pageable)
                .map(productService::toResponse);
    }

    @PostMapping
    public void addWish(@RequestBody WishRequest request, @LoginMember Member member) {
        wishService.addWish(member.getId(), request.getProductId(), request.getOptionId());
    }

    @DeleteMapping
    public void removeWish(@RequestBody WishRequest request, @LoginMember Member member) {
        wishService.removeWish(member.getId(), request.getProductId());
    }
}
