package gift;

import com.fasterxml.jackson.databind.JsonNode;
import gift.product.dto.*;
import gift.product.commons.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class E2ETest {

	@LocalServerPort
	private int port;

	private RestClient restClient;
	@Autowired
	private JwtUtil jwtUtil;


	@BeforeEach
	void setUp() {
		restClient = RestClient.builder()
			.baseUrl("http://localhost:" + port)
			.build();
	}

	@Test
	@DisplayName("인증인가 적용된 상품 e2e 테스트")
	void userAndItemE2ETest() {

		// 로그인
		LoginRequest loginRequest = new LoginRequest("s@s", "1");
		LoginResponse loginResponse = restClient.post()
			.uri("/users/login")
			.body(loginRequest)
			.retrieve()
			.body(LoginResponse.class);

		String accessToken = loginResponse.token();
		assertThat(accessToken).isNotNull();

		// 상품 생성
		ItemRequest createItemRequest = new ItemRequest("테스트콜라", 1500, "url");
		Long createdId = restClient.post()
			.uri("/products")
			.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
			.body(createItemRequest)
			.retrieve()
			.body(Long.class);

		assertThat(createdId).isEqualTo(16L); // 초기화된 목업 데이터 15개

		// 단건 조회
		GetItemResponse getItemResponse = restClient.get()
			.uri("/products/" + createdId)
			.retrieve()
			.body(GetItemResponse.class);

		assertThat(getItemResponse.name()).isEqualTo("테스트콜라");
		assertThat(getItemResponse.authorId()).isEqualTo(1L);

		// 추가한 상품에 옵션 추가
		restClient.post()
			.uri("/products/"+ createdId+"/options")
			.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
			.body(new CreateOptionRequest("테스트용옵션임", 50))
			.retrieve()
			.toBodilessEntity();

		// 옵션 조회
		// 추가한 상품에 옵션 추가
		List<GetOptionsResponse> getOptionsResponses = restClient.get()
			.uri("/products/"+ createdId+"/options")
			.retrieve()
			.body(new ParameterizedTypeReference<List<GetOptionsResponse>>() {});

		GetOptionsResponse firstOption = getOptionsResponses.getFirst();
		assertThat(firstOption.name()).isEqualTo("테스트용옵션임");


		// 수정
		ItemRequest updateItemRequest = new ItemRequest("테스트사이다", 2000, "url2");
		GetItemResponse updatedItemResponse = restClient.put()
			.uri("/products/" + createdId)
			.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
			.body(updateItemRequest)
			.retrieve()
			.body(GetItemResponse.class);

		assertThat(updatedItemResponse.name()).isEqualTo("테스트사이다");
		assertThat(updatedItemResponse.price()).isEqualTo(2000);

		// 삭제
		restClient.delete()
			.uri("/products/" + createdId)
			.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
			.retrieve()
			.toBodilessEntity();

		// 삭제 후 전체 목록 조회 (페이징 반영)
		JsonNode res = restClient.get()
			.uri("/products?size=2")
			.retrieve()
			.body(JsonNode.class);

		assertThat(res.get("content").size()).isEqualTo(2);

		// 위시리스트 추가
		CreateWishListRequest wishListRequest = new CreateWishListRequest(6L);
		Long createdWishListId = restClient.post()
			.uri("/wishlists")
			.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
			.body(wishListRequest)
			.retrieve()
			.body(Long.class);

		assertThat(createdWishListId).isEqualTo(13L);

		// 위시리스트 수량 변경 테스트
		// 수량변경 베드케이스 테스트
		assertThatThrownBy(() ->
			restClient.patch()
				.uri(("/wishlists/" + createdWishListId) +"/update?amount=-50")
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
				.retrieve()
				.toBodilessEntity()
		).isInstanceOf(HttpClientErrorException.BadRequest.class);

		// 제대로 수량변경 해보기
		restClient.patch()
				.uri(("/wishlists/" + createdWishListId) +"/update?amount=20")
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
				.retrieve()
				.toBodilessEntity();

		JsonNode myWishList = restClient.get()
			.uri("/wishlists?page=1&size=3")
			.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
			.retrieve()
			.body(JsonNode.class);

		JsonNode secondItem = myWishList.get("content").get(2);
		assertThat(secondItem.get("amount").asInt()).isEqualTo(20);



		// 위시리스트 삭제 후 목록조회
		restClient.delete()
			.uri("/wishlists/" + createdWishListId)
			.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
			.retrieve()
			.toBodilessEntity();

		JsonNode afterDeleteWishList = restClient.get()
			.uri("/wishlists")
			.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
			.retrieve()
			.body(JsonNode.class);

		JsonNode contents = afterDeleteWishList.get("content");
		assertThat(contents.size()).isEqualTo(5);
	}

	@Test
	@DisplayName("비인가 유저 에러테스트")
	void userBadRequestTest() {

		LoginRequest loginRequest = new LoginRequest("s@s", "1");
		LoginResponse loginResponse = restClient.post()
			.uri("/users/login")
			.body(loginRequest)
			.retrieve()
			.body(LoginResponse.class);

		String accessToken = loginResponse.token();
		assertThat(accessToken).isNotNull();

		// 상품 수정 실패
		ItemRequest updateRequest = new ItemRequest("사이다", 2000, "url");
		assertThatThrownBy(() ->
			restClient.put()
				.uri("/products/14")
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
				.body(updateRequest)
				.retrieve()
				.toBodilessEntity()
		).isInstanceOf(HttpClientErrorException.BadRequest.class);

		// 상품 삭제 실패
		assertThatThrownBy(() ->
			restClient.delete()
				.uri("/products/14")
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
				.retrieve()
				.toBodilessEntity()
		).isInstanceOf(HttpClientErrorException.BadRequest.class);
	}
}
