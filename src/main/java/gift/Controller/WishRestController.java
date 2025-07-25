package gift.Controller;

import gift.Entity.Member;
import gift.Entity.Option;
import gift.Entity.Product;
import gift.annotation.LoginMember;
import gift.request.WishRequest;
import gift.response.ProductResponse;
import gift.response.WishResponse;
import gift.service.ProductService;
import gift.service.WishService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wishes")
public class WishRestController {

    private final WishService wishService;
    private final ProductService productService;

    public WishRestController(WishService wishService, ProductService productService) {
        this.wishService = wishService;
        this.productService = productService;
    }

    @GetMapping
    public Page<WishResponse> getWishes(@LoginMember Member member, Pageable pageable) {
        return wishService.getWishes(member, pageable)
                .map(WishResponse::new);
    }

    @PostMapping
    public void addWish(@RequestBody WishRequest request, @LoginMember Member member) {
        Product product = productService.findById(request.getProductId());
        Option option = productService.findOptionById(request.getOptionId());
        wishService.addWish(member, product, option);
    }

    @DeleteMapping
    public void removeWish(@RequestBody WishRequest request, @LoginMember Member member) {
        Product product = productService.findById(request.getProductId());
        Option option = productService.findOptionById(request.getOptionId());
        wishService.removeWish(member, product, option);
    }
}
