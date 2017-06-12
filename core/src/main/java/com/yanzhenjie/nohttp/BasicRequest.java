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
package com.yanzhenjie.nohttp;

import android.text.TextUtils;

import com.yanzhenjie.nohttp.able.Cancelable;
import com.yanzhenjie.nohttp.able.Startable;
import com.yanzhenjie.nohttp.body.BodyWriter;
import com.yanzhenjie.nohttp.tools.CounterOutputStream;
import com.yanzhenjie.nohttp.tools.HeaderUtils;
import com.yanzhenjie.nohttp.tools.IOUtils;
import com.yanzhenjie.nohttp.tools.LinkedMultiValueMap;
import com.yanzhenjie.nohttp.tools.MultiValueMap;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpCookie;
import java.net.Proxy;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

/**
 * <p>
 * Implement the http protocol.
 * </p>
 * Created by Yan Zhenjie on Nov 4, 2015.
 */
public abstract class BasicRequest<Child extends BasicRequest> implements IClientRequest<Child>, Startable, Cancelable {

    /**
     * Target address.
     */
    private String mBaseUrl;
    /**
     * Request method.
     */
    private RequestMethod mRequestMethod;
    /**
     * RequestBody wrapper.
     */
    private BodyWriter mBodyWriter;
    /**
     * Proxy server.
     */
    private Proxy mProxy;
    /**
     * SSLSockets.
     */
    private SSLSocketFactory mSSLSocketFactory = null;
    /**
     * HostnameVerifier.
     */
    private HostnameVerifier mHostnameVerifier = null;
    /**
     * Connect timeout of request.
     */
    private int mConnectTimeout = NoHttp.getCoreConfig().getConnectTimeout();
    /**
     * Read data timeout.
     */
    private int mReadTimeout = NoHttp.getCoreConfig().getReadTimeout();
    /**
     * Request heads.
     */
    private Headers mHeaders = new Headers();
    /**
     * After the failure of retries.
     */
    private int mRetryCount;
    /**
     * The params encoding.
     */
    private String mParamEncoding = "UTF-8";
    /**
     * Param collection.
     */
    private MultiValueMap<String, Object> mParamKeyValues;
    /**
     * Redirect handler.
     */
    private RedirectHandler mRedirectHandler;
    /**
     * The record has started.
     */
    private boolean isStart = false;
    /**
     * Has been canceled.
     */
    private boolean isCanceled = false;
    /**
     * Tag of request.
     */
    private Object mTag;

    /**
     * Create a request, RequestMethod is {@link RequestMethod#GET}.
     *
     * @param url request address, like: http://www.yanzhenjie.com.
     */
    public BasicRequest(String url) {
        this(url, RequestMethod.GET);
    }

    /**
     * Create a request.
     *
     * @param url           request adress, like: http://www.yanzhenjie.com.
     * @param requestMethod request method, like {@link RequestMethod#GET}, {@link RequestMethod#POST}.
     */
    public BasicRequest(String url, RequestMethod requestMethod) {
        this.mBaseUrl = url;
        mRequestMethod = requestMethod;

        mHeaders = new Headers();
        mHeaders.set(Headers.HEAD_KEY_ACCEPT, Headers.HEAD_VALUE_ACCEPT_ALL);
        mHeaders.set(Headers.HEAD_KEY_ACCEPT_ENCODING, Headers.HEAD_VALUE_ACCEPT_ENCODING_GZIP_DEFLATE);
        mHeaders.set(Headers.HEAD_KEY_ACCEPT_LANGUAGE, HeaderUtils.systemAcceptLanguage());
        mHeaders.set(Headers.HEAD_KEY_USER_AGENT, UserAgent.instance());

        mParamKeyValues = new LinkedMultiValueMap<>();
    }

    /**
     * Return mBaseUrl of request.
     *
     * @return Url.
     */
    public String url() {
        StringBuilder urlBuilder = new StringBuilder(mBaseUrl);
        // first body.
        if (hasDefineRequestBody()) {
            buildUrl(urlBuilder);
            return urlBuilder.toString();
        }
        // form or push params.
        if (getRequestMethod().allowRequestBody())
            return urlBuilder.toString();

        // third common post.
        buildUrl(urlBuilder);
        return urlBuilder.toString();
    }

