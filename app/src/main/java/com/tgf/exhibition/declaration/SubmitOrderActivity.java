package com.tgf.exhibition.declaration;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tgf.exhibition.TgfActivity;
import com.tgf.exhibition.R;
import com.tgf.exhibition.http.ResponseMessage;
import com.tgf.exhibition.http.handler.DeclarationRequestHandler;
import com.tgf.exhibition.http.handler.JsonResponseHandler;
import com.tgf.exhibition.http.json.AreaWorkObject;
import com.tgf.exhibition.http.json.DeclarationItem;
import com.tgf.exhibition.http.json.DeclarationOrder;
import com.tgf.exhibition.http.json.DeclarationType;
import com.tgf.exhibition.http.json.LoginData;
import com.tgf.exhibition.http.json.WorkObject;
import com.tgf.exhibition.http.msg.AreaWorkObjectMessage;
import com.tgf.exhibition.http.msg.DeclarationItemMessage;
import com.tgf.exhibition.util.BitmapUtils;
import com.tgf.exhibition.widget.ControlNumberView;
import com.tgf.exhibition.widget.DatetimePickerView;
import com.tgf.exhibition.widget.LoadingDialog;

public class SubmitOrderActivity extends TgfActivity {
    private LoadingDialog mLoadingDialog = null;
    interface OnStepFinishedListener<T> {
        void OnStepFinished(T object);
    }
    interface Step<T> {
        void goStep(OnStepFinishedListener<T> listener);
    }

    private AreaWorkObject[] mAreaWorkObjects;
    private DeclarationItem[] mDeclarationItems;

    private DeclarationType mSelectedDeclarationType;
    private WorkObject mSelectedWorkObject;
    private DeclarationItem mSelectedDeclarationItem;

