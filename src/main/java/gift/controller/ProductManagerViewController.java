package gift.controller;

import gift.dto.request.ProductRequestDto;
import gift.dto.request.ProductUpdateRequestDto;
import gift.entity.Product;
import gift.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = "/managerHome")
public class ProductManagerViewController {

    private final ProductService productService;

    public ProductManagerViewController(ProductService productService) {
        this.productService = productService;
    }


    @GetMapping()
    public String managerHome() {
        return "products";
    }


    @GetMapping("/{productId}")
    public String getProductById(@PathVariable long productId, Model model) {
        Product product = productService.getProduct(productId);
        model.addAttribute("product", product);
        return "product"; // product.html로 이동
    }


    @PostMapping()
    public String createProduct(
        @ModelAttribute @Valid ProductRequestDto productRequestDto) {
        productService.createProduct(productRequestDto);
        return "redirect:/managerHome";
    }


    @PostMapping("/{productId}")
    public String updateProduct(
        @PathVariable long productId,
        @ModelAttribute @Valid ProductUpdateRequestDto productUpdateRequestDto) {
        productService.updateProduct(productId, productUpdateRequestDto);
        return "redirect:/managerHome";
    }


    @PostMapping("/delete")
    public String deleteProduct(@RequestParam long productId) {
        productService.deleteProduct(productId);
        return "redirect:/managerHome";
    }
}
