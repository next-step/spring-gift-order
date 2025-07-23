package gift.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getFieldErrors();
        StringBuilder sb = new StringBuilder();
        for (FieldError fieldError : fieldErrors) {
            sb.append(fieldError.getDefaultMessage()).append('\n');
        }
        ErrorResponse response = ErrorResponse.of(sb.toString());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFoundException(ProductNotFoundException e) {
        return new ResponseEntity<>(ErrorResponse.of(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException e) {
        return new ResponseEntity<>(ErrorResponse.of(e.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException e) {
        return new ResponseEntity<>(ErrorResponse.of(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidUserException.class)
    public ResponseEntity<ErrorResponse> handleInvalidUserException(InvalidUserException e) {
        return new ResponseEntity<>(ErrorResponse.of(e.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(WishlistNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleWishlistNotFoundException(WishlistNotFoundException e) {
        return new ResponseEntity<>(ErrorResponse.of(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(WishlistAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleWishlistAlreadyExistsException(WishlistAlreadyExistsException e) {
        return new ResponseEntity<>(ErrorResponse.of(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateOptionNameException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateOptionNameException(DuplicateOptionNameException e) {
        return new ResponseEntity<>(ErrorResponse.of(e.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
