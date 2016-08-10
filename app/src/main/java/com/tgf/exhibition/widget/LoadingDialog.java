package com.tgf.exhibition.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.tgf.exhibition.R;

/**
 * Created by jeff on 2016/5/24.
 */
public class LoadingDialog extends Dialog {
    private static final int CHANGE_TITLE_WHAT = 1;
    private static final int CHNAGE_TITLE_DELAYMILLIS = 300;
    private static final int MAX_SUFFIX_NUMBER = 3;
    private static final char SUFFIX = '.';

    private ImageView iv_route;
    private TextView detail_tv;
    private TextView tv_point;
    private RotateAnimation mAnim;

    public LoadingDialog(Context context) {
        super(context, R.style.LoadingDialog);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.PROGRESS_VISIBILITY_ON);
        setCanceledOnTouchOutside(false);
        init();
    }

    private void init() {
        setContentView(R.layout.loading_dialog_layout);
        iv_route = (ImageView) findViewById(R.id.iv_route);
        detail_tv = (TextView) findViewById(R.id.detail_tv);
        tv_point = (TextView) findViewById(R.id.tv_point);
        setTitle(null);
        initAnim();
        //getWindow().setWindowAnimations(R.anim.alpha_in);
    }

    private void initAnim() {
        // mAnim = new RotateAnimation(360, 0, Animation.RESTART, 0.5f, Animation.RESTART, 0.5f);
        mAnim = new RotateAnimation(0, 360, Animation.RESTART, 0.5f, Animation.RESTART, 0.5f);
        mAnim.setDuration(1500);
        mAnim.setRepeatCount(Animation.INFINITE);
        mAnim.setRepeatMode(Animation.RESTART);
        mAnim.setStartTime(Animation.START_ON_FIRST_FRAME);

        //mAnim.setInterpolator(new LinearInterpolator());
    }

    private Handler handler = new Handler() {
        private int num = -1;

        public void handleMessage(android.os.Message msg) {
            if (msg.what == CHANGE_TITLE_WHAT) {
                StringBuilder builder = new StringBuilder();
                if (num >= MAX_SUFFIX_NUMBER) {
                    num = -1;
                }
                num++;
                if(num == 0) {
                    tv_point.setText("");
                } else {
                    for (int i = 0; i <= num; i++) {
                        builder.append(SUFFIX);
                    }
                    tv_point.setText(builder.toString());
                }
                if (isShowing()) {
                    handler.sendEmptyMessageDelayed(CHANGE_TITLE_WHAT, CHNAGE_TITLE_DELAYMILLIS);
                } else {
                    num = 0;
                }
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 按下了键盘上返回按钮
            dismiss();
            return true;
        }
        return false;
    }

    @Override
    public void show() {//在要用到的地方调用这个方法
        iv_route.startAnimation(mAnim);
        handler.sendEmptyMessage(CHANGE_TITLE_WHAT);
        super.show();
    }

    @Override
    public void dismiss() {
        mAnim.cancel();
        super.dismiss();
    }

    @Override
    public void setTitle(CharSequence title) {
        if (TextUtils.isEmpty(title)) {
            detail_tv.setText("正在加载");
        } else {
            detail_tv.setText(title);
        }
    }

    @Override
    public void setTitle(int titleId) {
        setTitle(getContext().getString(titleId));
    }

    public static void dismissDialog(LoadingDialog loadingDialog) {
        if (null == loadingDialog) {
            return;
        }
        loadingDialog.dismiss();
    }
}
