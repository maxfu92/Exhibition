package com.tgf.exhibition.http;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.net.Proxy;
import android.text.TextUtils;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

import java.util.LinkedList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpHost;

/**
 * Created by jeff on 2016/5/19.
 */
public abstract class AbsHttpRequestHandler extends RequestParamKeys implements IHttpRequestHandler {
    private static final int TIME_OUT = 30 * 1000;
    private static final int MAX_RETRIES = 20;
    private static final int RETRY_SLEEP_TIME_MS = 2 * 1000;

    private static final String PROTOCOL_HTTP = "http://";
    private static final String PROTOCOL_HTTPS = "https://";
    protected static final String PROTOCOL = PROTOCOL_HTTP;

//    protected static final String API_HOST = "kcwc.zhongming.i.ubolixin.com";
    protected static final String API_HOST = "www.51kcwc.com";
    protected static final String XCAPPLY_URL_PATH = "/api/xcapply";

    private final AsyncHttpClient mAsyncHttpClient = new AsyncHttpClient();

    ////// Activity/Service生命周期内范围内有效
    protected final Context mContext;
    private final List<RequestHandle> requestHandles = new LinkedList<RequestHandle>();

    protected final RequestParams mRequestParams = new RequestParams();

    public AbsHttpRequestHandler(Activity activity) {
        mContext = activity;
        mAsyncHttpClient.setTimeout(TIME_OUT);
        mAsyncHttpClient.setMaxRetriesAndTimeout(MAX_RETRIES, RETRY_SLEEP_TIME_MS);
        String sysProxyHost = Proxy.getDefaultHost();
        int systeProxyPort = Proxy.getDefaultPort();
        if(!TextUtils.isEmpty(sysProxyHost) && systeProxyPort != -1) {
            mAsyncHttpClient.setProxy(sysProxyHost, systeProxyPort);
        }
    }

    public AbsHttpRequestHandler(Service service) {
        mContext = service;
    }

    public Context getContext() {
        return mContext;
    }

    public AsyncHttpClient getmAsyncHttpClient() {
        return mAsyncHttpClient;
    }

    public final <T extends IHttpRequestHandler> T putParam(String key, String value) {
        mRequestParams.put(key, value);
        return (T) this;
    }

    private void addRequestHandle(RequestHandle handle) {
        if (null != handle) {
            requestHandles.add(handle);
        }
    }

    @Override
    public void cancelAllHttpRequests(boolean mayInterruptIfRunning) {
        if(requestHandles.size() > 0) {
            mAsyncHttpClient.cancelAllRequests(mayInterruptIfRunning);
            requestHandles.clear();
        }
    }

    @Override
    public void doHttpRequest(ResponseHandlerInterface responseHandlerInterface) {
        addRequestHandle(onDoHttpRequest(
                    mAsyncHttpClient,
                    buildRequestUrlString(),
                    buildRequestHeaders(),
                    buildRequestParamsInternal(),
                    buildRequestEntity(),
                    responseHandlerInterface));
    }

    private final RequestParams buildRequestParamsInternal() {
        RequestParams rp = buildRequestParams();
        if(rp != null) {
            rp.put(PARAM_KEY_REQ_FROM, "app");
            rp.put("slog_force_client_id", "slog_beb3be");
        }
        return rp;
    }

    protected RequestParams buildRequestParams() {
        return mRequestParams;
    }

    private Header[] buildRequestHeaders() {
        List<Header> headers = buildRequestHeadersList();
        return headers==null?null:headers.toArray(new Header[headers.size()]);
    }

    /**
     * Http请求中 Header 的工厂方法，子类根据具体需求实现，默认返回 null
     * @return
     */
    protected List<Header> buildRequestHeadersList() {
        return null;
    }

    /**
     * Http请求中 HttpEntity 的工厂方法，子类根据具体需求实现，默认返回 null
     * @return
     */
    protected HttpEntity buildRequestEntity() {
        return null;
    }

    public static String buildUrlStringWithPrefix(String urlPath) {
        return PROTOCOL + API_HOST + (urlPath.startsWith("/") ? urlPath : "/" + urlPath);
    }

    /**
     * Http请求的具体URL地址字符串
     * @return
     */
    abstract protected String buildRequestUrlString();

    /**
     * 在该方法中实现AsyncHttpClient的具体请求（Get/Put/Post/Delete/Patch）方法
     * @param client
     * @param URL
     * @param headers
     * @param entity
     * @param responseHandler
     * @return
     */
    protected RequestHandle onDoHttpRequest(AsyncHttpClient client, String URL, Header[] headers, RequestParams params, HttpEntity entity, ResponseHandlerInterface responseHandler) {
        Log.i("HttpRequest", "URL: " + URL);
        Log.i("HttpRequest", "Params: " + params.toString());
        return client.post(mContext, URL, params, responseHandler);
    }
}
