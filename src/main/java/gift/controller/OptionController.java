package gift.controller;

import gift.entity.Option;
import gift.service.OptionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/options")
public class OptionController {
    private final OptionService optionService;

    private OptionController(OptionService optionService) {
        this.optionService = optionService;
    }

    /**
     * 옵션 하나 조회
     * @param id 옵션 아이디
     * @return Option JSON
     */
    @GetMapping("/{id}")
    public ResponseEntity<Option> findById(@PathVariable Long id){
        return new ResponseEntity<>(optionService.findOptionById(id), HttpStatus.OK);
    }

    /**
     * 모든 옵션 조회
     * @param pageable 페이징용 객체
     * @return Page<Option> JSON
     */
    @GetMapping()
    public ResponseEntity<Page<Option>> findAll(
            @SortDefault(sort = "id")
            Pageable pageable){
        return new ResponseEntity<>(optionService.findAllOptions(pageable), HttpStatus.OK);
    }

    /**
     * 옵션 생성
     * @param name 옵션 이름
     * @return Option JSON
     */
    @PostMapping()
    public ResponseEntity<Option> create(@RequestBody String name){
        return new ResponseEntity<>(optionService.addOption(name), HttpStatus.CREATED);
    }

    /**
     * 옵션 이름 수정
     * @param id 옵션 아이디
     * @param name 바꿀 이름
     * @return Option JSON
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Option> update(@PathVariable Long id, @RequestBody String name){
        return new ResponseEntity<>(optionService.updateOption(id, name), HttpStatus.OK);
    }

    /**
     * 옵션 삭제
     * @param id 옵션 아이디
     * @return Void
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        optionService.deleteOption(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
