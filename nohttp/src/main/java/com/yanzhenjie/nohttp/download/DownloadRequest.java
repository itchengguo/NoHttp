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
package com.yanzhenjie.nohttp.download;

import com.yanzhenjie.nohttp.Priority;

/**
 * Created by Yan Zhenjie on 2017/5/4.
 */
public interface DownloadRequest extends IDownloadRequest, Comparable<DownloadRequest> {

    /**
     * Set the priority of the request object. The default priority is {@link Priority#DEFAULT}.
     *
     * @param priority {@link Priority}.
     * @return {@link DownloadRequest}.
     */
    DownloadRequest setPriority(Priority priority);

    /**
     * Set the sequence in the queue, under the condition of two requests as priority, {@code left.sequence-right
     * .sequence} decision to order.
     *
     * @param sequence sequence code.
     * @return {@link DownloadRequest}.
     */
    DownloadRequest setSequence(int sequence);

    /**
     * Get the priority of the request object.
     *
     * @return {@link Priority}.
     */
    Priority getPriority();

    /**
     * Get the sequence in the queue, under the condition of two requests as priority, {@code left.sequence-right
     * .sequence} decision to order.
     *
     * @return sequence code.
     */
    int getSequence();

}
