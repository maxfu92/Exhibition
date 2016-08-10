package com.tgf.exhibition.declaration;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.tgf.exhibition.R;
import com.tgf.exhibition.http.handler.DeclarationRequestHandler;
import com.tgf.exhibition.http.handler.OrderListResponseHandler;
import com.tgf.exhibition.http.json.DeclarationOrder;
import com.tgf.exhibition.http.json.LoginData;
import com.tgf.exhibition.util.SystemInvoker;
import com.tgf.exhibition.widget.DeclarationOrderView;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OrderToBeServedFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OrderToBeServedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderToBeServedFragment extends BaseOrderFragment {

    public OrderToBeServedFragment() {
        super();
    }

    public static OrderToBeServedFragment newInstance(LoginData param1) {
        OrderToBeServedFragment fragment = new OrderToBeServedFragment();
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
     * 联系服务行为描述
     */
    private class ActionDescripterContactService implements DeclarationOrderView.OrderItemActionDescripter {

        @Override
        public int getActionViewResId() {
            return android.R.id.button2;
        }

        @Override
        public void setupActionView(View actionView) {
            Button button = (Button) actionView;
            button.setText(R.string.label_contact_service);
        }

        @Override
        public void onActionViewDataRefreshed(View actionView, DeclarationOrder data) {

        }

        @Override
        public void onActionClick(DeclarationOrderView itemView, DeclarationOrder data) {
            postTipsMassage("订单["+data.getOrderNumber()+"] 联系服务:" + data.staffRealname + "<" + data.staffTel + ">");
            SystemInvoker.launchDailPage(getContext(), data.staffTel);
        }
    }

    /**
     * 加载订单列表：待服务
     */
    @Override
    protected void loadOrderListData() {
        requestOrderList("starting", new OrderListResponseHandler.OnOrderListReceivedListener() {
            @Override
            public void onOrderListReceived(List<DeclarationOrder> data) {
                mOderListAdpter = new OderListAdpter(getContext(), data);
                mOderListAdpter.setItemLayoutResId(R.layout.declaration_order_item);
                mOderListAdpter.setItemViewSetter(new OderListAdpter.ItemViewSetter() {
                    @Override
                    public void onFinishInflateItemView(View itemView, DeclarationOrder item) {
                        DeclarationOrderView orderItemView = (DeclarationOrderView) itemView;
                        orderItemView.addOrderItemActionDescripter(new ActionDescripterComplaints());
                        orderItemView.addOrderItemActionDescripter(new ActionDescripterContactService());
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
