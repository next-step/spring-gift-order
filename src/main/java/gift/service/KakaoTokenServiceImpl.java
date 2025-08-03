package gift.service;

import gift.domain.UserKakaoToken;
import gift.repository.UserKakaoTokenRepository;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class KakaoTokenServiceImpl implements KakaoTokenService {

  private final UserKakaoTokenRepository userKakaoTokenRepository;

  public KakaoTokenServiceImpl(UserKakaoTokenRepository userKakaoTokenRepository) {
    this.userKakaoTokenRepository = userKakaoTokenRepository;
  }

  @Override
  @Transactional(readOnly = true)
  public String getMemberAccessToken(Long memberId) {
    UserKakaoToken token = userKakaoTokenRepository.findById(memberId)
        .orElseThrow(() -> new IllegalStateException("해당 사용자의 카카오 토큰이 존재하지 않습니다."));

    if (token.isExpired()) {
      throw new IllegalStateException("액세스 토큰이 만료되었습니다. 리프레시 필요.");
    }

    return token.getAccessToken();
  }
}
