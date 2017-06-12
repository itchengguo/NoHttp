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

import com.yanzhenjie.nohttp.Headers;
import com.yanzhenjie.nohttp.tools.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * <p>Define File Body.</p>
 * Created by YanZhenjie on 2017/6/3.
 */
public class FileBody implements RequestBody {

    private File file;

    public FileBody(File file) {
        this.file = file;
    }

    @Override
    public long contentLength() {
        return file.length();
    }

    @Override
    public String contentType() {
        return Headers.HEAD_VALUE_CONTENT_TYPE_OCTET_STREAM;
    }

    @Override
    public void onWrite(OutputStream stream) throws IOException {
        InputStream inputStream = new FileInputStream(file);
        IOUtils.write(inputStream, stream);
    }
}
