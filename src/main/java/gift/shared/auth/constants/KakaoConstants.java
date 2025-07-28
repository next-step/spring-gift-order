package gift.shared.auth.constants;

public enum KakaoConstants {
    KAKAO_LOGIN("https", "kauth.kakao.com", "/oauth/authorize"),
    KAKAO_TOKEN("https", "kauth.kakao.com", "/oauth/token"),
    KAKAO_USER("https", "kapi.kakao.com", "/v2/user/me");

    private final String scheme;
    private final String url;
    private final String path;

    KakaoConstants(String scheme, String url, String path) {
        this.scheme = scheme;
        this.url = url;
        this.path = path;
    }

    public String getScheme() {
        return scheme;
    }

    public String getUrl() {
        return url;
    }

    public String getPath() {
        return path;
    }

    public String getFullUrl(){
        return scheme + url + path;
    }
}
