package gift.common.model;

import gift.entity.type.Provider;

public record TokenValue(
        String value,
        long expiresAt,
        Provider provider
) {
    public boolean isExpired() {
        return expiresAt < System.currentTimeMillis();
    }
}