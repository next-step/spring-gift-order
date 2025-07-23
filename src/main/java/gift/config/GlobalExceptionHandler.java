package gift.config;

import gift.exception.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handlerUnexpected(Exception ex) {
        System.out.println(ex);
        return new ResponseEntity<>("INTERNAL_ERROR - 서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handlerException(NoSuchElementException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InsertFailedException.class)
    public ResponseEntity<String> handlerException(InsertFailedException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UpdateFailedException.class)
    public ResponseEntity<String> handlerException(UpdateFailedException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateWishException.class)
    public ResponseEntity<String> handlerException(DuplicateWishException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handlerException(EntityNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<String> handlerException(ValidationException ex) {
        return new ResponseEntity<>("요청 형식이 잘못 되었습니다.", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<String> handlerException(HttpRequestMethodNotSupportedException ex) {
        return new ResponseEntity<>(ex.getMethod() + " method는 지원되지 않습니다.", HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handlerException(IllegalArgumentException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidOptionCreateException.class)
    public ResponseEntity<String> handlerException(InvalidOptionCreateException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateOptionException.class)
    public ResponseEntity<String> handlerException(DuplicateOptionException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidQuantityException.class)
    public ResponseEntity<String> handlerException(InvalidQuantityException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handlerException(MethodArgumentNotValidException ex) {
        return new ResponseEntity<>(ex.getFieldError().getDefaultMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<String> handlerException(EmailAlreadyExistsException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidLoginException.class)
    public ResponseEntity<String> handlerException(InvalidLoginException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(InvalidRegisterParameterException.class)
    public ResponseEntity<String> handlerException(InvalidRegisterParameterException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthenticatedException.class)
    public ResponseEntity<String> handlerException(UnauthenticatedException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handlerException(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getMostSpecificCause();
        String message = cause != null ? cause.getMessage() : "잘못된 요청입니다";

        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }
}
