package org.springframework.security.oauth.cache.commons.entity;

public class StringCacheEntity implements CacheEntity<String> {
    private String cacheKey;
    private String cacheValue;
    private ICacheExpire iCacheExpire;

    @Override
    public String getCacheKey() {
        return cacheKey;
    }

    @Override
    public void setCacheKey(String cacheKey) {
        this.cacheKey = cacheKey;
    }

    @Override
    public String getCacheValue() {
        return cacheValue;
    }

    @Override
    public void setCacheValue(String cacheValue) {
        this.cacheValue = cacheValue;
    }

    @Override
    public ICacheExpire getICacheExpire() {
        return iCacheExpire;
    }

    @Override
    public void setICacheExpire(ICacheExpire iCacheExpire) {
        this.iCacheExpire = iCacheExpire;
    }

    @Override
    public void destory() {

    }
}
