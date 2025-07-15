package gift.authorization.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SaltedSHA256 {
    public static String hashWithSHA256(String password) {
        String salt = "a3f1b242f6e5d32cbd770b1e0a9e7c43";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String saltedPassword = password + salt;
            byte[] hashBytes = digest.digest(saltedPassword.getBytes());

            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
