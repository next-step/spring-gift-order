package gift.controller;

import gift.dto.PageResponse;
import gift.dto.Pagination;
import gift.dto.ProductOptionRequest;
import gift.dto.ProductRequest;
import gift.dto.ProductResponse;
import gift.service.ProductService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {

    private final ProductService productService;

    public AdminProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String showProducts(
        @Valid @ModelAttribute Pagination pagination,
        Model model
    ) {
        PageResponse<ProductResponse> productPage = productService.getAllProducts(pagination);

        model.addAttribute("productPage", productPage);
        return "admin/products";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        List<ProductOptionRequest> defaultOptions = List.of(new ProductOptionRequest("", 1L));
        model.addAttribute("product", new ProductRequest("", null, "", defaultOptions));
        return "admin/product-form";
    }

    @PostMapping
    public String createProduct(@Valid @ModelAttribute("product") ProductRequest request,
        BindingResult result) {

        if (result.hasErrors()) {
            return "admin/product-form";
        }

        productService.create(request);
        return "redirect:/admin/products";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        ProductResponse product = productService.getProduct(id);
        model.addAttribute("product", ProductRequest.from(product));
        model.addAttribute("productId", id);
        return "admin/product-form";
    }

    @PostMapping("/{id}")
    public String updateProduct(@PathVariable Long id,
        @Valid @ModelAttribute("product") ProductRequest request,
        BindingResult result,
        Model model) {

        if (result.hasErrors()) {
            model.addAttribute("productId", id);
            return "admin/product-form";
        }

        productService.update(id, request);
        return "redirect:/admin/products";
    }

    @PostMapping("/{id}/delete")
    public String deleteProduct(@PathVariable Long id) {
        productService.delete(id);
        return "redirect:/admin/products";
    }
}
