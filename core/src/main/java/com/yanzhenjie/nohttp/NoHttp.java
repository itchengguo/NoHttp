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
    public static void initialize(CoreConfig coreConfig) {
        sCoreConfig = coreConfig;
    }

    /**
     * Gets context of app.
     *
     * @return {@link Context}.
     */
    public static Context getContext() {
        testInitialize();
        return sCoreConfig.getContext();
    }

    public static CoreConfig getCoreConfig() {
        testInitialize();
        return sCoreConfig;
    }

    private static void testInitialize() {
        if (sCoreConfig == null)
            throw new ExceptionInInitializerError("Please invoke CoreConfig#initialize(CoreConfig) on Application#onCreate()");
    }

}
