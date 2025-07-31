package gift.dto;

import gift.domain.Member;

public record MemberRequest(
        Long id,
        String email
) {

    public static MemberRequest of(Long id, String email) {
        return new MemberRequest(id, email);
    }
}
