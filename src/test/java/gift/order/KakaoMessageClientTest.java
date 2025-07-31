package gift.order;

import gift.user.KakaoMessageClient;
import gift.user.exception.KakaoSendMessageException;
import gift.user.template.MessageTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class KakaoMessageClientTest {

    @Test
    void 메시지전송실패_예외발생() {
        RestClient restClient = mock(RestClient.class);
        MessageTemplate template = mock(MessageTemplate.class);
        when(template.create()).thenReturn("");

        RestClient.RequestBodyUriSpec uriSpec = mock(RestClient.RequestBodyUriSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.post()).thenReturn(uriSpec);
        doThrow(new RuntimeException("카카오 API 오류")).when(responseSpec).toBodilessEntity();

        KakaoMessageClient client = new KakaoMessageClient(restClient);

        assertThrows(KakaoSendMessageException.class, () -> {
            client.sendOrderMessageToUser("access-token", template);
        });
    }
}
