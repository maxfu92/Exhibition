package com.tgf.exhibition.declaration;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tgf.exhibition.TgfActivity;
import com.tgf.exhibition.R;
import com.tgf.exhibition.http.handler.DeclarationRequestHandler;
import com.tgf.exhibition.http.handler.DeclarationRequestHandler.DeclarationURL;
import com.tgf.exhibition.http.handler.JsonResponseHandler;
import com.tgf.exhibition.http.json.LoginData;
import com.tgf.exhibition.http.json.NewOrderStatusCount;
import com.tgf.exhibition.http.json.XCScene;
import com.tgf.exhibition.http.msg.DeclarationTypeMessage;
import com.tgf.exhibition.http.msg.NewOrderStatusCountMessage;
import com.tgf.exhibition.widget.BadgeView;
import com.tgf.exhibition.widget.LoadingDialog;
import com.tgf.exhibition.widget.MGridView;

import java.util.concurrent.atomic.AtomicInteger;

public class DeclarationActivity extends TgfActivity {
    private XCScene mXCScene;
    private NewOrderStatusCount mNewOrderStatusCount;
    private LoadingDialogWrapper loadingDialog;

    private class LoadingDialogWrapper {
        LoadingDialog loadingDialog;
        AtomicInteger showers = new AtomicInteger(0);

        LoadingDialogWrapper(Context context) {
            loadingDialog = new LoadingDialog(context);
        }

        public void show() {
            synchronized (loadingDialog) {
                showers.incrementAndGet();
                loadingDialog.show();
            }
        }

        public void dismiss() {
            synchronized (loadingDialog) {
                if (showers.decrementAndGet() == 0) {
                    loadingDialog.dismiss();
                }
            }
        }
    }

    private LoadingDialogWrapper getRefreshLoadingDialog() {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialogWrapper(this);
        }
        return loadingDialog;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mNewOrderMassageCount.setAutoLoading(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mXCScene = getTgfDelegate().getXcScene();
        if (mXCScene == null) {
            onBackPressed();
            return;
        }

        setContentView(R.layout.activity_declaration);
        initSceneTitleView();

        loadOrderPeddingStatusCount(mNewOrderMassageCount);
        findViewById(R.id.layout_dfk).setOnClickListener(mOrderCatalogListener);
        findViewById(R.id.layout_dfw).setOnClickListener(mOrderCatalogListener);
        findViewById(R.id.layout_dqr).setOnClickListener(mOrderCatalogListener);
        findViewById(R.id.layout_dpj).setOnClickListener(mOrderCatalogListener);
    }

    private void initSceneTitleView() {
        ImageView sceneLogo = (ImageView) findViewById(R.id.iv_scene_logo);
        if(!TextUtils.isEmpty(mXCScene.sceneIconUrl)) {
            ImageLoader.getInstance().displayImage(mXCScene.sceneIconUrl, sceneLogo);
        }

        TextView tvSceneName = (TextView) findViewById(R.id.tv_scenes);
        if (tvSceneName != null) {
            tvSceneName.setText(mXCScene.sceneTitle);
        }
    }

    @Override
    protected void onDataRefresh() {
        if (mXCScene != null) {
            mNewOrderMassageCount.setAutoLoading(true);
            loadOrderPeddingStatusCount(mNewOrderMassageCount);
            loadApplyTypeData();
        }
    }

    /**
     * 加载申报订单待处理状态数量
     */
    private void loadOrderPeddingStatusCount(NewOrderMassageCount newOrderMassageCount) {
        LoginData loginData = getTgfDelegate().getLoginData();
        DeclarationRequestHandler iHttpHandler = getTgfDelegate().getDeclarationRequestHandler()
                .setIRequestUrl(DeclarationURL.APPLY_NEW_ORDER_MSG);
        iHttpHandler.setUserToken(loginData.userToken)
                .setSceneId(loginData.loginSceneId);
        iHttpHandler.doHttpRequest(newOrderMassageCount);
    }

    /**
     * 加载申报类型
     */
    private void loadApplyTypeData() {
        LoginData loginData = getTgfDelegate().getLoginData();
        DeclarationRequestHandler iHttpHandler = getTgfDelegate().getDeclarationRequestHandler()
                .setIRequestUrl(DeclarationURL.APPLY_TYPE);
        iHttpHandler.setUserToken(loginData.userToken)
                .setSceneId(loginData.loginSceneId);
        iHttpHandler.doHttpRequest(mApplyTypeHandler);
    }

