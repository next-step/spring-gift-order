package gift.service;

import gift.dto.*;
import gift.entity.User;
import gift.exception.*;
import gift.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;
import java.util.HexFormat;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final String jwtKey;
    private final String aesKey;

    public AuthService(UserRepository userRepository, @Value("${jwt_key}") String jwtKey, @Value("${aes_key}") String aesKey) {
        this.userRepository = userRepository;
        this.jwtKey = jwtKey;
        this.aesKey = aesKey;
    }

    /**
     * SHA-256 방식으로 해싱
     * 결과를 HEX String으로 반환
     */
    public String encryptSHA256(String text) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        StringBuilder builder = new StringBuilder();
        md.update(text.getBytes());
        for (byte b : md.digest()) {
            builder.append(String.format("%02x", b));
        }

        return builder.toString();
    }

    /**
     * AES/ECB/PKCS5Padding 방식으로 평문을 암호화하고
     * 결과를 Base64 형태로 반환
     */
    public String encryptAES(String plainText) throws Exception {
        // 1) 키 준비 (hex 문자열 → 16/24/32byte)
        byte[] keyBytes = HexFormat.of().parseHex(aesKey);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

        // 2) Cipher 설정 (ECB 모드, 패딩은 PKCS5)
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);

        // 3) 암호화 수행
        byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        // 4) Ciphertext만 Base64로 인코딩해 반환
        return Base64.getEncoder().encodeToString(encrypted);
    }

    /**
     * encryptAES()가 반환한 Base64 암호문을 복호화해 평문으로 반환
     */
    public String decryptAES(String cipherTextBase64) throws Exception {
        // 1) Base64 디코딩
        byte[] cipherBytes = Base64.getDecoder().decode(cipherTextBase64);

        // 2) 키 준비 (hex 문자열 → byte[])
        byte[] keyBytes = HexFormat.of().parseHex(aesKey);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

        // 3) Cipher 설정 (ECB 모드, 패딩은 PKCS5)
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);

        // 4) 복호화
        byte[] plainBytes = cipher.doFinal(cipherBytes);
        return new String(plainBytes, StandardCharsets.UTF_8);
    }


    @Transactional
    public UserResponseDto userSignUp(UserRequestDto userRequestDto) {

        User user;

        // 이메일 AES 암호화, 비밀번호 SHA-256 해싱
        try {
            user = new User(
                    encryptAES(userRequestDto.email()),
                    encryptSHA256(userRequestDto.password()));
        } catch (Exception e) {
            throw new EncryptFailedException();
        }

        return new UserResponseDto(userRepository.save(user));
    }

    @Transactional
    public TokenResponseDto userLogin(UserRequestDto userRequestDto) {

        String email;
        String password;
        User user;

        // 이메일 AES 암호화, 비밀번호 SHA-256 해싱
        try {
            email = encryptAES(userRequestDto.email());
            password = encryptSHA256(userRequestDto.password());
        } catch (Exception e) {
            throw new EncryptFailedException();
        }

        // 유저 정보 확인
        user = userRepository.findByEmailAndPassword(email, password).orElseThrow(LoginFailedException::new);

        // JWT 생성 후 반환
        return new TokenResponseDto(Jwts.builder()
                .subject(user.getEmail())
                .claim("id", user.getId())
                .claim("role", user.getRole())
                .signWith(Keys.hmacShaKeyFor(jwtKey.getBytes()))
                .compact());
    }
}
