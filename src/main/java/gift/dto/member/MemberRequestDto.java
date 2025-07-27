package gift.dto.member;

import gift.entity.LoginType;

public record MemberRequestDto(String email, String password, LoginType loginType) {
}
