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
package com.yanzhenjie.nohttp.cookie;

import android.content.Context;
import android.text.TextUtils;

import com.yanzhenjie.nohttp.Logger;
import com.yanzhenjie.nohttp.db.BaseDao;
import com.yanzhenjie.nohttp.db.Where;
import com.yanzhenjie.nohttp.db.Where.Options;

import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <p>Default CookieStore.</p>
 * Created by Yan Zhenjie on Dec 17, 2015.
 */
public class DBCookieStore implements CookieStore {

    public static Builder newBuilder(Context context) {
        return new Builder(context);
    }

    private final static int MAX_COOKIE_SIZE = 8888;
    private Lock mLock;
    private BaseDao<CookieEntity> mDbDao;

    private CookieStoreListener mCookieStoreListener;
    private boolean mEnable = true;

    /**
     * @deprecated use {@link #newBuilder(Context)} instead.
     */
    @Deprecated
    public DBCookieStore(Context context) {
        this(newBuilder(context));
    }

    private DBCookieStore(Builder builder) {
        this.mCookieStoreListener = builder.mListener;
        this.mEnable = builder.enable;

        mLock = new ReentrantLock();
        if (mEnable) {
            mDbDao = new CookieEntityDao(builder.mContext);
            // Delete temp cookie.
            Where where = new Where(CookieSQLHelper.EXPIRY, Options.EQUAL, -1L);
            mDbDao.delete(where.get());
        }
    }

    /**
     * @deprecated use {@link #newBuilder(Context)} instead.
     */
    @Deprecated
    public CookieStore setCookieStoreListener(CookieStoreListener cookieStoreListener) {
        this.mCookieStoreListener = cookieStoreListener;
        return this;
    }

    /**
     * @deprecated use {@link #newBuilder(Context)} instead.
     */
    @Deprecated
    public CookieStore setEnable(boolean enable) {
        this.mEnable = enable;
        return this;
    }