    /**
     * Build complete mBaseUrl.
     *
     * @param urlBuilder mBaseUrl StringBuilder.
     */
    private void buildUrl(StringBuilder urlBuilder) {
        StringBuilder paramBuilder = buildCommonParams(getParamKeyValues(), getParamsEncoding());
        if (paramBuilder.length() <= 0) return;
        if (mBaseUrl.contains("?") && mBaseUrl.contains("=")) urlBuilder.append("&");
        else if (!mBaseUrl.endsWith("?")) urlBuilder.append("?");
        urlBuilder.append(paramBuilder);
    }

    /**
     * return method of request.
     *
     * @return {@link RequestMethod}.
     */
    public RequestMethod getRequestMethod() {
        return mRequestMethod;
    }

    /**
     * Is MultipartFormEnable ?
     * <p>MultipartFormEnable is request method is the premise of the POST/PUT/PATCH/DELETE, but the Android system
     * under API level 19 does not support the DELETE.</p>
     *
     * @return true enable, other wise false.
     */
    public boolean isFormDataEnable() {
        return hasBinary();
    }

    @Override
    public Child setProxy(Proxy proxy) {
        this.mProxy = proxy;
        return (Child) this;
    }

    /**
     * Get proxy server.
     *
     * @return Can use {@code Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("64.233.162.83", 80));}.
     */
    public Proxy getProxy() {
        return mProxy;
    }

    @Override
    public Child setSSLSocketFactory(SSLSocketFactory socketFactory) {
        mSSLSocketFactory = socketFactory;
        return (Child) this;
    }

    /**
     * Get SSLSocketFactory.
     *
     * @return {@link SSLSocketFactory}.
     */
    public SSLSocketFactory getSSLSocketFactory() {
        return mSSLSocketFactory;
    }

