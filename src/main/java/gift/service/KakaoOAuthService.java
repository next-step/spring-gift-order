package gift.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gift.dto.KakaoUserDTO;
import gift.dto.LoginRequestDTO;
import gift.jwt.JwtTokenProvider;
import gift.model.KakaoOAuthUtils;
import gift.model.Role;
import gift.model.User;
import gift.repository.UserRepository;
import gift.service.external.KakaoApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

@Service
public class KakaoOAuthService {
    private final UserRepository userRepository;
    private final UserService userService;
    private final KakaoApi kakaoApi;
    private final JwtTokenProvider jwtTokenProvider;



    public KakaoOAuthService(JwtTokenProvider jwtTokenProvider,KakaoApi kakaoApi, UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.kakaoApi = kakaoApi;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public String processKakaoLogin(String code) {
        String token = getAccessToken(code);
        KakaoUserDTO userInfo = getUserInfo(token);

        String kakaoIdStr = String.valueOf(userInfo.id());
        String userid = KakaoOAuthUtils.getUserId(kakaoIdStr);
        String password = KakaoOAuthUtils.getUserPw(kakaoIdStr);

        Optional<User> userOpt = userRepository.findByUserid(userid);
        User user = userOpt.orElseGet(() -> {
            User newUser = new User(userid,password, Role.USER);
            return userRepository.save(newUser);
        });
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUserid(userid);
        loginRequest.setPassword(password);

        return userService.kakaoLogin(loginRequest, token);
    }

    private KakaoUserDTO getUserInfo(String token) {
        return kakaoApi.getUserInfo(token);
    }

    private String getAccessToken(String code) {
        return kakaoApi.getAccessToken(code);
    }

    public String getKakaoAccessTokenFromPureToken(String pureToken) {
        return jwtTokenProvider.getKakaoAccessTokenFromToken(pureToken);
    }
}