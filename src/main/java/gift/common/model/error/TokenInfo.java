package gift.common.model.error;

import gift.entity.type.Provider;
import gift.entity.type.UserRole;

public record TokenInfo(
        String id,
        UserRole role,
        Provider provider
) {
}
