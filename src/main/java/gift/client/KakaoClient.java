package gift.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gift.config.KakaoProperties;
import gift.dto.login.KakaoErrorDto;
import gift.dto.login.KakaoProfileDto;
import gift.dto.login.KakaoTokenDto;
import gift.dto.order.KakaoOrderResponseDto;
import gift.dto.order.KakaoOrderTemplateMessageDto;
import gift.dto.order.KakaoOrderTemplateMessageDto.KakaoOrderTemplateMessageContentDto;
import gift.dto.order.KakaoOrderTemplateMessageDto.KakaoOrderTemplateMessageLinkDto;
import gift.exception.KakaoClientException;
import gift.exception.KakaoException;
import gift.exception.KakaoServerException;
import java.io.IOException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Component
public class KakaoClient {

    private final KakaoProperties kakaoProperties;

    private final RestClient restClient;

    private final ObjectMapper objectMapper;

    public KakaoClient(KakaoProperties kakaoProperties, ObjectMapper objectMapper) {
        this.kakaoProperties = kakaoProperties;
        this.objectMapper = objectMapper;
        this.restClient = RestClient.create();
    }

    public KakaoTokenDto fetchToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE,
            "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoProperties.restApiKey());
        body.add("redirect_uri", kakaoProperties.redirectUri());
        body.add("code", code);

        ResponseEntity<KakaoTokenDto> response = restClient.post()
            .uri(kakaoProperties.tokenUri())
            .headers(h -> h.addAll(headers))
            .body(body)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, (request, httpResponse) -> {
                try {
                    KakaoErrorDto errorDto = objectMapper.readValue(httpResponse.getBody(),
                        KakaoErrorDto.class);
                    throw new KakaoClientException(errorDto.code(), errorDto.message());
                } catch (IOException exception) {
                    throw new KakaoException("카카오 오류 응답을 파싱하지 못하였습니다.");
                }
            })
            .onStatus(HttpStatusCode::is5xxServerError, (request, httpResponse) -> {
                try {
                    KakaoErrorDto errorDto = objectMapper.readValue(httpResponse.getBody(),
                        KakaoErrorDto.class);
                    throw new KakaoServerException(errorDto.code(), errorDto.message());
                } catch (IOException exception) {
                    throw new KakaoException("카카오 오류 응답을 파싱하지 못하였습니다.");
                }
            })
            .toEntity(KakaoTokenDto.class);

        return response.getBody();
    }

    public KakaoProfileDto fetchProfile(String token) {
        String bearerToken = "Bearer " + token;

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, bearerToken);
        headers.add(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8");

        ResponseEntity<KakaoProfileDto> response = restClient.post()
            .uri(kakaoProperties.profileUri())
            .headers(h -> h.addAll(headers))
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, (request, httpResponse) -> {
                try {
                    KakaoErrorDto errorDto = objectMapper.readValue(httpResponse.getBody(),
                        KakaoErrorDto.class);
                    throw new KakaoClientException(errorDto.code(), errorDto.message());
                } catch (IOException exception) {
                    throw new KakaoException("카카오 오류 응답을 파싱하지 못하였습니다.");
                }
            })
            .onStatus(HttpStatusCode::is5xxServerError, (request, httpResponse) -> {
                try {
                    KakaoErrorDto errorDto = objectMapper.readValue(httpResponse.getBody(),
                        KakaoErrorDto.class);
                    throw new KakaoServerException(errorDto.code(), errorDto.message());
                } catch (IOException exception) {
                    throw new KakaoException("카카오 오류 응답을 파싱하지 못하였습니다.");
                }
            })
            .toEntity(KakaoProfileDto.class);

        return response.getBody();
    }

    public KakaoOrderResponseDto sendKakaoMessage(String accessToken, String message,
        String imageUrl) {
        String bearerToken = "Bearer " + accessToken;

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, bearerToken);
        headers.add(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8");

        KakaoOrderTemplateMessageLinkDto linkDto = new KakaoOrderTemplateMessageLinkDto(
            "www.example.com", "www.example.com");
        KakaoOrderTemplateMessageContentDto contentDto = new KakaoOrderTemplateMessageContentDto(
            "상품 주문이 완료되었습니다.", "배송 메시지: " + message, imageUrl, linkDto);
        KakaoOrderTemplateMessageDto messageDto = new KakaoOrderTemplateMessageDto("feed",
            contentDto);
        
        String templateJson = null;
        try {
            templateJson = objectMapper.writeValueAsString(messageDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Kakao message DTO을 JSON으로 변환하는데 실패하였습니다.");
        }

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("template_object", templateJson);

        ResponseEntity<KakaoOrderResponseDto> response = restClient.post()
            .uri(kakaoProperties.sendMessageUri())
            .headers(h -> h.addAll(headers))
            .body(body)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, (request, httpResponse) -> {
                try {
                    KakaoErrorDto errorDto = objectMapper.readValue(httpResponse.getBody(),
                        KakaoErrorDto.class);
                    throw new KakaoClientException(errorDto.code(), errorDto.message());
                } catch (IOException exception) {
                    throw new KakaoException("카카오 오류 응답을 파싱하지 못하였습니다.");
                }
            })
            .onStatus(HttpStatusCode::is5xxServerError, (request, httpResponse) -> {
                try {
                    KakaoErrorDto errorDto = objectMapper.readValue(httpResponse.getBody(),
                        KakaoErrorDto.class);
                    throw new KakaoServerException(errorDto.code(), errorDto.message());
                } catch (IOException exception) {
                    throw new KakaoException("카카오 오류 응답을 파싱하지 못하였습니다.");
                }
            })
            .toEntity(KakaoOrderResponseDto.class);

        return response.getBody();
    }
}