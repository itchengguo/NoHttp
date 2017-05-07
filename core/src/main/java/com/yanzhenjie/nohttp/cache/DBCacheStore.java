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
package com.yanzhenjie.nohttp.cache;

import android.content.Context;

import com.yanzhenjie.nohttp.db.BaseDao;
import com.yanzhenjie.nohttp.db.Where;
import com.yanzhenjie.nohttp.tools.CacheStore;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <p>Http cache interface implementation.</p>
 * Created by Yan Zhenjie on Jan 10, 2016.
 */
public class DBCacheStore implements CacheStore<CacheEntity> {

    public static Builder newBuilder(Context context) {
        return new Builder(context);
    }

    private Lock mLock;
    private BaseDao<CacheEntity> mDbDao;
    private boolean mEnable = true;

    /**
     * @param context
     * @deprecated use {@link #newBuilder(Context)} instead.
     */
    @Deprecated
    public DBCacheStore(Context context) {
        this(newBuilder(context));
    }

    private DBCacheStore(Builder builder) {
        mLock = new ReentrantLock();
        if (builder.mEnable)
            mDbDao = new CacheEntityDao(builder.mContext);
    }

    /**
     * Set cache enable.
     *
     * @param enable true or false.
     * @return {@link DBCacheStore}.
     * @deprecated use {@link #newBuilder(Context)} instead.
     */
    @Deprecated
    public CacheStore<CacheEntity> setEnable(boolean enable) {
        this.mEnable = enable;
        return this;
    }

    @Override
    public CacheEntity get(String key) {
        mLock.lock();
        try {
            if (!mEnable) return null;
            Where where = new Where(CacheSQLHelper.KEY, Where.Options.EQUAL, key);
            List<CacheEntity> cacheEntities = mDbDao.getList(where.get(), null, null, null);
            return cacheEntities.size() > 0 ? cacheEntities.get(0) : null;
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public CacheEntity replace(String key, CacheEntity cacheEntity) {
        mLock.lock();
        try {
            if (!mEnable) return cacheEntity;
            cacheEntity.setKey(key);
            mDbDao.replace(cacheEntity);
            return cacheEntity;
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public boolean remove(String key) {
        mLock.lock();
        try {
            if (key == null || !mEnable)
                return false;
            Where where = new Where(CacheSQLHelper.KEY, Where.Options.EQUAL, key);
            return mDbDao.delete(where.toString());
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public boolean clear() {
        mLock.lock();
        try {
            if (!mEnable) return false;
            return mDbDao.deleteAll();
        } finally {
            mLock.unlock();
        }
    }

    public static final class Builder {

        private Context mContext;
        private boolean mEnable = true;

        private Builder(Context context) {
            mContext = context.getApplicationContext();
        }

        public Builder enable(boolean enable) {
            this.mEnable = enable;
            return this;
        }

        public DBCacheStore build() {
            return new DBCacheStore(this);
        }
    }

}
