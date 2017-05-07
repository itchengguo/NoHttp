/*
 * Copyright © Yan Zhenjie
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
import com.yanzhenjie.nohttp.RequestMethod;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Yan Zhenjie on 2017/5/7.
 */
public class QueueDownloadRequest extends DefaultDownloadRequest implements DownloadRequest {

    private BlockingQueue<DownloadRequest> mBlockingQueue;
    private int sequence;
    private Priority priority;

    public QueueDownloadRequest(String url,
                                RequestMethod requestMethod,
                                String fileFolder,
                                boolean isDeleteOld) {
        super(url, requestMethod, fileFolder, isDeleteOld);
    }

    public QueueDownloadRequest(String url,
                                RequestMethod requestMethod,
                                String fileFolder,
                                String filename,
                                boolean isRange,
                                boolean isDeleteOld) {
        super(url, requestMethod, fileFolder, filename, isRange, isDeleteOld);
    }

    @Override
    public int compareTo(DownloadRequest another) {
        final Priority me = getPriority();
        final Priority it = another.getPriority();
        return me == it ? getSequence() - another.getSequence() : it.ordinal() - me.ordinal();
    }

    @Override
    public DownloadRequest setPriority(Priority priority) {
        this.priority = priority;
        return this;
    }

    @Override
    public DownloadRequest setSequence(int sequence) {
        this.sequence = sequence;
        return this;
    }

    @Override
    public Priority getPriority() {
        return this.priority;
    }

    @Override
    public int getSequence() {
        return this.sequence;
    }
}
