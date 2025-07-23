package gift.auth;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthErrorResponseHandler {

    public void handleAuthenticationError(HttpServletResponse response, String errorMessage) throws IOException {
        writeErrorResponse(response, HttpStatus.UNAUTHORIZED, errorMessage);
    }

    public void handleAuthorizationError(HttpServletResponse response, String errorMessage) throws IOException {
        writeErrorResponse(response, HttpStatus.FORBIDDEN, errorMessage);
    }

    private void writeErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }
}
