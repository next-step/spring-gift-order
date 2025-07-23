package gift.controller;

import gift.dto.ProductRequestDto;
import gift.dto.ProductResponseDto;
import gift.exception.product.MdApprovalException;
import gift.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final ProductService productService;

    public AdminController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String findAll(Model model) {
        List<ProductResponseDto> ProductResponseDtoList = productService.findAllProducts();
        model.addAttribute("products", ProductResponseDtoList);
        return "admin/product-list";
    }

    @GetMapping("/products-add")
    public String showAddProductForm(Model model) {
        model.addAttribute("product", new ProductRequestDto(null, null, null, null));
        return "admin/product-add";
    }

    @PostMapping("/products-add")
    public String createProduct(@Valid @ModelAttribute("product") ProductRequestDto dto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("product", dto);
            return "admin/product-add";
        }
        try {
            productService.saveProduct(dto);
        } catch (MdApprovalException | IllegalArgumentException e) {
            return handleException(e, bindingResult, dto, model, "admin/product-add", null);
        }
        return "redirect:/admin";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        ProductResponseDto product = productService.findProductById(id);
        model.addAttribute("product", product);
        return "admin/product-edit";
    }

    @PostMapping("/{id}")
    public String updateProduct(@PathVariable Long id, @Valid @ModelAttribute("product") ProductRequestDto dto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("product", dto);
            model.addAttribute("id", id);
            return "admin/product-edit";
        }
        try {
            productService.updateProduct(id, dto);
        } catch (MdApprovalException | IllegalArgumentException e) {
            return handleException(e, bindingResult, dto, model, "admin/product-edit", id);
        }
        return "redirect:/admin";
    }

    @PostMapping("/{id}/delete")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/admin";
    }

    private void handleMdApprovalException(MdApprovalException e, BindingResult bindingResult) {
        bindingResult.rejectValue("name", "md.not.approved", e.getMessage());
    }

    private String handleException(Exception e, BindingResult bindingResult, ProductRequestDto dto, Model model, String errorView, Long id) {
        if (e instanceof MdApprovalException) {
            handleMdApprovalException((MdApprovalException) e, bindingResult);
        } else if (e instanceof IllegalArgumentException) {
            bindingResult.rejectValue("name", "invalid.name", e.getMessage());
        }
        model.addAttribute("product", dto);
        if (id != null) {
            model.addAttribute("id", id);
        }
        return errorView;
    }
}