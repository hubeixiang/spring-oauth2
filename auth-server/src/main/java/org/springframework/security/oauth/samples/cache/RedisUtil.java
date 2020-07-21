package org.springframework.security.oauth.samples.cache;

import org.springframework.security.oauth.cache.commons.api.impl.SimpleMemoryCacheHolderService;
import org.springframework.security.oauth.cache.commons.entity.CacheEntity;
import org.springframework.security.oauth.cache.commons.entity.ICacheExpire;
import org.springframework.security.oauth.cache.commons.entity.StringCacheEntity;

public final class RedisUtil {
    private static final SimpleMemoryCacheHolderService memoryCacheHolder = SimpleMemoryCacheHolderService.getInstanse();

    public static boolean exists(String key) {
        return memoryCacheHolder.isExists(key);
    }

    public static void setString(String key, String value) {
        StringCacheEntity stringCacheEntity = new StringCacheEntity();
        stringCacheEntity.setCacheKey(key);
        stringCacheEntity.setCacheValue(value);
        memoryCacheHolder.register(stringCacheEntity, false);
    }

    public static void setString(String key, String value, int timeout) {
        StringCacheEntity stringCacheEntity = new StringCacheEntity();
        stringCacheEntity.setCacheKey(key);
        stringCacheEntity.setCacheValue(value);
        if (timeout > 0) {
            ICacheExpire iCacheExpire = memoryCacheHolder.createDefaultICacheExpire(true, timeout);
            stringCacheEntity.setICacheExpire(iCacheExpire);
            memoryCacheHolder.register(stringCacheEntity);
        } else {
            memoryCacheHolder.register(stringCacheEntity, false);
        }
    }

    public static StringCacheEntity getString(String key) {
        CacheEntity cacheEntity = memoryCacheHolder.get(key);
        if (cacheEntity != null) {
            return (StringCacheEntity) cacheEntity;
        }
        return null;
    }

    public static void remove(String key) {
        memoryCacheHolder.clean(key);
    }
}
