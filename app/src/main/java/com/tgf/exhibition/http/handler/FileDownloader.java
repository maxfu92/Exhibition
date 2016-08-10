package com.tgf.exhibition.http.handler;

import android.app.Service;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;
import com.tgf.exhibition.http.AbsHttpRequestHandler;
import com.tgf.exhibition.util.DebugLogger;

import java.io.File;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;

/**
 * Created by jeff on 2016/5/22.
 */
public final class FileDownloader  extends AbsHttpRequestHandler {
    private final String mDownloadUrl;

    private FileAsyncHttpResponseHandler mFileAsyncHttpResponseHandler;

    public static interface FileDownloadListener {
        void onDownloadFinished(File storedFile);
        void onDownloadFailure(File storedFile);
        void onDownloadProgress(long bytesWritten, long totalSize);
        void onDownloadStart();
        void onDownloadFinish();
    }
    private FileDownloadListener mFileDownloadListener;

    public FileDownloader(Service service, String downloadUrl, File saveTo) {
        super(service);
        mDownloadUrl = downloadUrl;
        mFileAsyncHttpResponseHandler = new FileAsyncHttpResponseHandler(saveTo) {
            private static final String LOG_TAG = "HttpResponse";
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File storedFile) {
                DebugLogger.debugHeaders(LOG_TAG, headers);
                DebugLogger.debugStatusCode(LOG_TAG, statusCode);
                DebugLogger.debugThrowable(LOG_TAG, throwable);
                DebugLogger.debugFile(LOG_TAG, storedFile);
                if(mFileDownloadListener != null) {
                    mFileDownloadListener.onDownloadFailure(storedFile);
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File storedFile) {
                if(storedFile != null && storedFile.exists()) {
                    if(mFileDownloadListener != null) {
                        mFileDownloadListener.onDownloadFinished(storedFile);
                    }
                } else if(mFileDownloadListener != null) {
                    mFileDownloadListener.onDownloadFailure(storedFile);
                }
                DebugLogger.debugHeaders(LOG_TAG, headers);
                DebugLogger.debugStatusCode(LOG_TAG, statusCode);
                DebugLogger.debugFile(LOG_TAG, file);
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                super.onProgress(bytesWritten, totalSize);
                if(mFileDownloadListener != null) {
                    mFileDownloadListener.onDownloadProgress(bytesWritten, totalSize);
                }
            }

            @Override
            public void onStart() {
                if(mFileDownloadListener != null) {
                    mFileDownloadListener.onDownloadStart();
                }
            }

            @Override
            public void onFinish() {
                if(mFileDownloadListener != null) {
                    mFileDownloadListener.onDownloadFinish();
                }
            }
        };
    }

    public void doHttpRequest(FileDownloadListener listener) {
        mFileDownloadListener = listener;
        doHttpRequest(mFileAsyncHttpResponseHandler);
    }

    @Override
    protected RequestParams buildRequestParams() {
        return null;
    }

    @Override
    protected String buildRequestUrlString() {
        return mDownloadUrl;
    }

    @Override
    protected RequestHandle onDoHttpRequest(AsyncHttpClient client, String URL, Header[] headers, RequestParams params, HttpEntity entity, ResponseHandlerInterface responseHandler) {
        Log.i("HttpRequest", "URL: " + URL);
        return client.get(mContext, URL, headers, null, responseHandler);
    }
}
