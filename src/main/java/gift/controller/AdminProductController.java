package gift.controller;

import gift.dto.ProductRequestDto;
import gift.dto.ProductResponseDto;
import gift.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller // @RestController는 Json 데이터 반환, @Controller는 html 화면 반환
@RequestMapping("/admin/products")
public class AdminProductController {

    private final ProductService productService;

    public AdminProductController(ProductService productService) {
        this.productService = productService;
    }

    // 1-1. 상품 등록 화면
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("product", new ProductRequestDto());
        model.addAttribute("actionUrl", "/admin/products/new");
        return "admin/form";
    }

    // 1-2.상품 등록 처리
    @PostMapping("/new")
    public String create(@Valid @ModelAttribute("product") ProductRequestDto dto,
            BindingResult br,
            Model model) {
        if (br.hasErrors()) {
            model.addAttribute("actionUrl", "/admin/products/new");
            return "admin/form";
        }

        try {
            productService.create(dto);
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("actionUrl", "/admin/products/new");
            return "admin/form";
        }
        return "redirect:/admin/products";
    }


    // 2. 상품 목록
    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, Model model) {

        Pageable pageable = PageRequest.of(page, size);

        Page<ProductResponseDto> products = productService.findAll(pageable);

        model.addAttribute("products", products);

        return "admin/list";
    }

    // 3-1. 상품 수정 폼
    @GetMapping("{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        ProductResponseDto product = productService.findById(id);
        model.addAttribute("product", product);
        model.addAttribute("actionUrl", "/admin/products/" + id + "/edit");
        return "admin/form";
    }

    // 3-2. 상품 수정 처리
    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("product") ProductRequestDto dto,
            BindingResult br, Model model) {
        if (br.hasErrors()) {
            model.addAttribute("actionUrl", "/admin/products/" + id + "/edit");
            return "admin/form";
        }

        try {
            productService.update(id, dto);
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("actionUrl", "/admin/products/" + id + "/edit");
            return "admin/form";
        }
        return "redirect:/admin/products";
    }

    // 4. 상품 삭제
    @PostMapping("{id}/delete")
    public String delete(@PathVariable Long id) {
        productService.delete(id);
        return "redirect:/admin/products";
    }
}
