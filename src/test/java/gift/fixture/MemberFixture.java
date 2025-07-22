package gift.fixture;

import gift.dto.request.MemberRequsetDto;
import gift.entity.Member;

public class MemberFixture {
    public static MemberRequsetDto createMember(){
        return new MemberRequsetDto("song@naver.com","1234");

    }

    public static Member createMember2(){
        return new Member("song@naver.com","1234");

    }
}
