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
import com.yanzhenjie.nohttp.network.NetworkExecutor;
import com.yanzhenjie.nohttp.network.URLConnectionNetworkExecutor;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

/**
 * <p>
 * CoreConfig.
 * </p>
 * Created by Yan Zhenjie on May 3, 2016.
 */
public class CoreConfig {

    public static Builder newBuilder(Context context) {
        return new Builder(context);
    }

    private Context mContext;
    private int mConnectTimeout;
    private int mReadTimeout;
    private Map<String, String> mParams;

    private CookieManager mCookieManager;
    private NetworkExecutor mNetworkExecutor;
    private CacheStore<CacheEntity> mCacheStore;

    private SSLSocketFactory mSslSocketFactory;
    private HostnameVerifier mHostnameVerifier;

    private CoreConfig(Builder builder) {
        this.mContext = builder.context.getApplicationContext();
        this.mConnectTimeout = builder.connectTimeout;
        this.mReadTimeout = builder.readTimeout;
        this.mParams = builder.params;

        CookieStore cookieStore = builder.cookieStore == null ?
                DBCookieStore.newBuilder(mContext).build() : builder.cookieStore;
        this.mCookieManager = new CookieManager(cookieStore, CookiePolicy.ACCEPT_ALL);
        this.mCacheStore = builder.cacheStore == null ?
                DBCacheStore.newBuilder(mContext).build() : builder.cacheStore;
        this.mNetworkExecutor = builder.networkExecutor == null ?
                URLConnectionNetworkExecutor.newBuilder().build() : builder.networkExecutor;

        this.mSslSocketFactory = builder.sslSocketFactory;
        this.mHostnameVerifier = builder.hostnameVerifier;
    }

    public Context getContext() {
        return mContext;
    }

    public int getConnectTimeout() {
        return mConnectTimeout;
    }

    public int getReadTimeout() {
        return mReadTimeout;
    }

    public Map<String, String> getParams() {
        return mParams;
    }

    public CookieManager getCookieManager() {
        return mCookieManager;
    }

    public NetworkExecutor getNetworkExecutor() {
        return mNetworkExecutor;
    }

    public CacheStore<CacheEntity> getCacheStore() {
        return mCacheStore;
    }

    public SSLSocketFactory getSSLSocketFactory() {
        return mSslSocketFactory;
    }

    public HostnameVerifier getHostnameVerifier() {
        return mHostnameVerifier;
    }

    /**
     * The Builder of Configuration.
     */
    public static final class Builder {

        private Context context;

        private int connectTimeout = 10 * 1000;
        private int readTimeout = 10 * 1000;
        private Map<String, String> params = new HashMap<>();

        private CookieStore cookieStore;
        private CacheStore<CacheEntity> cacheStore;

        private NetworkExecutor networkExecutor;

        private SSLSocketFactory sslSocketFactory;
        private HostnameVerifier hostnameVerifier;

        private Builder(Context context) {
            this.context = context;
        }

        /**
         * Set default connect timeout.
         *
         * @param timeout ms.
         * @return {@link Builder}.
         */
        public Builder connectTimeout(int timeout) {
            this.connectTimeout = timeout;
            return this;
        }

        /**
         * Set default read timeout.
         *
         * @param timeout ms.
         * @return {@link Builder}.
         */
        public Builder readTimeout(int timeout) {
            this.readTimeout = timeout;
            return this;
        }

        /**
         * Set generic param.
         *
         * @param key   key.
         * @param value value.
         * @return {@link Builder}.
         */
        public Builder addGenericParam(String key, String value) {
            params.put(key, value);
            return this;
        }

        /**
         * Sets cookie manager.
         *
         * @param cookieStore {@link CookieStore}.
         * @return {@link Builder}.
         */
        public Builder cookieStore(CookieStore cookieStore) {
            this.cookieStore = cookieStore;
            return this;
        }

        /**
         * Sets cache store.
         *
         * @param cacheStore {@link CacheStore}.
         * @return {@link Builder}.
         */
        public Builder cacheStore(CacheStore<CacheEntity> cacheStore) {
            this.cacheStore = cacheStore;
            return this;
        }

        /**
         * Set the Http request interface, realizes the Http request.
         *
         * @param executor {@link NetworkExecutor}.
         * @return {@link Builder}.
         */
        public Builder networkExecutor(NetworkExecutor executor) {
            this.networkExecutor = executor;
            return this;
        }

        /**
         * Set default SSLSocketFactory.
         *
         * @param sslSocketFactory {@link SSLSocketFactory}.
         * @return {@link Builder}.
         */
        public Builder sslSocketFactory(SSLSocketFactory sslSocketFactory) {
            this.sslSocketFactory = sslSocketFactory;
            return this;
        }

        /**
         * Set default HostnameVerifier.
         *
         * @param hostnameVerifier {@link HostnameVerifier}.
         * @return {@link Builder}.
         */
        public Builder hostnameVerifier(HostnameVerifier hostnameVerifier) {
            this.hostnameVerifier = hostnameVerifier;
            return this;
        }

        public CoreConfig build() {
            return new CoreConfig(this);
        }
    }

}
