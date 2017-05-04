package com.yanzhenjie.nohttp.download;

/**
 * Created by Yan Zhenjie on 2017/5/4.
 */
public interface Request {

    /**
     * Prepare the callback parameter, while waiting for the response callback with thread.
     *
     * @param what             the callback mark.
     * @param downloadListener {@link DownloadListener}.
     */
    void onPreResponse(int what, DownloadListener downloadListener);

    /**
     * The callback mark.
     *
     * @return Return when {@link #onPreResponse(int, DownloadListener)} incoming credit.
     * @see #onPreResponse(int, DownloadListener)
     */
    int what();

    /**
     * The request of the listener.
     *
     * @return Return when {@link #onPreResponse(int, DownloadListener)} incoming credit.
     * @see #onPreResponse(int, DownloadListener)
     */
    DownloadListener downloadListener();

}
