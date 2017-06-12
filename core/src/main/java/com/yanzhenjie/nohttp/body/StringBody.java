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

import com.yanzhenjie.nohttp.tools.IOUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * <p>Define String Body, such as: String, JSON, XML.</p>
 * Created by YanZhenjie on 2017/6/3.
 */
public class StringBody implements RequestBody {

    private final String body;
    private final String contentType;
    private final String charset;

    public StringBody(String body, String contentType, String charset) {
        this.body = body;
        this.contentType = contentType;
        this.charset = charset;
    }

    private byte[] getBytes() {
        try {
            return body.getBytes(charset);
        } catch (UnsupportedEncodingException ignored) {
            return body.getBytes();
        }
    }

    @Override
    public long contentLength() {
        return getBytes().length;
    }

    @Override
    public String contentType() {
        return contentType;
    }

    @Override
    public void onWrite(OutputStream stream) throws IOException {
        IOUtils.write(getBytes(), stream);
    }
}
