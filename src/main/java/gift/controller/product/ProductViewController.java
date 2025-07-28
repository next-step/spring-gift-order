package gift.controller.product;

import gift.dto.product.ProductRequestDto;
import gift.dto.product.ProductResponseDto;
import gift.service.product.ProductService;
import gift.service.wishlist.WishListService;
import gift.util.JwtUtil;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/view")
public class ProductViewController {

    private final JwtUtil jwtUtil;
    private final ProductService productService;
    private final WishListService wishListService;

    public ProductViewController(JwtUtil jwtUtil, ProductService productService,
        WishListService wishListService) {
        this.jwtUtil = jwtUtil;
        this.productService = productService;
        this.wishListService = wishListService;
    }

    @GetMapping("/products")
    public String showProducts(Model model) {
        Page<ProductResponseDto> productList = productService.findAll(0, 10);

        model.addAttribute("productList", productList);

        if (!model.containsAttribute("requestDto")) {
            model.addAttribute("requestDto", new ProductRequestDto(null, 0, 1, "", List.of()));
        }

        if (!model.containsAttribute("updateDto")) {
            model.addAttribute("updateDto", new ProductRequestDto(null, 0, 1, "", List.of()));
        }

        if (!model.containsAttribute("productId")) {
            model.addAttribute("productId", null);
        }

        return "home";
    }

    @PostMapping("/products")
    public String createProduct(@ModelAttribute("requestDto") @Valid ProductRequestDto requestDto,
        BindingResult bindingResult,
        Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("openCreateModal", true);
            model.addAttribute("requestDto", requestDto);
            model.addAttribute("updateDto", new ProductRequestDto(null, 0, 1, "", List.of()));
            model.addAttribute("productList", productService.findAll(0, 10));
            return "home";
        }
        productService.create(requestDto);

        return "redirect:/view/products";
    }

    @PostMapping("/products/update/{id}")
    public String updateProduct(
        @PathVariable Long id,
        @ModelAttribute("updateDto") @Valid ProductRequestDto requestDto,
        BindingResult bindingResult,
        RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("openUpdateModal", true);
            redirectAttributes.addFlashAttribute("productId", id);
            redirectAttributes.addFlashAttribute("updateDto", requestDto);
            redirectAttributes.addFlashAttribute("productList", productService.findAll(0, 10));
            redirectAttributes.addFlashAttribute(
                "org.springframework.validation.BindingResult.updateDto",
                bindingResult
            );

            if (!redirectAttributes.containsAttribute("requestDto")) {
                redirectAttributes.addFlashAttribute("requestDto",
                    new ProductRequestDto(null, 0, 1, "", List.of()));
            }

            return "redirect:/view/products";
        }

        productService.update(id,
            new ProductRequestDto(requestDto.name(), requestDto.price(), requestDto.quantity(),
                requestDto.imageUrl(), List.of()));

        return "redirect:/view/products";
    }

    @DeleteMapping("/products/{id}")
    public String deleteProduct(
        @PathVariable Long id,
        Model model
    ) {
        productService.delete(id);

        if (!model.containsAttribute("requestDto")) {
            model.addAttribute("requestDto", new ProductRequestDto(null, 0, 1, "", List.of()));
        }

        if (!model.containsAttribute("updateDto")) {
            model.addAttribute("updateDto", new ProductRequestDto(null, 0, 1, "", List.of()));
        }

        if (!model.containsAttribute("productId")) {
            model.addAttribute("productId", null);
        }

        return "home";
    }

    @GetMapping("/wishlist/select")
    public String showWishListSelectPage(
        @CookieValue("token") String jwtToken,
        Model model
    ) {
        Page<ProductResponseDto> productList = productService.findAll(0, 10);

        Long memberId = jwtUtil.getMemberIdFromToken(jwtToken);
        Page<ProductResponseDto> wishPage = wishListService.findAll(memberId, 0, 10);
        Set<Long> wishedProductIds = wishPage.getContent().stream()
            .map(ProductResponseDto::id)
            .collect(Collectors.toSet());

        model.addAttribute("productList", productList);
        model.addAttribute("wishedProductIds", wishedProductIds);

        return "wishlist-add";
    }

    @GetMapping("/wishlist/order/{id}")
    public String showWishListOrderPage(
        @CookieValue("token") String jwtToken,
        @PathVariable Long id,
        Model model
    ) {
        ProductResponseDto productResponseDto = productService.findById(id);
        model.addAttribute("productResponseDto", productResponseDto);

        return "product-order";
    }
}