package gift.exception;

import gift.exception.notfound.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(value = MethodArgumentNotValidException.class)
  public ResponseEntity<CustomErrorResponse> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException exception) {
    String errorMessage = exception.getBindingResult()
        .getFieldErrors()
        .stream()
        .findFirst()
        .map(fieldError -> fieldError.getDefaultMessage())
        .orElse("잘못된 요청입니다.");
    CustomErrorResponse errorResponse = new CustomErrorResponse(exception.getStatusCode(),
        errorMessage);
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(value = NameHasKakaoException.class)
  public ResponseEntity<CustomErrorResponse> NameHasKakaoExceptionHandler(
      NameHasKakaoException exception) {
    CustomErrorResponse errorResponse = new CustomErrorResponse(HttpStatus.BAD_REQUEST,
        exception.getMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(value = NotFoundException.class)
  public ResponseEntity<CustomErrorResponse> handleProductNotFoundException(
      NotFoundException exception) {
    CustomErrorResponse errorResponse = new CustomErrorResponse(HttpStatus.NOT_FOUND,
        exception.getMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(value = AlreadyRegisterException.class)
  public ResponseEntity<CustomErrorResponse> handleAlreadyRegisterException(
      AlreadyRegisterException exception) {
    CustomErrorResponse errorResponse = new CustomErrorResponse(HttpStatus.CONFLICT,
        exception.getMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(value = NotRegisterException.class)
  public ResponseEntity<CustomErrorResponse> handleNotRegisterException(
      NotRegisterException exception) {
    CustomErrorResponse errorResponse = new CustomErrorResponse(HttpStatus.UNAUTHORIZED,
        exception.getMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(value = InvalidPasswordException.class)
  public ResponseEntity<CustomErrorResponse> handleInvalidPasswordException(
      InvalidPasswordException exception) {
    CustomErrorResponse errorResponse = new CustomErrorResponse(HttpStatus.UNAUTHORIZED,
        exception.getMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(value = DuplicatedOptionException.class)
  public ResponseEntity<CustomErrorResponse> handleDuplicateOptionException(
      DuplicatedOptionException exception) {
    CustomErrorResponse errorResponse = new CustomErrorResponse(HttpStatus.CONFLICT,
        exception.getMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(value = CantSubtractException.class)
  public ResponseEntity<CustomErrorResponse> handleCantSubtractException(
      CantSubtractException exception) {
    CustomErrorResponse errorResponse = new CustomErrorResponse(HttpStatus.BAD_REQUEST,
        exception.getMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(value = KakaoApiResponseException.class)
  public ResponseEntity<CustomErrorResponse> handleKakaoApiResponseException(
      KakaoApiResponseException exception) {
    CustomErrorResponse errorResponse = new CustomErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
        exception.getMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(value = KakaoLoginTimeoutException.class)
  public ResponseEntity<CustomErrorResponse> handleKakaoLoginTimeoutException(
      KakaoLoginTimeoutException exception) {
    CustomErrorResponse errorResponse = new CustomErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
        exception.getMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(value = NotKakaoMemberException.class)
  public ResponseEntity<CustomErrorResponse> handleNotKakaoMemberException(
      NotKakaoMemberException exception) {
    CustomErrorResponse errorResponse = new CustomErrorResponse(HttpStatus.BAD_REQUEST,
        exception.getMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }
}
