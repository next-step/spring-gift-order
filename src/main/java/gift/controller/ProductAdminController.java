package gift.controller;

import gift.dto.product.ProductRequestDto;
import gift.entity.Product;
import gift.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/products")
public class ProductAdminController {

    private final ProductService service;

    public ProductAdminController(ProductService service) {
        this.service = service;
    }

    // 전체 목록 조회
    @GetMapping
    public String list(Pageable pageable, Model model) {
        Page<Product> productPage = service.getAll(pageable);
        model.addAttribute("products", service.getAll(pageable).getContent());
        return "admin/list";
    }

    // 추가 폼
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("productRequestDto", new ProductRequestDto("", 0L, ""));
        return "admin/create";
    }

    // 상품 추가
    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("productRequestDto") ProductRequestDto dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "admin/create";
        }

        Product product = new Product(dto.name(), dto.price(), dto.imageUrl());
        service.createProduct(product);
        return "redirect:/admin/products";
    }

    // 수정 폼
    @GetMapping("/{id}/update")
    public String updateForm(@PathVariable Long id, Model model) {
        Product product = service.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품 없음"));
        ProductRequestDto dto = new ProductRequestDto(
                product.getName(),
                product.getPrice(),
                product.getImageUrl()
        );
        model.addAttribute("product", dto);
        model.addAttribute("productId", id);
        return "admin/update";
    }

    // 상품 수정
    @PostMapping("/{id}/update")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("product") ProductRequestDto dto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("productRequestDto", dto);
            model.addAttribute("productId", id);
            return "admin/update";
        }

        Product product = new Product(dto.name(), dto.price(), dto.imageUrl());
        service.update(id, product)
                .orElseThrow(() -> new IllegalArgumentException("수정 실패"));
        return "redirect:/admin/products";
    }

    // 상품 삭제
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        boolean deleted = service.delete(id);
        if (!deleted) {
            throw new IllegalArgumentException("삭제 실패");
        }
        return "redirect:/admin/products";
    }
}
