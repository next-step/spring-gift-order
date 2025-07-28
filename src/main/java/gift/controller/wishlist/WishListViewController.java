package gift.controller.wishlist;

import gift.dto.product.ProductResponseDto;
import gift.entity.LoginMember;
import gift.entity.Member;
import gift.service.product.ProductService;
import gift.service.wishlist.WishListService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/view")
public class WishListViewController {
    private final ProductService productService;
    private final WishListService wishListService;

    public WishListViewController(ProductService productService, WishListService wishListService) {
        this.productService = productService;
        this.wishListService = wishListService;
    }

    @GetMapping("/wishlist/order")
    public String showWishListPage(
        Model model,
        @LoginMember Member member
    ) {

        Page<ProductResponseDto> productList = wishListService.findAll(member.getId(),0, 10);
        model.addAttribute("productList", productList);

        return "wishlist-order";
    }
}
