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

import com.yanzhenjie.nohttp.Binary;
import com.yanzhenjie.nohttp.Headers;
import com.yanzhenjie.nohttp.tools.CounterOutputStream;
import com.yanzhenjie.nohttp.tools.MultiValueMap;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;

/**
 * <p>Define Form Body.</p>
 * Created by YanZhenjie on 2017/6/3.
 */
public class FormBody implements RequestBody {

    /**
     * Param collection.
     */
    private final MultiValueMap<String, Object> mParamKeyValues;
    private final String boundary;
    private final String charset;

    public FormBody(MultiValueMap<String, Object> mParamKeyValues, String boundary, String charset) {
        this.mParamKeyValues = mParamKeyValues;
        this.boundary = boundary;
        this.charset = charset;
    }

    @Override
    public long contentLength() {
        CounterOutputStream stream = new CounterOutputStream();
        try {
            onWrite(stream);
        } catch (IOException ignored) {
        }
        return stream.get();
    }

    @Override
    public String contentType() {
        return Headers.HEAD_VALUE_CONTENT_TYPE_FORM_DATA;
    }

    @Override
    public void onWrite(OutputStream stream) throws IOException {
        Set<String> keys = mParamKeyValues.keySet();
        for (String key : keys) {
            List<Object> values = mParamKeyValues.getValues(key);
            for (Object value : values) {
                if (value instanceof String) {
                    writeFormString(stream, key, (String) value);
                } else if (value instanceof Binary) {
                    writeFormBinary(stream, key, (Binary) value);
                }
                stream.write("\r\n".getBytes());
            }
        }
        stream.write(("--" + boundary + "--").getBytes());
    }

    private void writeFormString(OutputStream writer, String key, String value) throws IOException {
        String stringFieldBuilder = "--" + boundary + "\r\n"
                + "Content-Disposition: form-data; name=\"" + key + "\"\r\n\n";

        writer.write(stringFieldBuilder.getBytes(charset));
        writer.write(value.getBytes(charset));
    }

    private void writeFormBinary(OutputStream writer, String key, Binary value) throws IOException {
        if (!value.isCanceled()) {
            String binaryFieldBuilder = "--" + boundary + "\r\n"
                    + "Content-Disposition: form-data; name=\"" + key + "\"" + "; filename=\"" + value.getFileName() + "\"\r\n"
                    + "Content-Type: " + value.getMimeType() + "\r\n\n";
            writer.write(binaryFieldBuilder.getBytes());

            if (writer instanceof CounterOutputStream) {
                ((CounterOutputStream) writer).add(value.getLength());
            } else {
                value.onWriteBinary(writer);
            }
        }
    }
}
