package com.yanzhenjie.nohttp.rx.request;

import com.yanzhenjie.nohttp.Headers;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.ProtocolRequest;
import com.yanzhenjie.nohttp.tools.HeaderUtils;
import com.yanzhenjie.nohttp.tools.IOUtils;

/**
 * Created by Yan Zhenjie on 2017/5/10.
 */
public class StringRequest extends ProtocolRequest<String> {

    public StringRequest(String url) {
        this(url, RequestMethod.GET);
    }

    public StringRequest(String url, RequestMethod requestMethod) {
        super(url, requestMethod);
    }

    @Override
    public String parseResponse(Headers responseHeaders, byte[] responseBody) throws Exception {
        return parseResponseString(responseHeaders, responseBody);
    }

    /**
     * Parse http response to string.
     *
     * @param responseHeaders header from http response.
     * @param responseBody    byteArray from http response.
     * @return result fro response.
     */
    public static String parseResponseString(Headers responseHeaders, byte[] responseBody) {
        if (responseBody == null || responseBody.length == 0)
            return "";
        String charset = HeaderUtils.parseHeadValue(responseHeaders.getContentType(), "charset", "");
        return IOUtils.toString(responseBody, charset);
    }
}
