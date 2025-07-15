package gift.exception;

import gift.authorization.exception.ForbiddenException;
import gift.authorization.exception.UnauthorizedException;
import gift.member.exception.InvalidMemberException;
import gift.member.exception.MemberNotFoundException;
import gift.product.exception.InvalidProductException;
import gift.product.exception.InvalidProductOptionException;
import gift.product.exception.NotEnoughInventoryException;
import gift.product.exception.ProductIsInWishlistException;
import gift.product.exception.ProductNotFoundException;
import gift.wishlist.exception.WishlistItemNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleProductNotFound(ProductNotFoundException ex) {
        ErrorResponseDto responseDto = ErrorResponseDto.of(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDto);
    }

    @ExceptionHandler(OperationFailedException.class)
    public ResponseEntity<ErrorResponseDto> handleOperationFailed(OperationFailedException ex) {
        ErrorResponseDto responseDto = ErrorResponseDto.of(
                HttpStatus.NOT_FOUND.value(),
                "Operation Failed",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDto);
    }

    @ExceptionHandler(InvalidProductException.class)
    public ResponseEntity<FieldErrorResponseDto> handleInvalidProduct(InvalidProductException ex) {
        FieldErrorResponseDto responseDto = FieldErrorResponseDto.of(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid Product",
                ex.getMessage(),
                ex.getField()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
    }

    @ExceptionHandler(InvalidProductOptionException.class)
    public ResponseEntity<FieldErrorResponseDto> handleInvalidProductOption(InvalidProductOptionException ex) {
        FieldErrorResponseDto responseDto = FieldErrorResponseDto.of(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid ProductOption",
                ex.getMessage(),
                ex.getField()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
    }

    @ExceptionHandler(NotEnoughInventoryException.class)
    public ResponseEntity<ErrorResponseDto> handleNotEnoughInventory(NotEnoughInventoryException ex) {
        ErrorResponseDto responseDto = ErrorResponseDto.of(
                HttpStatus.BAD_REQUEST.value(),
                "Not Enough Inventory",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
    }


    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleMemberNotFound(MemberNotFoundException ex) {
        ErrorResponseDto responseDto = ErrorResponseDto.of(
                HttpStatus.NOT_FOUND.value(),
                "NotFound",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDto);
    }

    @ExceptionHandler(InvalidMemberException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidMember(InvalidMemberException ex) {
        ErrorResponseDto responseDto = ErrorResponseDto.of(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid Member",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponseDto> handleUnauthorized(UnauthorizedException ex) {
        ErrorResponseDto responseDto = ErrorResponseDto.of(
                HttpStatus.UNAUTHORIZED.value(),
                "UnAuthorized",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseDto);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponseDto> handleForbidden(ForbiddenException ex) {
        ErrorResponseDto responseDto = ErrorResponseDto.of(
                HttpStatus.FORBIDDEN.value(),
                "Forbidden",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseDto);
    }

    @ExceptionHandler(WishlistItemNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleWishlistItemNotFound(WishlistItemNotFoundException ex) {
        ErrorResponseDto responseDto = ErrorResponseDto.of(
                HttpStatus.NOT_FOUND.value(),
                "NotFound",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDto);
    }

    @ExceptionHandler(ProductIsInWishlistException.class)
    public ResponseEntity<ErrorResponseDto> handleProductIsInWishlist(ProductIsInWishlistException ex) {
        ErrorResponseDto responseDto = ErrorResponseDto.of(
                HttpStatus.CONFLICT.value(),
                "Conflict",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(responseDto);
    }
}
