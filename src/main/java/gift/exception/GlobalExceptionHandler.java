package gift.exception;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ExceptionResponseDto> handleProductNotFoundException(
        ProductNotFoundException e) {

        ExceptionResponseDto exception = ExceptionResponseDto.singleIssue(e.getMessage(),
            LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception);
    }

    @ResponseBody
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponseDto> handleIllegalArgumentException(
        IllegalArgumentException e) {

        ExceptionResponseDto exception = ExceptionResponseDto.singleIssue(e.getMessage(),
            LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception);
    }

    @ResponseBody
    @ExceptionHandler(EmailDuplicationException.class)
    public ResponseEntity<ExceptionResponseDto> handleEmailDuplicationException(
        EmailDuplicationException e) {

        ExceptionResponseDto exception = ExceptionResponseDto.singleIssue(e.getMessage(),
            LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception);
    }

    @ResponseBody
    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<ExceptionResponseDto> handleUserNotFoundException(
        MemberNotFoundException e) {

        ExceptionResponseDto exception = ExceptionResponseDto.singleIssue(e.getMessage(),
            LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(exception);
    }

    @ResponseBody
    @ExceptionHandler(WishNotFoundException.class)
    public ResponseEntity<ExceptionResponseDto> handleWishNotFoundException(
        WishNotFoundException e) {

        ExceptionResponseDto exception = ExceptionResponseDto.singleIssue(e.getMessage(),
            LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(exception);
    }

    @ResponseBody
    @ExceptionHandler(UnauthorizedWishListException.class)
    public ResponseEntity<ExceptionResponseDto> handleUnauthorizedWishList(
        UnauthorizedWishListException e) {

        ExceptionResponseDto exception = ExceptionResponseDto.singleIssue(e.getMessage(),
            LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(exception);
    }

    @ResponseBody
    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ExceptionResponseDto> handleInvalidPasswordException(
        InvalidPasswordException e) {

        ExceptionResponseDto exception = ExceptionResponseDto.singleIssue(e.getMessage(),
            LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(exception);
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponseDto> handleMethodArgumentNotValidException(
        MethodArgumentNotValidException e) {

        List<String> errorMessage = e.getBindingResult().getFieldErrors()
            .stream()
            .map(fieldError -> fieldError.getField() + ":" + fieldError.getDefaultMessage())
            .toList();
        ExceptionResponseDto exception = new ExceptionResponseDto(
            errorMessage,
            LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception);
    }

    @ResponseBody
    @ExceptionHandler(OptionNameDuplicationException.class)
    public ResponseEntity<ExceptionResponseDto> handleOptionNameDuplicationException(
        OptionNameDuplicationException e
    ) {
        ExceptionResponseDto exception = ExceptionResponseDto.singleIssue(e.getMessage(),
            LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception);
    }

    @ResponseBody
    @ExceptionHandler(OptionNotFoundException.class)
    public ResponseEntity<ExceptionResponseDto> handleOptionNotFoundException(
        OptionNotFoundException e
    ) {
        ExceptionResponseDto exception = ExceptionResponseDto.singleIssue(e.getMessage(),
            LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception);
    }

    @ExceptionHandler(KakaoApproveException.class)
    public String handleKakaoApproveException(
        KakaoApproveException e,
        RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage",
            e.getMessage());
        return "redirect:/managerHome";
    }
}


