package gift.controller;

import gift.common.exception.InvalidUserException;
import gift.domain.product.Product;
import gift.dto.product.CreateProductOptionRequest;
import gift.dto.product.CreateProductRequest;
import gift.dto.product.ProductResponse;
import gift.dto.product.UpdateProductRequest;
import gift.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductApiController {

    private final ProductService productService;

    public ProductApiController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody @Valid CreateProductRequest request) {
        validProductName(request.name());
        Product product = productService.saveProduct(request);
        ProductResponse response = ProductResponse.from(product);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getProducts(
            @RequestParam(required = false) Long cursor,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        List<ProductResponse> products = productService.getAllProducts(cursor, size);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateProduct(@PathVariable Long id, @RequestBody @Valid UpdateProductRequest request) {
        validProductName(request.name());
        productService.updateProduct(id, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/options")
    public ResponseEntity<ProductResponse> addProductOption(@PathVariable Long id, @RequestBody @Valid CreateProductOptionRequest request) {
        Product product = productService.addOption(id, request);
        ProductResponse response = ProductResponse.from(product);
        return ResponseEntity.ok(response);
    }

    private void validProductName(String name) {
        if (name.contains("카카오")) {
            throw new InvalidUserException("이름에 [카카오]가 들어간 상품은 문의가 필요합니다.");
        }
    }
}
