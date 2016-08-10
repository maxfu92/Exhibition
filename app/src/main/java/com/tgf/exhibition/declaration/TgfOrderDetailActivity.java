package com.tgf.exhibition.declaration;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tgf.exhibition.TgfActivity;
import com.tgf.exhibition.R;
import com.tgf.exhibition.http.handler.DeclarationRequestHandler;
import com.tgf.exhibition.http.handler.JsonResponseHandler;
import com.tgf.exhibition.http.json.DeclarationOrder;
import com.tgf.exhibition.http.json.DeclarationOrderDetail;
import com.tgf.exhibition.http.json.LoginData;
import com.tgf.exhibition.http.msg.DeclarationOrderDetailMassage;
import com.tgf.exhibition.widget.LoadingDialog;

/**
 * Created by jeff on 2016/5/26.
 */
public abstract class TgfOrderDetailActivity extends TgfActivity {
    protected DeclarationOrder mOrder;
    protected DeclarationOrderDetail mOrderDetail;
    protected LoginData mLoginData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOrder = getIntent().getParcelableExtra("order");
        mLoginData = getTgfDelegate().getLoginData();
        retreiveOrderDetail(mOrder.orderNumber, mLoginData);
    }

    protected void bindImageViewUrlString(ImageView imageView, String url) {
        if(!TextUtils.isEmpty(url) && imageView != null) {
            ImageLoader.getInstance().displayImage(url, imageView);
        }
    }

    /**
     * 获取订单详情
     *
     * @param orderNumber
     * @param loginData
     */
    private final void retreiveOrderDetail(String orderNumber, LoginData loginData) {
        getTgfDelegate().getDeclarationRequestHandler()
                .setOrderNumber(orderNumber)
                .setSceneId(loginData.loginSceneId)
                .setUserToken(loginData.userToken)
                .setIRequestUrl(DeclarationRequestHandler.DeclarationURL.ORDER_DETAIL)
                .doHttpRequest(new OrderDetailHandler());
    }

    /**
     * 订单详情响应处理
     */
    private final class OrderDetailHandler extends JsonResponseHandler<DeclarationOrderDetailMassage> {

        public OrderDetailHandler() {
            super(DeclarationOrderDetailMassage.class);
        }

        @Override
        public void onStart() {
            setLoadingDialog(new LoadingDialog(TgfOrderDetailActivity.this));
            super.onStart();
        }

        @Override
        public void onFinish() {
            super.onFinish();
            dismissLoadingDialog();
        }

        @Override
        public void onSuccess(DeclarationOrderDetailMassage jsonObj, String rawJsonResponse) {
            if (jsonObj.statusCode == 0) {
                mOrderDetail = jsonObj.data;
                setContentView(R.layout.activity_order_detail);
                onOrderDetailRetreivedInternal(jsonObj.data);
            } else {
                getTgfDelegate().postTipsMassage(jsonObj.statusMessage);
            }
        }

        @Override
        public void onFailure(int statusCode, Throwable throwable) {
            getTgfDelegate().postTipsMassage(R.string.tips_network_error);
        }
    }

    private void setMoney(int resId, String stringMoney) {
        getTgfDelegate().setTextViewText((TextView) findViewById(resId), getString(R.string.label_total_price, Float.valueOf(stringMoney)));
    }

    protected abstract void onOrderDetailRetreived(DeclarationOrderDetail orderDetail);
    private void onOrderDetailRetreivedInternal(DeclarationOrderDetail orderDetail){
        getTgfDelegate().setTextViewText((TextView) findViewById(R.id.tv_order_num), orderDetail.getOrderNumber());

        bindImageViewUrlString((ImageView) findViewById(R.id.iv_obj_icon), orderDetail.objectIcon);
        getTgfDelegate().setTextViewText((TextView) findViewById(R.id.tv_obj_name), orderDetail.objectAddress);

        bindImageViewUrlString((ImageView) findViewById(R.id.iv_decalare_item_icon), orderDetail.typeIcon);
        getTgfDelegate().setTextViewText((TextView) findViewById(R.id.tv_decalare_item_desc), orderDetail.itemTitle);
        getTgfDelegate().setTextViewText((TextView) findViewById(R.id.tv_pay_count), "x " + orderDetail.number);

//        setTextViewText((TextView) findViewById(R.id.tv_total_price), getString(R.string.label_total_price, Float.valueOf(orderDetail.totalMoney)));
//        setTextViewText((TextView) findViewById(R.id.tv_total_deposit), "￥ " + orderDetail.totalDepositMoney);
//        setTextViewText((TextView) findViewById(R.id.tv_paid_price), "￥ " + orderDetail.totalUseMoney);
        setMoney(R.id.tv_total_price, orderDetail.totalMoney);
        setMoney(R.id.tv_total_deposit, orderDetail.totalDepositMoney);
        setMoney(R.id.tv_paid_price, orderDetail.totalUseMoney);

        getTgfDelegate().setTextViewText((TextView) findViewById(R.id.tv_start_time), orderDetail.startTime);
        getTgfDelegate().setTextViewText((TextView) findViewById(R.id.tv_end_time), orderDetail.endTime);

        bindImageViewUrlString((ImageView) findViewById(R.id.iv_person_photo), orderDetail.staffAvatar);
        getTgfDelegate().setTextViewText((TextView) findViewById(R.id.tv_person_name), orderDetail.staffRealname);

        onOrderDetailRetreived(orderDetail);
    }
}
