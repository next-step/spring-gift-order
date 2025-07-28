package gift.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import gift.auth.JwtTokenProvider;
import gift.dto.MemberRequest;
import gift.dto.TokenResponse;
import gift.entity.Member;
import gift.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Member mockMember;

    @Test
    @DisplayName("회원가입을 성공적으로 수행한다")
    void register() {
        // given
        MemberRequest request = new MemberRequest("test@example.com", "password123");

        // Mock Member 객체에 ID 설정
        given(mockMember.getId()).willReturn(1L);

        given(passwordEncoder.encode("password123")).willReturn("encodedPassword");

        // memberRepository가 위에서 만든 mockMember를 반환하도록 변경
        given(memberRepository.save(any(Member.class))).willReturn(
                mockMember);

        given(jwtTokenProvider.createToken(mockMember.getId().toString())).willReturn("test.token");

        // when
        TokenResponse response = memberService.register(request);

        // then
        assertThat(response.token()).isEqualTo("test.token");
        verify(memberRepository).save(any(Member.class));
    }
}