    @Override
    public void add(URI uri, HttpCookie cookie) {
        mLock.lock();
        try {
            if (mEnable && uri != null && cookie != null) {
                uri = getEffectiveURI(uri);
                if (mCookieStoreListener != null)
                    mCookieStoreListener.onSaveCookie(uri, cookie);
                mDbDao.replace(new CookieEntity(uri, cookie));
                trimSize();
            }
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public List<HttpCookie> get(URI uri) {
        mLock.lock();
        try {
            if (uri == null || !mEnable) return Collections.emptyList();

            uri = getEffectiveURI(uri);
            Where where = new Where();

            String host = uri.getHost();
            if (!TextUtils.isEmpty(host)) {
                Where subWhere = new Where(CookieSQLHelper.DOMAIN, Options.EQUAL, host).or(CookieSQLHelper.DOMAIN,
                        Options.EQUAL, "." + host);

                int firstDot = host.indexOf(".");
                int lastDot = host.lastIndexOf(".");
                if (firstDot > 0 && lastDot > firstDot) {
                    String domain = host.substring(firstDot, host.length());
                    if (!TextUtils.isEmpty(domain)) {
                        subWhere.or(CookieSQLHelper.DOMAIN, Options.EQUAL, domain);
                    }
                }
                where.set(subWhere.get());
            }

            String path = uri.getPath();
            if (!TextUtils.isEmpty(path)) {
                Where subWhere = new Where(CookieSQLHelper.PATH, Options.EQUAL, path).or(CookieSQLHelper.PATH,
                        Options.EQUAL, "/").orNull(CookieSQLHelper.PATH);
                int lastSplit = path.lastIndexOf("/");
                while (lastSplit > 0) {
                    path = path.substring(0, lastSplit);
                    subWhere.or(CookieSQLHelper.PATH, Options.EQUAL, path);
                    lastSplit = path.lastIndexOf("/");
                }
                subWhere.bracket();
                where.and(subWhere);
            }

            where.or(CookieSQLHelper.URI, Options.EQUAL, uri.toString());

            List<CookieEntity> cookieList = mDbDao.getList(where.get(), null, null, null);
            List<HttpCookie> returnedCookies = new ArrayList<>();
            for (CookieEntity cookieEntity : cookieList)
                if (!cookieEntity.isExpired())
                    returnedCookies.add(cookieEntity.toHttpCookie());
            return returnedCookies;
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public List<HttpCookie> getCookies() {
        mLock.lock();
        try {
            if (!mEnable) return Collections.emptyList();
            List<HttpCookie> rt = new ArrayList<>();
            List<CookieEntity> cookieEntityList = mDbDao.getAll();
            for (CookieEntity cookieEntity : cookieEntityList)
                if (!cookieEntity.isExpired())
                    rt.add(cookieEntity.toHttpCookie());
            return rt;
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public List<URI> getURIs() {
        mLock.lock();
        try {
            if (!mEnable) return Collections.emptyList();
            List<URI> uris = new ArrayList<>();
            List<CookieEntity> uriList = mDbDao.getAll();
            for (CookieEntity cookie : uriList) {
                String uri = cookie.getUri();
                if (!TextUtils.isEmpty(uri))
                    try {
                        uris.add(new URI(uri));
                    } catch (Throwable e) {
                        Logger.w(e);
                        mDbDao.delete(CookieSQLHelper.URI + '=' + uri);
                    }
            }
            return uris;
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public boolean remove(URI uri, HttpCookie httpCookie) {
        mLock.lock();
        try {
            if (httpCookie == null || !mEnable)
                return true;
            if (mCookieStoreListener != null)
                mCookieStoreListener.onRemoveCookie(uri, httpCookie);
            Where where = new Where(CookieSQLHelper.NAME, Options.EQUAL, httpCookie.getName());

            String domain = httpCookie.getDomain();
            if (!TextUtils.isEmpty(domain))
                where.and(CookieSQLHelper.DOMAIN, Options.EQUAL, domain);

            String path = httpCookie.getPath();
            if (!TextUtils.isEmpty(path)) {
                if (path.length() > 1 && path.endsWith("/")) {
                    path = path.substring(0, path.length() - 1);
                }
                where.and(CookieSQLHelper.PATH, Options.EQUAL, path);
            }
            return mDbDao.delete(where.toString());
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public boolean removeAll() {
        mLock.lock();
        try {
            if (!mEnable) return true;
            return mDbDao.deleteAll();
        } finally {
            mLock.unlock();
        }
    }

    /**
     * Trim the Cookie list.
     */
    private void trimSize() {
        int count = mDbDao.count();
        if (count > MAX_COOKIE_SIZE + 10) {
            List<CookieEntity> rmList = mDbDao.getList(null, null, Integer.toString(count -
                    MAX_COOKIE_SIZE), null);
            if (rmList != null)
                mDbDao.delete(rmList);
        }
    }

    /**
     * Get effective URI.
     *
     * @param uri cookie corresponding uri.
     */
    private URI getEffectiveURI(final URI uri) {
        URI effectiveURI;
        try {
            effectiveURI = new URI("http", uri.getHost(), uri.getPath(), null, null);
        } catch (URISyntaxException e) {
            effectiveURI = uri;
        }
        return effectiveURI;
    }

    public interface CookieStoreListener {
        /**
         * When saving a Cookie callback.
         *
         * @param uri    cookie corresponding uri.
         * @param cookie {@link HttpCookie}.
         */
        void onSaveCookie(URI uri, HttpCookie cookie);

        /**
         * The callback when deleting cookies.
         *
         * @param uri    cookie corresponding uri.
         * @param cookie {@link HttpCookie}.
         */
        void onRemoveCookie(URI uri, HttpCookie cookie);

    }

    public static final class Builder {

        private Context mContext;
        private CookieStoreListener mListener;
        private boolean enable = true;

        private Builder(Context context) {
            this.mContext = context.getApplicationContext();
        }

        public Builder listener(CookieStoreListener listener) {
            this.mListener = listener;
            return this;
        }

        public Builder enable(boolean enable) {
            this.enable = enable;
            return this;
        }

        public DBCookieStore build() {
            return new DBCookieStore(this);
        }

    }
}