package com.tgf.exhibition.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tgf.exhibition.http.json.AttachedUserInfo;
import com.tgf.exhibition.http.json.LoginData;

import java.io.IOException;

/**
 * Created by jeff on 2016/5/21.
 */
public class UserPreferences {
    public static final String GLOBAL_KEY_LAST_USER_ID = "user_id";

    public static final String KEY_USER = "user";
    public static final String KEY_ID = "id";
    public static final String KEY_LOGIN_STATUS = "status";
    public static final String KEY_LOGIN_SCENE_ID = "login_scene_id";
    public static final String KEY_TOKEN = "token";
    public static final String KEY_NICK_NAME = "nick_name";
    public static final String KEY_SEX = "sex";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_PHOTO_LOCAL_PATH = "photo_local";
    public static final String KEY_PHOTO_REMOTE_URL = "photo_remote";
    public static final String KEY_WX_BIND = "wx_bind";

    private final SharedPreferences mSharedPreferences;
    private UserPreferences(SharedPreferences sp) {
        mSharedPreferences = sp;
    }

    public static UserPreferences createUserPreferences(Context context, String userId) {
        if(!TextUtils.isEmpty(userId)) {
            return new UserPreferences(context.getSharedPreferences("u_" + userId, 0));
        }
        return null;
    }

    public UserPreferences putString(String key, String value) {
        mSharedPreferences.edit().putString(key, value).apply();
        return this;
    }

    public String getString(String key, String defValue) {
        return mSharedPreferences.getString(key, defValue);
    }

    public void recordUserLoginScene(String sceneId) {
        putString(KEY_LOGIN_SCENE_ID, sceneId);
    }

    public void recordLoginData(LoginData loginData) {
        mSharedPreferences
                .edit()
                .putString(KEY_ID, loginData.userId)
                .putString(KEY_PHONE, loginData.phoneNumber)
                .putString(KEY_TOKEN, loginData.userToken)
                .putString(KEY_LOGIN_STATUS, loginData.regStatus)
                .putString(KEY_WX_BIND, loginData.isBind)
                .apply();
    }

    public LoginData retreiveLoginData() {
        LoginData loginData = new LoginData();
        loginData.userId = mSharedPreferences.getString(KEY_ID, null);
        loginData.phoneNumber = mSharedPreferences.getString(KEY_PHONE, null);
        loginData.userToken = mSharedPreferences.getString(KEY_TOKEN, null);
        loginData.regStatus = mSharedPreferences.getString(KEY_LOGIN_STATUS, null);
        loginData.isBind = mSharedPreferences.getString(KEY_WX_BIND, null);
        loginData.loginSceneId = mSharedPreferences.getString(KEY_LOGIN_SCENE_ID, null);
        return  loginData;
    }

    public static void recordUserLoginData(Context context, LoginData userLoginData) {
        UserPreferences userPreferences = createUserPreferences(context, userLoginData.userId);
        if(userPreferences != null) {
            userPreferences.recordLoginData(userLoginData);
            recordUserLogin(context, userLoginData.userId);
        }
    }

    public static LoginData retreiveLoginUserLoginData(Context context) {
        UserPreferences userPreferences = createUserPreferences(context, getLastLoginUserId(context));
        LoginData loginData = null;
        if(userPreferences != null) {
            loginData = userPreferences.retreiveLoginData();
        }
        return  loginData;
    }

    public void recordUserInfo(AttachedUserInfo userInfo) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            mSharedPreferences.edit().putString(KEY_USER, objectMapper.writeValueAsString(userInfo)).apply();
        } catch (JsonProcessingException e) {
        }
    }

    public AttachedUserInfo getLoginUserInfo() {
        AttachedUserInfo userInfo = null;

        String userString = mSharedPreferences.getString(KEY_USER, "");
        if(!TextUtils.isEmpty(userString)) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                userInfo = objectMapper.readValue(userString, AttachedUserInfo.class);
            } catch (IOException e) {
            }
        }
        return userInfo;
    }


    /// Global Data
    public static String getLastLoginUserId(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(GLOBAL_KEY_LAST_USER_ID, null);
    }

    public static void recordUserLogin(Context context, String userId) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putString(GLOBAL_KEY_LAST_USER_ID, userId).apply();
    }

    public static void logoutUser(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().remove(GLOBAL_KEY_LAST_USER_ID).apply();
    }


}
