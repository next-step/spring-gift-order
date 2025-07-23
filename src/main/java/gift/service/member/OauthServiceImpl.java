package gift.service.member;

import gift.client.KakaoClient;
import gift.dto.login.KakaoTokenDto;
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
        return tokenDto.accessToken();
    }
}