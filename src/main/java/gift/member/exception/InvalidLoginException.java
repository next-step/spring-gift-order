package gift.member.exception;

public class InvalidLoginException extends RuntimeException {

    public InvalidLoginException() {
        super("로그인 정보가 올바르지 않습니다.");
    }
}
