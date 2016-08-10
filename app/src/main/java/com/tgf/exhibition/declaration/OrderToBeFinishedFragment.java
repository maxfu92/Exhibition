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
import android.widget.ListView;

import com.tgf.exhibition.R;
import com.tgf.exhibition.TgfActivity;
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
 * {@link OrderToBeFinishedFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OrderToBeFinishedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderToBeFinishedFragment extends BaseOrderFragment {

    public OrderToBeFinishedFragment() {
        super();
    }

    public static OrderToBeFinishedFragment newInstance(LoginData param1) {
        OrderToBeFinishedFragment fragment = new OrderToBeFinishedFragment();
        fragment.mLoginData = param1;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_layout, container, false);
        if (mOderListAdpter != null) {
            ListView listView = (ListView) view.findViewById(android.R.id.list);
            listView.setAdapter(mOderListAdpter);
        }
        return view;
    }

    /**
     * 删除订单行为描述
     */
    private ActionDescripterDeleteOrder mActionDescripterDeleteOrder = new ActionDescripterDeleteOrder();
    private class ActionDescripterDeleteOrder implements DeclarationOrderView.OrderItemActionDescripter {

        @Override
        public int getActionViewResId() {
            return android.R.id.button3;
        }

        @Override
        public void setupActionView(View actionView) {
            Button button = (Button) actionView;
            button.setText(R.string.label_delete_order);
        }

        @Override
        public void onActionViewDataRefreshed(View actionView, DeclarationOrder data) {

        }

        @Override
        public void onActionClick(DeclarationOrderView itemView, final DeclarationOrder data) {
            new AlertDialog.Builder(getContext())
                    .setCancelable(false)
                    .setTitle(R.string.label_delete_order)
                    .setMessage(getString(R.string.tips_confirm_delete_order, data.getOrderNumber()))
                    .setNegativeButton(R.string.label_negative, null)
                    .setPositiveButton(R.string.label_positive, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getDeclarationRequestHandler().setOrderNumber(data.orderNumber)
                                    .setUserToken(mLoginData.userToken)
                                    .setSceneId(mLoginData.loginSceneId)
                                    .setIRequestUrl(DeclarationRequestHandler.DeclarationURL.ORDER_DELETE)
                                    .doHttpRequest(new DeleteOrderResponseHandler());
                            dialog.dismiss();
                        }
                    }).show();
        }
    }

    private class DeleteOrderResponseHandler extends JsonResponseHandler<StringMessage> {

        public DeleteOrderResponseHandler() {
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
                ((TgfActivity)getActivity()).getTgfDelegate().postTipsMassage(jsonObj.statusMessage);
            }
        }

        @Override
        public void onFailure(int statusCode, Throwable throwable) {
            ((TgfActivity)getActivity()).getTgfDelegate().postTipsMassage(R.string.tips_network_error);
        }
    }

    /**
     * 投诉行为描述
     */
    private ActionDescripterComplaints mActionDescripterComplaints = new ActionDescripterComplaints() {
        @Override
        public int getActionViewResId() {
            return android.R.id.button2;
        }

        @Override
        public void onActionViewDataRefreshed(View actionView, DeclarationOrder data) {

        }
    };

    /**
     * 服务评价行为描述
     */
    private ActionDescripterFeedback mActionDescripterFeedback = new ActionDescripterFeedback();
    private class ActionDescripterFeedback implements DeclarationOrderView.OrderItemActionDescripter {

        @Override
        public int getActionViewResId() {
            return android.R.id.button1;
        }

        @Override
        public void setupActionView(View actionView) {
            Button button = (Button) actionView;
            button.setText(R.string.label_feedback);
        }

        @Override
        public void onActionViewDataRefreshed(View actionView, DeclarationOrder data) {
            Button button = (Button) actionView;
            boolean isComment = "Y".equals(data.isComment);
            button.setEnabled(!isComment);
            button.setText(isComment?R.string.label_feedbacked:R.string.label_feedback);
            button.setVisibility(isComment?View.GONE:View.VISIBLE);
        }

        @Override
        public void onActionClick(DeclarationOrderView itemView, DeclarationOrder data) {
            Intent intent = new Intent(getContext(), ServiceEvaluationActivity.class);
            intent.putExtra("order", data);
            startActivity(intent);
        }
    }

    /**
     * 加载订单列表：已完成
     */
    @Override
    protected void loadOrderListData() {
        requestOrderList("signin", new OrderListResponseHandler.OnOrderListReceivedListener() {
            @Override
            public void onOrderListReceived(List<DeclarationOrder> data) {
                mOderListAdpter = new OderListAdpter(getContext(), data);
                mOderListAdpter.setItemLayoutResId(R.layout.declaration_order_item);
                mOderListAdpter.setItemViewSetter(new OderListAdpter.ItemViewSetter() {
                    @Override
                    public void onFinishInflateItemView(View itemView, DeclarationOrder item) {
                        DeclarationOrderView orderItemView = (DeclarationOrderView) itemView;
                        orderItemView.findViewById(android.R.id.button3).setVisibility(View.VISIBLE);
                        orderItemView.addOrderItemActionDescripter(mActionDescripterFeedback);
                        orderItemView.addOrderItemActionDescripter(mActionDescripterComplaints);
                        orderItemView.addOrderItemActionDescripter(mActionDescripterDeleteOrder);
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
