package gift.common.util;

import gift.common.model.TokenValue;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ExternalTokenManager {
    private final Cache accessTokenCache;

    private static final String EXTERNAL_ACCESS_TOKEN_NAME = "connAccessToken";

    public ExternalTokenManager(CacheManager cacheManager) {
        if (!cacheManager.getCacheNames().contains(EXTERNAL_ACCESS_TOKEN_NAME)) {
            throw new BeanInitializationException(
                "ConnAccessToken 캐시가 존재하지 않습니다. spring.cache.cache-names 설정에 ConnAccessToken을 추가해야 합니다."
            );
        }
        this.accessTokenCache = cacheManager.getCache(EXTERNAL_ACCESS_TOKEN_NAME);
    }

    public void storeAccessToken(String key, TokenValue accessToken) {
        if (key == null || accessToken == null) {
            throw new IllegalArgumentException("키와 액세스 토큰은 null일 수 없습니다.");
        }
        if (accessToken.isExpired()) {
            throw new IllegalArgumentException("만료된 액세스 토큰은 저장할 수 없습니다.");
        }
        accessTokenCache.put(key, accessToken);
    }

    public Optional<TokenValue> getAccessToken(String key) {
        Cache.ValueWrapper valueWrapper = accessTokenCache.get(key);
        if (valueWrapper == null) {
            return Optional.empty();
        }

        TokenValue tokenValue = (TokenValue) valueWrapper.get();

        if (tokenValue == null || tokenValue.isExpired()) {
            accessTokenCache.evict(key);
            return Optional.empty();
        }
        return Optional.of(tokenValue);
    }

    public void evictAccessToken(String key) {
        accessTokenCache.evictIfPresent(key);
    }
}
