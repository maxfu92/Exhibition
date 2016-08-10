package com.tgf.exhibition.http.handler;

import android.content.Context;
import android.widget.Toast;

import com.tgf.exhibition.R;
import com.tgf.exhibition.http.json.DeclarationOrder;
import com.tgf.exhibition.http.msg.DeclarationOrderMassage;
import com.tgf.exhibition.widget.LoadingDialog;

import java.util.List;

/**
 * Created by jeff on 2016/5/23.
 */
public class OrderListResponseHandler extends JsonResponseHandler<DeclarationOrderMassage> {
    private Context mContext;

    public OrderListResponseHandler(Context context) {
        super(DeclarationOrderMassage.class);
        mContext = context;
    }

    @Override
    public void onStart() {
        setLoadingDialog(new LoadingDialog(mContext));
        super.onStart();
    }

    @Override
    public void onFinish() {
        super.onFinish();
        dismissLoadingDialog();
    }

    public interface OnOrderListReceivedListener {
        void onOrderListReceived(List<DeclarationOrder> data);
    }

    private OnOrderListReceivedListener mListener;
    public void setOnOrderListReceivedListener(OnOrderListReceivedListener listener) {
        mListener = listener;
    }

    @Override
    public void onSuccess(DeclarationOrderMassage jsonObj, String rawJsonResponse) {
        if (jsonObj.statusCode == 0) {
            if(mListener != null) {
                mListener.onOrderListReceived(jsonObj.data);
            } else {
                Toast.makeText(mContext, R.string.tips_data_load_success, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mContext, jsonObj.statusMessage, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFailure(int statusCode, Throwable throwable) {
        Toast.makeText(mContext, R.string.tips_data_load_faild, Toast.LENGTH_SHORT).show();
    }
}