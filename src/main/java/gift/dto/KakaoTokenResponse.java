package gift.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class KakaoTokenResponse {
    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("id_token")
    private String idToken;

    @JsonProperty("expires_in")
    private Integer expiresIn;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("refresh_token_expires_in")
    private Integer refreshTokenExpiresIn;

    @JsonProperty("scope")
    private String scope;

    public KakaoTokenResponse() {}

    public String getTokenType() {
        return tokenType;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getIdToken() {
        return idToken;
    }

    public Integer getExpiresIn() {
        return expiresIn;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public Integer getRefreshTokenExpiresIn() {
        return refreshTokenExpiresIn;
    }

    public String getScope() {
        return scope;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public void setExpiresIn(Integer expiresIn) {
        this.expiresIn = expiresIn;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void setRefreshTokenExpiresIn(Integer refreshTokenExpiresIn) {
        this.refreshTokenExpiresIn = refreshTokenExpiresIn;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
