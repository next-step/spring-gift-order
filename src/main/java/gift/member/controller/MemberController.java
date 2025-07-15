package gift.member.controller;

import gift.member.dto.MemberAddRequestDto;
import gift.member.dto.MemberResponseDto;
import gift.member.dto.MemberUpdateRequestDto;
import gift.member.exception.InvalidMemberException;
import gift.member.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    //멤버를 추가하는 api
    @PostMapping
    public ResponseEntity<Void> addMember(
            @Valid @RequestBody MemberAddRequestDto requestDto, BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throwInvalidMemberException(bindingResult);
        }
        memberService.addMember(requestDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberResponseDto> findMemberById(
            @PathVariable Long id
    ) {
        MemberResponseDto responseDto = memberService.findMemberById(id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateMemberById(
            @PathVariable Long id,
            @Valid @RequestBody MemberUpdateRequestDto requestDto, BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throwInvalidMemberException(bindingResult);
        }
        memberService.updateMemberById(id, requestDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMemberById(
            @PathVariable Long id
    ) {
        memberService.deleteMemberById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private String getDefaultMessage(BindingResult bindingResult) {
        FieldError fieldError = bindingResult.getFieldError();
        if (fieldError == null || fieldError.getDefaultMessage() == null) {
            throw new InvalidMemberException("잘못된 요청입니다.", "NonError");
        }
        return fieldError.getDefaultMessage();
    }

    private void throwInvalidMemberException(BindingResult bindingResult) {
        throw new InvalidMemberException(getDefaultMessage(bindingResult), bindingResult.getFieldErrors().getFirst().getField());
    }
}
