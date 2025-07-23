package gift.controller;

import gift.dto.ProductOptionRequest;
import gift.dto.ProductOptionResponse;
import gift.entity.ProductOption;
import gift.service.ProductOptionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products/{productId}/options")
public class ProductOptionController {
    private final ProductOptionService optionService;

    public ProductOptionController(ProductOptionService optionService) {
        this.optionService = optionService;
    }

    @GetMapping
    public ResponseEntity<List<ProductOptionResponse>> getOptions(@PathVariable Long productId) {
        List<ProductOptionResponse> options = optionService.getOptionsByProductId(productId);
        return ResponseEntity.ok(options);
    }

    @PostMapping
    public ResponseEntity<ProductOptionResponse> createOption(
            @PathVariable Long productId,
            @RequestBody ProductOptionRequest request) {
        ProductOptionResponse response = optionService.createOption(productId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{optionId}/subtract")
    public ResponseEntity<ProductOptionResponse> subtractQuantity(
            @PathVariable Long productId,
            @PathVariable Long optionId,
            @RequestParam int quantity) {
        ProductOption option = optionService.subtractOptionQuantity(productId, optionId, quantity);
        ProductOptionResponse response = new ProductOptionResponse(option);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{optionId}/add")
    public ResponseEntity<ProductOptionResponse> addQuantity(
            @PathVariable Long productId,
            @PathVariable Long optionId,
            @RequestParam int quantity) {
        ProductOption option = optionService.addOptionQuantity(productId, optionId, quantity);
        ProductOptionResponse response = new ProductOptionResponse(option);
        return ResponseEntity.ok(response);
    }
}
