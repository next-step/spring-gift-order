package gift.shared.token.utils;

import org.springframework.util.CollectionUtils;

import java.util.List;

public final class JwtParser {
    public static boolean isValidTokenType(List<String> token){
        if(CollectionUtils.isEmpty(token)) {
            return false;
        }
        return token.getFirst().split(" ")[0].equals("Bearer");
    }

    public static String getToken(List<String> token){
        return token.getFirst().split(" ")[1];
    }
}
