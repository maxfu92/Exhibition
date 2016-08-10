package com.tgf.exhibition.http.handler;

import android.app.Activity;

import com.loopj.android.http.RequestParams;
import com.tgf.exhibition.http.AbsHttpRequestHandler;
import com.tgf.exhibition.http.IHttpRequestHandler;
import com.tgf.exhibition.http.IRequestUrl;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by jeff on 2016/5/19.
 */
public class UserCenterRequestHandler extends AbsHttpRequestHandler {
    public enum UserCenterURL implements IRequestUrl {
        GET_VERIFY_CODE("/api/main/getMobileVerify"),
        LOGIN_POINT("/api/main/login"),
        PUT_REG_INFO("/api/user/bindUserInfo"),
        GET_XC_SCENE_INFO(XCAPPLY_URL_PATH+"/usercenter"),
        UPLOAD_IMG_FILE("/api/upload/index");

        private final String PATH;
        UserCenterURL(String path) {
            PATH = path;
        }

        @Override
        public String getStringUrl() {
            return PATH;
        }
    }

    public UserCenterRequestHandler(Activity activity) {
        super(activity);
        mRequestParams.setContentEncoding("UTF-8");
    }

    protected IRequestUrl mUrl;
    public final <T extends IHttpRequestHandler> T setIRequestUrl(IRequestUrl url) {
        mUrl = url;
        return (T) this;
    }

    @Override
    protected String buildRequestUrlString() {
        return buildUrlStringWithPrefix(mUrl.getStringUrl());
    }

    public final UserCenterRequestHandler setUserToken(String token) {
        return putParam(PARAM_KEY_USER_TOKEN, token);
    }

    public UserCenterRequestHandler setSceneId(String sceneId) {
        return putParam( PARAM_KEY_SCENE_ID, sceneId);
    }

    public final UserCenterRequestHandler setPhoneNumber(String phoneNumber) {
        return putParam(PARAM_KEY_TEL, phoneNumber);
    }

    public final UserCenterRequestHandler setNickName(String nickName) {
        return putParam(PARAM_KEY_USER_NICK_NAME, nickName);
    }

    public final UserCenterRequestHandler setUserSex(String sex) {
        return putParam(PARAM_KEY_USER_SEX, sex);
    }

    public final UserCenterRequestHandler setUserPictureUrl(String avatarUrl) {
        return putParam(PARAM_KEY_USER_AVATAR, avatarUrl);
    }

    public final UserCenterRequestHandler setVerifyCode(String verifyCode) {
        return putParam(PARAM_KEY_VERIFY_CODE, verifyCode);
    }

    public final UserCenterRequestHandler setUploadFile(File... files){
        if (files == null) {
            return this;
        }
        try {
            if (files.length == 1) {
                mRequestParams.put(PARAM_KEY_FILE, files[0], RequestParams.APPLICATION_OCTET_STREAM, null);

            } else if (files.length > 1) {
                mRequestParams.put(PARAM_KEY_FILE, files, RequestParams.APPLICATION_OCTET_STREAM, null);
            }
            mRequestParams.setHttpEntityIsRepeatable(true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return this;
    }
}
