package gift.authorization.service;

import gift.authorization.dto.LoginRequestByEmailDto;
import gift.authorization.dto.LoginRequestByKakaoDto;
import gift.authorization.dto.TokenResponseDto;
import gift.authorization.dto.UserRegisterRequestByEmailDto;
import gift.authorization.dto.UserRegisterRequestByKakaoDto;
import org.springframework.stereotype.Service;

@Service
public interface AuthorizationService {
    TokenResponseDto registerMemberByEmail(UserRegisterRequestByEmailDto requestDto);

    TokenResponseDto registerMemberByKakao(UserRegisterRequestByKakaoDto requestDto);

    TokenResponseDto loginMemberByEmail(LoginRequestByEmailDto requestDto);

    TokenResponseDto loginMemberByKakao(LoginRequestByKakaoDto requestDto);
}
