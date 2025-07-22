package gift.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import gift.dto.CreateOptionRequest;
import gift.dto.CreateProductRequest;
import gift.dto.CreateProductResponse;
import gift.dto.UpdateProductRequest;
import gift.repository.OptionJpaRepository;
import gift.repository.ProductJpaRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductRestControllerTest {

    @Autowired
    private OptionJpaRepository optionRepository;
    @Autowired
    private ProductJpaRepository productRepository;

    @LocalServerPort
    private int port;

    private RestClient client = RestClient.create();

    @BeforeEach
    void clear() {
        optionRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    @DisplayName("상품 정상 등록")
    void 상품_정상_등록() {
        String url = "http://localhost:" + port + "/api/products";
        ResponseEntity<CreateProductResponse> response = client.post()
                .uri(url)
                .body(new CreateProductRequest("product1", 1000, "exam.url", List.of(new CreateOptionRequest("옵션1", 10))))
                .retrieve()
                .toEntity(CreateProductResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    @DisplayName("상품 정상 실패(이름 누락)")
    void 상품_정상_실패_이름누락() {
        String url = "http://localhost:" + port + "/api/products";
        assertThatThrownBy(() ->
                client.post()
                        .uri(url)
                        .body(new CreateProductRequest("", 1000, "exam.url", List.of(new CreateOptionRequest("옵션1", 10))))
                        .retrieve()
                        .toEntity(CreateProductResponse.class)
        ).isInstanceOf(HttpClientErrorException.BadRequest.class);
    }

    @Test
    @DisplayName("상품 정상 실패(가격 음수)")
    void 상품_정상_실패_가격음수() {
        String url = "http://localhost:" + port + "/api/products";
        assertThatThrownBy(() ->
                client.post()
                        .uri(url)
                        .body(new CreateProductRequest("product1", -1000, "exam.url", List.of(new CreateOptionRequest("옵션1", 10))))
                        .retrieve()
                        .toEntity(CreateProductResponse.class)
        ).isInstanceOf(HttpClientErrorException.BadRequest.class);
    }


    @Test
    @DisplayName("상품 목록 조회 성공")
    void 상품_목록_조회() {
        String url = "http://localhost:" + port + "/api/products";
        ResponseEntity<Void> response = client.get()
                .uri(url)
                .retrieve()
                .toBodilessEntity();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("상품 조회 성공")
    void 상품_조회_성공() {
        String url = "http://localhost:" + port + "/api/products";
        ResponseEntity<CreateProductResponse> response = client.post()
                .uri(url)
                .body(new CreateProductRequest("product1", 1000, "exam.url", List.of(new CreateOptionRequest("옵션1", 10))))
                .retrieve()
                .toEntity(CreateProductResponse.class);
        Long id = response.getBody().id();

        url = "http://localhost:" + port + "/api/products/{id}";
        ResponseEntity<Void> findResponse = client.get()
                .uri(url, id)
                .retrieve()
                .toBodilessEntity();

        assertThat(findResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("상품 조회 실패")
    void 상품_조회_실패() {
        String url = "http://localhost:" + port + "/api/products";
        ResponseEntity<CreateProductResponse> response = client.post()
                .uri(url)
                .body(new CreateProductRequest("product1", 1000, "exam.url", List.of(new CreateOptionRequest("옵션1", 10))))
                .retrieve()
                .toEntity(CreateProductResponse.class);

        assertThatThrownBy(() ->
                client.get()
                        .uri("http://localhost:" + port + "/api/products/{id}", 2)
                        .retrieve()
                        .toBodilessEntity()
        ).isInstanceOf(HttpClientErrorException.NotFound.class);
    }

    @Test
    @DisplayName("상품 수정 성공")
    void 상품_수정_성공() {
        String url = "http://localhost:" + port + "/api/products";
        ResponseEntity<CreateProductResponse> response = client.post()
                .uri(url)
                .body(new CreateProductRequest("product1", 1000, "exam.url", List.of(new CreateOptionRequest("옵션1", 10))))
                .retrieve()
                .toEntity(CreateProductResponse.class);
        Long id = response.getBody().id();

        url = "http://localhost:" + port + "/api/products/{id}";
        ResponseEntity<Void> findResponse = client.put()
                .uri(url, id)
                .body(new UpdateProductRequest(1L, "update", 1000, "update.url"))
                .retrieve()
                .toBodilessEntity();

        assertThat(findResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("상품 수정 실패(없는 상품)")
    void 상품_수정_실패_없는_상품() {
        String url = "http://localhost:" + port + "/api/products";
        ResponseEntity<CreateProductResponse> response = client.post()
                .uri(url)
                .body(new CreateProductRequest("product1", 1000, "exam.url", List.of(new CreateOptionRequest("옵션1", 10))))
                .retrieve()
                .toEntity(CreateProductResponse.class);

        assertThatThrownBy(() ->
                client.put()
                        .uri("http://localhost:" + port + "/api/products/{id}", 2)
                        .body(new UpdateProductRequest(2L, "update", 1000, "update.url"))
                        .retrieve()
                        .toBodilessEntity()
        ).isInstanceOf(HttpClientErrorException.NotFound.class);
    }

    @Test
    @DisplayName("상품 수정 실패(이름 누락)")
    void 상품_수정_실패_이름_누락() {
        String url = "http://localhost:" + port + "/api/products";
        ResponseEntity<CreateProductResponse> response = client.post()
                .uri(url)
                .body(new CreateProductRequest("product1", 1000, "exam.url", List.of(new CreateOptionRequest("옵션1", 10))))
                .retrieve()
                .toEntity(CreateProductResponse.class);

        assertThatThrownBy(() ->
                client.put()
                        .uri("http://localhost:" + port + "/api/products/{id}", 2)
                        .body(new UpdateProductRequest(2L, "", 1000, "update.url"))
                        .retrieve()
                        .toBodilessEntity()
        ).isInstanceOf(HttpClientErrorException.BadRequest.class);
    }

    @Test
    @DisplayName("상품 수정 실패(잘못된 가격)")
    void 상품_수정_실패_가격_오류() {
        String url = "http://localhost:" + port + "/api/products";
        ResponseEntity<CreateProductResponse> response = client.post()
                .uri(url)
                .body(new CreateProductRequest("product1", 1000, "exam.url", List.of(new CreateOptionRequest("옵션1", 10))))
                .retrieve()
                .toEntity(CreateProductResponse.class);

        assertThatThrownBy(() ->
                client.put()
                        .uri("http://localhost:" + port + "/api/products/{id}", 2)
                        .body(new UpdateProductRequest(2L, "", -200, "update.url"))
                        .retrieve()
                        .toBodilessEntity()
        ).isInstanceOf(HttpClientErrorException.BadRequest.class);
    }

    @Test
    @DisplayName("상품 삭제 성공")
    void 상품_삭제_성공() {
        String url = "http://localhost:" + port + "/api/products";
        ResponseEntity<CreateProductResponse> response = client.post()
                .uri(url)
                .body(new CreateProductRequest("product1", 1000, "exam.url", List.of(new CreateOptionRequest("옵션1", 10))))
                .retrieve()
                .toEntity(CreateProductResponse.class);
        Long id = response.getBody().id();

        url = "http://localhost:" + port + "/api/products/{id}";
        ResponseEntity<Void> findResponse = client.delete()
                .uri(url, id)
                .retrieve()
                .toBodilessEntity();

        assertThat(findResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("상품 삭제 실패")
    void 상품_삭제_실패() {
        String url = "http://localhost:" + port + "/api/products";
        ResponseEntity<CreateProductResponse> response = client.post()
                .uri(url)
                .body(new CreateProductRequest("product1", 1000, "exam.url", List.of(new CreateOptionRequest("옵션1", 10))))
                .retrieve()
                .toEntity(CreateProductResponse.class);

        assertThatThrownBy(() ->
                client.delete()
                        .uri("http://localhost:" + port + "/api/products/{id}", 2)
                        .retrieve()
                        .toBodilessEntity()
        ).isInstanceOf(HttpClientErrorException.NotFound.class);
    }

    @Test
    @DisplayName("옵션 등록시 특수문자 검증(에러)")
    void 옵션_등록_실패_특수문자() {
        String url = "http://localhost:" + port + "/api/products";
        ResponseEntity<CreateProductResponse> response = client.post()
                .uri(url)
                .body(new CreateProductRequest("product1", 1000, "exam.url", List.of(new CreateOptionRequest("옵션1", 10))))
                .retrieve()
                .toEntity(CreateProductResponse.class);

        assertThatThrownBy(() ->
                client.post()
                        .uri("http://localhost:" + port + "/api/products/{id}/options", 1)
                        .body(new CreateOptionRequest("name*", 1000))
                        .retrieve()
                        .toBodilessEntity()
        ).isInstanceOf(HttpClientErrorException.BadRequest.class);
    }

    @Test
    @DisplayName("옵션 등록시 특수문자 검증(성공)")
    void 옵션_등록_성공_특수문자() {
        String url = "http://localhost:" + port + "/api/products";
        ResponseEntity<CreateProductResponse> response = client.post()
                .uri(url)
                .body(new CreateProductRequest("product1", 1000, "exam.url", List.of(new CreateOptionRequest("옵션1", 10))))
                .retrieve()
                .toEntity(CreateProductResponse.class);

        Long id = response.getBody().id();

        url = "http://localhost:" + port + "/api/products/{id}/options";
        ResponseEntity<Void> findResponse = client.post()
                .uri(url, id)
                .body(new CreateOptionRequest("(name)", 1000))
                .retrieve()
                .toBodilessEntity();

        assertThat(findResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }
}
