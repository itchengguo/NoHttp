/*
 * Copyright © Yan Zhenjie. All Rights Reserved
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
package com.yanzhenjie.nohttp;

import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.net.HttpCookie;
import java.net.Proxy;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

/**
 * <p>Client request interface.</p>
 * Created by Yan Zhenjie on 2017/5/18.
 */
public interface IClientRequest<Child extends IClientRequest> {

    /**
     * Set proxy server.
     *
     * @param proxy Can use {@code Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("64.233.162.83",
     *              80));}.
     * @return {@link Child}.
     */
    Child setProxy(Proxy proxy);

    /**
     * Sets the {@link SSLSocketFactory} for this request.
     *
     * @param socketFactory {@link SSLSocketFactory}.
     * @return {@link Child}.
     */
    Child setSSLSocketFactory(SSLSocketFactory socketFactory);

    /**
     * Set the {@link HostnameVerifier}.
     *
     * @param hostnameVerifier {@link HostnameVerifier}.
     * @return {@link Child}.
     */
    Child setHostnameVerifier(HostnameVerifier hostnameVerifier);

    /**
     * Sets the connection timeout time.
     *
     * @param connectTimeout timeout number, Unit is a millisecond.
     * @return {@link Child}.
     */
    Child setConnectTimeout(int connectTimeout);

    /**
     * Sets the read timeout time.
     *
     * @param readTimeout timeout number, Unit is a millisecond.
     * @return {@link Child}.
     */
    Child setReadTimeout(int readTimeout);

    /**
     * Add a new key-value header.
     *
     * @param key   key.
     * @param value value.
     * @return {@link Child}.
     */
    Child addHeader(String key, String value);

    /**
     * If there is a key to delete, and then add a new key-value header.
     *
     * @param key   key.
     * @param value value.
     * @return {@link Child}.
     */
    Child setHeader(String key, String value);

    /**
     * <p>Add a {@link HttpCookie}.</p>
     * Just like the:
     * <pre>
     *     HttpCookie httpCookie = getHttpCookie();
     *     if(httpCookie != null)
     *          request.addHeader("Cookie", cookie.getName() + "=" + cookie.getValue());
     *     ...
     * </pre>
     *
     * @param cookie {@link HttpCookie}.
     * @return {@link Child}.
     */
    Child addHeader(HttpCookie cookie);

    /**
     * Remove the key from the information.
     *
     * @param key key.
     * @return {@link Child}.
     */
    Child removeHeader(String key);

    /**
     * Remove all header.
     *
     * @return {@link Child}.
     */
    Child removeAllHeader();

    /**
     * Set the accept for head.
     *
     * @param accept such as: "{@value Headers#HEAD_VALUE_CONTENT_TYPE_JSON}", "{@value
     *               Headers#HEAD_VALUE_CONTENT_TYPE_XML}.
     * @return {@link Child}.
     */
    Child setAccept(String accept);

    /**
     * Set the acceptLanguage for head.
     *
     * @param acceptLanguage such as "zh-CN,zh", "en-US,us".
     * @return {@link Child}.
     */
    Child setAcceptLanguage(String acceptLanguage);

    /**
     * Set the contentType for head.
     *
     * @param contentType such as: "{@value Headers#HEAD_VALUE_CONTENT_TYPE_JSON}", "{@value
     *                    Headers#HEAD_VALUE_CONTENT_TYPE_XML}" or "{@value
     *                    Headers#HEAD_VALUE_CONTENT_TYPE_FORM_DATA}". Note,
     *                    does not need to include quotation marks.
     * @return {@link Child}.
     */
    Child setContentType(String contentType);

    /**
     * Set the userAgent for head.
     *
     * @param userAgent such as: {@code Mozilla/5.0 (Android U; Android 5.0) AppleWebKit/533.1 (KHTML, like Gecko)
     *                  Version/5.0 Safari/533.1}.
     * @return {@link Child}.
     */
    Child setUserAgent(String userAgent);

    /**
     * Set the request fails retry count.The default value is 0, that is to say, after the failure will not go to
     * this to initiate the request again.
     *
     * @param count the retry count, The default value is 0.
     * @return {@link Child}.
     */
    Child setRetryCount(int count);

    /**
     * Set the params encoding.
     *
     * @param encoding such as {@code utf-8, gbk, gb2312}.
     * @return {@link Child}.
     */
    Child setParamsEncoding(String encoding);

    /**
     * Add a path to url.
     *
     * @param path path.
     * @return {@link Child}.
     */
    Child path(int path);

