package gift.controller;

import gift.common.code.CustomResponseCode;
import gift.common.dto.CustomResponseBody;
import gift.dto.ProductOptionRequest;
import gift.dto.ProductOptionResponse;
import gift.service.OptionService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class OptionController {

    private final OptionService optionService;

    public OptionController(OptionService optionService) {
        this.optionService = optionService;
    }

    @PostMapping("/{productId}/options")
    public ResponseEntity<CustomResponseBody<ProductOptionResponse>> addOptionToProduct(
        @PathVariable Long productId,
        @Valid @RequestBody ProductOptionRequest request
    ) {
        ProductOptionResponse response = optionService.add(productId, request);

        return ResponseEntity
            .status(CustomResponseCode.CREATED.getHttpStatus())
            .body(CustomResponseBody.of(CustomResponseCode.CREATED, response));
    }


    @GetMapping("/{productId}/options")
    public ResponseEntity<CustomResponseBody<List<ProductOptionResponse>>> getOptionsByProduct(
        @PathVariable Long productId
    ) {
        List<ProductOptionResponse> responses = optionService.getOptionsByProductId(productId);

        return ResponseEntity
            .status(CustomResponseCode.RETRIEVED.getHttpStatus())
            .body(CustomResponseBody.of(CustomResponseCode.RETRIEVED, responses));
    }
}
