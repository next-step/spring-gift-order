package gift.dto;

import gift.entity.Member;

public record MemberInfoResponse(
        Long id,
        String email
) {

    public static MemberInfoResponse from(Member member) {
        return new MemberInfoResponse(member.getId(), member.getEmail());
    }
}
