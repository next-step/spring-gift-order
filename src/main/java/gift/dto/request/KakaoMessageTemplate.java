package gift.dto.request;

public record KakaoMessageTemplate(
        String object_type,
        String text,
        KakaoLink link,
        String button_title
) {
}
