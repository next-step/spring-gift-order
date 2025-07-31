package gift.controller.api;

import gift.common.code.CustomResponseCode;
import gift.common.dto.CustomResponseBody;
import gift.dto.Pagination.PageResponse;
import gift.dto.Pagination.Pagination;
import gift.dto.Product.ProductRequest;
import gift.dto.Product.ProductResponse;
import gift.dto.Product.ProductUpdateRequest;
import gift.service.Product.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<CustomResponseBody<ProductResponse>> createProduct(
        @Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.create(request);

        return ResponseEntity
            .status(201)
            .body(CustomResponseBody.of(CustomResponseCode.CREATED, response));
    }

    @GetMapping
    public ResponseEntity<CustomResponseBody<PageResponse<ProductResponse>>> getAllProducts(
        @Valid @ModelAttribute Pagination pagination
    ) {
        PageResponse<ProductResponse> responses = productService.getAllProducts(pagination);

        return ResponseEntity
            .status(200)
            .body(CustomResponseBody.of(CustomResponseCode.LIST_RETRIEVED, responses));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<CustomResponseBody<ProductResponse>> getProduct(
        @PathVariable Long productId) {
        ProductResponse response = productService.getProduct(productId);

        return ResponseEntity
            .status(200)
            .body(CustomResponseBody.of(CustomResponseCode.RETRIEVED, response));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<CustomResponseBody<ProductResponse>> updateProduct(
        @PathVariable Long productId,
        @Valid @RequestBody ProductUpdateRequest request
    ) {
        ProductResponse response = productService.update(productId, request);

        return ResponseEntity
            .status(200)
            .body(CustomResponseBody.of(CustomResponseCode.UPDATED, response));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<CustomResponseBody<Void>> deleteProduct(@PathVariable Long productId) {
        productService.delete(productId);

        return ResponseEntity
            .status(204)
            .body(CustomResponseBody.of(CustomResponseCode.DELETED));
    }
}
