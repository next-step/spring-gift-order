package gift.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordEncoder {

    private PasswordEncoder() {
        throw new UnsupportedOperationException("유틸리티 클래스는 인스턴스화 할 수 없습니다.");
    }

    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
