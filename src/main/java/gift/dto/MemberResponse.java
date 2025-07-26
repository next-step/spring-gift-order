package gift.dto;

import gift.domain.Member;

public record MemberResponse(
        Long id,
        String email
) {

    public static MemberResponse from(Member member) {
        return new MemberResponse(member.id(), member.email());
    }
}
