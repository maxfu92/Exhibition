package com.tgf.exhibition.http;

/**
 * Created by jeff on 2016/5/21.
 */
public interface IResponseHandler<T> {
    void onSuccess(T jsonObj, String rawJsonResponse);

    void onFailure(int statusCode, Throwable throwable);
}
