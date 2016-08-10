package com.tgf.exhibition.declaration2;

import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tgf.exhibition.TgfActivity;
import com.tgf.exhibition.R;
import com.tgf.exhibition.http.handler.DeclarationRequestHandler;
import com.tgf.exhibition.http.handler.JsonResponseHandler;
import com.tgf.exhibition.http.handler.UserCenterRequestHandler;
import com.tgf.exhibition.http.json.DeclarationOrder;
import com.tgf.exhibition.http.json.LoginData;
import com.tgf.exhibition.http.json.OnsiteDocument;
import com.tgf.exhibition.http.json.UploadedFile;
import com.tgf.exhibition.http.msg.ImageUploadedMessage;
import com.tgf.exhibition.http.msg.StringMessage;
import com.tgf.exhibition.util.BitmapUtils;
import com.tgf.exhibition.util.SystemInvoker;
import com.tgf.exhibition.widget.LoadingDialog;
import com.tgf.exhibition.widget.MGridView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderServiceFinishedActivity extends TgfActivity {
    private LoginData mLoginData;
    protected DeclarationOrder mOrder;

    private EditText mEtComments;
    private File mCurTakeFile;
    private PicturesAdapter mPicturesAdapter = new PicturesAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLoginData = getTgfDelegate().getLoginData();

        mOrder = getIntent().getParcelableExtra("order");

        setContentView(R.layout.activity_order_service_finished);

        mEtComments = (EditText)findViewById(R.id.et_comments);
        mEtComments.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                findViewById(R.id.btn_submit_certificates).setEnabled((mPicturesAdapter.getCount() != 0 || mEtComments.length() != 0));
            }
        });
        ImageView takePoto = (ImageView)findViewById(R.id.iv_take_photo);
        takePoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurTakeFile = generateRandomFile();
                SystemInvoker.showPickDialog(OrderServiceFinishedActivity.this, "获取现场凭证", Uri.fromFile(mCurTakeFile));
            }
        });

        MGridView gridview = (MGridView) findViewById(R.id.gridview);
        gridview.setAdapter(mPicturesAdapter);
        mPicturesAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                findViewById(R.id.btn_submit_certificates).setEnabled((mPicturesAdapter.getCount() != 0 || mEtComments.length() != 0));
            }
        });

        findViewById(R.id.btn_submit_certificates).setEnabled(false);
        findViewById(R.id.btn_submit_certificates).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPicturesAdapter.getCount() != 0 || mEtComments.length() != 0) {
                    mImageUploadResponseHandler.uploadFileStart();
                } else {
                    getTgfDelegate().postTipsMassage("数据为空，不可以提交");
                }
            }
        });
    }

    @Override
    protected void onDataRefresh() {
    }

    private File generateRandomFile() {
        try {
            return File.createTempFile(mLoginData.userId+"-", "-"+String.valueOf(new Date().getTime()+".jpg"), Environment.getExternalStorageDirectory());
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        mImageUploadResponseHandler = new ImageUploadedResponseHandler();
        mSubmitResponseHandler = new SubmitResponseHandler();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            // 直接从相册获取
            case SystemInvoker.PICTURE_REQUEST_GALLERY:
                if(intent != null) {
                    Uri uri = intent.getData();
                    //mCurTakeFile = FileUtil.fileFromUri(this, uri);
                    SystemInvoker.showPictureCrop(this, uri, null, Uri.fromFile(mCurTakeFile));
                }
                break;
            // 调用相机拍照时
            case SystemInvoker.PICTURE_REQUEST_CAMERA:
                Uri inputUri = Uri.fromFile(mCurTakeFile);
                mCurTakeFile = generateRandomFile();
                Uri output = Uri.fromFile(mCurTakeFile);
                SystemInvoker.showPictureCrop(this, inputUri, null, output);
                break;
            // 取得裁剪后的图片
            case SystemInvoker.PICTURE_REQUEST_CUT:
                if(resultCode == RESULT_OK) {
                    mPicturesAdapter.addBitmapFile(mCurTakeFile);
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    private class PicturesAdapter extends BaseAdapter {
        List<File> mBitmapFiles = new ArrayList<File>();
        List<Bitmap> mScaledBitmaps = new ArrayList<Bitmap>();
        private int itemMaxEdge = 0;

        public List<File> getBitmapFiles() {
            return mBitmapFiles;
        }

        @Override
        public int getCount() {
            return mBitmapFiles.size();
        }

        @Override
        public File getItem(int position) {
            return mBitmapFiles.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = View.inflate(OrderServiceFinishedActivity.this, R.layout.grid_picture_item, null);
            }
            ImageView imageView = (ImageView) convertView.findViewById(android.R.id.icon);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageBitmap(mScaledBitmaps.get(position));
            View closeView = convertView.findViewById(R.id.iv_action_close);
            closeView.setTag(position);
            closeView.setOnClickListener(mOnCloseButtonPressed);

            return convertView;
        }

        private OnCloseButtonPressed mOnCloseButtonPressed = new OnCloseButtonPressed();

        private class OnCloseButtonPressed implements View.OnClickListener {

            @Override
            public void onClick(View v) {
                removeBitmap((int) v.getTag());
            }
        }

        public void removeBitmap(int postion) {
            mBitmapFiles.remove(postion);
            mScaledBitmaps.remove(postion).recycle();
            notifyDataSetChanged();
        }

        public void addBitmapFile(File bitmapFile) {
            mBitmapFiles.add(mCurTakeFile);
            Bitmap bitmap = BitmapFactory.decodeFile(mCurTakeFile.getAbsolutePath());

            if(itemMaxEdge == 0) {
                itemMaxEdge = BitmapUtils.dp2px(OrderServiceFinishedActivity.this, 60.0f);
            }

            Matrix matrix = new Matrix();
            float scaler = 1.0f;
            if(bitmap.getWidth() > itemMaxEdge) {
                scaler = (float) itemMaxEdge /(float)bitmap.getWidth();
            } else if(bitmap.getHeight() > itemMaxEdge) {
                scaler = (float) itemMaxEdge /(float)bitmap.getHeight();
            }
            matrix.postScale(scaler, scaler);
            Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),bitmap.getHeight(),matrix,true);
            mScaledBitmaps.add(resizeBmp);
            bitmap.recycle();

            notifyDataSetChanged();
        }
    }

    private ImageUploadedResponseHandler mImageUploadResponseHandler;
    private class ImageUploadedResponseHandler extends JsonResponseHandler<ImageUploadedMessage> {
        List<OnsiteDocument> onsiteDocuments = new ArrayList<OnsiteDocument>();
        private int mFileIndex = 0;

        public ImageUploadedResponseHandler() {
            super(ImageUploadedMessage.class);
        }

        @Override
        public void onStart() {
            setLoadingDialog(new LoadingDialog(OrderServiceFinishedActivity.this));
            super.onStart();
        }

        @Override
        public void onFinish() {
            super.onFinish();
            dismissLoadingDialog();
        }

        private void uploadPicture(File file) throws FileNotFoundException {
            getTgfDelegate().getUserCenterHandler()
                    .setUploadFile(file)
                    .setIRequestUrl(UserCenterRequestHandler.UserCenterURL.UPLOAD_IMG_FILE)
                    .doHttpRequest(this);
        }

        /**
         * 开始上传图片列表
         */
        public void uploadFileStart() {
            if(mFileIndex < mPicturesAdapter.getCount()) {
                try {
                    uploadPicture(mPicturesAdapter.getItem(mFileIndex));
                    setLoadingDialogTitle(getString(R.string.tips_uploading_file, mFileIndex, mPicturesAdapter.getCount()));
                    mFileIndex++;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                submitFinishedData();
            }
        }

        /**
         * 上传下一个
         */
        private void uploadFileNext() {
            if(mFileIndex < mPicturesAdapter.getCount()) {
                try {
                    uploadPicture(mPicturesAdapter.getItem(mFileIndex));
                    setLoadingDialogTitle(getString(R.string.tips_uploading_file, mFileIndex, mPicturesAdapter.getCount()));
                    mFileIndex++;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                submitFinishedData();
            }
        }

        private void submitFinishedData() {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                // 上传完成订单数据
                getTgfDelegate().getDeclarationRequestHandler()
                        .setOrderNumber(mOrder.orderNumber)
                        .setServiceMessage(mEtComments.length()>0 ? mEtComments.getText().toString() : "")
                        .setServicePictureUrl("\""+objectMapper.writeValueAsString(onsiteDocuments)+"\"")
                        .setUserToken(mLoginData.userToken)
                        .setSceneId(mLoginData.loginSceneId)
                        .setIRequestUrl(DeclarationRequestHandler.DeclarationURL.SERVICE_ORDER_FINISHED)
                        .doHttpRequest(mSubmitResponseHandler);
            } catch (JsonProcessingException e) {
                getTgfDelegate().postTipsMassage("上传服务订单完成信息失败");
            }
        }

        private String getCurrentTime()  {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        }

        @Override
        public void onSuccess(ImageUploadedMessage jsonObj, String rawJsonResponse) {
            if(jsonObj.statusCode == 0) {
                UploadedFile uploadedFile = jsonObj.data;
                OnsiteDocument onsiteDocument = new OnsiteDocument();
                onsiteDocument.create_time = getCurrentTime();
                onsiteDocument.size = uploadedFile.source.size;
                onsiteDocument.filename = uploadedFile.source.saveName;
                onsiteDocument.url = uploadedFile.url;
                onsiteDocuments.add(onsiteDocument);
                uploadFileNext();
            } else {
                getTgfDelegate().postTipsMassage(R.string.tip_upload_failure);
            }

        }

        @Override
        public void onFailure(int statusCode, Throwable throwable) {
            getTgfDelegate().postTipsMassage(getString(R.string.tip_upload_failure) + ": " );
        }
    }

    /**
     * 上传完成订单数据响应处理
     */
    private SubmitResponseHandler mSubmitResponseHandler;
    private class SubmitResponseHandler extends JsonResponseHandler<StringMessage> {

        public SubmitResponseHandler() {
            super(StringMessage.class);
        }

        @Override
        public void onStart() {
            setLoadingDialog(new LoadingDialog(OrderServiceFinishedActivity.this));
            setLoadingDialogTitle("正在提交数据");
            super.onStart();
        }

        @Override
        public void onFinish() {
            super.onFinish();
            dismissLoadingDialog();

        }
        @Override
        public void onSuccess(StringMessage jsonObj, String rawJsonResponse) {
            if(jsonObj.statusCode == 0) {
                getTgfDelegate().postTipsMassage(R.string.tips_upload_success);
                finish();
            } else {
                getTgfDelegate().postTipsMassage(jsonObj.statusMessage);
            }
        }

        @Override
        public void onFailure(int statusCode, Throwable throwable) {
            getTgfDelegate().postTipsMassage(R.string.tip_upload_failure);
        }
    }

}
