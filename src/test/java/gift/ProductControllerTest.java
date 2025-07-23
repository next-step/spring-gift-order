package gift;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import gift.dto.request.ProductRequestDto;
import gift.dto.request.ProductUpdateRequestDto;
import gift.dto.response.ProductResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private RestClient.Builder builder;

    private RestClient client;

    @BeforeEach
    void setUp() {
        client = builder.build();
    }

    @Test
    @DisplayName("정상 상품 조회")
    void 존재하는_상품아이디_입력_200_반환(){
        var url = "http://localhost:" + port + "/api/products/1";

        var response = client.get()
            .uri(url)
            .retrieve()
            .toEntity(ProductResponseDto.class)
            ;

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("존재하지 않는 상품 조회")
    void 존재하지_않는_상품아이디_입력_404_반환(){
        var url = "http://localhost:" + port +"/api/products/111";
        assertThatExceptionOfType(HttpClientErrorException.NotFound.class).isThrownBy(
            () ->
                client.get()
                    .uri(url)
                    .retrieve()
                    .toEntity(ProductResponseDto.class)
        );
    }

    @Test
    @DisplayName("정상 상품 추가")
    void 정상적인_상품_추가_200_반환(){
        var url = "http://localhost:" + port +"/api/products";
        var requestDto = new ProductRequestDto(
            2,
            "정상적인상품명",
            1000,
            "http://example.com/image.png");

        var response = client.post()
            .uri(url)
            .contentType(MediaType.APPLICATION_JSON)
            .body(requestDto)
            .retrieve()
            .toEntity(ProductResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody())
            .isNotNull()
            .extracting("productId", "name", "price", "imageURL")
            .containsExactly(2L, "정상적인상품명",1000,"http://example.com/image.png")
            ;
    }

    @Test
    @DisplayName("유효하지 않은 상품 추가")
    void 유효하지_않은_상품명_입력_400_반환(){
        var url = "http://localhost:" + port +"/api/products";
        var requestDto = new ProductRequestDto(
            3,
            "입력테스트15자를넘어가면안되는데넘어버렸네",
            1000,
            "http://example.com/image.png");

        assertThatExceptionOfType(HttpClientErrorException.BadRequest.class).isThrownBy(
            () ->
                client.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestDto)
                    .retrieve()
                    .toEntity(ProductResponseDto.class)
        );
    }

    @Test
    @DisplayName("유효하지 않은 상품 수정")
    void 유효하지_않은_상품명_입력_400_반환2(){
        var url = "http://localhost:" + port +"/api/products/1";
        var requestDto = new ProductUpdateRequestDto(
            "입력테스트15자를넘어가면안되는데넘어버렸네",
            1000,
            "http://example.com/image.png");

        assertThatExceptionOfType(HttpClientErrorException.BadRequest.class).isThrownBy(
            () ->
                client.patch()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestDto)
                    .retrieve()
                    .toEntity(ProductResponseDto.class)
        );
    }
}
