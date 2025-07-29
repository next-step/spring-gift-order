package gift;

import gift.Entity.Member;
import gift.Entity.Option;
import gift.Entity.Product;
import gift.repository.MemberRepository;
import gift.repository.OptionRepository;
import gift.repository.ProductRepository;
import gift.request.MemberRequest;
import gift.request.WishRequest;
import gift.response.TokenResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WishRestControllerTest {

    @LocalServerPort
    int port;

    RestClient client = RestClient.builder().build();

    @Autowired
    ProductRepository productRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    OptionRepository optionRepository;

    private String token;
    private Long productId;
    private Long optionId;

    @BeforeEach
    void setup() {

        if (memberRepository.findByNickname("helloworld").isEmpty()) {
            memberRepository.save(new Member(
                    "helloworld", "hello@kakao.com", "123456789", "테스트", "대한민국", "USER"
            ));
        }

        Product product = new Product();
        product.setName("테스트 상품");
        product.setPrice(3000);
        product.setImageUrl("http://image.com");
        product.setMDapproved(true);
        product = productRepository.save(product);
        productId = product.getId();

        Option opt = new Option("default", 10, product);
        opt = optionRepository.save(opt); // 명시 저장
        optionId = opt.getId();

        // 3. 로그인
        var loginRes = client.post()
                .uri("http://localhost:" + port + "/api/login")
                .body(new MemberRequest("helloworld", "123456789"))
                .retrieve()
                .toEntity(TokenResponse.class);

        token = loginRes.getBody().getToken();
    }

    @Test
    void testAddWish() {
        var url = "http://localhost:" + port + "/wishes";
        WishRequest req = new WishRequest(productId, optionId);

        var response = client.post()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(req)
                .retrieve()
                .toBodilessEntity();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void testRemoveWish() {
        var url = "http://localhost:" + port + "/wishes";
        WishRequest req = new WishRequest(productId, optionId);

        // 먼저 등록
        client.post()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(req)
                .retrieve();

        // 삭제
        String deleteUrl = "http://localhost:" + port + "/wishes?productId=" + productId + "&optionId=" + optionId;

        var response = client.delete()
                .uri(deleteUrl)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .toBodilessEntity();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}

