package gift.controller;

import gift.domain.Product;
import gift.dto.ProductRequest;
import gift.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {

    private final ProductService productService;

    public AdminProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String list(Model model, Pageable pageable) {
        Page<Product> products = productService.findAll(pageable);
        model.addAttribute("products", products);
        return "product/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("product", new ProductRequest());
        return "product/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Product p = productService.findById(id)
                .orElseThrow(() -> new NoSuchElementException("상품을 찾을 수 없습니다."));
        model.addAttribute("product", new ProductRequest(
                p.getId(), p.getName(), p.getPrice(), p.getImageUrl()
        ));
        return "product/form";
    }

    @PostMapping
    public String save(
            @ModelAttribute("product") @Valid ProductRequest product,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return "product/form";
        }
        productService.save(new Product(
                null,
                product.getName(),
                product.getPrice(),
                product.getImageUrl()
        ));
        return "redirect:/admin/products";
    }

    @PostMapping("/{id}")
    public String update(
            @PathVariable Long id,
            @ModelAttribute("product") @Valid ProductRequest product,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return "product/form";
        }
        productService.update(id, new Product(
                id,
                product.getName(),
                product.getPrice(),
                product.getImageUrl()
        ));
        return "redirect:/admin/products";
    }


    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        productService.delete(id);
        return "redirect:/admin/products";
    }
}