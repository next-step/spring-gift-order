package gift.service;


import gift.domain.Member;
import gift.domain.UserKakaoToken;
import gift.dto.KakaoOAuthResponseDto;
import gift.dto.KakaoUserInfoResponseDto;
import gift.dto.LoginResponseDto;
import gift.repository.UserKakaoTokenRepository;
import gift.security.JwtProvider;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class KakaoOAuthServiceImpl implements KakaoOAuthService {

  private final KakaoApiClient kakaoApiClient;

  private final MemberService memberService;

  private final JwtProvider jwtProvider;

  private final UserKakaoTokenRepository userKakaoTokenRepository;

  public KakaoOAuthServiceImpl(KakaoApiClient kakaoApiClient, MemberService memberService, JwtProvider jwtProvider, UserKakaoTokenRepository userKakaoTokenRepository) {
    this.kakaoApiClient = kakaoApiClient;
    this.memberService = memberService;
    this.jwtProvider = jwtProvider;
    this.userKakaoTokenRepository = userKakaoTokenRepository;
  }

  @Override
  public KakaoOAuthResponseDto getAccessToken(String authorizationCode) {
    return kakaoApiClient.getAccessToken(authorizationCode);
  }
  @Override
  public KakaoUserInfoResponseDto getUserInfo(String accessToken) {
    return kakaoApiClient.getUserInfo(accessToken);
  }

  public LoginResponseDto registerOrLogin(String authorizationCode) {
    KakaoOAuthResponseDto tokenDto = getAccessToken(authorizationCode);
    KakaoUserInfoResponseDto userInfo = getUserInfo(tokenDto.accessToken());

    String email = userInfo.email();

    if (email == null || email.isEmpty()) {
      throw new IllegalStateException("카카오에서 이메일을 받아올 수 없습니다. 회원가입 불가.");
    }

    Member member = memberService.registerOrLoginByKakao(email, tokenDto);

    String jwtToken = jwtProvider.generateToken(member);

    return new LoginResponseDto(
        tokenDto.tokenType(),
        tokenDto.accessToken(),
        tokenDto.expiresIn(),
        tokenDto.refreshToken(),
        tokenDto.refreshTokenExpiresIn(),
        jwtToken
    );
  }

  @Override
  @Transactional(readOnly = true)
  public Member findMemberByAccessToken(String accessToken) {
    UserKakaoToken token = userKakaoTokenRepository.findByAccessToken(accessToken)
        .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 Kakao Access Token"));

    if (token.getAccessTokenExpiresAt() != null &&
        token.getAccessTokenExpiresAt().isBefore(Instant.now())) {
      throw new IllegalStateException("액세스 토큰이 만료되었습니다.");
    }

    return token.getMember();
  }

}
