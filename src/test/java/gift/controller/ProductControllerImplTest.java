package gift.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import gift.entity.Product;
import gift.repository.ProductRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductControllerImplTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository products;

    @LocalServerPort
    private int port;

    private RestClient client = RestClient.builder().build();

    Product product1;

    @BeforeEach
    void setup() {
        product1 = Product.of("단호박", "donhobak.com", 500L);
        Product product2 = Product.of("블루베리", "blueberry.com", 88L);
        Product product3 = Product.of("귤", "gyul.com", 900L);
        products.save(product1);
        products.save(product2);
        products.save(product3);
    }

    @Test
    @DisplayName("아이디로 조회가 되는지를 테스트")
    void findByIdTest() {

        var id = product1.getId();

        var url = "http://localhost:" + port + "/api/products/" + id;

        var response = client.get().uri(url).retrieve().toEntity(Product.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        var actual = response.getBody();
        assertThat(actual.getName()).isEqualTo("단호박");
    }

    @Test
    @DisplayName("존재하지 않는 아이디로 조회 시 404 반환")
    void notFoundHandlerTest() {
        var url = "http://localhost:" + port + "/api/products/999";

        Assertions.assertThatExceptionOfType(HttpClientErrorException.NotFound.class)
                .isThrownBy(() -> client.get().uri(url).retrieve().toEntity(Void.class));
    }

    @Test
    @DisplayName("페이지 테스트")
    void pageTest() {
        // given

        var url = "http://localhost:" + port + "/api/products?page=0&size=2";

        // when
        var response = client.get().uri(url).retrieve().toEntity(String.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        String actual = response.getBody();

        System.out.println(actual);

        assertThat(actual).contains("단호박");
        assertThat(actual).contains("블루베리");
    }
}
