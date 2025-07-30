package gift.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;
import java.util.Map;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class KakaoFeedMessageDto {

    private String objectType = "feed";
    private Content content;
    private ItemContent itemContent;

    public KakaoFeedMessageDto(Content content, ItemContent itemContent) {
        this.content = content;
        this.itemContent = itemContent;
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Content {
        private String title = "아래와 같은 메세지가 받는 분께 전송됩니다.";
        private String description; // 선물 메세지
        private String imageUrl = "https://mud-kage.kakao.com/dn/NTmhS/btqfEUdFAUf/FjKzkZsnoeE4o19klTOVI1/openlink_640x640s.jpg";
        private Map<String, String> link = Map.of("web_url", "http://www.daum.net", "mobile_web_url", "http://m.daum.net");

        public Content(String description) {
            this.description = description;
        }
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class ItemContent {
        private String profileText = "주문해주셔서 감사합니다.";
        private String titleImageUrl; // 제품 이미지 링크
        private String titleImageText; // 제품 이름
        private String titleImageCategory = "카테고리";
        private List<Item> items; // 선택한 제품 옵션
        private String sum = "총합";
        private String sumOp; // 가격 총합 넣기

        public ItemContent(String titleImageUrl, String titleImageText, List<Item> items, String sumOp) {
            this.titleImageUrl = titleImageUrl;
            this.titleImageText = titleImageText;
            this.items = items;
            this.sumOp = sumOp;
        }
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Item {
        private String item; // 제품 옵션 이름
        private String itemOp; // 제품 옵션 가격

        public Item(String item, String itemOp) {
            this.item = item;
            this.itemOp = itemOp;
        }
    }
}
