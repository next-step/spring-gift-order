package gift.controller.product;

import gift.dto.IdResponse;
import gift.dto.product.ProductRequest;
import gift.dto.product.ProductResponse;
import gift.service.product.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductApiController {

    private final ProductService productService;

    public ProductApiController(ProductService productService) {
        this.productService = productService;
    }

    // 상품 조회
    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProduct(
        @PathVariable Long productId
    ) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(productService.getProductById(productId));
    }

    // 상품 목록 조회
    @GetMapping("/all")
    public ResponseEntity<Page<ProductResponse>> getProductList(
        @PageableDefault(page = 0, size = 3, sort = "id", direction = Sort.Direction.ASC)
        Pageable pageable
    ) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(productService.getProductPage(pageable));
    }

    // 상품 생성
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
        @Valid @RequestBody ProductRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(productService.insert(request));
    }

    // 상품 수정
    @PatchMapping
    public ResponseEntity<?> updateProduct(
        @Valid @RequestBody ProductRequest request
    ) {
        productService.update(request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // 상품 삭제
    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteProduct(
        @PathVariable Long productId
    ) {
        productService.deleteById(productId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

