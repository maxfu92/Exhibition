package com.tgf.exhibition;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.tgf.exhibition.http.handler.JsonResponseHandler;
import com.tgf.exhibition.http.handler.UserCenterRequestHandler;
import com.tgf.exhibition.http.json.AttachedUserInfo;
import com.tgf.exhibition.http.json.LoginData;
import com.tgf.exhibition.http.json.XCScene;
import com.tgf.exhibition.http.msg.ImageUploadedMessage;
import com.tgf.exhibition.http.msg.StringMessage;
import com.tgf.exhibition.util.FileUtil;
import com.tgf.exhibition.util.SystemInvoker;
import com.tgf.exhibition.util.UserPreferences;
import com.tgf.exhibition.widget.LoadingDialog;

import java.io.File;
import java.io.FileOutputStream;

public class RegisterActivity extends AppCompatActivity {
    private static final String PHOTO_NAME = "userhead.jpg";

    private File mPhotoFile;
    private boolean mPhotoChanged = false;

    private LoginData mLoginData;

    private String mPhotoRemoteUrl = null;
    private UserSex mUserSex = UserSex.SECRECY;
    private ImageView mUserPotoView;
    private EditText mUserNickNameView;

    private enum UserSex {
        MAN("MAN"),
        WOMAN("WOMAN"),
        SECRECY("SECRECY");

        private final String mDesc;
        UserSex(String desc) {
            mDesc = desc;
        }

        public String desc() {
            return mDesc;
        }

    }

    private TgfDelegate mTgfDelegate;
    public TgfDelegate getTgfDelegate() {
        if (mTgfDelegate == null) {
            mTgfDelegate = TgfDelegate.create(this);
        }
        return mTgfDelegate;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mLoginData = (LoginData) getIntent().getParcelableExtra("data");

        initViews();
        initData();
    }

    private void initData() {
        XCScene xcScene = getTgfDelegate().getXcScene();
        AttachedUserInfo userInfo = null;
        if(xcScene != null) {
            userInfo = xcScene.userInfo;
        }
        UserPreferences sharedPreferences = UserPreferences.createUserPreferences(this, mLoginData.userId);
        mUserNickNameView.setText(sharedPreferences.getString(UserPreferences.KEY_NICK_NAME, userInfo==null?"":userInfo.nickName));
        mUserSex = UserSex.valueOf(sharedPreferences.getString(UserPreferences.KEY_SEX, userInfo==null?UserSex.SECRECY.desc():userInfo.sex));
        switch (mUserSex) {
            case MAN:
                ((RadioButton)findViewById(R.id.radioMale)).setChecked(true);
                break;
            case WOMAN:
                ((RadioButton)findViewById(R.id.radioFemale)).setChecked(true);
                break;
            case SECRECY:
            default:
                ((RadioButton)findViewById(R.id.radioSecrecy)).setChecked(true);
                break;
        }

        mPhotoFile = new File(sharedPreferences.getString(UserPreferences.KEY_PHOTO_LOCAL_PATH, ""));
        if(!mPhotoFile.exists()) {
            mPhotoFile = new File(getPhotoDir(), PHOTO_NAME);
        }
        if (mPhotoFile.exists()) {
            mUserPotoView.setImageDrawable(BitmapDrawable.createFromPath(mPhotoFile.getAbsolutePath()));
        }

        loadRemotePicture(userInfo);
    }

