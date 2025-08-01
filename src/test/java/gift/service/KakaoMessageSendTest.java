package gift.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import gift.client.KakaoClient;
import gift.entity.Member;
import gift.entity.Option;
import gift.entity.Product;
import gift.entity.Role;
import gift.entity.Order;
import gift.repository.OptionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.MultiValueMap;

import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class KakaoMessageSendTest {

    @Mock
    private KakaoClient kakaoClient;

    @Mock
    private OptionRepository optionRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private KakaoAuthService kakaoAuthService;

    @Test
    void test() throws Exception {
        String accessToken = "fake-token-for-test";
        Product product = new Product("Test Product", 10000, "test.jpg");
        Option option = new Option("Test Option", 100, product);
        Member member = new Member(1L, "email", "password", Role.USER, "nickname", "url");
        Order order = new Order(option.getId(), member.getId(), 1, "hello world");

        given(optionRepository.findById(option.getId())).willReturn(Optional.of(option));
        given(objectMapper.writeValueAsString(any())).willReturn("{\"template_object\":{}}");

        kakaoAuthService.sendMessageToMe(accessToken, order);

        verify(kakaoClient).sendKakaoTalkMessage(
                eq(accessToken),
                any(MultiValueMap.class)
        );
    }
}

