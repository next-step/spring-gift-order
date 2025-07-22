package gift.wishlist.status;

import org.springframework.http.HttpStatus;

public enum WishlistStatus {
    NO_WISHLIST("WE001", "해당 ID 의 Wishlist 가 없습니다.", HttpStatus.NOT_FOUND);

    private final String code;
    private final String message;
    private final HttpStatus status;

    WishlistStatus(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

    public String getMessage(){
        return "[" + code + "] " + message;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
