package gift.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gift.order.vo.Message;
import org.junit.jupiter.api.Test;

public class MessageUnitTest {

    @Test
    void 정상_문자열_테스트() {
        //given
        String content = "유효한 메세지";

        //when
        Message message = new Message(content);

        //then
        assertEquals(content, message.toValue());
    }

    @Test
    void null을_전달_시_예외() {
        // given
        String content = null;

        // when + then
        IllegalArgumentException e = assertThrows(
            IllegalArgumentException.class,
            () -> new Message(content)
        );
        assertEquals("Message는 null일 수 없습니다.", e.getMessage());
    }

    @Test
    void 길이_100_초과_문자열_전달_시_예외() {
        // given
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 101; i++) {
            sb.append('아');
        }
        String longContent = sb.toString();

        // when + then
        IllegalArgumentException e = assertThrows(
            IllegalArgumentException.class,
            () -> new Message(longContent)
        );
        assertEquals("Message 길이는 100이하이어야 합니다.", e.getMessage());
    }

    @Test
    void 길이_100_문자열은_정상_처리() {
        // given
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            sb.append('아');
        }
        String boundaryContent = sb.toString();

        // when
        Message message = new Message(boundaryContent);

        // then
        assertEquals(100, message.toValue().length());
        assertEquals(boundaryContent, message.toValue());
    }

}
