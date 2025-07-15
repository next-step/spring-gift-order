package gift.authorization.service;

import gift.authorization.dto.LoginRequestDto;
import gift.authorization.dto.TokenResponseDto;
import gift.authorization.dto.UserRegisterRequestDto;
import org.springframework.stereotype.Service;

@Service
public interface AuthorizationService {
    TokenResponseDto registerMember(UserRegisterRequestDto requestDto);

    TokenResponseDto loginMember(LoginRequestDto requestDto);
}
