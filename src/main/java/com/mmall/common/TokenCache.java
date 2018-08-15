package com.mmall.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class TokenCache {
    private static Logger logger = LoggerFactory.getLogger(TokenCache.class);

    public static final String TOKEN_PREFIX = "token_";

    //Guava本地缓存，initialCapacity设置每一页的缓存初始化容量，maximumSize当缓存超过指定值时使用LRU算法清除缓存,expireAfterAccess缓存过期时间
    //LRU算法
    private static LoadingCache<String, String>  localCache = CacheBuilder.newBuilder().initialCapacity(1000).maximumSize(10000)
            .expireAfterAccess(12, TimeUnit.HOURS).build(new CacheLoader<String, String>() {
                //默认的数据加载实现，当get取值时，如果key没有对应值，就调用该方法进行加载。
                @Override
                public String load(String key) throws Exception {
                    return "null";
                }
            });

    public static void setKey(String key, String value){
        localCache.put(key,value);
    }

    public static String getKey(String key){
        String value = null;
        try {
            value = localCache.get(key);
            if("null".equals(value)){
                return null;
            }
            return value;
        } catch(Exception e){
            logger.error("localCache get error", e);
        }
        return null;
    }
}
