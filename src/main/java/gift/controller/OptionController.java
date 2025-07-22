package gift.controller;

import gift.dto.option.OptionRequestDto;
import gift.dto.option.OptionResponseDto;
import gift.service.OptionService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class OptionController {

    private final OptionService optionService;

    public OptionController(OptionService optionService) {
        this.optionService = optionService;
    }

    @PostMapping("/products/{productId}/options")
    public ResponseEntity<OptionResponseDto> addOptionToProduct(
            @PathVariable Long productId,
            @Valid @RequestBody OptionRequestDto requestDto
    ) {
        OptionResponseDto responseDto = optionService.addOptionToProduct(productId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/products/{productId}/options")
    public ResponseEntity<List<OptionResponseDto>> getOptionsByProductId(
            @PathVariable Long productId
    ) {
        List<OptionResponseDto> options = optionService.getOptionsByProductId(productId);
        return ResponseEntity.ok(options);
    }

    @DeleteMapping("/options/{optionId}")
    public ResponseEntity<Void> deleteOption(@PathVariable Long optionId) {
        optionService.deleteOption(optionId);
        return ResponseEntity.noContent().build();
    }
}