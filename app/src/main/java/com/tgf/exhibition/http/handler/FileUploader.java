package com.tgf.exhibition.http.handler;

import android.app.Activity;

import com.loopj.android.http.RequestParams;
import com.tgf.exhibition.http.AbsHttpRequestHandler;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by jeff on 2016/5/19.
 */
public class FileUploader extends AbsHttpRequestHandler {
    private final RequestParams mRequestParams = new RequestParams();
    private String mUploadUrl;

    public FileUploader(Activity activity) {
        super(activity);
        mRequestParams.setHttpEntityIsRepeatable(true);
        mRequestParams.setUseJsonStreamer(false);
    }

    public final FileUploader setUploadUrl(String uploadUrl) {
        mUploadUrl = uploadUrl;
        return this;
    }

    public final FileUploader setUploadFile(File... files) throws FileNotFoundException {
        if (files == null) {
            return this;
        }
        if (files.length == 1) {
            mRequestParams.put(PARAM_KEY_FILE, files[0], RequestParams.APPLICATION_OCTET_STREAM, null);
        } else if (files.length > 1) {
            mRequestParams.put(PARAM_KEY_FILE, files, RequestParams.APPLICATION_OCTET_STREAM, null);
        }
        return this;
    }

    @Override
    protected String buildRequestUrlString() {
        return mUploadUrl;
    }

    @Override
    protected RequestParams buildRequestParams() {
        return mRequestParams;
    }

}
