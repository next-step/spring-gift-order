package gift.auth.cookie;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    private static final String ACCESS_TOKEN_COOKIE_NAME = "access_token";
    private static final int ACCESS_TOKEN_MAX_AGE = 60 * 60;

    public void addAuthCookies(String accessToken, HttpServletResponse response) {
        Cookie accessCookie = createCookie(
            ACCESS_TOKEN_COOKIE_NAME,
            accessToken,
            ACCESS_TOKEN_MAX_AGE
        );

        response.addCookie(accessCookie);
    }

    private Cookie createCookie(String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        //cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        return cookie;
    }
}
