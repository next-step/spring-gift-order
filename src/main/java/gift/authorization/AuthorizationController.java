package gift.authorization;

import gift.authorization.dto.LoginRequestDto;
import gift.authorization.dto.TokenResponseDto;
import gift.authorization.dto.UserRegisterRequestDto;
import gift.authorization.service.AuthorizationService;
import gift.member.exception.InvalidMemberException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("api")
@RestController
public class AuthorizationController {

    private final AuthorizationService AuthorizationService;

    public AuthorizationController(AuthorizationService authorizationService) {
        this.AuthorizationService = authorizationService;
    }

    //유저를 추가하고 토큰을 반환하는 api
    @PostMapping("/register")
    public ResponseEntity<TokenResponseDto> registerMember(
            @Valid @RequestBody UserRegisterRequestDto requestDto, BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throwInvalidMemberException(bindingResult);
        }
        TokenResponseDto responseDto = AuthorizationService.registerMember(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> loginMember(
            @Valid @RequestBody LoginRequestDto requestDto, BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throwInvalidMemberException(bindingResult);
        }
        TokenResponseDto responseDto = AuthorizationService.loginMember(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    private String getDefaultMessage(BindingResult bindingResult) {
        FieldError fieldError = bindingResult.getFieldError();
        if (fieldError == null || fieldError.getDefaultMessage() == null) {
            throw new InvalidMemberException("잘못된 요청입니다.","NonError");
        }
        return fieldError.getDefaultMessage();
    }

    private void throwInvalidMemberException(BindingResult bindingResult) {
        throw new InvalidMemberException(getDefaultMessage(bindingResult), bindingResult.getFieldErrors().getFirst().getField());
    }
}
