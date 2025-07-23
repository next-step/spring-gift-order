package gift.controller.optionController;

import gift.dto.optionDto.OptionDtoList;
import gift.dto.optionDto.OptionRequestDto;
import gift.dto.optionDto.OptionResponseDto;
import gift.entity.ItemOption;
import gift.service.optionService.OptionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/options")
public class OptionController {

    private final OptionService optionService;

    public OptionController(OptionService optionService) {
        this.optionService = optionService;
    }

    @PostMapping
    public ResponseEntity<OptionResponseDto> addItemOption(@RequestBody OptionRequestDto optionRequestDto, @RequestParam Long itemId) {

        ItemOption itemOption = optionService.save(optionRequestDto, itemId);
        OptionResponseDto optionResponseDto = OptionResponseDto.from(itemOption);

        return new ResponseEntity<>(optionResponseDto, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<OptionDtoList> getOptionList(@RequestParam Long itemId) {
        List<ItemOption> optionList = optionService.getOptions(itemId);

        return ResponseEntity.ok(OptionDtoList.from(optionList));
    }

    @PutMapping
    public ResponseEntity<OptionResponseDto> quantityControl(@RequestBody OptionRequestDto optionRequestDto, @RequestParam Long itemId) {
        ItemOption itemOption = optionService.quantityControl(optionRequestDto, itemId);
        OptionResponseDto optionResponseDto = OptionResponseDto.from(itemOption);

        return new ResponseEntity<>(optionResponseDto, HttpStatus.CREATED);
    }
}
