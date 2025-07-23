package gift.controller;

import gift.common.interceptor.AdminOnly;
import gift.dto.product.CreateProductRequest;
import gift.dto.product.ProductManageResponse;
import gift.dto.product.UpdateProductRequest;
import gift.service.ProductManageService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/products")
@AdminOnly
public class ProductManageController {

    private final ProductManageService productManageService;

    public ProductManageController(ProductManageService productManageService) {
        this.productManageService = productManageService;
    }

    @GetMapping
    public String getProductsForm(@PageableDefault(sort = "id", direction = Sort.Direction.DESC, page = 1) Pageable pageable, Model model) {
        Page<ProductManageResponse> products = productManageService.getAllProducts(pageable);
        model.addAttribute("products", products);
        String currentSort = pageable.getSort().toString().replace(": ", ",").toLowerCase();
        model.addAttribute("currentSort", currentSort);
        return "/admin/product/productList";
    }

    @GetMapping("/new")
    public String createProductForm(Model model) {
        model.addAttribute("request", CreateProductRequest.empty());
        return "/admin/product/productCreate";
    }


    @PostMapping
    public String createProduct(@ModelAttribute(name = "request") @Valid CreateProductRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "/admin/product/productCreate";
        }
        productManageService.saveProduct(request);
        return "redirect:/admin/products";
    }

    @GetMapping("/{id}/edit")
    public String updateProductForm(@PathVariable Long id, Model model) {
        ProductManageResponse response = productManageService.getProduct(id);
        model.addAttribute("id", id);
        model.addAttribute("request", UpdateProductRequest.from(response));
        return "/admin/product/productUpdate";
    }

    @PostMapping("/{id}")
    public String updateProduct(@PathVariable Long id, @ModelAttribute(name = "request") @Valid UpdateProductRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "/admin/product/productUpdate";
        }
        productManageService.updateProduct(id, request);
        return "redirect:/admin/products";
    }

    @PostMapping("/{id}/delete")
    public String deleteProduct(@PathVariable Long id) {
        productManageService.deleteProduct(id);
        return "redirect:/admin/products";
    }

}
