package gift.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gift.dto.kakao.KakaoLoginResponse;
import gift.dto.kakao.KakaoUserIdResponse;
import gift.service.api.KakaoLoginApi;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static gift.controller.ProductApiControllerTest.ProductApiControllerFixture.PRODUCT_EX1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class KakaoApiControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    KakaoLoginApi kakaoLoginApi;

    @Autowired
    ObjectMapper mapper;

    @Test
    @DisplayName("카카오 로그인을 하면 카카오의 액세스토큰과 서비스의 jwt 액세스토큰을 반환한다.")
    void test1() throws Exception{
        String code = "sample_code";
        String kakaoAccessToken = "저는카카오엑세스토큰입니다";

        given(kakaoLoginApi.getAccessToken(eq(code))).willReturn(kakaoAccessToken);
        given(kakaoLoginApi.getUserInfo(kakaoAccessToken)).willReturn(new KakaoUserIdResponse(12345678L));

        MvcResult result = mvc.perform(get("/kakao/callback")
                .param("code", code)
        ).andReturn();

        String content = result.getResponse().getContentAsString();
        KakaoLoginResponse kakaoLoginResponse = mapper.readValue(content, KakaoLoginResponse.class);

        assertThat(kakaoLoginResponse).isNotNull();
        assertThat(kakaoLoginResponse.kakaoAccessToken()).isEqualTo(kakaoAccessToken);
        assertThat(kakaoLoginResponse.jwtAccessToken()).isNotNull();
    }

    @Test
    @DisplayName("카카오 로그인을 한 유저도 jwtAccessToken을 이용하여 기존의 서비스를 사용 가능하다.")
    void test2() throws Exception{
        String code = "sample_code";
        String kakaoAccessToken = "저는카카오엑세스토큰입니다";

        given(kakaoLoginApi.getAccessToken(eq(code))).willReturn(kakaoAccessToken);
        given(kakaoLoginApi.getUserInfo(kakaoAccessToken)).willReturn(new KakaoUserIdResponse(12345678L));

        MvcResult result = mvc.perform(get("/kakao/callback")
                .param("code", code)
        ).andReturn();

        String content = result.getResponse().getContentAsString();
        KakaoLoginResponse kakaoLoginResponse = mapper.readValue(content, KakaoLoginResponse.class);
        String jwtAccessToken = "Bearer " + kakaoLoginResponse.jwtAccessToken();

        //상품 저장 로직
        String body = mapper.writeValueAsString(
                PRODUCT_EX1
        );

        mvc.perform(post("/api/products")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtAccessToken)
        ).andExpect(status().isCreated());
    }

}
