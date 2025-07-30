package gift.kakao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gift.kakao.dto.KakaoTokenResponseDto;
import gift.kakao.exception.KakaoServerException;
import gift.kakao.service.KakaoService;
import gift.member.MemberEntity;
import gift.member.repository.MemberRepository;
import java.util.Optional;
import java.util.function.Predicate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class KakaoServiceUnitTest {

    @InjectMocks
    private KakaoService kakaoService;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private RestClient mockClient;
    @Mock
    private RestClient.RequestBodyUriSpec bodyUriSpec;
    @Mock
    private RestClient.RequestBodySpec bodySpec;
    @Mock
    private RestClient.ResponseSpec responseSpec;

    private MemberEntity member;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(kakaoService, "client", mockClient);

        member = new MemberEntity("홍길동", "test@test.com", "123456");
        ReflectionTestUtils.setField(member, "id", 1L);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        when(mockClient.post()).thenReturn(bodyUriSpec);
        when(bodyUriSpec.uri(anyString())).thenReturn(bodySpec);
        when(bodySpec.header(
            HttpHeaders.CONTENT_TYPE,
            MediaType.APPLICATION_FORM_URLENCODED_VALUE
        )).thenReturn(bodySpec);
        when(bodySpec.body(any(String.class))).thenReturn(bodySpec);
        when(bodySpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    void fetchAndSaveToken_정상_흐름() {
        // Given
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);

        KakaoTokenResponseDto dto = new KakaoTokenResponseDto(
            "Bearer",
            "access1234",
            3600,
            "refresh1234",
            86400
        );
        when(responseSpec.body(KakaoTokenResponseDto.class)).thenReturn(dto);

        // When
        kakaoService.fetchAndSaveToken("authCode1234", 1L);

        // Then
        verify(memberRepository).save(member);
        assertThat(member.getKakaoToken().getAccessToken()).isEqualTo("access1234");
        assertThat(member.getKakaoToken().getRefreshToken()).isEqualTo("refresh1234");
    }

    @Test
    void fetchAndSaveToken_4xx_예외() {
        // Given
        when(responseSpec.onStatus(
            argThat(new ArgumentMatcher<Predicate<HttpStatusCode>>() {
                @Override
                public boolean matches(Predicate<HttpStatusCode> predicate) {
                    return predicate.test(HttpStatus.BAD_REQUEST);
                }
            }),
            any()
        )).thenAnswer(invocation -> {
            throw new IllegalArgumentException("파라미터 오류");
        });

        // When / Then
        assertThatThrownBy(() -> kakaoService.fetchAndSaveToken("badCode", 1L))
            .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("파라미터 오류");
    }

    @Test
    void fetchAndSaveToken_5xx_예외() {
        // Given
        when(responseSpec.onStatus(any(), any()))
            .thenThrow(new KakaoServerException());

        // When / Then
        assertThatThrownBy(() -> kakaoService.fetchAndSaveToken("badCode", 1L))
            .isInstanceOf(KakaoServerException.class);
    }
}
