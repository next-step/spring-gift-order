package gift.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gift.domain.Member;
import gift.domain.Role;
import gift.dto.WishRequest;
import gift.resolver.LoginMemberArgumentResolver;
import gift.service.WishService;
import gift.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = WishController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@Import(LoginMemberArgumentResolver.class)
class WishControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private WishService wishService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private HandlerMethodArgumentResolver argumentResolver;

    private final Long memberId = 1L;
    private final Long productId = 4L;
    private final Member fakeMember = new Member(memberId, "user@email.com", "pw", Role.USER);

    @BeforeEach
    void setup() {
        when(jwtUtil.isValidToken(any())).thenReturn(true);
        when(jwtUtil.extractMemberId(any())).thenReturn(memberId);
        when(jwtUtil.createToken(any())).thenReturn("test-token");
    }

    @Test
    @DisplayName("장바구니 상품 추가")
    void addWish() throws Exception {
        WishRequest request = new WishRequest(productId, 2);

        mockMvc.perform(post("/api/wishes")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(wishService).addWish(eq(memberId), eq(productId), eq(2));
    }

    @Test
    @DisplayName("장바구니 수량 변경")
    void updateWish() throws Exception {
        WishRequest request = new WishRequest(productId, 5);

        mockMvc.perform(patch("/api/wishes")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(wishService).updateWish(eq(memberId), eq(productId), eq(5));
    }

    @Test
    @DisplayName("장바구니 상품 삭제")
    void deleteWish() throws Exception {
        WishRequest request = new WishRequest(productId, 0);

        mockMvc.perform(delete("/api/wishes")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(wishService).deleteWish(eq(memberId), eq(productId));
    }

    @Test
    @DisplayName("장바구니 상품 조회")
    void getWishes() throws Exception {
        mockMvc.perform(get("/api/wishes")
                        .header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk());

        verify(wishService).getWishesPage(eq(memberId), any(Pageable.class));
    }
}

