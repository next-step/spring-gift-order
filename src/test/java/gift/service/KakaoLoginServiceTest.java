package gift.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import gift.entity.Member;
import gift.repository.MemberRepository;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
public class KakaoLoginServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private KakaoLoginService kakaoLoginService;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void getAccessTokenTest() {
        // given
        var code = "codee";
        var expectedToken = "tokenn";
        var expectedBody = "{\"access_token\":\"" + expectedToken + "\"}";
        var member = Member.of("aran@email.com", "1234");
        memberRepository.save(member);

        this.mockServer.expect(requestTo("https://kauth.kakao.com/oauth/token"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(expectedBody, MediaType.APPLICATION_JSON));

        // when
        var token = kakaoLoginService.getAccessToken(code);

        // then
        assertThat(token).isEqualTo(expectedToken);
    }

}
