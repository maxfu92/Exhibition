package com.tgf.exhibition.http.handler;

import android.app.Service;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;
import com.tgf.exhibition.R;
import com.tgf.exhibition.http.AbsHttpRequestHandler;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;

/**
 * Created by jeff on 2016/5/30.
 */
public class PackageVersionRequestHandler extends AbsHttpRequestHandler {
    public static final String PARAM_KEY_PKG_NAME = "package_name";

    public PackageVersionRequestHandler(Service service) {
        super(service);
    }

    @Override
    protected String buildRequestUrlString() {
        return mContext.getString(R.string.app_update_url);
    }

    public PackageVersionRequestHandler setPackageName(String packageName) {
        putParam(PARAM_KEY_PKG_NAME, packageName);
        return this;
    }
}
