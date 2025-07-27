package gift.controller.wishlist;

import gift.service.wishlist.WishListService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/view")
public class WishListViewController {
    private final WishListService wishListService;

    public WishListViewController(WishListService wishListService) {
        this.wishListService = wishListService;
    }

    @GetMapping("/wishlist/order")
    public String showWishListPage(Model model) {

        // TODO: 카카오 로그인 -> JWT 토큰 발급 과정 필요

        return "";
    }
}