    /**
     * Add a path to url.
     *
     * @param path path.
     * @return {@link Child}.
     */
    Child path(long path);

    /**
     * Add a path to url.
     *
     * @param path path.
     * @return {@link Child}.
     */
    Child path(short path);

    /**
     * Add a path to url.
     *
     * @param path path.
     * @return {@link Child}.
     */
    Child path(float path);

    /**
     * Add a path to url.
     *
     * @param path path.
     * @return {@link Child}.
     */
    Child path(double path);

    /**
     * Add a path to url.
     *
     * @param path path.
     * @return {@link Child}.
     */
    Child path(boolean path);

    /**
     * Add a path to url.
     *
     * @param path path.
     * @return {@link Child}.
     */
    Child path(char path);

    /**
     * Add a path to url.
     *
     * @param path path.
     * @return {@link Child}.
     */
    Child path(byte path);

    /**
     * Add a path to url.
     *
     * @param path path.
     * @return {@link Child}.
     */
    Child path(String path);

    /**
     * Add {@link Integer} param.
     *
     * @param key   param name.
     * @param value param value.
     * @return {@link Child}.
     */
    Child add(String key, int value);

    /**
     * Add {@link Long} param.
     *
     * @param key   param name.
     * @param value param value.
     * @return {@link Child}.
     */
    Child add(String key, long value);

    /**
     * Add {@link Boolean} param.
     *
     * @param key   param name.
     * @param value param value.
     * @return {@link Child}.
     */
    Child add(String key, boolean value);

    /**
     * Add {@code char} param.
     *
     * @param key   param name.
     * @param value param value.
     * @return {@link Child}.
     */
    Child add(String key, char value);

    /**
     * Add {@link Double} param.
     *
     * @param key   param name.
     * @param value param value.
     * @return {@link Child}.
     */
    Child add(String key, double value);

    /**
     * Add {@link Float} param.
     *
     * @param key   param name.
     * @param value param value.
     * @return {@link Child}.
     */
    Child add(String key, float value);

    /**
     * Add {@link Short} param.
     *
     * @param key   param name.
     * @param value param value.
     * @return {@link Child}.
     */
    Child add(String key, short value);

    /**
     * Add {@link Byte} param.
     *
     * @param key   param name.
     * @param value param value, for example, the result is {@code 1} of {@code 0x01}.
     * @return {@link Child}.
     */
    Child add(String key, byte value);

    /**
     * Add {@link String} param.
     *
     * @param key   param name.
     * @param value param value.
     * @return {@link Child}.
     */
    Child add(String key, String value);

    /**
     * Add {@link String} param.
     *
     * @param key   param name.
     * @param value param value.
     * @return {@link Child}.
     */
    Child set(String key, String value);

    /**
     * Add {@link Binary} param.
     *
     * @param key    param name.
     * @param binary param value.
     * @return {@link Child}.
     */
    Child add(String key, Binary binary);

    /**
     * Set {@link Binary} param.
     *
     * @param key    param name.
     * @param binary param value.
     * @return {@link Child}.
     */
    Child set(String key, Binary binary);

    /**
     * Add {@link File} param.
     *
     * @param key  param name.
     * @param file param value.
     * @return {@link Child}.
     */
    Child add(String key, File file);

    /**
     * Set {@link File} param.
     *
     * @param key  param name.
     * @param file param value.
     * @return {@link Child}.
     */
    Child set(String key, File file);

    /**
     * Add {@link Binary} param;
     *
     * @param key      param name.
     * @param binaries param value.
     * @return {@link Child}.
     */
    Child add(String key, List<Binary> binaries);

    /**
     * Set {@link Binary} param.
     *
     * @param key      param name.
     * @param binaries param value.
     * @return {@link Child}.
     */
    Child set(String key, List<Binary> binaries);

    /**
     * Add all param.
     *
     * @param params params {@link Map}.
     * @return {@link Child}.
     */
    Child add(Map<String, String> params);

    /**
     * Set all param.
     *
     * @param params params {@link Map}.
     * @return {@link Child}.
     */
    Child set(Map<String, String> params);

    /**
     * Remove a request param by key.
     *
     * @param key key
     * @return {@link Child}.
     */
    Child remove(String key);

    /**
     * Remove all request param.
     *
     * @return {@link Child}.
     */
    Child removeAll();

