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
package com.yanzhenjie.nohttp.body;

import com.yanzhenjie.nohttp.ProgressListener;
import com.yanzhenjie.nohttp.tools.ProgressOutputStream;

import java.io.IOException;
import java.io.OutputStream;

/**
 * <p>Body Writer.</p>
 * Created by YanZhenjie on 2017/6/3.
 */
public class BodyWriter {

    private final RequestBody body;
    private ProgressListener mListener;

    public BodyWriter(RequestBody body) {
        this.body = body;
    }

    public void setProgressListener(ProgressListener listener) {
        this.mListener = listener;
    }

    /**
     * Send request body data.
     */
    public void onWriteContent(OutputStream writer) throws IOException {
        OutputStream stream = new ProgressOutputStream(writer, mListener, body.contentLength());
        body.onWrite(stream);
    }

}
