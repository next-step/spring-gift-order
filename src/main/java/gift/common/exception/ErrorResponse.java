package gift.common.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.LocalDateTime;

public record ErrorResponse(LocalDateTime timestamp, String message) {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());

    public static ErrorResponse of(String message) {
        return new ErrorResponse(LocalDateTime.now(), message);
    }

    public String convertToJson() throws JsonProcessingException {
        return OBJECT_MAPPER.writeValueAsString(this);
    }
}
