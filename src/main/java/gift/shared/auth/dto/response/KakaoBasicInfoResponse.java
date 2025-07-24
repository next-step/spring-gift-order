package gift.shared.auth.dto.response;

import gift.shared.domain.KakaoAccount;

import java.time.LocalDateTime;

public record KakaoBasicInfoResponse(
        Long id,
        LocalDateTime connected_at,
        KakaoAccount kakao_account
) {
}
