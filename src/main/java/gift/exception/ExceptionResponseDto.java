package gift.exception;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public record ExceptionResponseDto(
    List<String> errMessages,
    LocalDateTime timeStamp) {

    public static ExceptionResponseDto singleIssue(String message, LocalDateTime time) {
        List<String> errMessages = new ArrayList<>();
        errMessages.add(message);
        return new ExceptionResponseDto(errMessages, time);
    }

}
