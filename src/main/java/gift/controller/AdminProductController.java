package gift.controller;

import gift.domain.Product;
import gift.dto.ProductRequest;
import gift.dto.ProductResponse;
import gift.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {

    private final ProductService productService;

    public AdminProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String list(Model model) {
        List<Product> products = productService.getAll();
        List<ProductResponse> responses = products.stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());
        model.addAttribute("products", responses);
        return "admin/product-list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Product product = productService.getById(id);
        ProductResponse response = ProductResponse.from(product);
        model.addAttribute("product", response);
        return "admin/product-detail";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        ProductRequest empty = new ProductRequest("", 0, "");
        model.addAttribute("productRequest", empty);
        return "admin/product-form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute ProductRequest productRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "admin/product-form";
        }
        productService.create(productRequest.toEntity());
        return "redirect:/admin/products";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable(name = "id") Long productId, Model model) {
        Product product = productService.getById(productId);
        ProductRequest dto = new ProductRequest(product.getName(), product.getPrice(), product.getImageUrl());
        model.addAttribute("productRequest", dto);
        model.addAttribute("productId", productId);
        return "admin/product-form";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable Long id, @Valid @ModelAttribute ProductRequest request, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("productId", id);
            return "admin/product-form";
        }
        productService.update(id, request);
        return "redirect:/admin/products";
    }


    @PostMapping("/{id}/delete")
    public String delete(@PathVariable(name = "id") Long productId) {
        productService.delete(productId);
        return "redirect:/admin/products";
    }

}
