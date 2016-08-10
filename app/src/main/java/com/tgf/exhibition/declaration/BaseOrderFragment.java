package com.tgf.exhibition.declaration;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.tgf.exhibition.R;
import com.tgf.exhibition.TgfActivity;
import com.tgf.exhibition.http.handler.DeclarationRequestHandler;
import com.tgf.exhibition.http.handler.OrderListResponseHandler;
import com.tgf.exhibition.http.json.DeclarationOrder;
import com.tgf.exhibition.http.json.LoginData;
import com.tgf.exhibition.util.SystemInvoker;
import com.tgf.exhibition.widget.DeclarationOrderView;

public abstract class BaseOrderFragment extends Fragment {
    protected LoginData mLoginData;
    private DeclarationRequestHandler mDeclarationRequestHandler;
    protected OderListAdpter mOderListAdpter = null;
    protected OnFragmentInteractionListener mListener;

    public BaseOrderFragment(){
    }

    DeclarationRequestHandler getDeclarationRequestHandler() {
        if(mDeclarationRequestHandler == null) {
            FragmentActivity activity = getActivity();
            if(activity instanceof TgfActivity) {
                mDeclarationRequestHandler = ((TgfActivity) activity).getTgfDelegate().getDeclarationRequestHandler();
            }
        }
        return mDeclarationRequestHandler;
    }

    protected void postTipsMassage(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    protected void postTipsMassage(int resId) {
        Toast.makeText(getContext(), resId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadOrderListData();
    }

    @Override
    public abstract View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState);

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    protected abstract void loadOrderListData();

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    protected void requestOrderList(String orderStatus, OrderListResponseHandler.OnOrderListReceivedListener receivedListener) {
        OrderListResponseHandler orderListHandler = new OrderListResponseHandler(getContext());
        orderListHandler.setOnOrderListReceivedListener(receivedListener);
        getDeclarationRequestHandler().setOrderStatus(orderStatus)
                .setUserToken(mLoginData.userToken)
                .setSceneId(mLoginData.loginSceneId)
                .setIRequestUrl(DeclarationRequestHandler.DeclarationURL.ORDER_LIST)
                .doHttpRequest(orderListHandler);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    /**
     * 投诉行为描述
     */
    protected class ActionDescripterComplaints implements DeclarationOrderView.OrderItemActionDescripter {

        @Override
        public int getActionViewResId() {
            return android.R.id.button1;
        }

        @Override
        public void setupActionView(View actionView) {
            Button button = (Button) actionView;
            button.setText(R.string.label_complaints);
        }

        @Override
        public void onActionViewDataRefreshed(View actionView, DeclarationOrder data) {

        }

        @Override
        public void onActionClick(DeclarationOrderView itemView, DeclarationOrder data) {
            postTipsMassage("订单["+data.getOrderNumber()+"]投诉:" + data.checkerRealName + "<" + data.checkerTel + ">");
            SystemInvoker.launchDailPage(getContext(), data.checkerTel);
        }
    }
}
