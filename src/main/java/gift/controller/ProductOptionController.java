package gift.controller;

import gift.domain.ProductOption;
import gift.domain.ProductOptionRequest;
import gift.service.ProductOptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductOptionController {

    private final ProductOptionService optionService;

    public ProductOptionController(ProductOptionService optionService) {
        this.optionService = optionService;
    }

    @GetMapping("/{id}/options")
    public List<ProductOption> getOptions(@PathVariable Long id) {
        return optionService.getOptionsByProductId(id);
    }

    @PostMapping("/{id}/options")
    public ResponseEntity<Void> addOption(
            @PathVariable Long id,
            @RequestBody ProductOptionRequest request
    ) {
        optionService.addOptionToProduct(id, request.getName(), request.getQuantity());
        return ResponseEntity.ok().build();
    }
}
