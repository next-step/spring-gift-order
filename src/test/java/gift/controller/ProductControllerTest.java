package gift.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gift.dto.OptionRequestDTO;
import gift.dto.ProductRequestDTO;
import gift.dto.ProductResponseDTO;
import gift.dto.RegisterRequestDTO;
import gift.dto.TokenResponseDTO;
import gift.entity.Product;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Sql("/cleanup.sql")
class ProductControllerTest {

    @LocalServerPort
    private int port;
    private RestClient client;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setup() {
        String url = "http://localhost:" + port + "/api/members"; // members로 수정
        client = RestClient.builder().baseUrl(url).build();
        final String email = "test@test.com";
        final String password = "password123";
        RegisterRequestDTO req = new RegisterRequestDTO(email, password);
        ResponseEntity<TokenResponseDTO> response = client.post()
            .uri("/register")
            .contentType(MediaType.APPLICATION_JSON)
            .body(req)
            .retrieve()
            .toEntity(TokenResponseDTO.class);
        assertNotNull(response);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().token()).isNotBlank();

        String jwtToken = response.getBody().token();

        url = "http://localhost:" + port + "/api/products";
        client = RestClient.builder()
            .baseUrl(url)
            .defaultHeader("Authorization", "Bearer " + jwtToken)
            .build();
    }

    @Test
    @DisplayName("상품 생성 - 성공")
    void createProduct() {
        ProductRequestDTO request = new ProductRequestDTO("테스트 상품", 4500L, "https://test.jpg", List.of(new OptionRequestDTO("기본 옵션", 1)));

        ResponseEntity<ProductResponseDTO> response = client.post()
            .uri("")
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .toEntity(ProductResponseDTO.class);

        assertNotNull(response);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo("테스트 상품");
        assertThat(response.getBody().price()).isEqualTo(4500L);
        assertThat(response.getBody().imageUrl()).isEqualTo("https://test.jpg");
    }

    @Test
    @DisplayName("상품 생성 - 유효성 검증 실패 (15자 초과)")
    void createProductNameTooLong() {
        ProductRequestDTO request = new ProductRequestDTO("01234567890123456789", 4500L, "https://test.jpg", List.of(new OptionRequestDTO("기본 옵션", 1)));

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () ->
            client.post()
                .uri("")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toEntity(ProductResponseDTO.class)
        );

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(exception.getResponseBodyAsString()).contains(
            "상품 이름은 공백을 포함하여 최대 15자까지 입력할 수 있습니다.");
    }

    @Test
    @DisplayName("상품 생성 - 유효성 검증 실패 (가격 0 이하)")
    void createProduct_InvalidPrice() {
        ProductRequestDTO request = new ProductRequestDTO("test", -123L, "https://test.jpg", List.of(new OptionRequestDTO("기본 옵션", 1)));

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () ->
            client.post()
                .uri("")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toEntity(ProductResponseDTO.class)
        );

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(exception.getResponseBodyAsString()).contains("상품 가격은 0보다 큰 값이어야 합니다.");
    }

    @Test
    @DisplayName("상품 생성 - 유효성 검증 실패 (잘못된 URL)")
    void createProduct_InvalidUrl() {
        ProductRequestDTO request = new ProductRequestDTO("테스트 상품", 4500L, "invalid-url", List.of(new OptionRequestDTO("기본 옵션", 1)));

        assertThrows(HttpClientErrorException.class, () ->
            client.post()
                .uri("")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toEntity(ProductResponseDTO.class)
        );
    }

    @Test
    @DisplayName("상품 조회 - 성공 테스트")
    void getProduct() {
        ProductRequestDTO createRequest = new ProductRequestDTO(
            "조회 테스트 상품",
            5000L,
            "https://example.jpg",
            List.of(new OptionRequestDTO("기본 옵션", 1))
        );

        ResponseEntity<ProductResponseDTO> createResponse = client.post()
            .uri("")
            .contentType(MediaType.APPLICATION_JSON)
            .body(createRequest)
            .retrieve()
            .toEntity(ProductResponseDTO.class);

        Long productId = createResponse.getBody() != null ? createResponse.getBody().id() : null;
        assertNotNull(productId);

        ResponseEntity<ProductResponseDTO> response = client.get()
            .uri("/" + productId)
            .retrieve()
            .toEntity(ProductResponseDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(productId);
        assertThat(response.getBody().name()).isEqualTo("조회 테스트 상품");
    }

    @Test
    @DisplayName("존재하지 않는 상품 조회 - 404 Not Found")
    void getProduct_NotFound() {
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () ->
            client.get()
                .uri("/99999")
                .retrieve()
                .toEntity(ProductResponseDTO.class)
        );

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("모든 상품 조회 - 성공 테스트")
    void getAllProducts() {
        ProductRequestDTO request1 = new ProductRequestDTO(
            "상품1",
            1000L,
            "https://test1.jpg",
            List.of(new OptionRequestDTO("옵션1", 1))
        );

        ProductRequestDTO request2 = new ProductRequestDTO(
            "상품2",
            2000L,
            "https://test2.jpg",
            List.of(new OptionRequestDTO("옵션2", 1))
        );

        client.post().uri("").contentType(MediaType.APPLICATION_JSON).body(request1).retrieve()
            .toBodilessEntity();
        client.post().uri("").contentType(MediaType.APPLICATION_JSON).body(request2).retrieve()
            .toBodilessEntity();

        ResponseEntity<List<ProductResponseDTO>> response = client.get()
            .uri("")
            .retrieve()
            .toEntity(new ParameterizedTypeReference<>() {
            });

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("상품 조회 - 페이지네이션 테스트")
    void getAllProductsWithPagination() {
        // 여러 상품 생성
        for (int i = 1; i <= 15; i++) {
            ProductRequestDTO request = new ProductRequestDTO(
                "상품" + i,
                1000L * i,
                "https://test" + i + ".jpg",
                List.of(new OptionRequestDTO("옵션" + i, 1))
            );
            client.post().uri("").contentType(MediaType.APPLICATION_JSON).body(request).retrieve()
                .toBodilessEntity();
        }

        // 첫 번째 페이지 조회 (page=0, size=10)
        ResponseEntity<List<ProductResponseDTO>> response = client.get()
            .uri("?page=0&size=10")
            .retrieve()
            .toEntity(new ParameterizedTypeReference<>() {
            });

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().size()).isEqualTo(10);
    }

    @Test
    @DisplayName("상품 조회 - 정렬 테스트 (이름 오름차순)")
    void getAllProductsWithNameSort() {
        ProductRequestDTO request1 = new ProductRequestDTO(
            "B상품",
            1000L,
            "https://test1.jpg",
            List.of(new OptionRequestDTO("B옵션", 1))
        );

        ProductRequestDTO request2 = new ProductRequestDTO(
            "A상품",
            2000L,
            "https://test2.jpg",
            List.of(new OptionRequestDTO("A옵션", 1))
        );

        client.post().uri("").contentType(MediaType.APPLICATION_JSON).body(request1).retrieve()
            .toBodilessEntity();
        client.post().uri("").contentType(MediaType.APPLICATION_JSON).body(request2).retrieve()
            .toBodilessEntity();

        ResponseEntity<List<ProductResponseDTO>> response = client.get()
            .uri("?sort=name,asc")
            .retrieve()
            .toEntity(new ParameterizedTypeReference<>() {
            });

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().size()).isGreaterThanOrEqualTo(2);
        // 첫 번째 상품이 A상품이어야 함 (이름 오름차순)
        assertThat(response.getBody().get(0).name()).isEqualTo("A상품");
    }

    @Test
    @DisplayName("상품 조회 - 정렬 테스트 (가격 내림차순)")
    void getAllProductsWithPriceSort() {
        ProductRequestDTO request1 = new ProductRequestDTO(
            "저가상품",
            1000L,
            "https://test1.jpg",
            List.of(new OptionRequestDTO("저가옵션", 1))
        );

        ProductRequestDTO request2 = new ProductRequestDTO(
            "고가상품",
            5000L,
            "https://test2.jpg",
            List.of(new OptionRequestDTO("고가옵션", 1))
        );

        client.post().uri("").contentType(MediaType.APPLICATION_JSON).body(request1).retrieve()
            .toBodilessEntity();
        client.post().uri("").contentType(MediaType.APPLICATION_JSON).body(request2).retrieve()
            .toBodilessEntity();

        ResponseEntity<List<ProductResponseDTO>> response = client.get()
            .uri("?sort=price,desc")
            .retrieve()
            .toEntity(new ParameterizedTypeReference<>() {
            });

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().size()).isGreaterThanOrEqualTo(2);
        // 첫 번째 상품이 고가상품이어야 함 (가격 내림차순)
        assertThat(response.getBody().get(0).price()).isGreaterThan(
            response.getBody().get(1).price());
    }

    @Test
    @DisplayName("상품 수정 - 성공 테스트")
    void updateProduct() {
        ProductRequestDTO createRequest = new ProductRequestDTO(
            "수정 전 상품",
            3000L,
            "https://before.jpg",
            List.of(new OptionRequestDTO("수정 전 옵션", 1))
        );

        ResponseEntity<ProductResponseDTO> createResponse = client.post()
            .uri("")
            .contentType(MediaType.APPLICATION_JSON)
            .body(createRequest)
            .retrieve()
            .toEntity(ProductResponseDTO.class);

        Long productId = createResponse.getBody() != null ? createResponse.getBody().id() : null;
        assertNotNull(productId);

        ProductRequestDTO updateRequest = new ProductRequestDTO(
            "수정 후 상품",
            4000L,
            "https://after.jpg",
            List.of(new OptionRequestDTO("수정 후 옵션", 1))
        );

        ResponseEntity<ProductResponseDTO> response = client.put()
            .uri("/" + productId)
            .contentType(MediaType.APPLICATION_JSON)
            .body(updateRequest)
            .retrieve()
            .toEntity(ProductResponseDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo("수정 후 상품");
        assertThat(response.getBody().price()).isEqualTo(4000L);
        assertThat(response.getBody().imageUrl()).isEqualTo("https://after.jpg");
    }

    @Test
    @DisplayName("존재하지 않는 상품 수정 - 404 Not Found")
    void updateProduct_not_found() {
        ProductRequestDTO updateRequest = new ProductRequestDTO(
            "수정할 상품",
            4000L,
            "https://test.jpg",
            List.of(new OptionRequestDTO("기본 옵션", 1))
        );

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () ->
            client.put()
                .uri("/99999")
                .contentType(MediaType.APPLICATION_JSON)
                .body(updateRequest)
                .retrieve()
                .toEntity(ProductResponseDTO.class)
        );

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("상품 삭제 - 성공 테스트")
    void deleteProduct() {
        ProductRequestDTO createRequest = new ProductRequestDTO(
            "삭제할 상품",
            5000L,
            "https://delete.jpg",
            List.of(new OptionRequestDTO("삭제할 옵션", 1))
        );

        ResponseEntity<ProductResponseDTO> createResponse = client.post()
            .uri("")
            .contentType(MediaType.APPLICATION_JSON)
            .body(createRequest)
            .retrieve()
            .toEntity(ProductResponseDTO.class);

        Long productId = createResponse.getBody() != null ? createResponse.getBody().id() : null;
        assertNotNull(productId);

        ResponseEntity<Void> response = client.delete()
            .uri("/" + productId)
            .retrieve()
            .toBodilessEntity();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // 삭제된 상품을 조회할 때는 404 NOT_FOUND가 반환되어야 함
        try {
            client.get()
                .uri("/" + productId)
                .retrieve()
                .toEntity(ProductResponseDTO.class);
        } catch (Exception e) {
            // 404 오류가 발생하는 것이 정상
            assertThat(e.getMessage()).contains("404");
        }
    }

    @Test
    @DisplayName("존재하지 않는 상품 삭제 - 204 No Content")
    void deleteProduct_NotFound() {
        ResponseEntity<Void> response = client.delete()
            .uri("/99999")
            .retrieve()
            .toBodilessEntity();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("상품 생성 - 유효성 검증 실패 (카카오 포함)")
    void createProduct_ContainsKakao() {
        ProductRequestDTO request = new ProductRequestDTO(
            "카카오 상품",
            4500L,
            "https://test.jpg",
            List.of(new OptionRequestDTO("기본 옵션", 1))
        );

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () ->
            client.post()
                .uri("")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toEntity(ProductResponseDTO.class)
        );

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(exception.getResponseBodyAsString()).contains(
            "카카오가 포함된 문구는 담당 MD와 협의한 경우에만 사용할 수 있습니다.");
    }

    @Test
    @DisplayName("상품 수정 - 유효성 검증 실패 (카카오 포함)")
    void updateProduct_ContainsKakao() {
        ProductRequestDTO createRequest = new ProductRequestDTO(
            "일반 상품",
            3000L,
            "https://test.jpg",
            List.of(new OptionRequestDTO("기본 옵션", 1))
        );

        ResponseEntity<ProductResponseDTO> createResponse = client.post()
            .uri("")
            .contentType(MediaType.APPLICATION_JSON)
            .body(createRequest)
            .retrieve()
            .toEntity(ProductResponseDTO.class);

        Long productId = createResponse.getBody() != null ? createResponse.getBody().id() : null;
        assertNotNull(productId);

        ProductRequestDTO updateRequest = new ProductRequestDTO(
            "카카오프렌즈 상품",
            4000L,
            "https://test.jpg",
            List.of(new OptionRequestDTO("기본 옵션", 1))
        );

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () ->
            client.put()
                .uri("/" + productId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(updateRequest)
                .retrieve()
                .toEntity(ProductResponseDTO.class)
        );

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(exception.getResponseBodyAsString()).contains(
            "카카오가 포함된 문구는 담당 MD와 협의한 경우에만 사용할 수 있습니다.");
    }

    @Test
    @DisplayName("상품 목록 조회 - 페이징 파라미터 유효성 검증: 음수 페이지")
    void getProducts_InvalidNegativePage() {
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () ->
            client.get()
                .uri("?page=-1&size=10")
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<ProductResponseDTO>>() {
                })
        );

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(exception.getResponseBodyAsString()).contains("페이지 번호는 0 이상이어야 합니다");
    }

    @Test
    @DisplayName("상품 목록 조회 - 페이징 파라미터 유효성 검증: 페이지 크기 0")
    void getProducts_InvalidZeroSize() {
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () ->
            client.get()
                .uri("?page=0&size=0")
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<ProductResponseDTO>>() {
                })
        );

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(exception.getResponseBodyAsString()).contains("페이지 크기는 1 이상이어야 합니다");
    }

    @Test
    @DisplayName("상품 목록 조회 - 페이징 파라미터 유효성 검증: 음수 페이지 크기")
    void getProducts_InvalidNegativeSize() {
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () ->
            client.get()
                .uri("?page=0&size=-5")
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<ProductResponseDTO>>() {
                })
        );

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(exception.getResponseBodyAsString()).contains("페이지 크기는 1 이상이어야 합니다");
    }

    @Test
    @DisplayName("상품 목록 조회 - 페이징 파라미터 유효성 검증: 페이지 크기 초과 (51)")
    void getProducts_InvalidOversizedPage() {
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () ->
            client.get()
                .uri("?page=0&size=51")
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<ProductResponseDTO>>() {
                })
        );

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(exception.getResponseBodyAsString()).contains("페이지 크기는 50 이하여야 합니다");
    }

    @Test
    @DisplayName("상품 목록 조회 - 페이징 파라미터 유효성 검증: 극단적 페이지 크기 (1억)")
    void getProducts_InvalidExtremePageSize() {
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () ->
            client.get()
                .uri("?page=0&size=100000000")
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<ProductResponseDTO>>() {
                })
        );

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(exception.getResponseBodyAsString()).contains("페이지 크기는 50 이하여야 합니다");
    }

    @Test
    @DisplayName("상품 목록 조회 - 페이징 파라미터 유효성 검증: 최대 허용 페이지 크기 (50)")
    void getProducts_ValidMaxPageSize() {
        // 테스트용 상품 몇 개 생성
        createTestProduct("상품1", 1000L);
        createTestProduct("상품2", 2000L);

        ResponseEntity<List<ProductResponseDTO>> response = client.get()
            .uri("?page=0&size=50")
            .retrieve()
            .toEntity(new ParameterizedTypeReference<List<ProductResponseDTO>>() {
            });

        assertNotNull(response);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    @DisplayName("상품 목록 조회 - 페이징 파라미터 유효성 검증: 정상 케이스")
    void getProducts_ValidPagination() {
        // 테스트용 상품 생성
        createTestProduct("상품1", 1000L);
        createTestProduct("상품2", 2000L);
        createTestProduct("상품3", 3000L);

        ResponseEntity<List<ProductResponseDTO>> response = client.get()
            .uri("?page=0&size=2")
            .retrieve()
            .toEntity(new ParameterizedTypeReference<List<ProductResponseDTO>>() {
            });

        assertNotNull(response);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("상품 목록 조회 - 기본값 테스트 (파라미터 없음)")
    void getProducts_DefaultParameters() {
        createTestProduct("상품1", 1000L);

        ResponseEntity<List<ProductResponseDTO>> response = client.get()
            .uri("")
            .retrieve()
            .toEntity(new ParameterizedTypeReference<List<ProductResponseDTO>>() {
            });

        assertNotNull(response);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    private Long createTestProduct(String name, Long price) {
        ProductRequestDTO request = new ProductRequestDTO(
            name,
            price,
            "https://test.jpg",
            List.of(new OptionRequestDTO("기본 옵션", 1))
        );

        ResponseEntity<ProductResponseDTO> response = client.post()
            .uri("")
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .toEntity(ProductResponseDTO.class);

        return response.getBody().id();
    }
}
