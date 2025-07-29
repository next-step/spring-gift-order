package gift.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import gift.dto.request.MessageRequestDto;
import gift.service.KakaoMessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KakaoMessageController {
    private final KakaoMessageService kakaoMessageService;

    public KakaoMessageController(KakaoMessageService kakaoMessageService) {
        this.kakaoMessageService = kakaoMessageService;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(
            @RequestHeader("Authorization") String bearerToken,
            @RequestBody MessageRequestDto request
    ) throws JsonProcessingException {
        String accessToken = bearerToken.replace("Bearer ", "");
//        kakaoMessageService.sendMessageToMe(accessToken, request.message());
        return ResponseEntity.ok("메시지 전송 완료");
    }
}
