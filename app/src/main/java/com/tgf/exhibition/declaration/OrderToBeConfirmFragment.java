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
import com.tgf.exhibition.http.handler.DeclarationRequestHandler;
import com.tgf.exhibition.http.handler.JsonResponseHandler;
import com.tgf.exhibition.http.handler.OrderListResponseHandler;
import com.tgf.exhibition.http.json.DeclarationOrder;
import com.tgf.exhibition.http.json.LoginData;
import com.tgf.exhibition.http.msg.StringMessage;
import com.tgf.exhibition.util.SystemInvoker;
import com.tgf.exhibition.widget.DeclarationOrderView;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OrderToBeConfirmFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OrderToBeConfirmFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderToBeConfirmFragment extends BaseOrderFragment {
    public OrderToBeConfirmFragment() {
        super();
    }

    public static OrderToBeConfirmFragment newInstance(LoginData param1) {
        OrderToBeConfirmFragment fragment = new OrderToBeConfirmFragment();
        fragment.mLoginData = param1;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_layout, container, false);
        if(mOderListAdpter != null) {
            ListView listView = (ListView) view.findViewById(android.R.id.list);
            listView.setAdapter(mOderListAdpter);
        }
        return view;
    }

    /**
     * 签收行为描述
     */
    private class ActionDescripterSigned implements DeclarationOrderView.OrderItemActionDescripter {

        @Override
        public int getActionViewResId() {
            return android.R.id.button1;
        }

        @Override
        public void setupActionView(View actionView) {
            Button button = (Button) actionView;
            button.setText(R.string.label_signed);
        }

        @Override
        public void onActionViewDataRefreshed(View actionView, DeclarationOrder data) {

        }

        @Override
        public void onActionClick(DeclarationOrderView itemView, final DeclarationOrder data) {
            new AlertDialog.Builder(getContext())
                    .setCancelable(true)
                    .setMessage(R.string.tips_order_signed_up)
                    .setNegativeButton(R.string.label_negative2, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //SystemInvoker.launchDailPage(getContext(), mOrder.checkerTel);
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton(R.string.label_positive2, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getDeclarationRequestHandler().setOrderNumber(data.orderNumber)
                                    .setUserToken(mLoginData.userToken)
                                    .setSceneId(mLoginData.loginSceneId)
                                    .setIRequestUrl(DeclarationRequestHandler.DeclarationURL.ORDER_CONFIRM_SERVICE)
                                    .doHttpRequest(new SignItUpHandler(data));
                            dialog.dismiss();
                        }
                    }).show();
        }
    }

    private class SignItUpHandler extends JsonResponseHandler<StringMessage> {
        private DeclarationOrder mOrder;
        public SignItUpHandler(DeclarationOrder order) {
            super(StringMessage.class);
            mOrder = order;
        }

        @Override
        public void onSuccess(StringMessage jsonObj, String rawJsonResponse) {
            if(jsonObj.statusCode == 0) {
                // TODO : 评价
                Intent intent = new Intent(getContext(), ServiceEvaluationActivity.class);
                intent.putExtra("order", mOrder);
                startActivity(intent);
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
     * 加载订单列表：待确认
     */
    @Override
    protected void loadOrderListData() {
        requestOrderList("finish", new OrderListResponseHandler.OnOrderListReceivedListener() {
            @Override
            public void onOrderListReceived(List<DeclarationOrder> data) {
                mOderListAdpter = new OderListAdpter(getContext(), data);
                mOderListAdpter.setItemLayoutResId(R.layout.declaration_order_item);
                mOderListAdpter.setItemViewSetter(new OderListAdpter.ItemViewSetter() {
                    @Override
                    public void onFinishInflateItemView(View itemView, DeclarationOrder item) {
                        DeclarationOrderView orderItemView = (DeclarationOrderView) itemView;
                        orderItemView.addOrderItemActionDescripter(new ActionDescripterComplaints(){
                            @Override
                            public int getActionViewResId() {
                                return android.R.id.button2;
                            }

                            @Override
                            public void onActionViewDataRefreshed(View actionView, DeclarationOrder data) {

                            }
                        });
                        orderItemView.addOrderItemActionDescripter(new ActionDescripterSigned());
                    }

                    @Override
                    public void bindingItemViewData(View itemView, DeclarationOrder item) {
                        DeclarationOrderView orderItemView = (DeclarationOrderView) itemView;
                        orderItemView.bindDeclarationOrderData(item);
                    }
                });

                if(getView() != null) {
                    ListView listView = (ListView) getView().findViewById(android.R.id.list);
                    listView.setAdapter(mOderListAdpter);
                    listView.setOnItemClickListener(mOderListAdpter);
                }
            }
        });
    }
}
