package gift.member.dto;

import gift.member.Member;
import gift.member.Role;

public record MemberResponseDto (
        Long id,
        String email,
        String name,
        Role role
) {
    public MemberResponseDto(Member member) {
        this(member.getId(), member.getEmail(), member.getName(), member.getRole());
    }
}
