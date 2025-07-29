package gift;

import gift.Entity.Member;
import gift.repository.MemberRepository;
import gift.request.MemberRequest;
import gift.response.TokenResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import static com.jayway.jsonpath.internal.path.PathCompiler.fail;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MemberRestControllerTest {

    @LocalServerPort
    private int port;

    private RestClient client = RestClient.builder().build();

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setupTestMember() {

        // 테스트용 계정 등록
        Member member = new Member("hello_world", "hello@kakao.com", "123456789", "테스트", "대한민국", "USER");
        memberRepository.save(member);
    }

    @Transactional
    @Test
    public void testRegisterMember() {
        var url = "http://localhost:" + port + "/api/register";
        var member = new Member("bye_world", "byeworld@kakao.com", "123456789", "안녕세상", "대한민국", "USER");

        var response = client.post()
                .uri(url)
                .body(member)
                .retrieve()
                .toEntity(Member.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getNickname()).isEqualTo("bye_world");
    }


    @Test
    public void testLogin() {
        var url = "http://localhost:" + port + "/api/login";
        var req = new MemberRequest("hello_world", "123456789");

        var response = client.post()
                .uri(url)
                .body(req)
                .retrieve()
                .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void testRegisterDuplicateId() {
        var url = "http://localhost:" + port + "/members/register";
        var duplicate = new Member("helloworld", "new@kakao.com", "password", "중복유저", "주소", "USER");

        try {
            client.post()
                    .uri(url)
                    .body(duplicate)
                    .retrieve()
                    .toEntity(String.class);
            fail("예외가 발생해야 합니다.");
        } catch (HttpClientErrorException.BadRequest e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(e.getResponseBodyAsString()).contains("이미 사용 중인 아이디입니다.");
        }
    }

    @Test
    public void testLoginAsAdminAndUser() {
        // 관리자 로그인
        var adminLoginRes = client.post()
                .uri("http://localhost:" + port + "/members/login")
                .body(new MemberRequest("admin01", "123456789"))
                .retrieve()
                .toEntity(TokenResponse.class);

        assertThat(adminLoginRes.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(adminLoginRes.getBody().getRole()).isEqualTo("ADMIN");

        // 유저 로그인
        var userLoginRes = client.post()
                .uri("http://localhost:" + port + "/members/login")
                .body(new MemberRequest("hello_world", "123456789"))
                .retrieve()
                .toEntity(TokenResponse.class);

        assertThat(userLoginRes.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(userLoginRes.getBody().getRole()).isEqualTo("USER");
    }


}
