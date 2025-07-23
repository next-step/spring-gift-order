package gift.controller;

import gift.dto.CreateOptionRequest;
import gift.dto.CreateOptionResponse;
import gift.entity.Option;
import gift.repository.OptionRepository;
import gift.service.OptionService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products/{productId}/options")
public class OptionController {

    private final OptionService optionService;
    private final OptionRepository optionRepository;

    public OptionController(OptionService optionService, OptionRepository optionRepository) {
        this.optionService = optionService;
        this.optionRepository = optionRepository;
    }

    @PostMapping
    public ResponseEntity<CreateOptionResponse> addOption(@RequestBody CreateOptionRequest request, @PathVariable
            Long productId) {

        Option option = optionService.create(productId, request);

        CreateOptionResponse response = new CreateOptionResponse(option.getId(), option.getName(), option.getQuantity());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<Option>> getAllOptions(@PathVariable Long productId) {
        List<Option> options = optionService.getAllOption(productId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(options);
    }
}
