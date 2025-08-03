package gift.product.controller;

import gift.product.dto.CreateOptionRequest;
import gift.product.dto.OptionResponseDto;
import gift.product.dto.ProductRequestDto;
import gift.product.dto.ProductResponseDto;
import gift.product.repository.ProductRepository;
import gift.product.service.OptionService;
import gift.product.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductRestController {
    private final ProductService productService;
    private final OptionService optionService;

    public ProductRestController(ProductService productService, OptionService optionService) {
        this.productService = productService;
        this.optionService = optionService;
    }

    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(@Valid @RequestBody ProductRequestDto requestDto) {
        return new ResponseEntity<>(productService.saveProduct(requestDto), HttpStatus.CREATED);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponseDto> getProduct(@PathVariable("productId") Long productId) {
        return new ResponseEntity<>(productService.findProductById(productId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> getAllProducts() {
        return new ResponseEntity<>(productService.findAllProducts(), HttpStatus.OK);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponseDto> updateProduct(
        @PathVariable("productId") Long productId,
        @Valid @RequestBody ProductRequestDto requestDto
    ) {
        productService.updateProduct(productId, requestDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("productId") Long productId) {
        productService.deleteProductById(productId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllProducts() {
        productService.deleteAllProducts();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{productId}/options")
    public ResponseEntity<List<OptionResponseDto>> getProductOptions(@PathVariable("productId") Long productId) {
        List<OptionResponseDto> options = optionService.getOptionsByProductId(productId);
        return new ResponseEntity<>(options, HttpStatus.OK);
    }

    @PostMapping("/{productId}/options")
    public ResponseEntity<OptionResponseDto> addOption(
        @PathVariable("productId") Long productId,
        @Valid @RequestBody CreateOptionRequest requestDto
    ) {
        OptionResponseDto createdOption = optionService.addOptionToProduct(productId, requestDto);
        return new ResponseEntity<>(createdOption, HttpStatus.CREATED);
    }
}
