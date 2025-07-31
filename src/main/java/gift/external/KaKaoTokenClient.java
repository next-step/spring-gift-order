package gift.external;

import gift.common.code.CustomResponseCode;
import gift.common.exception.KaKaoClientException;
import gift.common.exception.ServerErrorException;
import gift.dto.auth.KaKaoTokenInfo;
import gift.dto.auth.KaKaoUserInfo;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Component
public class KaKaoTokenClient {

    private final RestClient restClient;

    @Value("${kakao.token-url}")
    private String tokenUrl;
    @Value("${kakao.user-info-url}")
    private String userInfoUrl;

    public KaKaoTokenClient(RestClient.Builder builder) {
        this.restClient = builder.build();
    }

    public KaKaoTokenInfo requestToken(Map<String, String> params) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        params.forEach(body::add);

        return restClient.post()
            .uri(tokenUrl)
            .headers(h -> h.setContentType(MediaType.APPLICATION_FORM_URLENCODED))
            .body(body)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                throw new KaKaoClientException();
            })
            .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                throw new ServerErrorException(CustomResponseCode.SERVER_ERROR);
            })
            .body(KaKaoTokenInfo.class);
    }

    public KaKaoUserInfo requestUserInfo(String accessToken) {
        return restClient.get()
            .uri(userInfoUrl)
            .header("Authorization", "Bearer " + accessToken)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                throw new KaKaoClientException();
            })
            .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                throw new ServerErrorException(CustomResponseCode.SERVER_ERROR);
            })
            .body(KaKaoUserInfo.class);
    }
}