    /**
     * Settings you want to push data and contentType. Can only accept {@link java.io.ByteArrayInputStream} and
     * {@link java.io.FileInputStream} type.
     * <p>It is important to note that the request method must be {@link RequestMethod#PUT},
     * {@link RequestMethod#POST}, {@link RequestMethod#PATCH} in one of them.</p>
     *
     * @param requestBody There can be a file, pictures, any other data flow.You don't need to close it, CoreConfig when
     *                    complete request will be automatically closed.
     * @param contentType such as: "{@value Headers#HEAD_VALUE_CONTENT_TYPE_XML}{@code ; charset=utf-8}",
     *                    "{@value Headers#HEAD_VALUE_CONTENT_TYPE_JSON}{@code ; charset=utf-8}" or "{@value
     *                    Headers#HEAD_VALUE_CONTENT_TYPE_FORM_DATA}". Note, does not need to include quotation
     *                    marks.
     * @return {@link Child}.
     * @see #setDefineRequestBody(String, String)
     * @see #setDefineRequestBodyForJson(JSONObject)
     * @see #setDefineRequestBodyForJson(String)
     * @see #setDefineRequestBodyForXML(String)
     */
    Child setDefineRequestBody(InputStream requestBody, String contentType);


    /**
     * Sets the request body and content type.
     * <p>It is important to note that the request method must be {@link RequestMethod#PUT},
     * {@link RequestMethod#POST}, {@link RequestMethod#PATCH} in one of them.</p>
     *
     * @param requestBody string body.
     * @param contentType such as: "{@value Headers#HEAD_VALUE_CONTENT_TYPE_JSON}" or "{@value
     *                    Headers#HEAD_VALUE_CONTENT_TYPE_XML}". Note, does not need to include quotation marks.
     *                    <p>If ContentType parameter into "" or null, the default for the {@value
     *                    Headers#HEAD_VALUE_CONTENT_TYPE_JSON}.</p>
     * @return {@link Child}.
     * @see #setDefineRequestBody(InputStream, String)
     * @see #setDefineRequestBodyForJson(JSONObject)
     * @see #setDefineRequestBodyForJson(String)
     * @see #setDefineRequestBodyForXML(String)
     */
    Child setDefineRequestBody(String requestBody, String contentType);

    /**
     * Set the request json body.
     * <p>It is important to note that the request method must be {@link RequestMethod#PUT},
     * {@link RequestMethod#POST}, {@link RequestMethod#PATCH} in one of them.</p>
     * <p>The content type is {@value Headers#HEAD_VALUE_CONTENT_TYPE_JSON}</p>
     *
     * @param jsonBody json body.
     * @return {@link Child}.
     * @see #setDefineRequestBody(InputStream, String)
     * @see #setDefineRequestBody(String, String)
     * @see #setDefineRequestBodyForJson(JSONObject)
     * @see #setDefineRequestBodyForXML(String)
     */
    Child setDefineRequestBodyForJson(String jsonBody);

    /**
     * Set the request json body.
     * <p>It is important to note that the request method must be {@link RequestMethod#PUT},
     * {@link RequestMethod#POST}, {@link RequestMethod#PATCH} in one of them.</p>
     * <p>The content type is {@value Headers#HEAD_VALUE_CONTENT_TYPE_JSON}</p>
     *
     * @param jsonBody json body.
     * @return {@link Child}.
     * @see #setDefineRequestBody(InputStream, String)
     * @see #setDefineRequestBody(String, String)
     * @see #setDefineRequestBodyForJson(String)
     * @see #setDefineRequestBodyForXML(String)
     */
    Child setDefineRequestBodyForJson(JSONObject jsonBody);

    /**
     * Set the request XML body.
     * <p>It is important to note that the request method must be {@link RequestMethod#PUT},
     * {@link RequestMethod#POST}, {@link RequestMethod#PATCH} in one of them.</p>
     * <p>The content type is {@value Headers#HEAD_VALUE_CONTENT_TYPE_XML}</p>
     *
     * @param xmlBody xml body.
     * @return {@link Child}.
     * @see #setDefineRequestBody(InputStream, String)
     * @see #setDefineRequestBody(String, String)
     * @see #setDefineRequestBody(String, String)
     * @see #setDefineRequestBodyForJson(String)
     */
    Child setDefineRequestBodyForXML(String xmlBody);

    /**
     * Sets redirect interface.
     *
     * @param redirectHandler {@link RedirectHandler}.
     * @return {@link Child}.
     */
    Child setRedirectHandler(RedirectHandler redirectHandler);

    /**
     * Set tag of task, At the end of the task is returned to you.
     *
     * @param tag {@link Object}.
     * @return {@link Child}.
     */
    Child setTag(Object tag);

}
