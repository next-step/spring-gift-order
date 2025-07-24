package gift.controller;

import gift.domain.ProductOption;
import gift.dto.ProductOptionResponse;
import gift.service.ProductOptionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/options")
public class ProductOptionController {

    private final ProductOptionService optionService;

    public ProductOptionController(ProductOptionService optionService) {
        this.optionService = optionService;
    }

    @GetMapping("/product/{productId}")
    public List<ProductOptionResponse> getOptionsByProductId(@PathVariable Long productId) {
        List<ProductOption> options = optionService.getOptionsByProductId(productId);
        return options.stream()
                .map(ProductOptionResponse::from)
                .toList();
    }

    @PostMapping("/product/{productId}")
    public void addOption(@PathVariable Long productId, @RequestBody ProductOptionResponse request) {
        optionService.addOptionToProduct(productId, request.getName(), request.getQuantity());
    }

    @PatchMapping("/{optionId}/subtract")
    public void subtractQuantity(@PathVariable Long optionId, @RequestParam Long quantity) {
        optionService.subtractQuantity(optionId, quantity);
    }
}

