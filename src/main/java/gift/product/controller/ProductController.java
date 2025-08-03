package gift.product.controller;

import gift.product.dto.ProductResponseDto;
import gift.product.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * 상품 목록 페이지를 보여줍니다.
     * 페이지네이션과 정렬 기능을 지원합니다.
     */
    @GetMapping
    public String showProductListPage(Model model,
                                      @PageableDefault(size = 10, page = 0, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        // 1. 서비스에서 Pageable 객체를 사용하여 상품 데이터를 조회합니다.
        Page<ProductResponseDto> products = productService.findAll(pageable);
        model.addAttribute("products", products);

        String currentSort = pageable.getSort().stream()
            .map(order -> order.getProperty() + "," + order.getDirection().name().toLowerCase())
            .findFirst()
            .orElse("id,desc");

        model.addAttribute("currentSort", currentSort);
        model.addAttribute("currentSize", pageable.getPageSize());

        return "user/product/list";
    }

    @GetMapping("/{id}")
    public String showProductDetailPage(@PathVariable("id") Long productId, Model model) {
        ProductResponseDto product = productService.findProductById(productId);
        model.addAttribute("product", product);
        return "user/product/detail";
    }
}
