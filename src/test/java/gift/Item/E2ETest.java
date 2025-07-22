package gift.Item;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gift.common.dto.PageResponseDto;
import gift.item.dto.ItemCreateDto;
import gift.item.dto.ItemResponseDto;
import gift.item.dto.ItemUpdateDto;
import gift.item.dto.OptionCreateDto;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// 각 메서드 실행 전 스프링 컨텍스트, DB 초기화
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Sql(scripts = "/testData.sql")
public class E2ETest {

    @LocalServerPort
    int port;

    RestClient client = RestClient.builder().build();


    // -------------------정상 동작 테스트-------------------
    @Test
    void 모든_아이템_조회_테스트() {
        String url = "http://localhost:" + port + "/api/items";
        var response = client.get()
            .uri(url)
            .retrieve()
            .toEntity(new ParameterizedTypeReference<PageResponseDto<ItemResponseDto>>() {
            });

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().contents()).hasSize(3);
        assertThat(response.getBody().totalElements()).isEqualTo(3L);

        var contents = response.getBody().contents();
        assertThat(contents.get(0).name()).isEqualTo("무탠다드 슬랙스");
        assertThat(contents.get(1).price()).isEqualTo(38000);
        assertThat(contents.get(2).imageUrl()).isEqualTo("www.nike.com");
    }

    @Test
    void 단일_아이템_조회_테스트() {
        String url = "http://localhost:" + port + "/api/items/1";
        var response = client.get()
            .uri(url)
            .retrieve()
            .toEntity(ItemResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo("나이키 모자");
        assertThat(response.getBody().price()).isEqualTo(22000);
        assertThat(response.getBody().imageUrl()).isEqualTo("www.nike.com");
    }

    @Test
    void 아이템_생성_테스트() {
        String url = "http://localhost:" + port + "/api/items";
        var response = client.post()
            .uri(url)
            .body(new ItemCreateDto(
                "CK 티셔츠",
                32000,
                "www.ck.com",
                List.of(new OptionCreateDto("빨간색", 5))
            ))
            .retrieve()
            .toEntity(ItemResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo("CK 티셔츠");
        assertThat(response.getBody().price()).isEqualTo(32000);
        assertThat(response.getBody().imageUrl()).isEqualTo("www.ck.com");
    }

    @Test
    void 허용된_특수문자로_이루어진_아이템_생성_테스트() {
        String url = "http://localhost:" + port + "/api/items";
        var response = client.post()
            .uri(url)
            .body(new ItemCreateDto(
                "([_&/_+- ])",
                32000,
                "www.allowedchar.com",
                List.of(new OptionCreateDto("빨간색", 5))
            ))
            .retrieve()
            .toEntity(ItemResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo("([_&/_+- ])");
        assertThat(response.getBody().price()).isEqualTo(32000);
        assertThat(response.getBody().imageUrl()).isEqualTo("www.allowedchar.com");
    }

    @Test
    void 아이템_수정_테스트() {
        String url = "http://localhost:" + port + "/api/items/1";
        var response = client.put()
            .uri(url)
            .body(new ItemUpdateDto(
                "아디다스 모자",
                24000,
                "www.adidas.com"
            ))
            .retrieve()
            .toEntity(ItemResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo("아디다스 모자");
        assertThat(response.getBody().price()).isEqualTo(24000);
        assertThat(response.getBody().imageUrl()).isEqualTo("www.adidas.com");
    }

    @Test
    void 아이템_삭제_테스트() {
        String url = "http://localhost:" + port + "/api/items/1";
        var response = client.delete()
            .uri(url)
            .retrieve()
            .toEntity(Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
    }

    // -------------------예외 상황 테스트-------------------
    @Test
    void 존재하지_않는_아이템_조회_시_404() {
        String url = "http://localhost:" + port + "/api/items/999";

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            client.get()
                .uri(url)
                .retrieve()
                .toEntity(ItemResponseDto.class);
        });
        String body = exception.getResponseBodyAsString();

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(body).contains("\"type\":\"about:blank\"");
        assertThat(body).contains("\"title\":\"Not Found\"");
        assertThat(body).contains("\"detail\":\"Item not found with id: 999\"");
        assertThat(body).contains("\"status\":404");
        assertThat(body).contains("\"instance\":\"/api/items/999\"");
    }

    @Test
    void 존재하지_않는_아이템_수정_시_404() {
        String url = "http://localhost:" + port + "/api/items/999";

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            client.put()
                .uri(url)
                .body(new ItemUpdateDto(
                    "아디다스 모자",
                    24000,
                    "www.adidas.com"
                ))
                .retrieve()
                .toEntity(ItemResponseDto.class);
        });
        String body = exception.getResponseBodyAsString();

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(body).contains("\"type\":\"about:blank\"");
        assertThat(body).contains("\"title\":\"Not Found\"");
        assertThat(body).contains("\"detail\":\"Item not found with id: 999\"");
        assertThat(body).contains("\"status\":404");
        assertThat(body).contains("\"instance\":\"/api/items/999\"");
    }

    @Test
    void 존재하지_않는_아이템_삭제_시_404() {
        String url = "http://localhost:" + port + "/api/items/999";

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            client.delete()
                .uri(url)
                .retrieve()
                .toEntity(Void.class);
        });
        String body = exception.getResponseBodyAsString();

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(body).contains("\"type\":\"about:blank\"");
        assertThat(body).contains("\"title\":\"Not Found\"");
        assertThat(body).contains("\"detail\":\"Item not found with id: 999\"");
        assertThat(body).contains("\"status\":404");
        assertThat(body).contains("\"instance\":\"/api/items/999\"");
    }

    @Test
    void 빈_상품명으로_아이템_생성_시_400() {
        String url = "http://localhost:" + port + "/api/items";

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            client.post()
                .uri(url)
                .body(new ItemCreateDto(
                    "",
                    32000,
                    "www.ck.com",
                    List.of(new OptionCreateDto("빨간색", 5))
                ))
                .retrieve()
                .toEntity(ItemResponseDto.class);
        });
        String body = exception.getResponseBodyAsString();

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(body).contains("\"type\":\"about:blank\"");
        assertThat(body).contains("\"title\":\"Bad Request\"");
        assertThat(body).contains("\"detail\":\"상품명은 필수입니다.\"");
        assertThat(body).contains("\"status\":400");
        assertThat(body).contains("\"instance\":\"/api/items\"");
    }

    @Test
    void 글자수_15_초과하는_상품명으로_아이템_생성_시_400() {
        String url = "http://localhost:" + port + "/api/items";

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            client.post()
                .uri(url)
                .body(new ItemCreateDto(
                    "1234567890123456",
                    32000,
                    "www.ck.com",
                    List.of(new OptionCreateDto("빨간색", 5))
                ))
                .retrieve()
                .toEntity(ItemResponseDto.class);
        });
        String body = exception.getResponseBodyAsString();

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(body).contains("\"type\":\"about:blank\"");
        assertThat(body).contains("\"title\":\"Bad Request\"");
        assertThat(body).contains("\"detail\":\"상품명은 공백포함 최대 15자까지 입력할 수 있습니다.\"");
        assertThat(body).contains("\"status\":400");
        assertThat(body).contains("\"instance\":\"/api/items\"");
    }

    @Test
    void 허용되지_않은_문자가_포함된_상품명으로_아이템_생성_시_400() {
        String url = "http://localhost:" + port + "/api/items";

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            client.post()
                .uri(url)
                .body(new ItemCreateDto(
                    "<CK>",
                    32000,
                    "www.ck.com",
                    List.of(new OptionCreateDto("빨간색", 5))
                ))
                .retrieve()
                .toEntity(ItemResponseDto.class);
        });
        String body = exception.getResponseBodyAsString();

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(body).contains("\"type\":\"about:blank\"");
        assertThat(body).contains("\"title\":\"Bad Request\"");
        assertThat(body).contains(
            "\"detail\":\"허용되지 않은 특수문자가 포함되어 있습니다. (허용: 한글, 영문, 숫자, 공백, (, ), [, ], +, -, &, /, _)\"");
        assertThat(body).contains("\"status\":400");
        assertThat(body).contains("\"instance\":\"/api/items\"");
    }

    @Test
    void 금지된_문자열이_포함된_상품명으로_아이템_생성_시_400() {
        String url = "http://localhost:" + port + "/api/items";

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            client.post()
                .uri(url)
                .body(new ItemCreateDto(
                    "카카오 춘식이 인형",
                    32000,
                    "www.kakao.com",
                    List.of(new OptionCreateDto("빨간색", 5))
                ))
                .retrieve()
                .toEntity(ItemResponseDto.class);
        });
        String body = exception.getResponseBodyAsString();

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(body).contains("\"type\":\"about:blank\"");
        assertThat(body).contains("\"title\":\"Bad Request\"");
        assertThat(body).contains("\"detail\":\"금지된 단어가 포함되어 있습니다.\"");
        assertThat(body).contains("\"status\":400");
        assertThat(body).contains("\"instance\":\"/api/items\"");
    }
}
