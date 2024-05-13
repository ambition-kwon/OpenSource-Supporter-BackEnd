package me.jejunu.opensource_supporter.config;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Component
public class CacheChecker {
    private final CacheManager cacheManager;

    public CacheChecker(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public boolean isCacheHit(String cacheName, Object key) {
        Cache cache = cacheManager.getCache(cacheName);
        Object value = cache.get(key);
        if (value != null) {
            System.out.println("Cache hit!");
            return true;
        }
        System.out.println("Cache miss!");
        return false;
    }
}
