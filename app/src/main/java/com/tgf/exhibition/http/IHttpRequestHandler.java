package com.tgf.exhibition.http;

import com.loopj.android.http.ResponseHandlerInterface;

/**
 * Created by jeff on 2016/5/19.
 */
public interface IHttpRequestHandler {
    /**
     * 执行/发起一个Http请求
     */
    void doHttpRequest(ResponseHandlerInterface responseHandlerInterface);

    void cancelAllHttpRequests(boolean mayInterruptIfRunning);
}
