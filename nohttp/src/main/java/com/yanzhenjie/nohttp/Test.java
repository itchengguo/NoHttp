package com.yanzhenjie.nohttp;

import android.graphics.Bitmap;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Yan Zhenjie on 2017/5/4.
 */
public class Test {

    /**
     * Create a queue of request, the default thread pool size is 3.
     *
     * @return returns the request queue, the queue is used to control the entry of the request.
     * @see #newRequestQueue(int)
     */
    public static RequestQueue newRequestQueue() {
        return newRequestQueue(3);
    }

    /**
     * Create a queue of request.
     *
     * @param threadPoolSize request the number of concurrent.
     * @return returns the request queue, the queue is used to control the entry of the request.
     * @see #newRequestQueue()
     */
    public static RequestQueue newRequestQueue(int threadPoolSize) {
        RequestQueue requestQueue = new RequestQueue(threadPoolSize);
        requestQueue.start();
        return requestQueue;
    }

    /**
     * Create a String type request, the request method is {@link RequestMethod#GET}.
     *
     * @param url such as: {@code http://www.google.com}.
     * @return {@code Request<String>}.
     * @see #createStringRequest(String, RequestMethod)
     */
    public static Request<String> createStringRequest(String url) {
        return new StringRequest(url);
    }

    /**
     * Create a String type request, custom request method, method from {@link RequestMethod}.
     *
     * @param url           such as: {@code http://www.google.com}.
     * @param requestMethod {@link RequestMethod}.
     * @return {@code Request<String>}.
     * @see #createStringRequest(String)
     */
    public static Request<String> createStringRequest(String url, RequestMethod requestMethod) {
        return new StringRequest(url, requestMethod);
    }

    /**
     * Create a JSONObject type request, the request method is {@link RequestMethod#GET}.
     *
     * @param url such as: {@code http://www.google.com}.
     * @return {@code Request<JSONObject>}.
     * @see #createJsonObjectRequest(String, RequestMethod)
     */
    public static Request<JSONObject> createJsonObjectRequest(String url) {
        return new JsonObjectRequest(url);
    }

    /**
     * Create a JSONObject type request, custom request method, method from {@link RequestMethod}.
     *
     * @param url           such as: {@code http://www.google.com}.
     * @param requestMethod {@link RequestMethod}.
     * @return {@code Request<JSONObject>}.
     * @see #createJsonObjectRequest(String)
     */
    public static Request<JSONObject> createJsonObjectRequest(String url, RequestMethod requestMethod) {
        return new JsonObjectRequest(url, requestMethod);
    }

    /**
     * Create a JSONArray type request, the request method is {@link RequestMethod#GET}.
     *
     * @param url such as: {@code http://www.google.com}.
     * @return {@code Request<JSONArray>}.
     * @see #createJsonArrayRequest(String, RequestMethod)
     */
    public static Request<JSONArray> createJsonArrayRequest(String url) {
        return new JsonArrayRequest(url);
    }

    /**
     * Create a JSONArray type request, custom request method, method from {@link RequestMethod}.
     *
     * @param url           such as: {@code http://www.google.com}.
     * @param requestMethod {@link RequestMethod}.
     * @return {@code Request<JSONArray>}.
     * @see #createJsonArrayRequest(String)
     */
    public static Request<JSONArray> createJsonArrayRequest(String url, RequestMethod requestMethod) {
        return new JsonArrayRequest(url, requestMethod);
    }

    /**
     * Create a Image type request, the request method is {@link RequestMethod#GET}.
     *
     * @param url such as: {@code http://www.google.com}.
     * @return {@code Request<Bitmap>}.
     * @see #createImageRequest(String, RequestMethod)
     * @see #createImageRequest(String, RequestMethod, int, int, Bitmap.Config, ImageView.ScaleType)
     */
    public static Request<Bitmap> createImageRequest(String url) {
        return createImageRequest(url, RequestMethod.GET);
    }

    /**
     * Create a Image type request.
     *
     * @param url           such as: {@code http://www.google.com}.
     * @param requestMethod {@link RequestMethod}.
     * @return {@code Request<Bitmap>}.
     * @see #createImageRequest(String)
     * @see #createImageRequest(String, RequestMethod, int, int, Bitmap.Config, ImageView.ScaleType)
     */
    public static Request<Bitmap> createImageRequest(String url, RequestMethod requestMethod) {
        return createImageRequest(url, requestMethod, 1000, 1000, Bitmap.Config.ARGB_8888, ImageView.ScaleType
                .CENTER_INSIDE);
    }

    /**
     * Create a Image type request.
     *
     * @param url           such as: {@code http://www.google.com}.
     * @param requestMethod {@link RequestMethod}.
     * @param maxWidth      width.
     * @param maxHeight     height.
     * @param config        config.
     * @param scaleType     scaleType.
     * @return {@code Request<Bitmap>}.
     * @see #createImageRequest(String)
     * @see #createImageRequest(String, RequestMethod)
     */
    public static Request<Bitmap> createImageRequest(String url, RequestMethod requestMethod, int maxWidth, int
            maxHeight, Bitmap.Config config, ImageView.ScaleType scaleType) {
        return new ImageRequest(url, requestMethod, maxWidth, maxHeight, config, scaleType);
    }

    /**
     * Create a byte array request, the request method is {@link RequestMethod#GET}.
     *
     * @param url url.
     * @return {@code Request<byte[]>}.
     * @see #createByteArrayRequest(String, RequestMethod)
     */
    public static Request<byte[]> createByteArrayRequest(String url) {
        return new ByteArrayRequest(url);
    }

    /**
     * Create a byte array request.
     *
     * @param url    url.
     * @param method {@link RequestMethod}.
     * @return {@code Request<byte[]>}.
     * @see #createByteArrayRequest(String)
     */
    public static Request<byte[]> createByteArrayRequest(String url, RequestMethod method) {
        return new ByteArrayRequest(url, method);
    }

    /**
     * Create a new download queue, the default thread pool size is 3.
     *
     * @return {@link DownloadQueue}.
     * @see #newDownloadQueue(int)
     */
    public static DownloadQueue newDownloadQueue() {
        return newDownloadQueue(3);
    }

    /**
     * Create a new download queue.
     *
     * @param threadPoolSize thread pool number, here is the number of concurrent tasks.
     * @return {@link DownloadQueue}.
     * @see #newDownloadQueue()
     */
    public static DownloadQueue newDownloadQueue(int threadPoolSize) {
        DownloadQueue downloadQueue = new DownloadQueue(threadPoolSize);
        downloadQueue.start();
        return downloadQueue;
    }

    /**
     * Default thread pool size for request queue.
     */
    private static RequestQueue sRequestQueueInstance;

    /**
     * Default thread pool size for request queue.
     */
    private static DownloadQueue sDownloadQueueInstance;

    /**
     * Get default RequestQueue.
     *
     * @return {@link RequestQueue}.
     */
    public static RequestQueue getRequestQueueInstance() {
        if (sRequestQueueInstance == null)
            synchronized (NoHttp.class) {
                if (sRequestQueueInstance == null) {
                    sRequestQueueInstance = newRequestQueue();
                }
            }
        return sRequestQueueInstance;
    }

    /**
     * Get default DownloadQueue.
     *
     * @return {@link DownloadQueue}.
     */
    public static DownloadQueue getDownloadQueueInstance() {
        if (sDownloadQueueInstance == null)
            synchronized (NoHttp.class) {
                if (sDownloadQueueInstance == null) {
                    sDownloadQueueInstance = newDownloadQueue();
                }
            }
        return sDownloadQueueInstance;
    }
}
