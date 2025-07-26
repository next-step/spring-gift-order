package gift.controller.wishlist;

import gift.service.wishlist.WishListService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WishListViewController {
    private final WishListService wishListService;

    public WishListViewController(WishListService wishListService) {
        this.wishListService = wishListService;
    }
}