    private DatetimePickerView mStartDatetimePickerView;
    private DatetimePickerView mEndDatetimePickerView;
    private ControlNumberView mControlNumberView;
    private Float mPrice;
    private Float mDeposit;
    private boolean mIsFreeSource = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSelectedDeclarationType = getIntent().getParcelableExtra("applyType");
        setContentView(R.layout.activity_declaration_submit_order1);
        setTitle(mSelectedDeclarationType.displayTitle);
        loadAreaWorkObject();
    }

    @Override
    protected void onDataRefresh() {
    }

    private void setStepTitle(String title) {
        TextView textView = (TextView)findViewById(R.id.tv_apply_type);
        setViewText(textView, title);
    }

    private void setViewText(TextView textView, String value) {
        if (textView != null) {
            textView.setText(value);
        }
    }

    private void initSubmitViews() {
        setTitle(R.string.title_activity_declaration_order);
        setContentView(R.layout.activity_declaration_submit_order3);

        // 绑定申购科目数据
        TextView textView;
        textView = (TextView)findViewById(R.id.tv_apply_item_name);
        setViewText(textView, mSelectedDeclarationItem.displayTitle);
        textView = (TextView)findViewById(R.id.tv_unit);
        setViewText(textView, mSelectedDeclarationItem.unit);

        textView = (TextView)findViewById(R.id.tv_price);
        mPrice = Float.parseFloat(mSelectedDeclarationItem.price);
        if(mPrice == 0.0f) {
            setViewText(textView, getString(R.string.value_price_free));
        } else {
            setViewText(textView, getString(R.string.value_float_price, mPrice));
        }

        textView = (TextView)findViewById(R.id.tv_deposit);
        setViewText(textView, mSelectedDeclarationItem.deposit);
        mDeposit = Float.parseFloat(mSelectedDeclarationItem.deposit);
        if(mDeposit == 0.0f) {
            setViewText(textView, getString(R.string.value_price_free));
        } else {
            setViewText(textView, getString(R.string.value_float_price, mDeposit));
        }

        textView = (TextView)findViewById(R.id.tv_intro);
        setViewText(textView, mSelectedDeclarationItem.description);

        /// 申购科目订购数量&价格控制组件
        mControlNumberView = (ControlNumberView) findViewById(R.id.cnt_number);
        setControlNumber(1);
        mControlNumberView.setmOnNumberChangedListener(new ControlNumberView.OnNumberChangedListener() {
            @Override
            public void onNumberChangedListener(ControlNumberView controlNumberView, float number) {
                if(number > 0) {
                    findViewById(R.id.btn_submit_declaration).setEnabled(true);
                } else {
                    findViewById(R.id.btn_submit_declaration).setEnabled(false);
                }
                setControlNumber(number);
            }
        });

        // 是否计算作业对象/管理点面积
        View objectSquarePanel = findViewById(R.id.object_square_panel);
        if(mSelectedDeclarationType.isCalcAreaSize() /*&& mSelectedWorkObject.usedSize > 0.0f*/) {
            objectSquarePanel.setVisibility(View.VISIBLE);
            textView = (TextView) findViewById(R.id.object_square);
            setViewText(textView, getString(R.string.object_used_size, mSelectedWorkObject.usedSize));
        } else {
            objectSquarePanel.setVisibility(View.GONE);
        }

        // 订单时间选择组件
        mStartDatetimePickerView = (DatetimePickerView) findViewById(R.id.dpv_start_time);
        mStartDatetimePickerView.setFragmentManager(getFragmentManager());
        mEndDatetimePickerView = (DatetimePickerView) findViewById(R.id.dpv_end_time);
        mEndDatetimePickerView.setFragmentManager(getFragmentManager());

        findViewById(R.id.btn_submit_declaration).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(mStartDatetimePickerView.getTimeInMillis() < mEndDatetimePickerView.getTimeInMillis()) {
                    if(mIsFreeSource) {
                        new AlertDialog.Builder(SubmitOrderActivity.this)
                                .setCancelable(false)
                                .setTitle(R.string.title_activity_declaration_order)
                                .setMessage(R.string.tips_confirm_submit_free)
                                .setNegativeButton(R.string.label_negative2, null)
                                .setPositiveButton(R.string.label_positive2, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        submitOrder();
                                        dialog.dismiss();
                                    }
                                }).show();
                    } else {
                        submitOrder();
                    }
                } else {
                    getTgfDelegate().postTipsMassage(R.string.submit_error_time);
                }
            }
        });
    }

    public void setControlNumber(float number) {
        mControlNumberView.setNumber(number);
        TextView textView;
        Float totalPrice = number * mPrice;
        if(mSelectedDeclarationType.isCalcAreaSize() && mSelectedWorkObject.usedSize > 0.0f) {
            totalPrice *= mSelectedWorkObject.usedSize;
        }
        textView = (TextView)findViewById(R.id.tv_total_price);
        setViewText(textView, totalPrice.toString());
        Float totalDeposit = number * mDeposit;
        textView = (TextView)findViewById(R.id.tv_total_deposit);
        setViewText(textView, totalDeposit.toString());
        Float totalAll = totalPrice + totalDeposit;
        mIsFreeSource = (totalAll == 0.0f);
        textView = (TextView)findViewById(R.id.tv_total_all);
        setViewText(textView, totalAll.toString());
    }

    private LoadingDialog getmLoadingDialog() {
        if(mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(SubmitOrderActivity.this);
        }
        return mLoadingDialog;
    }

    /**
     * 加载区域作业对象
     */
    private void loadAreaWorkObject() {
        LoginData loginData = getTgfDelegate().getLoginData();
        getTgfDelegate().getDeclarationRequestHandler()
                .setUserToken(loginData.userToken)
                .setSceneId(loginData.loginSceneId)
                .setIRequestUrl(DeclarationRequestHandler.DeclarationURL.APPLY_WORK_OBJ)
                .doHttpRequest(new AreaWorkObjectHandler());
    }

    /**
     * 加载申购科目
     */
    private void loadDeclarationItem() {
        LoginData loginData = getTgfDelegate().getLoginData();
        getTgfDelegate().getDeclarationRequestHandler()
                .setDeclarationTypeId(mSelectedDeclarationType.typeId)
                .setUserToken(loginData.userToken)
                .setSceneId(loginData.loginSceneId)
                .setIRequestUrl(DeclarationRequestHandler.DeclarationURL.APPLY_ITEM)
                .doHttpRequest(new DeclarationItemHandler());
    }

    private DeclarationOrder getOrder(String orderNumber) {
        float cnt = mControlNumberView.getNumber();
        DeclarationOrder order = new DeclarationOrder();
        order.orderNumber = orderNumber;
        order.objectId = mSelectedWorkObject.objectId;
        order.objectAddress = mSelectedWorkObject.title;
        order.number = String.valueOf(cnt);
        order.itemTitle = mSelectedDeclarationItem.displayTitle;
        float price = Float.valueOf(mSelectedDeclarationItem.price);
        float totalDeposit = Float.valueOf(mSelectedDeclarationItem.deposit);

        Float totalPrice = cnt * price;
        if(mSelectedDeclarationType.isCalcAreaSize() && mSelectedWorkObject.usedSize > 0.0f) {
            totalPrice *= mSelectedWorkObject.usedSize;
        }
        totalDeposit *= cnt;

        order.totalMoney = String.valueOf(totalPrice + totalDeposit);

        return order;
    }

    /**
     * 提交订单
     */
    private void submitOrder() {
        LoginData loginData = getTgfDelegate().getLoginData();
        getTgfDelegate().getDeclarationRequestHandler()
                .setWorkObjectId(mSelectedWorkObject.objectId)
                .setCheckItemId(mSelectedDeclarationItem.itemId)
                .setPayForNumber(String.valueOf(mControlNumberView.getNumber()))
                .setStartTime(mStartDatetimePickerView.getTime())
                .setEndTime(mEndDatetimePickerView.getTime())
                .setUserToken(loginData.userToken)
                .setSceneId(loginData.loginSceneId)
                .setIRequestUrl(DeclarationRequestHandler.DeclarationURL.APPLY_CREATE_ORDER)
                .doHttpRequest(new OrderSumberHandler());
    }

    /**
     * HTTP响应处理：获取申购科目
     */
    private class DeclarationItemHandler extends JsonResponseHandler<DeclarationItemMessage> {

        public DeclarationItemHandler() {
            super(DeclarationItemMessage.class);
        }

        @Override
        public void onStart() {
            setLoadingDialog(getmLoadingDialog());
            super.onStart();
        }

        @Override
        public void onFinish() {
            super.onFinish();
            dismissLoadingDialog();
        }

        @Override
        public void onSuccess(DeclarationItemMessage jsonObj, String rawJsonResponse) {
            mDeclarationItems = jsonObj.data;
            setContentView(R.layout.activity_declaration_submit_order2);
            setTitle(mSelectedWorkObject.title);
            setStepTitle(mSelectedDeclarationType.displayTitle);
            ListView listView = (ListView) findViewById(android.R.id.list);
            Step step = new SubmitOrderStepTwo(listView, mDeclarationItems);
            step.goStep(new OnStepFinishedListener<DeclarationItem>() {
                @Override
                public void OnStepFinished(DeclarationItem item) {
                    mSelectedDeclarationItem = item;
                    initSubmitViews();
                }
            });
        }

        @Override
        public void onFailure(int statusCode, Throwable throwable) {

        }
    }

    /**
     * HTTP响应处理：获取区域作业对象
     */
    private class AreaWorkObjectHandler extends JsonResponseHandler<AreaWorkObjectMessage> {
        public AreaWorkObjectHandler() {
            super(AreaWorkObjectMessage.class);
        }

        @Override
        public void onStart() {
            setLoadingDialog(getmLoadingDialog());
            super.onStart();
        }

        @Override
        public void onFinish() {
            super.onFinish();
            dismissLoadingDialog();
        }

        @Override
        public void onSuccess(AreaWorkObjectMessage jsonObj, String rawJsonResponse) {
            mAreaWorkObjects = jsonObj.data;
            ListView listView = (ListView) findViewById(android.R.id.list);

            Step step = new SubmitOrderStepOne(listView, mAreaWorkObjects);
            step.goStep(new OnStepFinishedListener<WorkObject>() {
                @Override
                public void OnStepFinished(WorkObject workObject) {
                    mSelectedWorkObject = workObject;
                    loadDeclarationItem();
                }
            });
        }

        @Override
        public void onFailure(int statusCode, Throwable throwable) {
            getTgfDelegate().postTipsMassage(R.string.tips_data_load_faild);
        }
    }

    /**
     * HTTP响应处理：提交申购订单
     */
    private class OrderSumberHandler extends JsonResponseHandler<OrderSubmitResponseMessage> {

        public OrderSumberHandler() {
            super(OrderSubmitResponseMessage.class);
        }

        @Override
        public void onStart() {
            setLoadingDialog(getmLoadingDialog());
            super.onStart();
        }

        @Override
        public void onFinish() {
            super.onFinish();
            dismissLoadingDialog();
        }

        @Override
        public void onSuccess(OrderSubmitResponseMessage jsonObj, String rawJsonResponse) {
            if(jsonObj.statusCode == 0) {
                if(mIsFreeSource) {
                    Intent intent = new Intent(SubmitOrderActivity.this, DeclarationOrderActivity.class);
                    intent.putExtra(DeclarationOrderActivity.ORDER_CATALOG_KEY, DeclarationOrderActivity.ORDER_CATALOG_DFW);
                    SubmitOrderActivity.this.startActivity(intent);
                } else {
                    Intent intent = new Intent(SubmitOrderActivity.this, OrderPayForActivity.class);
                    intent.putExtra("order", getOrder(jsonObj.data.orderNumber));
                    startActivity(intent);
                }
                SubmitOrderActivity.this.onBackPressed();
            } else {
                getTgfDelegate().postTipsMassage(jsonObj.statusMessage);
            }
        }

        @Override
        public void onFailure(int statusCode, Throwable throwable) {
            getTgfDelegate().postTipsMassage(R.string.tips_data_load_faild);
        }
    }

}

class OrderSubmitResponseMessage extends ResponseMessage<OrderSubmitResult> {}
class OrderSubmitResult {
    @JsonProperty("order_num")
    public String orderNumber;
}

abstract class AbsSubmitOrderAdpater extends BaseAdapter {
    protected Context mContext;

    AbsSubmitOrderAdpater(Context context) {
        mContext = context;
    }

    protected int dp2px(float dpValue) {
        return BitmapUtils.dp2px (mContext, dpValue);
    }
}
