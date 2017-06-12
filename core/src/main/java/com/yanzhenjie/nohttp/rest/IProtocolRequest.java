/*
 * Copyright © Yan Zhenjie. All Rights Reserved
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
package com.yanzhenjie.nohttp.rest;

import com.yanzhenjie.nohttp.Headers;
import com.yanzhenjie.nohttp.IBasicRequest;
import com.yanzhenjie.nohttp.cache.CacheMode;

/**
 * <p>For the Request to encapsulate some Http protocol related properties.</p>
 * Created by Yan Zhenjie on 2016/8/20.
 *
 * @param <Result> result.
 */
public interface IProtocolRequest<Result, Child extends IBasicRequest > extends IClientProtocol<>, IBasicRequest<Child> {

    /**
     * Get key of cache data.
     *
     * @return cache key.
     */
    String getCacheKey();

    /**
     * He got the request cache mode.
     *
     * @return value from {@link CacheMode}.
     */
    CacheMode getCacheMode();

    /**
     * Parse request results for generic objects.
     *
     * @param responseHeaders response headers of server.
     * @param responseBody    response data of server.
     * @return your response result.
     * @throws Exception parse error.
     */
    Result parseResponse(Headers responseHeaders, byte[] responseBody) throws Exception;

}
