package gift.dto.request;

public class MessageRequestDto {
    String message;

    public MessageRequestDto() {
    }
    public MessageRequestDto(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
