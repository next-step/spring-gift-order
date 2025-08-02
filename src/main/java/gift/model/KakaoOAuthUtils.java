package gift.model;

public final class KakaoOAuthUtils {
    private static final String KAKAO_ID_PREFIX = "kakao_ID_";
    private static final String KAKAO_PW_PREFIX = "kakao_PW_";

    private KakaoOAuthUtils() {}

    public static String getUserId(String kakaoId) {
        return KAKAO_ID_PREFIX + kakaoId;
    }
    public static String getUserPw(String kakaoId) {
        return KAKAO_PW_PREFIX + kakaoId;
    }
}