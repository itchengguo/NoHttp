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
package com.yanzhenjie.nohttp.http;

/**
 * Created by YanZhenjie on 2017/6/4.
 */
public interface Param<T extends Path> {

    T param(CharSequence path);

    T param(int path);

    T param(long path);

    T param(short path);

    T param(double path);

    T param(float path);

    T param(char path);

    T param(boolean path);

}