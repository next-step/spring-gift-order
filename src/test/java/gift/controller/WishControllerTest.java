package gift.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gift.dto.ProductRequestDTO;
import gift.dto.ProductResponseDTO;
import gift.dto.RegisterRequestDTO;
import gift.dto.TokenResponseDTO;
import gift.dto.WishRequestDTO;
import gift.dto.WishResponseDTO;
import gift.dto.WishUpdateDTO;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class WishControllerTest {

  @LocalServerPort
  private int port;
  private RestClient wishClient;
  private RestClient productClient;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @BeforeEach
  void setupTest() {
    jdbcTemplate.update("DELETE FROM wish");
    jdbcTemplate.update("DELETE FROM product");
    jdbcTemplate.update("DELETE FROM member");

    String memberUrl = "http://localhost:" + port + "/api/members";
    RestClient memberClient = RestClient.builder().baseUrl(memberUrl).build();

    final String email = "test@test.com";
    final String password = "password123";
    RegisterRequestDTO req = new RegisterRequestDTO(email, password);
    ResponseEntity<TokenResponseDTO> response = memberClient.post()
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

    String wishUrl = "http://localhost:" + port + "/api/wishes";
    wishClient = RestClient.builder()
        .baseUrl(wishUrl)
        .defaultHeader("Authorization", "Bearer " + jwtToken)
        .build();

    String productUrl = "http://localhost:" + port + "/api/products";
    productClient = RestClient.builder()
        .baseUrl(productUrl)
        .defaultHeader("Authorization", "Bearer " + jwtToken)
        .build();
  }

  private Long createTestProduct(String name, Long price) {
    ProductRequestDTO request = new ProductRequestDTO();
    request.setName(name);
    request.setPrice(price);
    request.setImageUrl("https://test.jpg");

    ResponseEntity<ProductResponseDTO> response = productClient.post()
        .uri("")
        .contentType(MediaType.APPLICATION_JSON)
        .body(request)
        .retrieve()
        .toEntity(ProductResponseDTO.class);

    assertNotNull(response.getBody());
    return response.getBody().getId();
  }

  @Test
  @DisplayName("위시리스트 추가 - 성공")
  void addWish() {
    Long productId = createTestProduct("테스트 상품", 10000L);

    WishRequestDTO request = new WishRequestDTO(productId, 2);

    ResponseEntity<WishResponseDTO> response = wishClient.post()
        .uri("")
        .contentType(MediaType.APPLICATION_JSON)
        .body(request)
        .retrieve()
        .toEntity(WishResponseDTO.class);

    assertNotNull(response);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().product().getId()).isEqualTo(productId);
    assertThat(response.getBody().quantity()).isEqualTo(2);
  }

  @Test
  @DisplayName("위시리스트 추가 - 유효성 검증 실패 (음수 수량)")
  void addWishWithNegativeQuantity() {
    Long productId = createTestProduct("테스트 상품", 10000L);

    WishRequestDTO request = new WishRequestDTO(productId, -1);

    HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () ->
      wishClient.post()
          .uri("")
          .contentType(MediaType.APPLICATION_JSON)
          .body(request)
          .retrieve()
          .toEntity(WishResponseDTO.class)
    );

    assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(exception.getResponseBodyAsString()).contains("수량은 0 이상이어야 합니다.");
  }

  @Test
  @DisplayName("위시리스트 추가 - 존재하지 않는 상품")
  void addWishWithNonExistentProduct() {
    WishRequestDTO request = new WishRequestDTO(99999L, 1);

    HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () ->
      wishClient.post()
          .uri("")
          .contentType(MediaType.APPLICATION_JSON)
          .body(request)
          .retrieve()
          .toEntity(WishResponseDTO.class)
    );

    assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  @DisplayName("위시리스트 조회 - 성공")
  void getWishes() {
    Long productId1 = createTestProduct("상품1", 10000L);
    Long productId2 = createTestProduct("상품2", 20000L);

    // 위시리스트에 상품 추가
    wishClient.post()
        .uri("")
        .contentType(MediaType.APPLICATION_JSON)
        .body(new WishRequestDTO(productId1, 1))
        .retrieve()
        .toEntity(WishResponseDTO.class);

    wishClient.post()
        .uri("")
        .contentType(MediaType.APPLICATION_JSON)
        .body(new WishRequestDTO(productId2, 2))
        .retrieve()
        .toEntity(WishResponseDTO.class);

    ResponseEntity<List<WishResponseDTO>> response = wishClient.get()
        .uri("")
        .retrieve()
        .toEntity(new ParameterizedTypeReference<List<WishResponseDTO>>() {});

    assertNotNull(response);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().size()).isEqualTo(2);
  }

  @Test
  @DisplayName("위시리스트 조회 - 페이징")
  void getWishesWithPaging() {
    Long productId1 = createTestProduct("상품1", 10000L);
    Long productId2 = createTestProduct("상품2", 20000L);

    // 위시리스트에 상품 추가
    wishClient.post()
        .uri("")
        .contentType(MediaType.APPLICATION_JSON)
        .body(new WishRequestDTO(productId1, 1))
        .retrieve()
        .toEntity(WishResponseDTO.class);

    wishClient.post()
        .uri("")
        .contentType(MediaType.APPLICATION_JSON)
        .body(new WishRequestDTO(productId2, 2))
        .retrieve()
        .toEntity(WishResponseDTO.class);

    ResponseEntity<List<WishResponseDTO>> response = wishClient.get()
        .uri("?page=0&size=1")
        .retrieve()
        .toEntity(new ParameterizedTypeReference<List<WishResponseDTO>>() {});

    assertNotNull(response);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().size()).isEqualTo(1);
  }

  @Test
  @DisplayName("위시리스트 수량 수정 - 성공")
  void updateWishQuantity() {
    Long productId = createTestProduct("테스트 상품", 10000L);

    // 위시리스트에 상품 추가
    wishClient.post()
        .uri("")
        .contentType(MediaType.APPLICATION_JSON)
        .body(new WishRequestDTO(productId, 1))
        .retrieve()
        .toEntity(WishResponseDTO.class);

    WishUpdateDTO updateRequest = new WishUpdateDTO(5);

    ResponseEntity<Void> response = wishClient.patch()
        .uri("/" + productId)
        .contentType(MediaType.APPLICATION_JSON)
        .body(updateRequest)
        .retrieve()
        .toEntity(Void.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  @DisplayName("위시리스트 수량 수정 - 0으로 수정시 삭제")
  void updateWishQuantityToZero() {
    Long productId = createTestProduct("테스트 상품", 10000L);

    // 위시리스트에 상품 추가
    wishClient.post()
        .uri("")
        .contentType(MediaType.APPLICATION_JSON)
        .body(new WishRequestDTO(productId, 1))
        .retrieve()
        .toEntity(WishResponseDTO.class);

    WishUpdateDTO updateRequest = new WishUpdateDTO(0);

    ResponseEntity<Void> response = wishClient.patch()
        .uri("/" + productId)
        .contentType(MediaType.APPLICATION_JSON)
        .body(updateRequest)
        .retrieve()
        .toEntity(Void.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Test
  @DisplayName("위시리스트 수량 수정 - 유효성 검증 실패 (음수 수량)")
  void updateWishQuantityWithNegativeValue() {
    Long productId = createTestProduct("테스트 상품", 10000L);

    // 위시리스트에 상품 추가
    wishClient.post()
        .uri("")
        .contentType(MediaType.APPLICATION_JSON)
        .body(new WishRequestDTO(productId, 1))
        .retrieve()
        .toEntity(WishResponseDTO.class);

    WishUpdateDTO updateRequest = new WishUpdateDTO(-1);

    HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () ->
      wishClient.patch()
          .uri("/" + productId)
          .contentType(MediaType.APPLICATION_JSON)
          .body(updateRequest)
          .retrieve()
          .toEntity(Void.class)
    );

    assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(exception.getResponseBodyAsString()).contains("수량은 0 이상이어야 합니다.");
  }

  @Test
  @DisplayName("위시리스트 삭제 - 성공")
  void deleteWish() {
    Long productId = createTestProduct("테스트 상품", 10000L);

    // 위시리스트에 상품 추가
    wishClient.post()
        .uri("")
        .contentType(MediaType.APPLICATION_JSON)
        .body(new WishRequestDTO(productId, 1))
        .retrieve()
        .toEntity(WishResponseDTO.class);

    ResponseEntity<Void> response = wishClient.delete()
        .uri("/" + productId)
        .retrieve()
        .toEntity(Void.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    ResponseEntity<List<WishResponseDTO>> wishListRes = wishClient.get()
        .uri("?page=0&size=10")
        .retrieve()
        .toEntity(new ParameterizedTypeReference<List<WishResponseDTO>>() {});

    assertNotNull(wishListRes);
    assertThat(wishListRes.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(wishListRes.getBody()).isNotNull();
    assertThat(wishListRes.getBody().size()).isEqualTo(0);
  }

  @Test
  @DisplayName("위시리스트 삭제 - 존재하지 않는 상품")
  void deleteNonExistentWish() {
    HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () ->
      wishClient.delete()
          .uri("/99999")
          .retrieve()
          .toEntity(Void.class)
    );

    assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }
}
