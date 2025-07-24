package gift.controller;

import gift.Jwt.JwtUtil;
import gift.Jwt.TokenUtils;
import gift.entity.User;
import gift.entity.UserRole;
import gift.repository.itemRepository.ItemRepository;
import gift.repository.userRepository.UserRepository;
import gift.service.itemService.ItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@AutoConfigureWebTestClient
@Transactional
class AdminItemControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenUtils tokenUtils;

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void 관리자상품저장_성공() throws Exception {
        User admin = new User("example@exmaple.com", "1234", UserRole.ADMIN);
        userRepository.save(admin);

        String token = jwtUtil.generateToken(admin);

        mockMvc.perform(post("/admin/products")
                        .header("Authorization", "Bearer " + token)
                        .param("name", "카카오")
                        .param("price", "1500")
                        .param("imageUrl", "juice.png")
                        .param("useKakaoName", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/products"));

        assertThat(itemRepository.findAll())
                .anyMatch(item -> item.getName().equals("카카오"));
    }

    @Test
    void 인증없이상품저장_오류응답() throws Exception {
        mockMvc.perform(post("/admin/products")
                        .param("name", "카카오")
                        .param("price", "1500")
                        .param("imageUrl", "juice.png")
                        .param("useKakaoName", "false"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void 토큰에서이메일_추출성공() {
        User user = new User(1L, "tester@example.com", "securePassword", UserRole.USER);
        String token = jwtUtil.generateToken(user);

        String extractedEmail = tokenUtils.extractEmail(token);

        assertThat(extractedEmail).isEqualTo("tester@example.com");
    }
}
