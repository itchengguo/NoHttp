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

import android.os.Process;

import com.yanzhenjie.nohttp.Delivery;
import com.yanzhenjie.nohttp.Logger;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * <p>
 * Request queue polling thread.
 * </p>
 * Created in Oct 19, 2015 8:35:35 AM.
 *
 * @author Yan Zhenjie.
 */
public class RequestDispatcher extends Thread {

    private final Map<Request, ListenerEntity<?>> mListenerEntityMap;
    private final BlockingQueue<Request<?>> mRequestQueue;

    /**
     * Delivery.
     */
    private Delivery mDelivery;
    /**
     * Whether the current request queue polling thread is out of.
     */
    private volatile boolean mQuit = false;

    /**
     * Create a request queue polling thread.
     *
     * @param listenerEntityMap listener entity map.
     * @param requestQueue      request queue.
     * @param delivery          delivery.
     */
    public RequestDispatcher(Map<Request, ListenerEntity<?>> listenerEntityMap,
                             BlockingQueue<Request<?>> requestQueue,
                             Delivery delivery) {
        this.mListenerEntityMap = listenerEntityMap;
        this.mRequestQueue = requestQueue;
        this.mDelivery = delivery;
    }

    /**
     * Exit polling thread.
     */
    public void quit() {
        this.mQuit = true;
        interrupt();
    }

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        while (!mQuit) {
            final Request<?> request;
            try {
                request = mRequestQueue.take();
            } catch (InterruptedException e) {
                if (mQuit) {
                    Logger.w("Queue exit, stop blocking.");
                    break;
                }
                Logger.e(e);
                continue;
            }

            if (request.isCanceled()) {
                Logger.d(request.url() + " is canceled.");
                continue;
            }

            ListenerEntity<?> listenerEntity = mListenerEntityMap.get(request);

            // start
            Messenger.prepare(listenerEntity.what(), listenerEntity.responseListener())
                    .start()
                    .post(mDelivery);

            // request.
            Response response = SyncRequestExecutor.INSTANCE.execute(request);

            // response
            if (request.isCanceled())
                Logger.d(request.url() + " finish, but it's canceled.");
            else
                //noinspection unchecked
                Messenger.prepare(listenerEntity.what(), listenerEntity.responseListener())
                        .response(response)
                        .post(mDelivery);

            // finish.
            Messenger.prepare(listenerEntity.what(), listenerEntity.responseListener())
                    .finish()
                    .post(mDelivery);
            mListenerEntityMap.remove(request);
        }
    }
}
