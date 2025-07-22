package gift.item.controller;

import gift.item.dto.OptionCreateDto;
import gift.item.dto.OptionResponseDto;
import gift.item.dto.OptionUpdateDto;
import gift.item.service.OptionService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/items/{itemId}")
public class OptionController {

    private final OptionService optionService;

    public OptionController(OptionService optionService) {
        this.optionService = optionService;
    }

    @GetMapping("/options")
    public ResponseEntity<List<OptionResponseDto>> getOptionByItemId(@PathVariable Long itemId) {
        List<OptionResponseDto> optionDtos = optionService.findOptionsByItemId(itemId);
        return ResponseEntity.ok(optionDtos);
    }

    @PostMapping("/options")
    public ResponseEntity<OptionResponseDto> createOption(
        @PathVariable Long itemId,
        @RequestBody @Valid OptionCreateDto optionCreateDto
    ) {
        OptionResponseDto optionDto = optionService.createOption(itemId, optionCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(optionDto);
    }

    @PutMapping("/options/{optionId}")
    public ResponseEntity<OptionResponseDto> updateOption(
        @PathVariable Long optionId,
        @RequestBody @Valid OptionUpdateDto optionUpdateDto
    ) {
        OptionResponseDto optionDto = optionService.updateOption(optionId, optionUpdateDto);
        return ResponseEntity.ok(optionDto);
    }

    @DeleteMapping("/options/{optionId}")
    public ResponseEntity<Void> deleteOption(
        @PathVariable Long optionId
    ) {
        optionService.deleteOption(optionId);
        return ResponseEntity.noContent().build();
    }
    
}
