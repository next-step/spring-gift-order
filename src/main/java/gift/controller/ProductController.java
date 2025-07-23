package gift.controller;

import gift.dto.ProductRequest;
import gift.dto.ProductResponse;
import gift.entity.Product;
import gift.enums.ProductSortKey;
import gift.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getProductPage(@RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "10") int size,
                                                                @RequestParam(defaultValue = "name-asc") String sortKey)
    {

        List<ProductResponse> productPage = productService.getProductPage(page, size, ProductSortKey.from(sortKey));
        return new ResponseEntity<>(productPage, HttpStatus.OK);
    }

    // 상품 id 기반 조회
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Optional<Product> product = productService.getProductById(id);

        if (product.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(product.get(), HttpStatus.OK);
    }

    // 상품 등록
    @PostMapping
    public ResponseEntity<Product> postProduct(@Valid @RequestBody ProductRequest productRequest) {
        return new ResponseEntity<>(productService.createProduct(productRequest), HttpStatus.CREATED);
    }

    // 상품 수정
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@Valid @PathVariable Long id, @Valid @RequestBody ProductRequest productRequest) {
        Product updated = productService.updateProduct(id, productRequest);
        return ResponseEntity.ok(updated);
    }

    // 상품 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
