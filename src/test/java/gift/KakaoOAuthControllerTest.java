package gift;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import gift.member.dto.MemberDto;
import gift.member.dto.MemberLoginResponseDto;
import gift.member.dto.MemberTokenDto;
import gift.oauth.kakao.service.KakaoOAuthService;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SuppressWarnings("NonAsciiCharacters")
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class KakaoOAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private KakaoOAuthService kakaoOAuthService;

    @Test
    void 카카오_OAuth_콜백_기존_회원_로그인_성공() throws Exception {
        String authCode = "test_auth_code";
        
        UUID memberUuid = UUID.randomUUID();
        MemberDto memberDto = new MemberDto(
            memberUuid, "test@example.com", "테스트유저", 
            LocalDateTime.now(), LocalDateTime.now()
        );
        MemberTokenDto memberTokenDto = MemberTokenDto.of("jwt_access_token", "jwt_refresh_token");
        MemberLoginResponseDto loginResponse = MemberLoginResponseDto.of(memberDto, memberTokenDto);

        given(kakaoOAuthService.authenticateAndLogin(authCode)).willReturn(loginResponse);

        mockMvc.perform(get("/api/oauth/kakao/callback").param("code", authCode))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberDto.uuid").value(memberUuid.toString()))
                .andExpect(jsonPath("$.memberDto.email").value("test@example.com"))
                .andExpect(jsonPath("$.memberDto.name").value("테스트유저"))
                .andExpect(jsonPath("$.tokenInfo.accessToken").value("jwt_access_token"))
                .andExpect(jsonPath("$.tokenInfo.refreshToken").value("jwt_refresh_token"));
    }

    @Test
    void 카카오_OAuth_콜백_신규_회원_가입_후_로그인_성공() throws Exception {
        String authCode = "test_auth_code";
        
        UUID memberUuid = UUID.randomUUID();
        MemberDto memberDto = new MemberDto(
            memberUuid, "newuser@example.com", "신규유저", 
            LocalDateTime.now(), LocalDateTime.now()
        );
        MemberTokenDto memberTokenDto = MemberTokenDto.of("jwt_access_token", "jwt_refresh_token");
        MemberLoginResponseDto loginResponse = MemberLoginResponseDto.of(memberDto, memberTokenDto);

        given(kakaoOAuthService.authenticateAndLogin(authCode)).willReturn(loginResponse);

        mockMvc.perform(get("/api/oauth/kakao/callback").param("code", authCode))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberDto.uuid").value(memberUuid.toString()))
                .andExpect(jsonPath("$.memberDto.email").value("newuser@example.com"))
                .andExpect(jsonPath("$.memberDto.name").value("신규유저"))
                .andExpect(jsonPath("$.tokenInfo.accessToken").value("jwt_access_token"))
                .andExpect(jsonPath("$.tokenInfo.refreshToken").value("jwt_refresh_token"));
    }
} 