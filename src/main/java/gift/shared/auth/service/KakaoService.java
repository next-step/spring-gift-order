package gift.shared.auth.service;

import gift.shared.auth.dto.response.KakaoBasicInfoResponse;
import gift.shared.auth.dto.response.KakaoTokenResponse;
import gift.shared.auth.dto.response.TokenResponse;
import gift.shared.exception.token.NoTokenException;
import gift.shared.exception.user.NoUserException;
import gift.shared.token.service.TokenService;
import gift.user.entity.User;
import gift.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import static gift.shared.auth.constants.KakaoConstants.*;
import static gift.shared.token.status.TokenStatus.*;
import static org.springframework.http.MediaType.*;

@Service
public class KakaoService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserRepository userRepository;

    private final TokenService tokenService;

    private final RestClient restClient;

    @Value("${kakao-client-id}")
    private String kakaoLoginClientId;

    @Value("${kakao-redirect-url}")
    private String redirectUrl;

    public KakaoService(UserRepository userRepository, TokenService tokenService, RestClient restClient) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.restClient = restClient;
    }

    public String getAuthorizationCode(){
        return createKakaoAuthUrl();
    }

    public TokenResponse kakaoLogin(String accessCode) throws NoUserException, NoTokenException, NoSuchAlgorithmException {
        KakaoTokenResponse kakaoTokenResponse = getKakaoToken(accessCode);
        KakaoBasicInfoResponse kakaoEmail = getKakaoEmail(kakaoTokenResponse.access_token());
        Optional<User> user = userRepository.findByEmail(kakaoEmail.makeEmailById());
        if(user.isPresent()){
            return new TokenResponse(tokenService.generateToken(user.get()));
        }
        User newUser = new User(kakaoEmail.makeEmailById());
        userRepository.save(newUser);
        return new TokenResponse(tokenService.generateToken(newUser));
    }

    private String createKakaoAuthUrl(){
        UriComponentsBuilder uri =  UriComponentsBuilder.newInstance()
                .scheme(KAKAO_LOGIN.getScheme())
                .host(KAKAO_LOGIN.getUrl())
                .path(KAKAO_LOGIN.getPath())
                .queryParam("response_type", "code")
                .queryParam("client_id", kakaoLoginClientId)
                .queryParam("redirect_uri", redirectUrl);
        return uri.build().toUriString();
    }

    private String createKakaoTokenUrl(){
        UriComponentsBuilder uri =  UriComponentsBuilder.newInstance()
                .scheme(KAKAO_TOKEN.getScheme())
                .host(KAKAO_TOKEN.getUrl())
                .path(KAKAO_TOKEN.getPath());
        return uri.build().toUriString();
    }

    private String createKakaoUserUrl(){
        UriComponentsBuilder uri =  UriComponentsBuilder.newInstance()
                .scheme(KAKAO_USER.getScheme())
                .host(KAKAO_USER.getUrl())
                .path(KAKAO_USER.getPath());
        return uri.build().toUriString();
    }

    private KakaoTokenResponse getKakaoToken(String accessCode){
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", kakaoLoginClientId);
        params.add("grant_type", "authorization_code");
        params.add("redirect_uri", redirectUrl);
        params.add("code", accessCode);

        try{
            ResponseEntity<KakaoTokenResponse> response = restClient.post()
                    .uri(createKakaoTokenUrl())
                    .contentType(APPLICATION_FORM_URLENCODED)
                    .accept(APPLICATION_JSON)
                    .body(params)
                    .retrieve()
                    .toEntity(KakaoTokenResponse.class);
            if(response.getBody() == null){
                throw new NoUserException(NO_KAKAO_TOKEN.getMessage());
            }
            return response.getBody();
        }catch(HttpClientErrorException e){
            logger.error(NO_KAKAO_TOKEN.getMessage());
            throw new NoUserException(NO_KAKAO_TOKEN.getMessage());
        }
    }

    private KakaoBasicInfoResponse getKakaoEmail(String accessToken){
        if(accessToken == null){
            throw new NoTokenException(NO_TOKEN.getMessage());
        }
        try {
            ResponseEntity<KakaoBasicInfoResponse> response = restClient.post()
                    .uri(createKakaoUserUrl())
                    .header(HttpHeaders.CONTENT_TYPE, String.valueOf(APPLICATION_FORM_URLENCODED))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .accept(APPLICATION_JSON)
                    .retrieve()
                    .toEntity(KakaoBasicInfoResponse.class);
            if(response.getBody() == null){
                throw new NoUserException(NO_TOKEN.getMessage());
            }
            return response.getBody();
        }catch(HttpClientErrorException e) {
            logger.error(NO_KAKAO_TOKEN.getMessage());
            throw new NoUserException(e.getMessage());
        }
    }
}
