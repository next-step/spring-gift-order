package gift.controller;

import gift.dto.ProductRequestDto;
import gift.dto.ProductResponseDto;
import gift.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // 1. 상품 추가
    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(@Valid @RequestBody ProductRequestDto dto) {

        ProductResponseDto response = productService.create(dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    // 2-1. 상품 전체 조회
    @GetMapping
    public ResponseEntity<Page<ProductResponseDto>> getProducts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductResponseDto> products = productService.findAll(pageable);
        return ResponseEntity.status(HttpStatus.OK)
                .body(products);
    }

    // 2-2. 상품 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProduct(@PathVariable Long id) {
        ProductResponseDto response = productService.findById(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    // 3. 상품 수정
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> updateProduct(@Valid
    @PathVariable Long id,
            @RequestBody ProductRequestDto dto
    ) {
        ProductResponseDto response = productService.update(id, dto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    // 4. 상품 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.delete(id);

        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }
}