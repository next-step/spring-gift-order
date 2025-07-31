package gift;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
public class CorsAcceptanceTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("허용된 출처(EC2 IP)에서의 Pre-flight 요청은 성공한다")
    void allowedOriginCorsTest() throws Exception {
        // WebConfig에 설정된 허용된 출처
        String allowedOrigin = "http://3.39.239.230";

        mockMvc.perform(
                        options("/api/products")
                                .header(HttpHeaders.ORIGIN, allowedOrigin)
                                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET")
                )
                .andExpect(status().isOk()) // 200 OK 응답
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,
                        allowedOrigin)); // 허용 응답 헤더
    }

    @Test
    @DisplayName("허용되지 않은 출처(localhost)에서의 Pre-flight 요청은 거부된다")
    void disallowedOriginCorsTest() throws Exception {
        // 허용 목록에 없는 임의의 출처
        String disallowedOrigin = "http://localhost:3000";

        mockMvc.perform(
                        options("/api/products")
                                .header(HttpHeaders.ORIGIN, disallowedOrigin)
                                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET")
                )
                // CORS 정책 위반이므로 정상적으로 거부(Forbidden)되어야 함
                .andExpect(status().isForbidden());
    }
}