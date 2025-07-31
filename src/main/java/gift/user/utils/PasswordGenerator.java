package gift.user.utils;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PasswordGenerator {
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SYMBOLS = "!@#$%^&*()-_=+<>?";
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generatePassword(int length) {
        if (length < 8) {
            throw new IllegalArgumentException("비밀번호 길이는 최소 8자 이상이어야 합니다.");
        };

        List<Character> passwordChars = new ArrayList<>();
        passwordChars.add(UPPER.charAt(RANDOM.nextInt(UPPER.length())));
        passwordChars.add(LOWER.charAt(RANDOM.nextInt(LOWER.length())));
        passwordChars.add(DIGITS.charAt(RANDOM.nextInt(DIGITS.length())));
        passwordChars.add(SYMBOLS.charAt(RANDOM.nextInt(SYMBOLS.length())));

        String all = UPPER + LOWER + DIGITS + SYMBOLS;
        for (int i = 4; i < length; i++) {
            passwordChars.add(all.charAt(RANDOM.nextInt(all.length())));
        }

        Collections.shuffle(passwordChars);
        StringBuilder password = new StringBuilder();
        for (char c : passwordChars) password.append(c);

        return password.toString();
    }
}
