package com.yanzhenjie.nohttp.rest;

import com.yanzhenjie.nohttp.cache.CacheMode;

/**
 * Created by Yan Zhenjie on 2017/5/22.
 */
public interface IClientProtocol<Child extends IClientProtocol> {

    /**
     * Set the request cache primary key, it should be globally unique.
     *
     * @param key unique key.
     * @return {@link Child}.
     */
    Child setCacheKey(String key);

    /**
     * Set the cache mode.
     *
     * @param cacheMode The value from {@link CacheMode}.
     * @return {@link Child}.
     */
    Child setCacheMode(CacheMode cacheMode);
}
