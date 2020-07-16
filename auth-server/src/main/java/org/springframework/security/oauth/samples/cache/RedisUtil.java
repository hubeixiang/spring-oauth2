package org.springframework.security.oauth.samples.cache;

import java.util.HashMap;
import java.util.Map;

public final class RedisUtil {
    private static final Map<String, String> cache = new HashMap<>();

    public static boolean exists(String key) {
        return cache.containsKey(key);
    }

    public static void set(String key, String value) {
        cache.put(key, value);
    }

    public static String get(String key) {
        return cache.get(key);
    }

    public static void remove(String key) {
        cache.remove(key);
    }
}
