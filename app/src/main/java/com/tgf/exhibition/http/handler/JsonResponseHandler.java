package com.tgf.exhibition.http.handler;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.tgf.exhibition.http.ResponseMessage;
import com.tgf.exhibition.http.msg.StringMessage;
import com.tgf.exhibition.util.DebugLogger;
import com.tgf.exhibition.http.IResponseHandler;
import com.tgf.exhibition.widget.LoadingDialog;

import java.util.concurrent.atomic.AtomicBoolean;

import cz.msebera.android.httpclient.Header;

/**
 * Created by jeff on 2016/5/20.
 */
public abstract class JsonResponseHandler<JSON_OBJ_T extends ResponseMessage<?>> extends BaseJsonHttpResponseHandler<JSON_OBJ_T> implements IResponseHandler<JSON_OBJ_T> {
    private static final String LOG_TAG = "HttpResponse";
    private final Class<? extends JSON_OBJ_T> mJsonTypeClass;

    public JsonResponseHandler(Class<? extends JSON_OBJ_T> clazz) {
        mJsonTypeClass = clazz;
    }

    private AtomicBoolean mIsLoadingDialogShown = new AtomicBoolean(false);
    private LoadingDialog mLoadingDialog = null;

    public void setLoadingDialog(LoadingDialog loadingDialog) {
        if(mLoadingDialog != null && loadingDialog == null) {
            mLoadingDialog.dismiss();
        }
        mLoadingDialog = loadingDialog;
    }

    public void setLoadingDialogTitle(String title) {
        if(mLoadingDialog != null) {
            mLoadingDialog.setTitle(title);
        }
    }

    public void setLoadingDialogTitle(int titleResId) {
        if(mLoadingDialog != null) {
            mLoadingDialog.setTitle(titleResId);
        }
    }


    @Override
    public void onStart() {
        if(mLoadingDialog != null) {
            mIsLoadingDialogShown.set(true);
            mLoadingDialog.show();
        }
    }

    protected void dismissLoadingDialog() {
        if(mLoadingDialog != null) {
            mLoadingDialog.dismiss();
            mIsLoadingDialogShown.set(false);
            mLoadingDialog = null;
        }
    }

    public boolean isLoadingDialogShown() {
        return mIsLoadingDialogShown.get();
    }

    @Override
    public void onCancel() {
        super.onCancel();
        dismissLoadingDialog();
    }

    @Override
    public void onFinish() {
        super.onFinish();
        dismissLoadingDialog();
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, JSON_OBJ_T response) {
        DebugLogger.debugHeaders(LOG_TAG, headers);
        DebugLogger.debugStatusCode(LOG_TAG, statusCode);
        DebugLogger.debugResponse(LOG_TAG, rawJsonResponse);

        if (response == null) {
            return;
        }
        if("1001".equals(response.statusCode)) {
            // TODO; GOTO Login page
        }else {
            onSuccess(response, rawJsonResponse);
        }
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, JSON_OBJ_T errorResponse) {
        DebugLogger.debugHeaders(LOG_TAG, headers);
        DebugLogger.debugStatusCode(LOG_TAG, statusCode);
        DebugLogger.debugThrowable(LOG_TAG, throwable);
        DebugLogger.debugResponse(LOG_TAG, rawJsonData);
        onFailure(statusCode, throwable);
    }

    @Override
    protected JSON_OBJ_T parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        if(mJsonTypeClass == StringMessage.class) {
            String s = rawJsonData.replace("[]", "\"\"").replace("[ ]", "\"\"");
            rawJsonData = s;
        }
        return objectMapper.readValues(new JsonFactory().createParser(rawJsonData), mJsonTypeClass).next();
    }
}
