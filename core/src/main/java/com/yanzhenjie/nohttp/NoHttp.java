/*
 * Copyright 2015 Yan Zhenjie
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yanzhenjie.nohttp;

import android.content.Context;

import com.yanzhenjie.nohttp.cache.CacheEntity;
import com.yanzhenjie.nohttp.cache.DBCacheStore;
import com.yanzhenjie.nohttp.cookie.DBCookieStore;
import com.yanzhenjie.nohttp.tools.CacheStore;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;

/**
 * <p>
 * CoreConfig.
 * </p>
 * Created by Yan Zhenjie on May 3, 2016.
 */
public class NoHttp {

    private static CoreConfig sCoreConfig;

    /**
     * Initialize CoreConfig, should invoke on {@link android.app.Application#onCreate()}.
     *
     * @param coreConfig {@link NoHttp}.
     */
    public static void initialize(NoHttp coreConfig) {
        instance = coreConfig;
    }

    /**
     * Gets instance for config.
     *
     * @return {@link NoHttp}.
     */
    public static NoHttp getInstance() {
        testInitialize();
        return instance;
    }

    /**
     * Gets context of app.
     *
     * @return {@link Context}.
     */
    public static Context getContext() {
        testInitialize();
        return getInstance().mContext;
    }

    /**
     * Test initialized. {@link #initialize(NoHttp)}
     */
    private static void testInitialize() {
        if (instance == null)
            throw new ExceptionInInitializerError("Please invoke CoreConfig#initialize(CoreConfig) on Application#onCreate()");
    }

    private Context mContext;
    private int mConnectTimeout;
    private int mReadTimeout;

    private CookieManager mCookieManager;
    private NetworkExecutor mNetworkExecutor;
    private CacheStore<CacheEntity> mCacheStore;

    private NoHttp(Builder builder) {
        this.mContext = builder.mContext;
        this.mConnectTimeout = builder.mConnectTimeout;
        this.mReadTimeout = builder.mReadTimeout;

        CookieStore cookieStore = builder.mCookieStore == null ?
                DBCookieStore.newBuilder(mContext).build() : builder.mCookieStore;
        this.mCookieManager = new CookieManager(cookieStore, CookiePolicy.ACCEPT_ALL);
        this.mCacheStore = builder.mCacheStore == null ?
                DBCacheStore.newBuilder(mContext).build() : builder.mCacheStore;
        this.mNetworkExecutor = builder.mNetworkExecutor == null ?
                URLConnectionNetworkExecutor.newBuilder().build() : builder.mNetworkExecutor;
    }


    /**
     * Gets connect timeout.
     *
     * @return ms.
     */
    public int getConnectTimeout() {
        return getInstance().mConnectTimeout;
    }

    /**
     * Gets read timeout.
     *
     * @return ms.
     */
    public int getReadTimeout() {
        return getInstance().mReadTimeout;
    }

    /**
     * Gets cookie manager.
     *
     * @return {@link CookieHandler}.
     */
    public CookieManager getCookieManager() {
        return getInstance().mCookieManager;
    }

    /**
     * Gets cache store.
     *
     * @return {@link CacheStore}.
     */
    public CacheStore<CacheEntity> getCacheStore() {
        return getInstance().mCacheStore;
    }

    /**
     * Gets executor implement of http.
     *
     * @return {@link NetworkExecutor}.
     */
    public NetworkExecutor getNetworkExecutor() {
        return getInstance().mNetworkExecutor;
    }

    public static final class Builder {

        private Context mContext;

        private int mConnectTimeout = 10 * 1000;
        private int mReadTimeout = 10 * 1000;

        private CookieStore mCookieStore;
        private CacheStore<CacheEntity> mCacheStore;

        private NetworkExecutor mNetworkExecutor;

        private Builder(Context context) {
            this.mContext = context.getApplicationContext();
        }

        /**
         * Set default connect timeout.
         *
         * @param timeout ms.
         * @return {@link Builder}.
         */
        public Builder setConnectTimeout(int timeout) {
            this.mConnectTimeout = timeout;
            return this;
        }

        /**
         * Set default read timeout.
         *
         * @param timeout ms.
         * @return {@link Builder}.
         */
        public Builder setReadTimeout(int timeout) {
            this.mReadTimeout = timeout;
            return this;
        }

        /**
         * Sets cookie manager.
         *
         * @param cookieStore {@link CookieStore}.
         * @return {@link Builder}.
         */
        public Builder setCookieStore(CookieStore cookieStore) {
            this.mCookieStore = cookieStore;
            return this;
        }

        /**
         * Sets cache store.
         *
         * @param cacheStore {@link CacheStore}.
         * @return {@link Builder}.
         */
        public Builder setCacheStore(CacheStore<CacheEntity> cacheStore) {
            this.mCacheStore = cacheStore;
            return this;
        }

        /**
         * Set the Http request interface, realizes the Http request.
         *
         * @param executor {@link NetworkExecutor}.
         * @return {@link Builder}.
         */
        public Builder setNetworkExecutor(NetworkExecutor executor) {
            this.mNetworkExecutor = executor;
            return this;
        }

        public NoHttp build() {
            return new NoHttp(this);
        }
    }

}
