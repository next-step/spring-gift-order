package gift.controller;

import gift.dto.option.OptionRequestDto;
import gift.dto.option.OptionResponseDto;
import gift.service.OptionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/products/{productId}/options")
public class OptionController {
    private final OptionService optionService;

    public OptionController(OptionService optionService) {
        this.optionService = optionService;
    }

    @PostMapping
    public ResponseEntity<OptionResponseDto> addOptionToProduct(@PathVariable Long productId, @Valid @RequestBody OptionRequestDto requestDto) {
        OptionResponseDto responseDto = optionService.addOptionToProduct(productId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping
    public ResponseEntity<List<OptionResponseDto>> getOptionsByProductId(@PathVariable Long productId) {
        List<OptionResponseDto> options = optionService.getOptionsByProductId(productId);
        return ResponseEntity.ok(options);
    }

    @PutMapping("/{optionId}")
    public ResponseEntity<Void> updateOption(@PathVariable Long productId, @PathVariable Long optionId, @Valid @RequestBody OptionRequestDto requestDto) {
        optionService.updateOption(productId, optionId, requestDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{optionId}")
    public ResponseEntity<Void> deleteOption(@PathVariable Long productId, @PathVariable Long optionId) {
        optionService.deleteOption(productId, optionId);
        return ResponseEntity.noContent().build();
    }
}