    @Override
    public Child setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        mHostnameVerifier = hostnameVerifier;
        return (Child) this;
    }

    /**
     * Get the HostnameVerifier.
     *
     * @return {@link HostnameVerifier}.
     */
    public HostnameVerifier getHostnameVerifier() {
        return mHostnameVerifier;
    }

    @Override
    public Child setConnectTimeout(int connectTimeout) {
        mConnectTimeout = connectTimeout;
        return (Child) this;
    }

    /**
     * Get the connection timeout time, Unit is a millisecond.
     *
     * @return Connection timeout.
     */
    public int getConnectTimeout() {
        return mConnectTimeout;
    }

    @Override
    public Child setReadTimeout(int readTimeout) {
        mReadTimeout = readTimeout;
        return (Child) this;
    }

    /**
     * Get the read timeout time, Unit is a millisecond.
     *
     * @return Read timeout.
     */
    public int getReadTimeout() {
        return mReadTimeout;
    }

    @Override
    public Child addHeader(String key, String value) {
        mHeaders.add(key, value);
        return (Child) this;
    }

    @Override
    public Child setHeader(String key, String value) {
        mHeaders.set(key, value);
        return (Child) this;
    }

    @Override
    public Child addHeader(HttpCookie cookie) {
        if (cookie != null)
            mHeaders.add(Headers.HEAD_KEY_COOKIE, cookie.getName() + "=" + cookie.getValue());
        return (Child) this;
    }

    @Override
    public Child removeHeader(String key) {
        mHeaders.remove(key);
        return (Child) this;
    }

    @Override
    public Child removeAllHeader() {
        mHeaders.clear();
        return (Child) this;
    }

    /**
     * Get all Heads.
     *
     * @return {@code Headers}.
     */
    public Headers headers() {
        return mHeaders;
    }

    @Override
    public Child setAccept(String accept) {
        mHeaders.set(Headers.HEAD_KEY_ACCEPT, accept);
        return (Child) this;
    }

    @Override
    public Child setAcceptLanguage(String acceptLanguage) {
        mHeaders.set(Headers.HEAD_KEY_ACCEPT_LANGUAGE, acceptLanguage);
        return (Child) this;
    }

    /**
     * The contentLength of the request body.
     *
     * @return Such as: {@code 2048}.
     */
    public long getContentLength() {
        CounterOutputStream outputStream = new CounterOutputStream();
        try {
            onWriteRequestBody(outputStream);
        } catch (IOException e) {
            Logger.e(e);
        }
        return outputStream.get();
    }

    @Override
    public Child setContentType(String contentType) {
        mHeaders.set(Headers.HEAD_KEY_CONTENT_TYPE, contentType);
        return (Child) this;
    }

    /**
     * Get {@value Headers#HEAD_KEY_CONTENT_TYPE}.
     *
     * @return string, such as: {@value Headers#HEAD_VALUE_CONTENT_TYPE_JSON}.
     */
    public String getContentType() {
        String contentType = mHeaders.getValue(Headers.HEAD_KEY_CONTENT_TYPE, 0);
        if (!TextUtils.isEmpty(contentType))
            return contentType;
        if (getRequestMethod().allowRequestBody() && isFormDataEnable())
            return Headers.HEAD_VALUE_CONTENT_TYPE_FORM_DATA + "; boundary=" + boundary;
        else
            return Headers.HEAD_VALUE_CONTENT_TYPE_URL + "; charset=" + getParamsEncoding();
    }

    @Override
    public Child setUserAgent(String userAgent) {
        mHeaders.set(Headers.HEAD_KEY_USER_AGENT, userAgent);
        return (Child) this;
    }

    @Override
    public Child setRetryCount(int count) {
        this.mRetryCount = count;
        return (Child) this;
    }

    /**
     * To getList the failure after retries.
     *
     * @return The default value is 0.
     */
    public int getRetryCount() {
        return mRetryCount;
    }

    @Override
    public Child setParamsEncoding(String encoding) {
        this.mParamEncoding = encoding;
        return (Child) this;
    }

    /**
     * Get the params encoding.
     *
     * @return such as {@code "utf-8, gbk, bg2312"}.
     */
    public String getParamsEncoding() {
        return mParamEncoding;
    }

    @Override
    public Child path(int path) {
        return path(Integer.toString(path));
    }

    @Override
    public Child path(long path) {
        return path(Long.toString(path));
    }

    @Override
    public Child path(short path) {
        return path(Integer.toString(path));
    }

    @Override
    public Child path(float path) {
        return path(Float.toString(path));
    }

    @Override
    public Child path(double path) {
        return path(Double.toString(path));
    }

    @Override
    public Child path(boolean path) {
        return path(Boolean.toString(path));
    }

    @Override
    public Child path(char path) {
        return path(String.valueOf(path));
    }

    @Override
    public Child path(byte path) {
        return path(Integer.toString(path));
    }

    @Override
    public Child path(String path) {
        mBaseUrl += mBaseUrl.endsWith("/") ? path : ("/" + path);
        return (Child) this;
    }

    @Override
    public Child add(String key, int value) {
        return add(key, Integer.toString(value));
    }

    @Override
    public Child add(String key, long value) {
        return add(key, Long.toString(value));
    }

    @Override
    public Child add(String key, boolean value) {
        return add(key, Boolean.toString(value));
    }

    @Override
    public Child add(String key, char value) {
        return add(key, String.valueOf(value));
    }

    @Override
    public Child add(String key, double value) {
        return add(key, Double.toString(value));
    }

    @Override
    public Child add(String key, float value) {
        return add(key, Float.toString(value));
    }

    @Override
    public Child add(String key, short value) {
        add(key, Integer.toString(value));
        return (Child) this;
    }

    @Override
    public Child add(String key, byte value) {
        return add(key, Integer.toString(value));
    }

    @Override
    public Child add(String key, String value) {
        value = value == null ? "" : value;
        mParamKeyValues.add(key, value);
        return (Child) this;
    }

    @Override
    public Child set(String key, String value) {
        value = value == null ? "" : value;
        mParamKeyValues.set(key, value);
        return (Child) this;
    }

    @Override
    public Child add(String key, Binary binary) {
        validateMethodForBody("The Binary param");
        mParamKeyValues.add(key, binary);
        return (Child) this;
    }

    @Override
    public Child set(String key, Binary binary) {
        validateMethodForBody("The Binary param");
        mParamKeyValues.set(key, binary);
        return (Child) this;
    }

    @Override
    public Child add(String key, File file) {
        validateMethodForBody("The File param");
        add(key, new FileBinary(file));
        return (Child) this;
    }

    @Override
    public Child set(String key, File file) {
        validateMethodForBody("The File param");
        set(key, new FileBinary(file));
        return (Child) this;
    }

    @Override
    public Child add(String key, List<Binary> binaries) {
        validateMethodForBody("The List<Binary> param");
        if (binaries != null) {
            for (Binary binary : binaries)
                mParamKeyValues.add(key, binary);
        }
        return (Child) this;
    }

    @Override
    public Child set(String key, List<Binary> binaries) {
        validateMethodForBody("The List<Binary> param");
        mParamKeyValues.remove(key);
        add(key, binaries);
        return (Child) this;
    }

    @Override
    public Child add(Map<String, String> params) {
        if (params != null) {
            for (Map.Entry<String, String> stringEntry : params.entrySet())
                add(stringEntry.getKey(), stringEntry.getValue());
        }
        return (Child) this;
    }

    @Override
    public Child set(Map<String, String> params) {
        if (params != null) {
            for (Map.Entry<String, String> stringEntry : params.entrySet())
                set(stringEntry.getKey(), stringEntry.getValue());
        }
        return (Child) this;
    }

    @Override
    public Child remove(String key) {
        mParamKeyValues.remove(key);
        return (Child) this;
    }

    @Override
    public Child removeAll() {
        mParamKeyValues.clear();
        return (Child) this;
    }

    /**
     * Get the parameters of key-value pairs.
     *
     * @return Not empty Map.
     */
    public MultiValueMap<String, Object> getParamKeyValues() {
        return mParamKeyValues;
    }

    /**
     * Validate param null.
     *
     * @param body        request body.
     * @param contentType content type.
     */
    private void validateParamForBody(Object body, String contentType) {
        if (body == null || TextUtils.isEmpty(contentType))
            throw new NullPointerException("The requestBody and contentType must be can't be null");
    }

    @Override
    public Child setDefineRequestBody(InputStream requestBody, String contentType) {
        validateMethodForBody("Request body");
        validateParamForBody(requestBody, contentType);
        if (requestBody instanceof ByteArrayInputStream || requestBody instanceof FileInputStream) {
            this.mRequestBody = requestBody;
            mHeaders.set(Headers.HEAD_KEY_CONTENT_TYPE, contentType);
        } else {
            throw new IllegalArgumentException("Can only accept ByteArrayInputStream and FileInputStream type of " +
                    "stream");
        }
        return (Child) this;
    }

    @Override
    public Child setDefineRequestBody(String requestBody, String contentType) {
        validateMethodForBody("Request body");
        validateParamForBody(requestBody, contentType);
        try {
            mRequestBody = IOUtils.toInputStream(requestBody, getParamsEncoding());
            mHeaders.set(Headers.HEAD_KEY_CONTENT_TYPE, contentType + "; charset=" + getParamsEncoding());
        } catch (UnsupportedEncodingException e) {
            mRequestBody = IOUtils.toInputStream(requestBody);
            mHeaders.set(Headers.HEAD_KEY_CONTENT_TYPE, contentType);
        }
        return (Child) this;
    }

    @Override
    public Child setDefineRequestBodyForJson(String jsonBody) {
        setDefineRequestBody(jsonBody, Headers.HEAD_VALUE_CONTENT_TYPE_JSON);
        return (Child) this;
    }

    @Override
    public Child setDefineRequestBodyForJson(JSONObject jsonBody) {
        setDefineRequestBody(jsonBody.toString(), Headers.HEAD_VALUE_CONTENT_TYPE_JSON);
        return (Child) this;
    }

    @Override
    public Child setDefineRequestBodyForXML(String xmlBody) {
        setDefineRequestBody(xmlBody, Headers.HEAD_VALUE_CONTENT_TYPE_XML);
        return (Child) this;
    }

    /**
     * Has Binary.
     *
     * @return true, other wise is false.
     */
    protected boolean hasBinary() {
        Set<String> keys = mParamKeyValues.keySet();
        for (String key : keys) {
            List<Object> values = mParamKeyValues.getValues(key);
            for (Object value : values) {
                if (value instanceof Binary)
                    return true;
            }
        }
        return false;
    }

    /**
     * Is there a custom request inclusions.
     *
     * @return Returns true representatives have, return false on behalf of the no.
     */
    protected boolean hasDefineRequestBody() {
        return mRequestBody != null;
    }

    /**
     * To getList custom inclusions.
     *
     * @return {@link InputStream}.
     */
    protected InputStream getDefineRequestBody() {
        return mRequestBody;
    }

    /**
     * Call before carry out the request, you can do some preparation work.
     */
    public void onPreExecute() {
        // Do some time-consuming operation.
    }

    /**
     * Send request body data.
     *
     * @param writer {@link OutputStream}.
     * @throws IOException add error.
     */
    public void onWriteRequestBody(OutputStream writer) throws IOException {
    }

    @Override
    public Child setRedirectHandler(RedirectHandler redirectHandler) {
        mRedirectHandler = redirectHandler;
        return (Child) this;
    }

    /**
     * Get the redirect handler.
     *
     * @return {@link RedirectHandler}.
     */
    public RedirectHandler getRedirectHandler() {
        return mRedirectHandler;
    }

    @Override
    public Child setTag(Object tag) {
        this.mTag = tag;
        return (Child) this;
    }

    /**
     * Should to return the tag of the object.
     *
     * @return {@link Object}.
     */
    public Object getTag() {
        return this.mTag;
    }

    @Override
    public void start() {
        this.isStart = true;
    }

    @Override
    public boolean isStarted() {
        return isStart;
    }

    @Override
    public void cancel() {
        this.isCanceled = true;
    }

    @Override
    public boolean isCanceled() {
        return isCanceled;
    }

    ////////// static module /////////

    /**
     * Split joint non form data.
     *
     * @param paramMap      param map.
     * @param encodeCharset charset.
     * @return string parameter combination, each key value on nails with {@code "&"} space.
     */
    public static StringBuilder buildCommonParams(MultiValueMap<String, Object> paramMap, String encodeCharset) {
        StringBuilder paramBuilder = new StringBuilder();
        Set<String> keySet = paramMap.keySet();
        for (String key : keySet) {
            List<Object> values = paramMap.getValues(key);
            for (Object value : values) {
                if (value != null && value instanceof CharSequence) {
                    paramBuilder.append("&").append(key).append("=");
                    try {
                        paramBuilder.append(URLEncoder.encode(value.toString(), encodeCharset));
                    } catch (UnsupportedEncodingException e) {
                        Logger.e("Encoding " + encodeCharset + " format is not supported by the system.");
                        paramBuilder.append(value.toString());
                    }
                }
            }
        }
        if (paramBuilder.length() > 0)
            paramBuilder.deleteCharAt(0);
        return paramBuilder;
    }

    /**
     * Randomly generated boundary mark.
     *
     * @return Random code.
     */
    public static String createBoundary() {
        StringBuilder sb = new StringBuilder("----NoHttpFormBoundary");
        for (int t = 1; t < 12; t++) {
            long time = System.currentTimeMillis() + t;
            if (time % 3L == 0L) {
                sb.append((char) (int) time % '\t');
            } else if (time % 3L == 1L) {
                sb.append((char) (int) (65L + time % 26L));
            } else {
                sb.append((char) (int) (97L + time % 26L));
            }
        }
        return sb.toString();
    }

}