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
package com.yanzhenjie.nohttp.rest;

import com.yanzhenjie.nohttp.Priority;

/**
 * <p>
 * Extended {@link IProtocolRequest} class, to increase the method of recording response.
 * </p>
 * Created in Oct 16, 2015 8:22:06 PM.
 *
 * @param <T> a generic, on behalf of you can accept the result type, .It should be with the
 *            {@link OnResponseListener}, {@link Response}.
 * @author Yan Zhenjie.
 */
public interface Request<T> extends IProtocolRequest<T>, Comparable<IBasicRequest> {

    /**
     * Prepare the callback parameter, while waiting for the response callback with thread.
     *
     * @param what             the callback mark.
     * @param responseListener {@link OnResponseListener}.
     */
    void onPreResponse(int what, OnResponseListener<T> responseListener);

    /**
     * The callback mark.
     *
     * @return Return when {@link #onPreResponse(int, OnResponseListener)} incoming credit.
     * @see #onPreResponse(int, OnResponseListener)
     */
    int what();

    /**
     * The request of the listener.
     *
     * @return Return when {@link #onPreResponse(int, OnResponseListener)} incoming credit.
     * @see #onPreResponse(int, OnResponseListener)
     */
    OnResponseListener<T> responseListener();

    /**
     * Set the priority of the request object. The default priority is {@link Priority#DEFAULT}.
     *
     * @param priority {@link Priority}.
     * @return {@link IBasicRequest}.
     */
    Request setPriority(Priority priority);

    /**
     * Set the sequence in the queue, under the condition of two requests as priority, {@code left.sequence-right
     * .sequence} decision to order.
     *
     * @param sequence sequence code.
     * @return {@link IBasicRequest}.
     */
    Request setSequence(int sequence);

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