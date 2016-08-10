package com.tgf.exhibition.declaration;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.tgf.exhibition.TgfActivity;
import com.tgf.exhibition.R;
import com.tgf.exhibition.http.handler.DeclarationRequestHandler;
import com.tgf.exhibition.http.handler.JsonResponseHandler;
import com.tgf.exhibition.http.json.DeclarationOrder;
import com.tgf.exhibition.http.json.LoginData;
import com.tgf.exhibition.http.msg.AccountYuEMessage;
import com.tgf.exhibition.http.msg.StringMessage;
import com.tgf.exhibition.widget.LoadingDialog;

public class OrderPayForActivity extends TgfActivity {
    private LoginData mLoginData;
    private Parcelable[] mOrders = null;
    private Float mTotalPrice = new Float(0.00f);
    private Float mPrepayYE = new Float(0.00f);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_pay_for);

        Bundle extras = getIntent().getExtras();
        if (extras.containsKey("order")) {
            mOrders = new DeclarationOrder[1];
            mOrders[0] = extras.getParcelable("order");
        } else if (extras.containsKey("orders")) {
            mOrders = extras.getParcelableArray("orders");
        } else {
            getTgfDelegate().postTipsMassage(R.string.tips_no_pay_order_info);
            return;
        }

        mLoginData = getTgfDelegate().getLoginData();

        LinearLayout ordersPanle = (LinearLayout) findViewById(R.id.orders_title_panel);
        DeclarationOrder order;
        for (Parcelable parcelable : mOrders) {
            order = (DeclarationOrder) parcelable;
            mTotalPrice += Float.parseFloat(order.totalMoney);

            View view = View.inflate(this, R.layout.declaration_order_title, null);
            TextView title = (TextView) view.findViewById(R.id.tv_order_title);
            title.setText(order.itemTitle);
            ordersPanle.addView(view);
        }

        TextView totalPrice = (TextView) findViewById(R.id.tv_total_price);
        String exchange = getString(R.string.label_total_price, mTotalPrice);
        totalPrice.setText(Html.fromHtml(exchange));

        // 查询余额
        getTgfDelegate().getDeclarationRequestHandler()
                .setWorkObjectId(((DeclarationOrder) mOrders[0]).objectId)
                .setUserToken(mLoginData.userToken)
                .setSceneId(mLoginData.loginSceneId)
                .setIRequestUrl(DeclarationRequestHandler.DeclarationURL.APPLY_REMAIND_MONEY)
                .doHttpRequest(new AccountYuEHandler());
        findViewById(R.id.btn_confirm_payfor).setEnabled(false);
        findViewById(R.id.radio_wxzf).setEnabled(false);
        findViewById(R.id.radio_yfk).setEnabled(false);
        findViewById(R.id.btn_confirm_payfor).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                gotoPrepayForThis();
