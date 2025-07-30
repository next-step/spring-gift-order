package gift.oauth.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gift.api.member.domain.Member;
import gift.api.member.domain.MemberRole;
import gift.api.option.domain.Option;
import gift.api.order.domain.Order;
import gift.api.product.domain.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class KakaoMessageTemplateGeneratorTest {

    private KakaoMessageTemplateGenerator templateGenerator;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        templateGenerator = new KakaoMessageTemplateGenerator(objectMapper);
    }

    @Test
    @DisplayName("주문 정보로 카카오 메시지 템플릿 JSON 문자열을 생성한다")
    void createOrderTemplate() throws JsonProcessingException {
        // given
        // given - Member를 포함하여 테스트 데이터 생성
        Member member = new Member("test@example.com", "password",
                MemberRole.USER); // 실제 Member 생성자에 맞게 수정
        Product product = new Product("초콜릿 세트", 25000L, "chocolate.jpg");
        Option option = new Option("기본 포장", 5, product);
        // Order 생성자에 member 추가
        Order order = new Order(member, option, 5, "생일 축하 메시지");

        // when
        String jsonResult = templateGenerator.createOrderTemplateAsString(order);

        // then
        JsonNode root = objectMapper.readTree(jsonResult);
        assertThat(root.get("object_type").asText()).isEqualTo("text");
        assertThat(root.get("text").asText()).contains("상품명: 초콜릿 세트", "수량: 5",
                "메시지: 생일 축하 메시지");
        assertThat(root.get("button_title").asText()).isEqualTo("주문 확인하기");
    }
}