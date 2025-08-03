package gift.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import gift.dto.LoginMemberDto;
import gift.dto.OrderRequest;
import gift.dto.OrderResponse;
import gift.exception.GlobalExceptionHandler;
import gift.service.OrderService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
class OrderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private OrderService orderService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private OrderRequest orderRequest;
    private LoginMemberDto loginMember;

    @BeforeEach
    void setUp() {
        OrderController controller = new OrderController(orderService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(new GlobalExceptionHandler())  // 예외핸들러 등록
            .build();

        orderRequest = new OrderRequest(1L, 2, "gift message");
        loginMember = new LoginMemberDto("test@email.com", "jwtAccessToken");
    }

    @Test
    void 주문_성공() throws Exception {
        // given
        OrderResponse response = new OrderResponse(1L, 1L, 2, LocalDateTime.now(), "생일 축하해!");
        given(orderService.order(any(OrderRequest.class), any(LoginMemberDto.class)))
            .willReturn(response);

        // when & then
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest))
                .requestAttr("LoginMember", loginMember)) // 커스텀 리졸버로 주입되는 값 수동 설정
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(response.getId()))
            .andExpect(jsonPath("$.optionId").value(response.getOptionId()))
            .andExpect(jsonPath("$.quantity").value(response.getQuantity()))
            .andExpect(jsonPath("$.message").value(response.getMessage()));
    }

    @Test
    void 수량이_재고보다_많으면_주문실패() throws Exception {
        // given
        given(orderService.order(any(OrderRequest.class), any(LoginMemberDto.class)))
            .willThrow(new IllegalStateException("현재 재고보다 작은 수량만 주문할 수 있습니다. 현재 재고:1"));

        // when & then
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest))
                .requestAttr("LoginMember", loginMember))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(containsString("현재 재고보다 작은 수량만 주문할 수 있습니다")));
    }
}