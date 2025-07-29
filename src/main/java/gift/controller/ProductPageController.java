package gift.controller;

import gift.domain.Product;
import gift.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/products")
public class ProductPageController {

    private final ProductService productService;

    public ProductPageController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("products", productService.findAll());
        return "Products";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("product",
                new Product());
        return "Productform";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute Product product, BindingResult result) {

        if (result.hasErrors()) {
            return "Productform";
        }

        productService.create(product);
        return "redirect:/products";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Product product = productService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. id=" + id));
        model.addAttribute("product", product);
        return "Productform";
    }

    @PostMapping("/{id}")
    public String edit(@PathVariable Long id, @Valid @ModelAttribute Product updated, BindingResult result) {
        if (result.hasErrors()) {
            return "Productform";
        }

        productService.update(id, updated);
        return "redirect:/products";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        productService.delete(id);
        return "redirect:/products";
    }
}
