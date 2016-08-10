package com.tgf.exhibition;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tgf.exhibition.http.handler.DeclarationRequestHandler;
import com.tgf.exhibition.http.handler.UserCenterRequestHandler;
import com.tgf.exhibition.http.json.AttachedUserInfo;
import com.tgf.exhibition.http.json.LoginData;
import com.tgf.exhibition.http.json.PackageInfo;
import com.tgf.exhibition.http.json.Scene;
import com.tgf.exhibition.http.json.XCScene;
import com.tgf.exhibition.util.UserPreferences;

import java.io.IOException;

/**
 * Created by jeff on 2016/5/27.
 */
public class TgfDelegate {
    protected static final String CTX_KEY_LOGIN_DATA = "loginData";
    protected static final String CTX_KEY_LOGIN_SCENE = "xcScene";
    protected static final String CTX_KEY_USER_PREFERENCES = "userPreferences";

    private UserCenterRequestHandler mUserCenterHandler;
    private DeclarationRequestHandler mDeclarationRequestHandler;

    final Activity mActivity;

    private TgfDelegate(Activity activity){
        mActivity = activity;
    }

    public static TgfDelegate create(Activity activity) {
        return new TgfDelegate(activity);
    }

    public void setTextViewText(TextView tvText, CharSequence value) {
        if(tvText != null) {
            tvText.setText(value);
        }
    }

    public UserCenterRequestHandler getUserCenterHandler() {
        if(mUserCenterHandler == null) {
            mUserCenterHandler = new UserCenterRequestHandler(mActivity);
        }
        return mUserCenterHandler;
    }

    public DeclarationRequestHandler getDeclarationRequestHandler() {
        if(mDeclarationRequestHandler == null) {
            mDeclarationRequestHandler = new DeclarationRequestHandler(mActivity);
        }
        return mDeclarationRequestHandler;
    }

    public void postTipsMassage(String tips) {
        if(!TextUtils.isEmpty(tips)) {
            Toast.makeText(mActivity, tips, Toast.LENGTH_SHORT).show();
        }
    }

    public void postTipsMassage(int resId) {
        Toast.makeText(mActivity, resId, Toast.LENGTH_SHORT).show();
    }

    public LoginData getLoginData() {
        return TgfApplication.getValue(CTX_KEY_LOGIN_DATA);
    }
    public void putLoginData(LoginData loginData) {
        TgfApplication.putValue(CTX_KEY_LOGIN_DATA, loginData);
    }

    public XCScene getXcScene() {
        return TgfApplication.getValue(CTX_KEY_LOGIN_SCENE);
    }
    public void putXcScene(XCScene scene) {
        TgfApplication.putValue(CTX_KEY_LOGIN_SCENE, scene);
    }

    public UserPreferences getUserPreferences() {
        return TgfApplication.getValue(CTX_KEY_USER_PREFERENCES);
    }
    public void putUserPreferences(UserPreferences userPreferences) {
        TgfApplication.putValue(CTX_KEY_USER_PREFERENCES, userPreferences);
    }



    public boolean hasNewVersion(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String jsonPackageInfo = preferences.getString(PackageUpdater.KEY_NEW_PACKAGE_INFO, null);
        if (TextUtils.isEmpty(jsonPackageInfo)) {
            return false;
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            final PackageInfo packageInfo = objectMapper.readValue(jsonPackageInfo, PackageInfo.class);
            if(PackageUpdater.getVerName(context).equals(packageInfo.versionName) &&
                    (PackageUpdater.getVerCode(context) < Integer.parseInt(packageInfo.versionCode))) {
                return true;
            } else {
                preferences.edit().remove(PackageUpdater.KEY_NEW_PACKAGE_INFO).apply();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void gotoLoginActivity() {
        if(mActivity instanceof LoginActivity) {
            return;
        }
        mActivity.startActivity(new Intent(mActivity, LoginActivity.class));
    }

    public void gotoMainActivity() {
        if(mActivity instanceof MainActivity) {
            return;
        }
        mActivity.startActivity(new Intent(mActivity, MainActivity.class));
    }

    public void gotoRegisterActivity() {
        if(mActivity instanceof RegisterActivity) {
            return;
        }
        mActivity.startActivity(new Intent(mActivity, RegisterActivity.class).putExtra("data", getLoginData()));
    }

    public void logoutUser() {
        TgfApplication.removeValue(CTX_KEY_LOGIN_DATA);
        TgfApplication.removeValue(CTX_KEY_LOGIN_SCENE);
        TgfApplication.removeValue(CTX_KEY_USER_PREFERENCES);
        UserPreferences.logoutUser(mActivity);
    }

}
