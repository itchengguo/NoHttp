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
package com.yanzhenjie.nohttp.http;

/**
 * Created by YanZhenjie on 2017/6/4.
 */
public interface Path<T extends Path> {

    T path(CharSequence path);

    T path(int path);

    T path(long path);

    T path(short path);

    T path(double path);

    T path(float path);

    T path(char path);

    T path(boolean path);

}