//                RadioGroup radioGroup = (RadioGroup) findViewById(R.id.rg_pay_mathod);
//                int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
//                switch (checkedRadioButtonId) {
//                    case R.id.radio_wxzf:
//                        // TODO: 微信支付
//                        gotoWXPayForThis();
//                        break;
//                    case R.id.radio_yfk:
//                    default:
//                        gotoPrepayForThis();
//                        break;
//                }
            }
        });

        // TODO: 临时方案，后面等微信支付可以使用后需要删除这段逻辑
        RadioButton radioYFK = (RadioButton) findViewById(R.id.radio_yfk);
        radioYFK.setChecked(true);
        radioYFK.setEnabled(false);
    }

    @Override
    protected void onDataRefresh() {
    }

    /**
     * 执行微信支付逻辑
     */
    private void gotoWXPayForThis() {
        getTgfDelegate().postTipsMassage("微信支付! 开发中。。。");
    }

    /**
     * 执行预支付逻辑
     */
    private void gotoPrepayForThis() {
        if (mOrders.length == 1) {
            if (mPrepayYE < mTotalPrice) {
                getTgfDelegate().postTipsMassage("余额不足");
                return;
            }
            final DeclarationOrder order = (DeclarationOrder) mOrders[0];
            new AlertDialog.Builder(OrderPayForActivity.this)
                    .setCancelable(true)
                    .setMessage(R.string.tips_confirm_payfor_order)
                    .setNegativeButton(R.string.label_negative2, null)
                    .setPositiveButton(R.string.label_positive2, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getTgfDelegate().getDeclarationRequestHandler()
                                    .setOrderNumber(order.orderNumber)
                                    .setUserToken(mLoginData.userToken)
                                    .setSceneId(mLoginData.loginSceneId)
                                    .setIRequestUrl(DeclarationRequestHandler.DeclarationURL.APPLY_PAY_PY_PREPAID)
                                    .doHttpRequest(new PrepayForHandler());
                            dialog.dismiss();
                        }
                    }).show();

        }
    }
    private class PrepayForHandler extends JsonResponseHandler<StringMessage> {

        public PrepayForHandler() {
            super(StringMessage.class);
        }

        @Override
        public void onStart() {
            setLoadingDialog(new LoadingDialog(OrderPayForActivity.this));
            super.onStart();
        }

        @Override
        public void onFinish() {
            super.onFinish();
            dismissLoadingDialog();
        }

        @Override
        public void onSuccess(StringMessage jsonObj, String rawJsonResponse) {
            if (jsonObj.statusCode != 0) {
                getTgfDelegate().postTipsMassage(jsonObj.statusMessage);
            }
            gotoResultPage(jsonObj.statusCode == 0);
        }

        @Override
        public void onFailure(int statusCode, Throwable throwable) {
            gotoResultPage(false);
        }

        private void gotoResultPage(boolean success) {
            Intent intent = new Intent(OrderPayForActivity.this, OrderPaidFinishedActivity.class);
            intent.putExtra("payForResult", success);
            startActivity(intent);
            OrderPayForActivity.this.onBackPressed();
        }
    }

    /**
     * 余额查询结果处理
     */
    private class AccountYuEHandler extends JsonResponseHandler<AccountYuEMessage> {
        public AccountYuEHandler() {
            super(AccountYuEMessage.class);
        }

        @Override
        public void onStart() {
            setLoadingDialog(new LoadingDialog(OrderPayForActivity.this));
            super.onStart();
        }

        @Override
        public void onFinish() {
            super.onFinish();
            dismissLoadingDialog();
        }

        @Override
        public void onSuccess(AccountYuEMessage jsonObj, String rawJsonResponse) {
            TextView tvYe = (TextView) findViewById(R.id.tv_ye);
            RadioButton radioWXZF = (RadioButton) findViewById(R.id.radio_wxzf);
            RadioButton radioYFK = (RadioButton) findViewById(R.id.radio_yfk);

            if (jsonObj.statusCode == 0) {
                findViewById(R.id.btn_confirm_payfor).setEnabled(true);
                findViewById(R.id.radio_wxzf).setEnabled(true);
                findViewById(R.id.radio_yfk).setEnabled(true);
                mPrepayYE = Float.valueOf(jsonObj.data.yuE);
                tvYe.setText(getString(R.string.label_remaind_money, mPrepayYE));
                // 余额不足是，预支付的Radio项置灰

                if (mPrepayYE < mTotalPrice) {
                    radioYFK.setEnabled(false);
                    radioYFK.setChecked(false);

                    radioWXZF.setChecked(true);
                } else {
                    radioYFK.setChecked(true);
                }
            } else if (jsonObj.statusCode == 8002) {
                tvYe.setText("("+jsonObj.statusMessage+")");
                radioWXZF.setEnabled(true);
                radioWXZF.setChecked(true);
                findViewById(R.id.btn_confirm_payfor).setEnabled(true);
            } else {
                getTgfDelegate().postTipsMassage(jsonObj.statusMessage);
            }
        }

        @Override
        public void onFailure(int statusCode, Throwable throwable) {
            getTgfDelegate().postTipsMassage(R.string.tips_network_error);
        }
    }
}
