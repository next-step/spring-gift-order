package gift.service;

import gift.dto.LoginResponseDto;
import gift.dto.MemberRequestDto;
import gift.entity.Member;

import java.util.Optional;

public interface AuthService {
    LoginResponseDto saveMember(MemberRequestDto dto);
    LoginResponseDto loginMember(MemberRequestDto dto);
    Optional<Member> findByEmail(String email);
}