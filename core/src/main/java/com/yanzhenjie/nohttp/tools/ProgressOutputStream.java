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
package com.yanzhenjie.nohttp.tools;

import com.yanzhenjie.nohttp.ProgressListener;

import java.io.IOException;
import java.io.OutputStream;

/**
 * <p>You can monitor the OutputStream of the progress.</p>
 * Created by YanZhenjie on 2017/6/3.
 */
public class ProgressOutputStream extends OutputStream {

    private final OutputStream stream;
    private final ProgressListener listener;

    private final long totalLength;
    private long writeLength;

    private int oldProgress;

    public ProgressOutputStream(OutputStream stream, ProgressListener listener, long totalLength) {
        this.stream = stream;
        this.listener = listener;
        this.totalLength = totalLength;
    }

    /**
     * Handle progress.
     *
     * @param addLength new contentLength.
     */
    private void handleProgress(int addLength) {
        if (listener == null) return;
        writeLength += addLength;
        int progress = (int) (writeLength * 100 / totalLength);
        if (progress > oldProgress) {
            oldProgress = progress;
            listener.onProgress(-1, oldProgress);
        }
    }

    @Override
    public void write(int b) throws IOException {
        stream.write(b);
        handleProgress(1);
    }

    @Override
    public void write(byte[] b) throws IOException {
        stream.write(b);
        handleProgress(b.length);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        stream.write(b, off, len);
        handleProgress(len);
    }

}
