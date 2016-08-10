package com.tgf.exhibition;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.tgf.exhibition.http.handler.JsonResponseHandler;
import com.tgf.exhibition.http.handler.UserCenterRequestHandler;
import com.tgf.exhibition.http.json.LoginData;
import com.tgf.exhibition.http.msg.LoginResponseMessage;
import com.tgf.exhibition.http.msg.StringMessage;
import com.tgf.exhibition.util.FileUtil;
import com.tgf.exhibition.util.OSChecker;
import com.tgf.exhibition.util.UserPreferences;
import com.tgf.exhibition.widget.LoadingDialog;

import java.io.IOException;
import java.io.InputStream;

import cn.jpush.android.api.JPushInterface;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    // UI references.
    private AutoCompleteTextView mMoblieView;
    private EditText mVerificationCodeView;
    private Button mBtnGetVerificationCode;

    private TgfDelegate mTgfDelegate;
    public TgfDelegate getTgfDelegate() {
        if (mTgfDelegate == null) {
            mTgfDelegate = TgfDelegate.create(this);
        }
        return mTgfDelegate;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean hasLogin = false;
        LoginData loginData = getTgfDelegate().getLoginData();
        if(loginData != null) {
            getTgfDelegate().gotoMainActivity();
            hasLogin = true;
        } else {
            loginData = UserPreferences.retreiveLoginUserLoginData(this);
            if(loginData != null) {
                getTgfDelegate().putLoginData(loginData);
                getTgfDelegate().gotoMainActivity();
                hasLogin = true;
            }
        }

        if(hasLogin) {
            finish();
        }

        // Set StatusBar Transparent
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }

        setContentView(R.layout.activity_login);
        initViews();

        findViewById(R.id.action_license).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, LicenseActivity.class));
            }
        });
    }

    private void initViews() {
        // set up Head Background & Logo
        InputStream is = null;
        try {
            is = getAssets().open("images/login_bg.jpg");
            BitmapDrawable bd = (BitmapDrawable) BitmapDrawable.createFromStream(is, "login_bg");
            View headView = findViewById(R.id.head_background);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                headView.setBackground(bd);
            } else {
                headView.setBackgroundDrawable(bd);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            FileUtil.closeSafed(is);
        }

        CheckBox checkBoxLicense = (CheckBox) findViewById(R.id.checkBox_license);
        checkBoxLicense.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                findViewById(R.id.sign_in_button).setEnabled(isChecked);
            }
        });

        // Set up the login form.
        mMoblieView = (AutoCompleteTextView) findViewById(R.id.moblie_phone);
        mMoblieView.addTextChangedListener(new PhoneNumberFormattingTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                ;
                mBtnGetVerificationCode.setEnabled(isPhoneNumberValid(s.toString()));
            }
        });
        mMoblieView.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_NEXT) {
                    mVerificationCodeView.requestFocus();
                    return true;
                }
                return false;
            }
        });
        mVerificationCodeView = (EditText) findViewById(R.id.verification_code);
        mVerificationCodeView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptSignin();
                    return true;
                }
                return false;
            }
        });

        mBtnGetVerificationCode = (Button) findViewById(R.id.btn_get_verification_code);
        mBtnGetVerificationCode.setAllCaps(false);
        mBtnGetVerificationCode.setOnClickListener(mGetVerificationCodeListener);
        mBtnGetVerificationCode.setEnabled(isPhoneNumberValid(mMoblieView.getText().toString()));
        findViewById(R.id.sign_in_button).setOnClickListener(mSigninListener);
    }

    //////////////////////////////
    ///// 验证码UI&获取逻辑 /////
    private GetVerificationCodeListener mGetVerificationCodeListener = new GetVerificationCodeListener();

    private class GetVerificationCodeListener extends JsonResponseHandler<StringMessage> implements View.OnClickListener, Runnable {
        private String mCountdownFormat;
        private int mCountdownNumber = 60;
        private boolean mCoundownCanceled = false;

        public GetVerificationCodeListener() {
            super(StringMessage.class);
        }

        @Override
        public void onSuccess(StringMessage jsonObj, String rawJsonResponse) {
            Log.i("HttpResponseHandler", rawJsonResponse);
            String msg = "";
            if (jsonObj.statusCode == 0) {
                msg = jsonObj.data;
            } else {
                msg = jsonObj.statusMessage;
                mCoundownCanceled = true;
            }

            getTgfDelegate().postTipsMassage(msg);
        }

        @Override
        public void onFailure(int statusCode, Throwable throwable) {
            mCoundownCanceled = true;
            getTgfDelegate().postTipsMassage(R.string.tips_network_error);
        }

        @Override
        public void onClick(View v) {
            if (OSChecker.isNetworkAvailable(LoginActivity.this)) {
                getTgfDelegate().getUserCenterHandler()
                        .setPhoneNumber(mMoblieView.getText().toString())
                        .setIRequestUrl(UserCenterRequestHandler.UserCenterURL.GET_VERIFY_CODE)
                        .doHttpRequest(mGetVerificationCodeListener);
                mBtnGetVerificationCode.setEnabled(false);

                if (mCountdownFormat == null) {
                    mCountdownFormat = getString(R.string.action_get_verification_code2);
                }
                updateCountdown();
            }
        }

        private void updateCountdown() {
            mBtnGetVerificationCode.setText(mCountdownNumber + "s");
            //mBtnGetVerificationCode.setText(String.format(mCountdownFormat, mCountdownNumber));
            if (mCountdownNumber > 0) {
                mBtnGetVerificationCode.postDelayed(this, 1000);
                --mCountdownNumber;
            } else {
                resetCountdown();
            }
        }

        private void resetCountdown() {
            mCountdownNumber = 60;
            mCoundownCanceled = false;
            mBtnGetVerificationCode.setText(R.string.action_get_verification_code);
            mBtnGetVerificationCode.setEnabled(true);
        }

        @Override
        public void run() {
            if (mCoundownCanceled) {
                resetCountdown();
                return;
            }
            updateCountdown();
        }
    }

    //////////////////////////
    ///// Sign-in 逻辑 /////
    private SigninListener mSigninListener = new SigninListener();
    private class SigninListener extends JsonResponseHandler<LoginResponseMessage> implements View.OnClickListener {
        public SigninListener() {
            super(LoginResponseMessage.class);
        }

        @Override
        public void onStart() {
            setLoadingDialog(new LoadingDialog(LoginActivity.this));
            setLoadingDialogTitle(R.string.tips_logining);
            super.onStart();
        }

        @Override
        public void onFinish() {
            super.onFinish();
            dismissLoadingDialog();
        }

        @Override
        public void onSuccess(LoginResponseMessage loginResponseMessage, String rawJsonResponse) {
            if (loginResponseMessage.statusCode == 0) {
                LoginData loginData = loginResponseMessage.data;
                loginData.phoneNumber = mMoblieView.getText().toString();

                UserPreferences.recordUserLoginData(LoginActivity.this, loginData);
                getTgfDelegate().putLoginData(loginData);

                getTgfDelegate().gotoMainActivity();
                onBackPressed();
            } else {
                getTgfDelegate().postTipsMassage(loginResponseMessage.statusMessage);
            }
        }

        @Override
        public void onFailure(int statusCode, Throwable throwable) {
            getTgfDelegate().postTipsMassage(R.string.tips_network_error);
        }

        @Override
        public void onClick(View v) {
            attemptSignin();
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptSignin() {
        // Reset errors.
        mMoblieView.setError(null);
        mVerificationCodeView.setError(null);

        // Store values at the time of the login attempt.
        String mobile = mMoblieView.getText().toString().replace(" ", "");
        String verificationCode = mVerificationCodeView.getText().toString();

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(verificationCode)) {
            mVerificationCodeView.setError(getString(R.string.error_field_required));
        } else if (!isVerificationCodeValid(verificationCode)) {
            mVerificationCodeView.setError(getString(R.string.error_invalid_verification_code));
        }

        // Check for a valid email address.
        else if (TextUtils.isEmpty(mobile)) {
            mMoblieView.setError(getString(R.string.error_field_required));
        } else if (!isPhoneNumberValid(mobile)) {
            mMoblieView.setError(getString(R.string.error_invalid_phone));
        } else {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(mVerificationCodeView.getWindowToken(), 0);
            }

            if (OSChecker.isNetworkAvailable(this)) {
                ///////////////////
                //// do Signin ///
                // Show a progress spinner, and kick off a background task to
                // perform the user login attempt.
                //showProgress(true);
                getTgfDelegate().getUserCenterHandler()
                        .setPhoneNumber(mobile)
                        .setVerifyCode(verificationCode)
                        .setIRequestUrl(UserCenterRequestHandler.UserCenterURL.LOGIN_POINT)
                        .doHttpRequest(mSigninListener);
            }
        }
    }

    /**
     * 验证手机格式
     */
    public static boolean isPhoneNumberValid(String mobiles) {
    /*
    移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
    联通：130、131、132、152、155、156、185、186
    电信：133、153、180、189、（1349卫通）
    总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
    */
        String replaced = mobiles.replace(" ", "");
        String telRegex = "[1][358]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(replaced) || replaced.length() != 11) {
            return false;
        } else {
            return replaced.matches(telRegex);
        }
    }

    private static boolean isVerificationCodeValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() >= 4;
    }
}

//    private boolean mayRequestContacts() {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//            return true;
//        }
//        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
//            return true;
//        }
//        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
//            Snackbar.make(mMoblieView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
//                    .setAction(android.R.string.ok, new View.OnClickListener() {
//                        @Override
//                        @TargetApi(Build.VERSION_CODES.M)
//                        public void onClick(View v) {
//                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
//                        }
//                    });
//        } else {
//            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
//        }
//        return false;
//    }

/**
 * Callback received when a permissions request has been completed.
 */
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        if (requestCode == REQUEST_READ_CONTACTS) {
//            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                populateAutoComplete();
//            }
//        }
//    }