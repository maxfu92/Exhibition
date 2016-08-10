package com.tgf.exhibition.declaration;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tgf.exhibition.R;
import com.tgf.exhibition.TgfDelegate;
import com.tgf.exhibition.http.handler.DeclarationRequestHandler;
import com.tgf.exhibition.http.handler.JsonResponseHandler;
import com.tgf.exhibition.http.json.DeclarationOrderDetail;
import com.tgf.exhibition.http.json.OrderProgress;
import com.tgf.exhibition.http.msg.StringMessage;
import com.tgf.exhibition.util.BitmapUtils;
import com.tgf.exhibition.util.SystemInvoker;
import com.tgf.exhibition.widget.LoadingDialog;
import com.tgf.exhibition.widget.VTimeLineView;

public class OrderDetailActivity extends TgfOrderDetailActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDataRefresh() {}

    @Override
    protected void onOrderDetailRetreived(DeclarationOrderDetail orderDetail) {
        TgfDelegate tgfDelegate = getTgfDelegate();
        tgfDelegate.setTextViewText((TextView) findViewById(R.id.tv_order_status), mOrder.getOrderStatusDisplayName(this));

        tgfDelegate.setTextViewText((TextView) findViewById(R.id.tv_person_title), getString(R.string.label_person_title1));
        bindImageViewUrlString((ImageView) findViewById(R.id.iv_person_photo), orderDetail.staffAvatar);
        tgfDelegate.setTextViewText((TextView) findViewById(R.id.tv_person_name), orderDetail.staffRealname);
        findViewById(R.id.action_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SystemInvoker.launchDailPage(OrderDetailActivity.this, mOrderDetail.staffTel);
            }
        });

        if(orderDetail.progresses != null) {
            findViewById(R.id.tv_detail_lable).setVisibility(View.VISIBLE);
            tgfDelegate.setTextViewText((TextView) findViewById(R.id.tv_detail_lable), getString(R.string.label_detail_title1));
            LinearLayout container = (LinearLayout) findViewById(R.id.order_progress_container);
            container.setVisibility(View.VISIBLE);

            VTimeLineView vTimeLineView;
            OrderProgress orderProgress;
            for (int i = 0; i < orderDetail.progresses.length; i++) {
                orderProgress = orderDetail.progresses[i];
                vTimeLineView = (VTimeLineView) View.inflate(OrderDetailActivity.this, R.layout.declaration_order_progress_item, null);
                ViewGroup.LayoutParams layoutParams = vTimeLineView.getLayoutParams();
                if(layoutParams == null) {
                    layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, BitmapUtils.dp2px (OrderDetailActivity.this, 60));
                    vTimeLineView.setLayoutParams(layoutParams);
                } else {
                    layoutParams.height = BitmapUtils.dp2px (OrderDetailActivity.this, 60);
                }

                bindDataToTimeLineView(vTimeLineView, orderProgress, i, orderDetail.progresses.length);
                container.addView(vTimeLineView, layoutParams);
            }
        } else {
            findViewById(R.id.tv_detail_lable).setVisibility(View.GONE);
            findViewById(R.id.order_progress_container).setVisibility(View.GONE);
        }

        FrameLayout actionContainer = (FrameLayout) findViewById(R.id.action_pannel_container);
        actionContainer.removeAllViews();
        View actions = View.inflate(OrderDetailActivity.this, R.layout.declaration_order_action, actionContainer);
        bindActins(actions, orderDetail);
    }

    private void bindDataToTimeLineView(VTimeLineView vTimeLineView, OrderProgress item, int position, int count) {
        vTimeLineView.setUperLineDisplay((position != 0));
        vTimeLineView.setActived(position == (count - 1));
        vTimeLineView.setDividingLineDisplay(position != (count - 1));
        vTimeLineView.setLowerLineDisplay(position != (count - 1));

        vTimeLineView.setTitle(item.title);
        vTimeLineView.setTitle2(item.create_time);
        vTimeLineView.setSummery(item.message);
    }

    private void bindActins(View container, DeclarationOrderDetail orderDetail) {
        Button button;
        button = (Button) container.findViewById(android.R.id.button1);
        if("N".equals(orderDetail.isComment) && "signin".equals(orderDetail.status)) {
            button.setVisibility(View.VISIBLE);
            setupActionButton(button, R.string.label_feedback, mActionsListener);
        } else {
            button.setVisibility(View.GONE);
        }

        button = (Button) container.findViewById(android.R.id.button2);
        setupActionButton(button, R.string.label_complaints, mActionsListener);

        button = (Button) container.findViewById(android.R.id.button3);
        setupActionButton(button, R.string.label_cancel_order, mActionsListener);
        button.setEnabled("pending".equals(orderDetail.status));
        if("pending".equals(orderDetail.status)) {
            button.setVisibility(View.VISIBLE);
        } else {
            button.setVisibility(View.GONE);
        }
    }

    private void setupActionButton(Button button, int titleResId, ActionsListener listener) {
        if (button != null) {
            button.setText(titleResId);
            button.setOnClickListener(listener);
        }
    }

    private ActionsListener mActionsListener = new ActionsListener();

    private class ActionsListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case android.R.id.button1: //评价
                    Intent intent = new Intent(OrderDetailActivity.this, ServiceEvaluationActivity.class);
                    intent.putExtra("order", mOrder);
                    startActivity(intent);
                    break;
                case android.R.id.button2: //投诉
                    SystemInvoker.launchDailPage(OrderDetailActivity.this, mOrderDetail.checkerTel);
                    break;
                case android.R.id.button3: //取消订单
                    new AlertDialog.Builder(OrderDetailActivity.this)
                            .setCancelable(false)
                            .setTitle(R.string.label_cancel_order)
                            .setMessage(getString(R.string.tips_confirm_cancel_order, mOrderDetail.getOrderNumber()))
                            .setNegativeButton(R.string.label_negative, null)
                            .setPositiveButton(R.string.label_positive, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    getTgfDelegate().getDeclarationRequestHandler()
                                            .setOrderNumber(mOrderDetail.getOrderNumber())
                                            .setUserToken(mLoginData.userToken)
                                            .setSceneId(mLoginData.loginSceneId)
                                            .setIRequestUrl(DeclarationRequestHandler.DeclarationURL.ORDER_CANCEL)
                                            .doHttpRequest(new CancelOrderResponseHandler());
                                    dialog.dismiss();
                                }
                            }).show();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 取消订单结果响应处理
     */
    private class CancelOrderResponseHandler extends JsonResponseHandler<StringMessage> {

        public CancelOrderResponseHandler() {
            super(StringMessage.class);
        }

        @Override
        public void onStart() {
            setLoadingDialog(new LoadingDialog(OrderDetailActivity.this));
            super.onStart();
        }

        @Override
        public void onFinish() {
            super.onFinish();
            dismissLoadingDialog();
        }
        @Override
        public void onSuccess(StringMessage jsonObj, String rawJsonResponse) {
            getTgfDelegate().postTipsMassage(jsonObj.statusMessage);
            if (jsonObj.statusCode == 0) {
                onBackPressed();
            }
        }

        @Override
        public void onFailure(int statusCode, Throwable throwable) {
            getTgfDelegate().postTipsMassage(R.string.tips_data_load_faild);
        }
    }
}
