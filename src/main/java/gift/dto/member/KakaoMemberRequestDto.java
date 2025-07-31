package gift.dto.member;

import gift.entity.LoginType;

public record KakaoMemberRequestDto(String email, String password, LoginType loginType,
                                    String accessToken, String refreshToken) {

}
