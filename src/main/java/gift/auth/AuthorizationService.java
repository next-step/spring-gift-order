package gift.auth;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class AuthorizationService {
    private final Map<String, Set<String>> pathRoleMap = Map.of(
            "/admin", Set.of("ADMIN"),
            "/api/products", Set.of("USER", "ADMIN")
    );

    public AuthorizationResult authorize(String path, String role) {
        String matchedPath = pathRoleMap.keySet().stream()
                .filter(path::startsWith)
                .findFirst()
                .orElse(null);

        if (matchedPath == null) {
            return AuthorizationResult.success();
        }

        Set<String> allowedRoles = pathRoleMap.get(matchedPath);
        if (!allowedRoles.contains(role)) {
            return AuthorizationResult.failure("Access denied: " + String.join(" or ", allowedRoles) + " only");
        }

        return AuthorizationResult.success();
    }
}
