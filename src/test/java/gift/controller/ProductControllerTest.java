package gift.controller;

import gift.builder.ProductBuilder;
import gift.dto.request.ProductRequestDto;
import gift.dto.response.ProductResponseDto;
import gift.fixture.ProductFixture;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductControllerTest {
    @LocalServerPort
    private int port;

    private RestClient client=RestClient.builder().build();

    String baseUrl() {
        return "http://localhost:" + port + "/api/products";
    }


    @Test
    void 정상_등록_되지는_확인() {
        ProductRequestDto dto = ProductFixture.createProduct();

        var response = client.post()
                .uri(baseUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .body(dto)
                .retrieve()
                .toEntity(ProductResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void 상품_전체_조회() {
        var response = client.get()
                .uri(baseUrl())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<ProductResponseDto>>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void 상품_정보_수정() {
        ProductRequestDto createDto = ProductFixture.createProduct();
        var createdResponse = client.post()
                .uri(baseUrl())
                .body(createDto)
                .retrieve()
                .toEntity(ProductResponseDto.class);
        Long id = 1L;


        ProductRequestDto updateDto = new ProductRequestDto("수정후", 2000, "https://after.img");
        var updateResponse = client.put()
                .uri(baseUrl() + "/" + id)
                .body(updateDto)
                .retrieve()
                .toEntity(ProductResponseDto.class);


        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        ProductResponseDto updated = updateResponse.getBody();
        assertThat(updated.getName()).isEqualTo("수정후");
        assertThat(updated.getPrice()).isEqualTo(2000);
        assertThat(updated.getImageUrl()).isEqualTo("https://after.img");
    }


    @Test
    void 상품_삭제() {

        ProductRequestDto dto = ProductFixture.createProduct();
        var savedResponse = client.post()
                .uri(baseUrl())
                .body(dto)
                .retrieve()
                .toEntity(ProductResponseDto.class);

        Long id = 1L;


        var deleteResponse = client.delete()
                .uri(baseUrl() + "/" + id)
                .retrieve()
                .toBodilessEntity();

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    }


    @Test
    void 카카오가_포함된_상품명을_등록하려_할_때_예외_발생_확인() {

        ProductRequestDto requestDto = ProductBuilder.aProduct().withName("카카오").build();


        assertThatThrownBy(() ->
                client.post()
                        .uri(baseUrl())
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(requestDto)
                        .retrieve()
                        .toBodilessEntity()
        )
                .isInstanceOf(HttpClientErrorException.BadRequest.class);

    }

    @Test
    void 이름이_15자_이상일때_예외_발생() {
        ProductRequestDto requestDto = ProductBuilder.aProduct().withName("가나다라마바사아자차카타파하하하하하").build();


        assertThatThrownBy(() ->
                client.post()
                        .uri(baseUrl())
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(requestDto)
                        .retrieve()
                        .toBodilessEntity()
        )
                .isInstanceOf(HttpClientErrorException.BadRequest.class);
    }


    @Test
    void 이름에_허용되지_않은_특수문자_예외_발생() {
        ProductRequestDto requestDto = ProductBuilder.aProduct().withName("테스트@상품").build();


        assertThatThrownBy(() ->
                client.post()
                        .uri(baseUrl())
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(requestDto)
                        .retrieve()
                        .toBodilessEntity()
        )
                .isInstanceOf(HttpClientErrorException.BadRequest.class);
    }
}
