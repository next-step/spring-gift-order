package gift.controller.product;

import gift.dto.product.ProductRequestDto;
import gift.dto.product.ProductResponseDto;
import gift.service.product.ProductService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
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
class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(
        @RequestBody @Valid ProductRequestDto requestDto) {
        return new ResponseEntity<>(productService.create(requestDto), HttpStatus.CREATED);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponseDto> findProduct(@PathVariable Long productId) {
        return new ResponseEntity<>(productService.findById(productId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> findAll(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        Page<ProductResponseDto> productPage = productService.findAll(page, size);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Page-Number", String.valueOf(productPage.getNumber()));
        headers.add("Page-Size", String.valueOf(productPage.getSize()));
        headers.add("Total-Count", String.valueOf(productPage.getTotalElements()));

        return ResponseEntity.ok()
            .headers(headers)
            .body(productPage.getContent());
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponseDto> updateProduct(
        @PathVariable Long productId,
        @RequestBody @Valid ProductRequestDto requestDto
    ) {
        return new ResponseEntity<>(productService.update(productId, requestDto),
            HttpStatus.OK);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {

        productService.delete(productId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
