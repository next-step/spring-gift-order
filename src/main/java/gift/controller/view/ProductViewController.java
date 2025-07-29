package gift.controller.view;

import gift.common.aop.annotation.PreAuthorize;
import gift.common.mapper.ModelMapper;
import gift.common.model.CustomPage;
import gift.dto.CustomPageRequest;
import gift.entity.Product;
import gift.entity.type.UserRole;
import gift.service.product.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/products")
public class ProductViewController {
    private final ProductService productService;

    public ProductViewController(
            ProductService productService
    ) {
        this.productService = productService;
    }

    @PreAuthorize(UserRole.ROLE_ADMIN)
    @GetMapping
    public String showProductList(
        Model model,
        @Valid @ModelAttribute CustomPageRequest request
    ) {
        CustomPage<Product> currentPage = productService.findAllBy(ModelMapper.toPageRequest(request));
        int start = Math.max(0, currentPage.getPage() - 2);
        int end = Math.min(currentPage.getTotalPages() - 1, currentPage.getPage() + 2);

        model.addAttribute("title", "관리자 상품 목록");
        model.addAttribute("pageInfo", currentPage);
        model.addAttribute("pageStart", start);
        model.addAttribute("pageEnd", end);
        model.addAttribute("baseUrl", "/admin/products");

        return "admin/product/product-list";
    }

    @PreAuthorize(UserRole.ROLE_ADMIN)
    @GetMapping("/{id}")
    public String showProductDetails(
            @PathVariable @Min(value = 0, message = "상품 ID는 0 이상이어야 합니다.") Long id,
            Model model
    ) {
        Product product = productService.findById(id);

        model.addAttribute("title", "상품 상세 정보");
        model.addAttribute("product", product);
        return "admin/product/product-detail";
    }

    @PreAuthorize(UserRole.ROLE_ADMIN)
    @GetMapping("/create")
    public String createProductForm(Model model) {
        model.addAttribute("title", "상품 등록");
        return "admin/product/product-create";
    }

}
