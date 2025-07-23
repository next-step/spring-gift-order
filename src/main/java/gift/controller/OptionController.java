package gift.controller;

import gift.dto.OptionRequest;
import gift.dto.OptionResponse;
import gift.enums.OptionSortKey;
import gift.service.OptionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products/{productId}/options")
public class OptionController {

    private final OptionService optionService;

    OptionController(OptionService optionService) {
        this.optionService = optionService;
    }

    @GetMapping
    public ResponseEntity<List<OptionResponse>> getOptionPage(@PathVariable Long productId,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size,
                                                           @RequestParam(defaultValue = "name-asc") String sortKey) {
        List<OptionResponse> optionPage = optionService.getOptionPage(productId, page, size, OptionSortKey.from(sortKey));

        return new ResponseEntity<>(optionPage, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<OptionResponse> postOption(@PathVariable Long productId,
                                                     @RequestBody OptionRequest optionRequest) {
        return new ResponseEntity<>(optionService.createOption(productId, optionRequest), HttpStatus.CREATED);
    }
}
