package gift.dto.member;

import gift.entity.LoginType;

public record MemberPasswordChangeDto(String email, String beforePassword, String afterPassword,
                                      LoginType loginType) {

}
