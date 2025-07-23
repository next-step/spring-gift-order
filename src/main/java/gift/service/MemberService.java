package gift.service;

import gift.dto.request.LoginRequestDto;
import gift.dto.request.RegisterRequestDto;
import gift.dto.response.TokenResponseDto;
import gift.entity.Member;

public interface MemberService {

    TokenResponseDto registerAndReturnToken(RegisterRequestDto registerRequestDto);

    TokenResponseDto login(LoginRequestDto loginRequest);

    Member getMemberWithEncodedPassword(RegisterRequestDto registerRequestDto);

    Long getMemberIdByEmail(String email);

    Member findMemberByEmail(String email);
}

