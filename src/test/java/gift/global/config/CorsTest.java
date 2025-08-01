package gift.global.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "JWT_SECRET=qsnJ4L4zNMsrJQfIqOjwxy7EuDc2YIXOpJ3CgdspxXo=")
public class CorsTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void cors_설정_확인() throws Exception {
        mockMvc.perform(
                        options("/api/admin/products")
                                .header(HttpHeaders.ORIGIN, "http://localhost:3000")
                                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET")
                                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "Content-Type")
                )
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*"))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET,POST,PUT,DELETE,OPTIONS"))
                .andExpect(header().doesNotExist(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS));
    }
}