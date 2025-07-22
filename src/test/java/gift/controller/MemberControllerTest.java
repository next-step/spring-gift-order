package gift.controller;

import gift.dto.request.MemberRequsetDto;
import gift.dto.response.ProductResponseDto;
import gift.dto.response.TokenResponseDto;
import gift.fixture.MemberFixture;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MemberControllerTest {

    @LocalServerPort
    private int port;

    private RestClient client=RestClient.builder().build();

    String baseUrl() {
        return "http://localhost:" + port + "/api/members";
    }


    @Test
    void 회원가입_로그인_확인() {
        MemberRequsetDto memberRequsetDto= MemberFixture.createMember();



        /// /회원가입 테스트///
        var response= client.post()
                .uri(baseUrl()+"/register")
                .contentType(MediaType.APPLICATION_JSON)
                .body(memberRequsetDto)
                .retrieve()
                .toEntity(TokenResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getToken()).isNotBlank();


    /// ///로그인 테스트/////
        var response2= client.post()
                .uri(baseUrl()+"/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(memberRequsetDto)
                .retrieve()
                .toEntity(TokenResponseDto.class);

        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response2.getBody().getToken()).isNotBlank();
    }




}
