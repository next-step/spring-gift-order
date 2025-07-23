package gift.controller;

import gift.dto.request.OptionRequestDto;
import gift.dto.response.OptionResponseDto;
import gift.service.OptionService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/api/products")
public class OptionController {

    private final OptionService optionService;

    public OptionController(OptionService optionService) {
        this.optionService = optionService;
    }

    @PostMapping(value = "/{productId}/options")
    public ResponseEntity<OptionResponseDto> addOption(
        @PathVariable("productId") Long productId,
        @Valid @RequestBody OptionRequestDto optionRequestDto) {

        return new ResponseEntity<>(
            optionService.addOption(productId, optionRequestDto),
            HttpStatus.CREATED);
    }

    @PutMapping(value = "/{productId}/options/{optionId}")
    public ResponseEntity<OptionResponseDto> updateOption(
        @PathVariable("productId") Long productId,
        @PathVariable("optionId") Long optionId,
        @Valid @RequestBody OptionRequestDto optionRequestDto
    ) {
        return new ResponseEntity<>(
            optionService.updateOption(productId, optionId, optionRequestDto),
            HttpStatus.OK);
    }

    @DeleteMapping(value = "/{productId}/options/{optionId}")
    public ResponseEntity<Void> deleteOption(
        @PathVariable("productId") Long productId,
        @PathVariable("optionId") Long optionId
    ) {
        optionService.deleteOption(productId, optionId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/{productId}/options")
    public ResponseEntity<List<OptionResponseDto>> getOptions(
        @PathVariable("productId") Long productId
    ) {
        return new ResponseEntity<>(optionService.getOptions(productId), HttpStatus.OK);
    }
}
