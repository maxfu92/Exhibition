package com.tgf.exhibition.declaration;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.tgf.exhibition.R;
import com.tgf.exhibition.http.handler.DeclarationRequestHandler;
import com.tgf.exhibition.http.handler.JsonResponseHandler;
import com.tgf.exhibition.http.handler.OrderListResponseHandler;
import com.tgf.exhibition.http.json.DeclarationOrder;
import com.tgf.exhibition.http.json.LoginData;
import com.tgf.exhibition.http.msg.StringMessage;
import com.tgf.exhibition.widget.DeclarationOrderView;
import com.tgf.exhibition.widget.LoadingDialog;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OrderToBePaidFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OrderToBePaidFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderToBePaidFragment extends BaseOrderFragment {
    private CheckBox mAllItemChecked;
    private boolean mAllItemCheckedIgnoreChange = false;
    private TextView mTotalPrice1;
    private TextView mTotalPrice2;
    private View mBtnMergePay;

    public OrderToBePaidFragment(){super();}

    public static OrderToBePaidFragment newInstance(LoginData param1) {
        OrderToBePaidFragment fragment = new OrderToBePaidFragment();
        fragment.mLoginData = param1;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_to_be_paid, container, false);
        mAllItemChecked = (CheckBox) view.findViewById(R.id.cb_order_all);
        mTotalPrice1 = (TextView) view.findViewById(R.id.tv_total_all_price_1);
        mTotalPrice1.setText("0");
        mTotalPrice2 = (TextView) view.findViewById(R.id.tv_total_all_price_2);
        mTotalPrice2.setText(".00");
        mAllItemChecked.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mAllItemCheckedIgnoreChange) {
                    return;
                }
                mOderListAdpter.setAllItemsChecked(isChecked);
                updateTotalPriceView();
            }
        });
        mBtnMergePay = view.findViewById(R.id.btn_merge_pay);
        mBtnMergePay.setEnabled(false);

        mBtnMergePay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeclarationOrder[] selectedOrders = mOderListAdpter.getSelectedOrders();
                if(selectedOrders.length > 0) {
                    Intent intent = new Intent(getContext(), OrderPayForActivity.class);
                    intent.putExtra("orders", selectedOrders);
                    startActivity(intent);
                }
            }
        });

        if (mOderListAdpter != null) {
            ListView listView = (ListView) view.findViewById(android.R.id.list);
            listView.setAdapter(mOderListAdpter);
        }
        return view;
    }

    private void updateTotalViews() {
        mAllItemCheckedIgnoreChange = true;
        mAllItemChecked.setChecked(mOderListAdpter.isAllItemSelected());
        updateTotalPriceView();
        mAllItemCheckedIgnoreChange = false;
    }

    private void updateTotalPriceView() {
        Float selectedItemTotalPrice = mOderListAdpter.getSelectedItemTotalPrice();
        mBtnMergePay.setEnabled(selectedItemTotalPrice > 0.0F);
        String totalPrice = selectedItemTotalPrice.toString();
        String[] split = totalPrice.split("\\.");
        if (split.length == 2) {
            mTotalPrice1.setText(split[0]);
            mTotalPrice2.setText("." + split[1]);
        } else {
            mTotalPrice1.setText(split[0]);
            mTotalPrice2.setText(".00");
        }
    }

    private class OrderItemSelectedChangedListener implements DeclarationOrderView.OnItemSelectedChangedListener {

        @Override
        public void onItemSelectedChanged(DeclarationOrderView itemView, DeclarationOrder data, boolean selected) {
            mOderListAdpter.onItemSelectedChanged(itemView, data, selected);
            updateTotalViews();
        }
    }

    /**
     * 付款行为描述
     */
    private class ActionDescripterPayFor implements DeclarationOrderView.OrderItemActionDescripter {

        @Override
        public int getActionViewResId() {
            return android.R.id.button1;
        }

        @Override
        public void setupActionView(View actionView) {
            Button button = (Button) actionView;
            button.setText(R.string.label_pay_for);
        }

        @Override
        public void onActionViewDataRefreshed(View actionView, DeclarationOrder data) {

        }

        @Override
        public void onActionClick(DeclarationOrderView itemView, DeclarationOrder data) {
            Intent intent = new Intent(getContext(), OrderPayForActivity.class);
            intent.putExtra("order", data);
            startActivity(intent);
        }
    }

    /**
     * 取消订单行为描述
     */
    private class ActionDescripterCancelOrder implements DeclarationOrderView.OrderItemActionDescripter {

        @Override
        public int getActionViewResId() {
            return android.R.id.button2;
        }

        @Override
        public void setupActionView(View actionView) {
            Button button = (Button) actionView;
            button.setText(R.string.label_cancel_order);
        }

        @Override
        public void onActionViewDataRefreshed(View actionView, DeclarationOrder data) {

        }

        @Override
        public void onActionClick(DeclarationOrderView itemView, final DeclarationOrder data) {
            new AlertDialog.Builder(getContext())
                    .setCancelable(false)
                    .setTitle(R.string.label_cancel_order)
                    .setMessage(getString(R.string.tips_confirm_cancel_order, data.getOrderNumber()))
                    .setNegativeButton(R.string.label_negative, null)
                    .setPositiveButton(R.string.label_positive, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getDeclarationRequestHandler().setOrderNumber(data.orderNumber)
                                    .setUserToken(mLoginData.userToken)
                                    .setSceneId(mLoginData.loginSceneId)
                                    .setIRequestUrl(DeclarationRequestHandler.DeclarationURL.ORDER_CANCEL)
                                    .doHttpRequest(new CancelOrderResponseHandler());
                            dialog.dismiss();
                        }
                    }).show();

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
            setLoadingDialog(new LoadingDialog(getContext()));
            super.onStart();
        }

        @Override
        public void onFinish() {
            super.onFinish();
            dismissLoadingDialog();
        }
        @Override
        public void onSuccess(StringMessage jsonObj, String rawJsonResponse) {
            if (jsonObj.statusCode == 0) {
                loadOrderListData(); // 刷新数据
            } else {
                postTipsMassage(jsonObj.statusMessage);
            }
        }

        @Override
        public void onFailure(int statusCode, Throwable throwable) {
            postTipsMassage(R.string.tips_data_load_faild);
        }
    }

    /**
     * 加载订单列表：待付款状态
     */
    @Override
    protected void loadOrderListData() {
        requestOrderList("pending", new OrderListResponseHandler.OnOrderListReceivedListener() {
            @Override
            public void onOrderListReceived(List<DeclarationOrder> data) {
                mOderListAdpter = new OderListAdpter(getContext(), data);
                mOderListAdpter.setItemLayoutResId(R.layout.declaration_order_item_df);
                mOderListAdpter.setItemViewSetter(new OderListAdpter.ItemViewSetter() {
                    @Override
                    public void onFinishInflateItemView(View itemView, DeclarationOrder item) {
                        DeclarationOrderView orderItemView = (DeclarationOrderView) itemView;
                        orderItemView.addOrderItemActionDescripter(new ActionDescripterPayFor());
                        orderItemView.addOrderItemActionDescripter(new ActionDescripterCancelOrder());
                        orderItemView.setOnItemSelectedChangedListener(new OrderItemSelectedChangedListener());
                    }

                    @Override
                    public void bindingItemViewData(View itemView, DeclarationOrder item) {
                        DeclarationOrderView orderItemView = (DeclarationOrderView) itemView;
                        orderItemView.bindDeclarationOrderData(item);
                    }
                });

                if (getView() != null) {
                    ListView listView = (ListView) getView().findViewById(android.R.id.list);
                    listView.setAdapter(mOderListAdpter);
                    listView.setOnItemClickListener(mOderListAdpter);
                }
            }
        });
    }
}

