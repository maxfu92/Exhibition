package com.tgf.exhibition.declaration2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.tgf.exhibition.TgfActivity;
import com.tgf.exhibition.R;
import com.tgf.exhibition.http.handler.DeclarationRequestHandler;
import com.tgf.exhibition.http.handler.OrderListResponseHandler;
import com.tgf.exhibition.http.json.DeclarationOrder;
import com.tgf.exhibition.http.json.LoginData;
import com.tgf.exhibition.widget.DeclarationServiceOrderView;
import com.tgf.exhibition.widget.LoadingDialog;

import java.util.List;

public class DeclarationActivity extends TgfActivity implements OrderListResponseHandler.OnOrderListReceivedListener {
    private OrderListResponseHandler orderListHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);
        orderListHandler = new OrderListResponseHandler(this);
        orderListHandler.setOnOrderListReceivedListener(this);
    }

    @Override
    protected void onDataRefresh() {
        LoginData loginData = getTgfDelegate().getLoginData();
        getTgfDelegate().getDeclarationRequestHandler()
                .setUserToken(loginData.userToken)
                .setSceneId(loginData.loginSceneId)
                .setIRequestUrl(DeclarationRequestHandler.DeclarationURL.SERVICE_ORDER_LIST)
                .doHttpRequest(orderListHandler);
    }

    @Override
    public void onOrderListReceived(List<DeclarationOrder> orders) {
        ServiceOrderListAdapter listAdapter = new ServiceOrderListAdapter(orders);
        ListView listView = (ListView) findViewById(android.R.id.list);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(listAdapter);
    }

    /**
     * 完成服务，进入提交服务凭证页面
     */
    private ServiceFinishedAction mServiceFinishedAction = new ServiceFinishedAction();
    private class ServiceFinishedAction implements DeclarationServiceOrderView.OnFinishedActionPressedListener {

        @Override
        public void onFinishedActionPressed(DeclarationOrder order) {
            Intent intent = new Intent(DeclarationActivity.this, OrderServiceFinishedActivity.class);
            intent.putExtra("order", order);
            startActivity(intent);
        }
    }

    /**
     * 服务列表ListAdapter
     */
    private class ServiceOrderListAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {
        private DeclarationServiceOrderView mOrderView;
        private List<DeclarationOrder> mOrders;

        public ServiceOrderListAdapter(List<DeclarationOrder> orders) {
            mOrders = orders;
        }

        @Override
        public int getCount() {
            return mOrders.size();
        }

        @Override
        public DeclarationOrder getItem(int position) {
            return mOrders.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = View.inflate(DeclarationActivity.this, R.layout.declaration_service_order_item, null);
                ((DeclarationServiceOrderView) convertView).setOrderFihishedAction(mServiceFinishedAction);
            }
            mOrderView = (DeclarationServiceOrderView) convertView;
            mOrderView.bindDeclarationOrderData(getItem(position));
            return convertView;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(DeclarationActivity.this, OrderDetailActivity.class);
            intent.putExtra("order", getItem(position));
            startActivity(intent);
        }
    }
}
