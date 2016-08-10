package com.tgf.exhibition.declaration;

import android.os.Bundle;
import android.support.v7.view.menu.ListMenuItemView;
import android.support.v7.widget.ListPopupWindow;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tgf.exhibition.R;
import com.tgf.exhibition.TgfActivity;
import com.tgf.exhibition.TgfDelegate;
import com.tgf.exhibition.http.handler.DeclarationRequestHandler;
import com.tgf.exhibition.http.handler.JsonResponseHandler;
import com.tgf.exhibition.http.json.LoginData;
import com.tgf.exhibition.http.json.PrepayLog;
import com.tgf.exhibition.http.json.PrepaySummery;
import com.tgf.exhibition.http.json.WorkObject;
import com.tgf.exhibition.http.msg.PrepayLogMessage;
import com.tgf.exhibition.widget.LoadingDialog;

public class PrepayLogActivity extends TgfActivity {
    private ListPopupWindow mListPopupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prepay_log);

        ListView listView = (ListView) findViewById(android.R.id.list);
        listView.setAdapter(mPrepayLogListAdapter);
    }

    @Override
    protected void onDataRefresh() {
        requestPrepayLog(null);
    }

    /**
     * 请求作业对象/工作点预付款数据
     *
     * @param workObjectId 可以为空，请求将返回作业对象/工作点数据
     */
    private void requestPrepayLog(String workObjectId) {
        LoginData loginData = getTgfDelegate().getLoginData();
        if (loginData == null) {
            return;
        }
        DeclarationRequestHandler declarationRequestHandler = getTgfDelegate().getDeclarationRequestHandler();
        if (!TextUtils.isEmpty(workObjectId)) {
            declarationRequestHandler.setWorkObjectId(workObjectId);
        }

        declarationRequestHandler.setOrderPayForType("prepay")
                .setUserToken(loginData.userToken)
                .setSceneId(loginData.loginSceneId)
                .setIRequestUrl(DeclarationRequestHandler.DeclarationURL.ORDER_PREPAY_LOG)
                .doHttpRequest(mOrderPrepayLogHandler);
    }

    private void refreshPrepayDataToView(PrepayLog data) {
        refreshWorkObjectView(data);
        refreshListView(data);
    }

    /**
     * 绑定/刷新作业对象数据
     * @param data
     */
    private void refreshWorkObjectView(PrepayLog data) {
        ImageView objOwerIcon = (ImageView) findViewById(R.id.iv_object_ower_icon);
        if (!TextUtils.isEmpty(data.defaultWorkObjectIcon)) {
            ImageLoader.getInstance().displayImage(data.defaultWorkObjectIcon, objOwerIcon);
        }
        TgfDelegate tgfDelegate = getTgfDelegate();
        tgfDelegate.setTextViewText((TextView) findViewById(R.id.tv_object_name), data.defaultWorkObjectTitle);
        tgfDelegate.setTextViewText((TextView) findViewById(R.id.tv_ye), data.remaindMoney);
        tgfDelegate.setTextViewText((TextView) findViewById(R.id.tv_yyed), data.paidMoney);

        View btn = findViewById(R.id.iv_action_go);
        View objsListView = findViewById(R.id.objs_dropdown_list);
        if (data.workObjects != null && data.workObjects.length > 1) {
            btn.setVisibility(View.VISIBLE);
            objsListView.setClickable(true);

            if (mListPopupWindow == null) {
                mListPopupWindow = new ListPopupWindow(this);
                mListPopupWindow.setAdapter(mWorkObjectListAdapter);
                mListPopupWindow.setAnchorView(objsListView);

                // Register a listener to be notified when an item in our popup window has
                // been clicked.
                mListPopupWindow.setOnItemClickListener(mWorkObjectListAdapter);
                // Set popup window modality based on the current checkbox state.
                mListPopupWindow.setModal(true);

                objsListView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListPopupWindow != null) {
                            mListPopupWindow.show();
                        }
                    }
                });
            }
            mWorkObjectListAdapter.addWorkObjects(data.workObjects);

        } else {
            if (mListPopupWindow != null && mListPopupWindow.isShowing()) {
                mListPopupWindow.dismiss();
            }
            mListPopupWindow = null;
            btn.setVisibility(View.GONE);
            objsListView.setClickable(false);
        }
    }

    /**
     * 绑定/刷新预付款交易流水数据
     * @param data
     */
    private void refreshListView(PrepayLog data) {
        mPrepayLogListAdapter.addPrepaySummeris(data.prepaySummeries);
    }

    private PrepayLogListAdapter mPrepayLogListAdapter = new PrepayLogListAdapter();
    private class PrepayLogListAdapter extends BaseAdapter {
        private PrepaySummery[] mPrepaySummeries;
        public void addPrepaySummeris(PrepaySummery[] prepaySummeries) {
            mPrepaySummeries = prepaySummeries;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mPrepaySummeries == null ? 0 : mPrepaySummeries.length;
        }

        @Override
        public PrepaySummery getItem(int position) {
            return mPrepaySummeries == null ? null : mPrepaySummeries[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.prepay_log_item, parent, false);
            }
            bindPrepayLogData(convertView, getItem(position));
            return convertView;
        }

        private void bindPrepayLogData(View convertView, PrepaySummery item) {
            TgfDelegate tgfDelegate = getTgfDelegate();
            tgfDelegate.setTextViewText((TextView) convertView.findViewById(R.id.tv_person_name), item.realname);
            tgfDelegate.setTextViewText((TextView) convertView.findViewById(R.id.tv_paid_money), item.money);

            if(!TextUtils.isEmpty(item.icon)) {
                ImageView imageView = (ImageView) convertView.findViewById(R.id.iv_object_ower_icon);
                ImageLoader.getInstance().displayImage(item.icon, imageView);
            }
            tgfDelegate.setTextViewText((TextView) convertView.findViewById(R.id.tv_type_item_name), item.title);
            tgfDelegate.setTextViewText((TextView) convertView.findViewById(R.id.tv_time), item.createTime);
        }
    }

    /**
     * 作业对象 Adapter: 提供 Popup List Menu 切换作业对象
     */
    private WorkObjectListAdapter mWorkObjectListAdapter = new WorkObjectListAdapter();
    private class WorkObjectListAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {
        private WorkObject[] mWorkObjects;
        private WorkObject mSelectedWorkObject;

        public void addWorkObjects(WorkObject[] workObjects) {
            mWorkObjects = workObjects;
            if (mWorkObjects != null) {
                for (int i = 0; i < mWorkObjects.length; i++) {
                    if ("Y".equals(mWorkObjects[i].isSelected)) {
                        mSelectedWorkObject = mWorkObjects[i];
                        mListPopupWindow.setSelection(i);
                        break;
                    }
                }
            }
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mWorkObjects == null ? 0 : mWorkObjects.length;
        }

        @Override
        public WorkObject getItem(int position) {
            return mWorkObjects == null ? null : mWorkObjects[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.list_item, parent, false);
            }

            WorkObject item = getItem(position);
            getTgfDelegate().setTextViewText((TextView) convertView.findViewById(android.R.id.text1), item.objectTitle);
            ImageView icon = (ImageView) convertView.findViewById(android.R.id.icon);
            ImageLoader.getInstance().displayImage(item.icon, icon);

            return convertView;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(mSelectedWorkObject != null) {
                mSelectedWorkObject.isSelected = "N";
            }
            mSelectedWorkObject = getItem(position);
            getItem(position).isSelected = "Y";

            ImageView objOwerIcon = (ImageView) findViewById(R.id.iv_object_ower_icon);
            if (!TextUtils.isEmpty(mSelectedWorkObject.icon)) {
                ImageLoader.getInstance().displayImage(mSelectedWorkObject.icon, objOwerIcon);
            }
            TgfDelegate tgfDelegate = getTgfDelegate();
            tgfDelegate.setTextViewText((TextView) findViewById(R.id.tv_object_name), mSelectedWorkObject.objectTitle);
            tgfDelegate.setTextViewText((TextView) findViewById(R.id.tv_ye), mSelectedWorkObject.money);

            mListPopupWindow.dismiss();
            // 获取选中对象作业预付款流水数据
            requestPrepayLog(mSelectedWorkObject.objectId);
        }
    }

    /**
     * 预付款数据请求处理
     */
    private OrderPrepayLogHandler mOrderPrepayLogHandler = new OrderPrepayLogHandler();

    private class OrderPrepayLogHandler extends JsonResponseHandler<PrepayLogMessage> {

        public OrderPrepayLogHandler() {
            super(PrepayLogMessage.class);
        }

        @Override
        public void onStart() {
            setLoadingDialog(new LoadingDialog(PrepayLogActivity.this));
            super.onStart();
        }

        @Override
        public void onFinish() {
            super.onFinish();
            dismissLoadingDialog();
        }

        @Override
        public void onSuccess(PrepayLogMessage jsonObj, String rawJsonResponse) {
            if (jsonObj.statusCode == 0) {
                refreshPrepayDataToView(jsonObj.data);
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
