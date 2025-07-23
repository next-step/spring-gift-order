package gift.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ErrorResponseDTO {
    final private LocalDateTime timestamp;
    final private int status;
    final private String error;
    final private String message;
    private List<FieldError> fieldErrors;

    public ErrorResponseDTO(int status, String error, String message) {
      this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
    }

    public ErrorResponseDTO(int status, String error, String message, List<FieldError> fieldErrors) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.fieldErrors = fieldErrors;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public List<FieldError> getFieldErrors() {
        return fieldErrors;
    }
    public record FieldError(String field, String message) { }
}
