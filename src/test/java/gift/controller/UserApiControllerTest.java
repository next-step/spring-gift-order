package gift.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gift.dto.jwt.TokenResponse;
import gift.dto.user.ChangePasswordRequest;
import gift.dto.user.CreateUserRequest;
import gift.dto.user.LoginRequest;
import gift.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserApiControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    UserService userService;

    @Test
    @DisplayName("회원가입을 할 수 있다.")
    void test1() throws Exception {
        String body = mapper.writeValueAsString(
                new CreateUserRequest("tkddnr@thanks.com", "1234")
        );

        mvc.perform(post("/api/users/register")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("로그인을 할 수 있다.")
    void test2() throws Exception {
        userService.saveUser(new CreateUserRequest("tkddnr@thanks.com", "1234"));

        String login = mapper.writeValueAsString(
                new LoginRequest("tkddnr@thanks.com", "1234")
        );

        mvc.perform(post("/api/users/login")
                        .content(login)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

    @Test
    @DisplayName("비밀번호 변경을 할 수 있다.")
    void test3() throws Exception {
        userService.saveUser(new CreateUserRequest("tkddnr@thanks.com", "1234"));

        String login1 = mapper.writeValueAsString(
                new LoginRequest("tkddnr@thanks.com", "1234")
        );

        MvcResult result = mvc.perform(post("/api/users/login")
                        .content(login1)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andReturn();

        //get accessToken
        String accessToken = mapper.readValue(result.getResponse().getContentAsString(), TokenResponse.class).accessToken();

        String changePw = mapper.writeValueAsString(
                new ChangePasswordRequest("tkddnr@thanks.com", "1234", "12345")
        );

        mvc.perform(post("/api/users/password")
                        .content(changePw)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isNoContent());

        String login2 = mapper.writeValueAsString(
                new LoginRequest("tkddnr@thanks.com", "12345")
        );

        mvc.perform(post("/api/users/login")
                        .content(login2)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

    @Test
    @DisplayName("Authorization 헤더의 jwt 토큰 없이 비밀번호 변경을 할 수 없다.")
    public void test4() throws Exception {
        userService.saveUser(new CreateUserRequest("tkddnr@thanks.com", "1234"));

        String changePw = mapper.writeValueAsString(
                new ChangePasswordRequest("tkddnr@thanks.com", "1234", "12345")
        );

        mvc.perform(post("/api/users/password")
                        .content(changePw)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isUnauthorized());
    }
}