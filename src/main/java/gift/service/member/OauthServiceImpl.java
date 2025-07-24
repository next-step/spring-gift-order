package gift.service.member;

import gift.client.KakaoClient;
import gift.dto.login.KakaoTokenDto;
import gift.exception.KakaoTokenFetchException;
import gift.repository.member.MemberRepository;
import org.springframework.stereotype.Service;

@Service
public class OauthServiceImpl implements OauthService {

    private final KakaoClient kakaoClient;
    private final MemberRepository memberRepository;

    public OauthServiceImpl(KakaoClient kakaoClient, MemberRepository memberRepository) {
        this.kakaoClient = kakaoClient;
        this.memberRepository = memberRepository;
    }

    @Override
    public String fetchKakaoToken(String code) {
        KakaoTokenDto tokenDto = kakaoClient.fetchToken(code);

        if (tokenDto == null || tokenDto.accessToken() == null) {
            throw new KakaoTokenFetchException("카카오 토큰 발급에 문제가 발생했습니다.");
        }

        return tokenDto.accessToken();
    }
}