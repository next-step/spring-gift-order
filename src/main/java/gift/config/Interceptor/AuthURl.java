package gift.config.Interceptor;

import java.util.List;

public class AuthURl {
    public static final List<String> PUBLIC_PATHS = List.of(
            "/api/users/register",
            "/api/users/login"
    );

    public static final List<String> AUTH_REQUIRED_PREFIXES = List.of(
            "/wish/",
            "/admin/products",
            "/api/products",
            "/api/wish",
            "/api/orders",
            "/api/options",
            "/api/users"
    );

}
