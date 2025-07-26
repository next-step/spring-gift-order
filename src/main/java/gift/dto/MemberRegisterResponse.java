package gift.dto;

import gift.domain.Member;

public record MemberRegisterResponse(
        String token,
        MemberResponse user
) {


    public static MemberRegisterResponse of(String token, Member member) {
        MemberResponse memberResponse = MemberResponse.from(member);
        return new MemberRegisterResponse(token,memberResponse);
    }
}