    private void loadRemotePicture(AttachedUserInfo userInfo) {
        UserPreferences sharedPreferences = UserPreferences.createUserPreferences(this, mLoginData.userId);
        mPhotoRemoteUrl = sharedPreferences.getString(UserPreferences.KEY_PHOTO_REMOTE_URL, userInfo==null?"":userInfo.headPictureUrl);
        if(!TextUtils.isEmpty(mPhotoRemoteUrl)) {
            ImageLoader.getInstance().displayImage(mPhotoRemoteUrl, mUserPotoView, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    if(loadedImage != null) {
                        FileOutputStream os = null;
                        try {
                            if (mPhotoFile.exists()) {
                                mPhotoFile.delete();
                            }
                            if (mPhotoFile.createNewFile()) {
                                os = new FileOutputStream(mPhotoFile);
                                loadedImage.compress(Bitmap.CompressFormat.JPEG, 80, os);
                            }
                        } catch (Exception e) {
                        } finally {
                            FileUtil.closeSafed(os);
                        }
                    }
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });
        }
    }

    private File getPhotoDir() {
        String[] split = getPackageName().split("\\.");
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        for (int i = 0; i < split.length; i++) {
            externalStorageDirectory = new File(externalStorageDirectory, split[i]);
            if (!externalStorageDirectory.exists()) {
                externalStorageDirectory.mkdir();
            }
        }

        if (!TextUtils.isEmpty(mLoginData.userId)) {
            externalStorageDirectory = new File(externalStorageDirectory, mLoginData.userId);
            if (!externalStorageDirectory.exists()) {
                externalStorageDirectory.mkdir();
            }
        }
        return externalStorageDirectory;
    }

    private void initViews() {
        mUserPotoView = (ImageView) findViewById(R.id.iv_user_photo);
        mUserPotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File outputFile = new File(mPhotoFile.getParentFile(), "user_toke.png");
                SystemInvoker.showPickDialog(RegisterActivity.this, "设置头像...", Uri.fromFile(outputFile));
            }
        });
        mUserNickNameView = (EditText) findViewById(R.id.etv_nickname);
        RadioGroup group = (RadioGroup) findViewById(R.id.radioGroup);
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int radioButtonId = group.getCheckedRadioButtonId();
                switch (radioButtonId) {
                    case R.id.radioMale:
                        mUserSex = UserSex.MAN;
                        break;
                    case R.id.radioFemale:
                        mUserSex = UserSex.WOMAN;
                    case R.id.radioSecrecy:
                        mUserSex = UserSex.SECRECY;
                }
            }
        });

        findViewById(R.id.btn_next).setOnClickListener(mSubmitResponseHandler);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(resultCode == RESULT_CANCELED) {
            return;
        }

        switch (requestCode) {
            // 直接从相册获取
            case SystemInvoker.PICTURE_REQUEST_GALLERY:
                if(intent != null) {
                    launchCropPicture(intent.getData());
                }
                break;
            // 调用相机拍照时
            case SystemInvoker.PICTURE_REQUEST_CAMERA:
                launchCropPicture(Uri.fromFile(new File(mPhotoFile.getParentFile(), "user_toke.png")));
                break;
            // 取得裁剪后的图片
            case SystemInvoker.PICTURE_REQUEST_CUT:
                mPhotoChanged = true;
                mUserPotoView.setImageDrawable(BitmapDrawable.createFromPath(mPhotoFile.getAbsolutePath()));
                break;
            default:
                break;

        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void launchCropPicture(Uri uri) {
        Bundle bundle = new Bundle();
        // aspectX aspectY 是宽高的比例
        bundle.putInt("aspectX", 1);
        bundle.putInt("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        bundle.putInt("outputX", 150);
        bundle.putInt("outputY", 150);
        SystemInvoker.showPictureCrop(this, uri, bundle, Uri.fromFile(mPhotoFile)); //TODO;
    }


    private ImageUploadedResponseHandler mImageUploadResponseHandler = new ImageUploadedResponseHandler();
    private class ImageUploadedResponseHandler extends JsonResponseHandler<ImageUploadedMessage> {

        public ImageUploadedResponseHandler() {
            super(ImageUploadedMessage.class);
        }

        @Override
        public void onSuccess(ImageUploadedMessage jsonObj, String rawJsonResponse) {
            if(jsonObj.statusCode == 0) {
                mPhotoRemoteUrl = jsonObj.data.url;
                UserPreferences sharedPreferences = UserPreferences.createUserPreferences(RegisterActivity.this, mLoginData.userId);
                sharedPreferences.putString(UserPreferences.KEY_PHOTO_REMOTE_URL, mPhotoRemoteUrl);
                updateUserBasicInfo();
            } else {
                getTgfDelegate().postTipsMassage(jsonObj.statusMessage);
            }
        }

        @Override
        public void onFailure(int statusCode, Throwable throwable) {
            getTgfDelegate().postTipsMassage(R.string.tip_upload_failure);
        }
    }

    //// Http Request & Response Handler
    private SubmitResponseHandler mSubmitResponseHandler = new SubmitResponseHandler();
    private class SubmitResponseHandler extends JsonResponseHandler<StringMessage> implements View.OnClickListener {
        public SubmitResponseHandler() {
            super(StringMessage.class);
        }

        @Override
        public void onFinish() {
            super.onFinish();
            dismissLoadingDialog();
        }

        @Override
        public void onStart() {
            setLoadingDialog(new LoadingDialog(RegisterActivity.this));
            super.onStart();
        }

        @Override
        public void onSuccess(StringMessage jsonObj, String rawJsonResponse) {
            if(jsonObj.statusCode == 0) {
                XCScene xcScene = getTgfDelegate().getXcScene();
                if(xcScene != null && xcScene.userInfo != null) {
                    xcScene.userInfo.nickName = mUserNickNameView.getText().toString();
                    xcScene.userInfo.sex = mUserSex.desc();
                    if(!TextUtils.isEmpty(mPhotoRemoteUrl)) {
                        xcScene.userInfo.headPictureUrl = mPhotoRemoteUrl;
                    }
                }
                getTgfDelegate().postTipsMassage(R.string.tips_upload_success);
                onBackPressed();
                finish();
            } else {
                getTgfDelegate().postTipsMassage(jsonObj.statusMessage);
            }
        }

        @Override
        public void onFailure(int statusCode, Throwable throwable) {
            getTgfDelegate().postTipsMassage(R.string.tip_upload_failure);
        }

        @Override
        public void onClick(View v) {
            if(mPhotoChanged) {
                getTgfDelegate().getUserCenterHandler()
                        .setUploadFile(mPhotoFile)
                        .setIRequestUrl(UserCenterRequestHandler.UserCenterURL.UPLOAD_IMG_FILE)
                        .doHttpRequest(mImageUploadResponseHandler);
            } else {
                updateUserBasicInfo();
            }
        }
    }

    private void updateUserBasicInfo () {
        UserCenterRequestHandler userCenterHandler = getTgfDelegate().getUserCenterHandler()
                .setUserToken(mLoginData.userToken)
                .setNickName(mUserNickNameView.getText().toString().trim())
                .setUserSex(mUserSex.desc())
                .setIRequestUrl(UserCenterRequestHandler.UserCenterURL.PUT_REG_INFO);


        if(!TextUtils.isEmpty(mPhotoRemoteUrl)) {
            userCenterHandler.setUserPictureUrl(mPhotoRemoteUrl);
        }

        userCenterHandler.doHttpRequest(mSubmitResponseHandler);
        saveUserData();
    }

    private void saveUserData() {
        UserPreferences sharedPreferences = UserPreferences.createUserPreferences(this, mLoginData.userId);
        sharedPreferences.putString(UserPreferences.KEY_NICK_NAME, mUserNickNameView.getText().toString())
                .putString(UserPreferences.KEY_SEX, mUserSex.desc())
                .putString(UserPreferences.KEY_PHOTO_LOCAL_PATH, mPhotoFile.getAbsolutePath())
                .putString(UserPreferences.KEY_PHOTO_REMOTE_URL, mPhotoRemoteUrl)
                .putString(UserPreferences.KEY_LOGIN_STATUS, "login");
    }
}
