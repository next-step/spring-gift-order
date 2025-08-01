package gift;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
class CorsTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Test
    void CORS_허용된_오리진에서_요청시_성공() throws Exception {
        mockMvc =
                MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        mockMvc.perform(options("/api/products")
                        .header("Origin", "http://localhost:3000")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())

                .andExpect(header().string("Access-Control-Allow-Origin",
                        "http://localhost:3000"))

                .andExpect(header().string("Access-Control-Allow-Credentials",
                        "true"));
    }

    @Test
    void CORS_허용되지_않은_오리진에서_요청시_실패() throws
            Exception {
        mockMvc =
                MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        mockMvc.perform(options("/api/products")
                        .header("Origin", "http://localhost:3001")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isForbidden());
    }
}

