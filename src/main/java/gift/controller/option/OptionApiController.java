package gift.controller.option;

import gift.dto.option.OptionRequest;
import gift.dto.option.OptionResponse;
import gift.dto.option.OptionSubtractRequest;
import gift.service.option.OptionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/options")
public class OptionApiController {
    private final OptionService optionService;

    public OptionApiController(OptionService optionService) {
        this.optionService = optionService;
    }

    // option 조회 메서드: 이후 관리자 기능으로 전환 예정
    @GetMapping("/{optionId}")
    public ResponseEntity<OptionResponse> getOptionById(
        @PathVariable(name = "optionId") Long optionId
    ){
        return ResponseEntity.status(HttpStatus.OK)
            .body(optionService.getOptionById(optionId));
    }

    // option subtract 메서드: 이후 관리자 기능으로 전환 예정
    @PostMapping("/{optionId}")
    public ResponseEntity<OptionResponse> subtractOption(
        @PathVariable Long optionId,
        @Valid @RequestBody OptionSubtractRequest optionSubtractRequest
    ){
        return ResponseEntity.status(HttpStatus.OK)
                .body(optionService.subtractOption(optionId, optionSubtractRequest));
    }

    // option 생성 메서드: 이후 관리자 기능으로 전환 예정
    @PostMapping
    public ResponseEntity<OptionResponse> createOption(
        @Valid @RequestBody OptionRequest optionRequest
    ){
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(optionService.insertOption(optionRequest));
    }
}
