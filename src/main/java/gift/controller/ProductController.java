package gift.controller;

import gift.dto.request.ProductRequestDto;
import gift.dto.request.ProductUpdateRequestDto;
import gift.dto.response.ProductResponseDto;
import gift.service.ProductService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api")
class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<ProductResponseDto> getProduct(@PathVariable long productId) {
        return new ResponseEntity<>(
            productService.productToResponseDto(productService.getProduct(productId)),
            HttpStatus.OK);
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductResponseDto>> getAllProducts(
        @RequestParam(required = false, defaultValue = "0", value = "page") int page,
        @RequestParam(required = false, defaultValue = "name", value = "sortBy") String sortBy) {
        return new ResponseEntity<>(
            productService.getAllProducts(page, sortBy).stream()
                .map(productService::productToResponseDto)
                .collect(Collectors.toList())
            , HttpStatus.OK);
    }

    @PostMapping("/products")
    public ResponseEntity<ProductResponseDto> createProduct(
        @RequestBody @Valid ProductRequestDto productRequestDto) {
        return new ResponseEntity<>(productService.createProduct(productRequestDto),
            HttpStatus.CREATED);
    }

    @PatchMapping("/products/{productId}")
    public ResponseEntity<ProductResponseDto> updateProduct(
        @PathVariable long productId,
        @RequestBody @Valid ProductUpdateRequestDto productUpdateRequestDto) {

        return new ResponseEntity<>(
            productService.updateProduct(productId, productUpdateRequestDto), HttpStatus.OK);
    }

    @DeleteMapping("/products/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable long productId) {

        productService.deleteProduct(productId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}