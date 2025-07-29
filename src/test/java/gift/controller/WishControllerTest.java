package gift.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gift.domain.LoginType;
import gift.domain.Member;
import gift.domain.Role;
import gift.dto.WishRequest;
import gift.resolver.LoginMemberArgumentResolver;
import gift.service.WishService;
import gift.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = WishController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class WishControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private WishService wishService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private LoginMemberArgumentResolver loginMemberArgumentResolver;

    private final Long memberId = 1L;
    private final Long optionId = 4L;
    private final Member fakeMember = new Member(memberId, "test@example.com", "password", LoginType.LOCAL, Role.USER);

    @BeforeEach
    void setup() {
        given(jwtUtil.isValidToken(any())).willReturn(true);
        given(jwtUtil.extractMemberId(any())).willReturn(memberId);
        given(jwtUtil.createToken(any())).willReturn("test-token");

        given(loginMemberArgumentResolver.supportsParameter(any())).willReturn(true);
        given(loginMemberArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(fakeMember);
    }

    @Test
    @DisplayName("장바구니 옵션 추가")
    void addWish() throws Exception {
        WishRequest request = new WishRequest(optionId, 2);

        mockMvc.perform(post("/api/wishes")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(wishService).addWish(eq(memberId), eq(optionId), eq(2));
    }

    @Test
    @DisplayName("장바구니 옵션 수량 변경")
    void updateWish() throws Exception {
        WishRequest request = new WishRequest(optionId, 5);

        mockMvc.perform(patch("/api/wishes")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(wishService).updateWish(eq(memberId), eq(optionId), eq(5));
    }

    @Test
    @DisplayName("장바구니 옵션 삭제")
    void deleteWish() throws Exception {
        WishRequest request = new WishRequest(optionId, 0);

        mockMvc.perform(delete("/api/wishes")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(wishService).deleteWish(eq(memberId), eq(optionId));
    }

    @Test
    @DisplayName("장바구니 옵션 목록 조회")
    void getWishes() throws Exception {
        mockMvc.perform(get("/api/wishes")
                        .header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk());

        verify(wishService).getWishesPage(eq(memberId), any(Pageable.class));
    }
}


