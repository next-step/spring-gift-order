package gift.Controller;

import gift.Entity.Member;
import gift.Entity.Option;
import gift.Entity.Product;
import gift.Entity.Wish;
import gift.annotation.LoginMember;
import gift.service.ProductService;
import gift.service.WishService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/user/wishes")
public class WishViewController {

    private final WishService wishService;
    private final ProductService productService;

    public WishViewController(WishService wishService, ProductService productService) {
        this.wishService = wishService;
        this.productService = productService;
    }

    // 위시리스트 보기
    @GetMapping
    public String showWishList(@LoginMember Member member,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "5") int size,
                               Model model) {
        if (member == null){
            throw new RuntimeException("로그인이 되어있지 않습니다.");
        }

        Page<Wish> wishPage = wishService.getWishes(member, PageRequest.of(page, size));
        int maxPage = Math.max(3, wishPage.getTotalPages());  // 최소 3페이지

        model.addAttribute("wishPage", wishPage);
        model.addAttribute("maxPage", maxPage);
        return "products/wishlist";
    }

    // 위시리스트에 추가
    @PostMapping("/wish")
    public String addWish(@RequestParam Long optionId ,@LoginMember Member member) {
        Option option = productService.findOptionById(optionId);
        Product product = option.getProduct();
        wishService.addWish(member, product, option);
        return "redirect:/user/products";
    }

    // 위시리스트에서 제거
    @PostMapping("/delete")
    public String removeWish(@RequestParam Long optionId, @LoginMember Member member) {
        Option option = productService.findOptionById(optionId);
        Product product = option.getProduct();
        wishService.removeWish(member, product, option);
        return "redirect:/user/products";
    }
}