    private View.OnClickListener mOrderCatalogListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), DeclarationOrderActivity.class);
            switch (v.getId()) {
                case R.id.layout_dfk:
                    intent.putExtra(DeclarationOrderActivity.ORDER_CATALOG_KEY, DeclarationOrderActivity.ORDER_CATALOG_DFK);
                    break;
                case R.id.layout_dfw:
                    intent.putExtra(DeclarationOrderActivity.ORDER_CATALOG_KEY, DeclarationOrderActivity.ORDER_CATALOG_DFW);
                    break;
                case R.id.layout_dqr:
                    intent.putExtra(DeclarationOrderActivity.ORDER_CATALOG_KEY, DeclarationOrderActivity.ORDER_CATALOG_DQR);
                    break;
                case R.id.layout_dpj:
                    intent.putExtra(DeclarationOrderActivity.ORDER_CATALOG_KEY, DeclarationOrderActivity.ORDER_CATALOG_DPJ);
                    break;
                default:
                    intent.putExtra(DeclarationOrderActivity.ORDER_CATALOG_KEY, DeclarationOrderActivity.ORDER_CATALOG_DFK);
            }
            startActivity(intent);
        }
    };

    /**
     * HTTP响应处理：获取申购类型
     */
    private ApplyTypeHandler mApplyTypeHandler = new ApplyTypeHandler();
    private class ApplyTypeHandler extends JsonResponseHandler<DeclarationTypeMessage> {
        public ApplyTypeHandler() {
            super(DeclarationTypeMessage.class);
        }

        @Override
        public void onStart() {
            super.onStart();
            getRefreshLoadingDialog().show();
        }

        @Override
        public void onFinish() {
            super.onFinish();
            getRefreshLoadingDialog().dismiss();
        }

        @Override
        public void onSuccess(DeclarationTypeMessage jsonObj, String rawJsonResponse) {
            MGridView gridview = (MGridView) findViewById(R.id.gridview);
            DeclarationItemGridAdapter declarationItemGridAdapter = new DeclarationItemGridAdapter(DeclarationActivity.this, jsonObj.data);
            gridview.setAdapter(declarationItemGridAdapter);
            gridview.setOnItemClickListener(declarationItemGridAdapter);
        }

        @Override
        public void onFailure(int statusCode, Throwable throwable) {
            getTgfDelegate().postTipsMassage(R.string.tips_data_load_faild);
        }
    }

    /**
     * HTTP响应处理：申购订单状态更新数
     */
    private NewOrderMassageCount mNewOrderMassageCount = new NewOrderMassageCount();
    private class NewOrderMassageCount extends JsonResponseHandler<NewOrderStatusCountMessage>  implements Runnable {
        private boolean showDialog = true;

        public NewOrderMassageCount() {
            super(NewOrderStatusCountMessage.class);
        }

        private void showBadgeIfNeeded(View target, String stringCount) {
            BadgeView badge = (BadgeView) target.getTag();
            int count = Integer.parseInt(stringCount);
            if (count > 0) {
                if (badge == null) {
                    badge = new BadgeView(DeclarationActivity.this, target);
                    badge.setBadgeMargin(0);
                    target.setTag(badge);
                }
                badge.setText(stringCount);
                badge.show();
            } else {
                badge = (BadgeView) target.getTag();
                if (badge != null) {
                    badge.hide(true);
                }
            }
        }

        @Override
        public void onStart() {
            super.onStart();
            if(showDialog) {
                getRefreshLoadingDialog().show();
            }
        }

        @Override
        public void onFinish() {
            super.onFinish();
            if(showDialog) {
                getRefreshLoadingDialog().dismiss();
            }
        }

        @Override
        public void onSuccess(NewOrderStatusCountMessage jsonObj, String rawJsonResponse) {
            if (jsonObj.statusCode == 0) {
                mNewOrderStatusCount = jsonObj.data;
                showBadgeIfNeeded(findViewById(R.id.iv_dfk), mNewOrderStatusCount.penddingPayCount);
                showBadgeIfNeeded(findViewById(R.id.iv_dfw), mNewOrderStatusCount.penddingServiceCount);
                showBadgeIfNeeded(findViewById(R.id.iv_dqr), mNewOrderStatusCount.penddingConfirmCount);
                showBadgeIfNeeded(findViewById(R.id.iv_dpj), mNewOrderStatusCount.penddingCommentCount);
            } else {
                getTgfDelegate().postTipsMassage(jsonObj.statusMessage);
            }
            if(mEnableAutoLoading) {
                findViewById(R.id.iv_dpj).postDelayed(this, 30 * DateUtils.SECOND_IN_MILLIS);
            }
        }

        @Override
        public void onFailure(int statusCode, Throwable throwable) {
            getTgfDelegate().postTipsMassage(R.string.tips_data_load_faild);
        }

        @Override
        public void run() {
            showDialog = false;
            if(mEnableAutoLoading) {
                loadOrderPeddingStatusCount(this);
            }
        }

        private boolean mEnableAutoLoading = true;
        public void setAutoLoading(boolean enableAutoLoading) {
            mEnableAutoLoading = enableAutoLoading;
        }
    }
}
