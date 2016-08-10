package com.tgf.exhibition.declaration;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import com.tgf.exhibition.R;
import com.tgf.exhibition.http.json.DeclarationOrder;
import com.tgf.exhibition.widget.DeclarationOrderView;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by jeff on 2016/5/23.
 */
public class OderListAdpter extends BaseAdapter implements DeclarationOrderView.OnItemSelectedChangedListener, AdapterView.OnItemClickListener {
    private Context mContext;
    private List<DeclarationOrder> mOrderList;
    private Set<DeclarationOrder> mSelectedOrders = new HashSet<DeclarationOrder>();

    private int mItemLayoutResId = R.layout.order_item;
    private ItemViewSetter mItemViewSetter;

    public interface ItemViewSetter {
        void onFinishInflateItemView(View itemView, DeclarationOrder item);
        void bindingItemViewData(View itemView, DeclarationOrder item);
    }

    public OderListAdpter(Context context, List<DeclarationOrder> data) {
        mContext = context;
        mOrderList = data;
    }

    public void refreshData(List<DeclarationOrder> data) {
        mOrderList.clear();
        mOrderList = data;
        notifyDataSetChanged();
    }

    public OderListAdpter setItemLayoutResId(int resId) {
        mItemLayoutResId = resId;
        return this;
    }

    public OderListAdpter setItemViewSetter(ItemViewSetter itemViewSetter) {
        mItemViewSetter = itemViewSetter;
        return this;
    }

    public DeclarationOrder[] getSelectedOrders() {
        DeclarationOrder[] orders = new DeclarationOrder[mSelectedOrders.size()];
        return mSelectedOrders.toArray(orders);
    }

    public boolean isAllItemSelected() {
        return mSelectedOrders.size() == mOrderList.size();
    }

    public void setAllItemsChecked(boolean isChecked) {
        Iterator<DeclarationOrder> iterator;
        DeclarationOrder order;
        if(isChecked) {
            iterator = mOrderList.iterator();
            while(iterator.hasNext()) {
                order = iterator.next();
                if(!order.isSelected) {
                    order.isSelected = true;
                    mSelectedOrders.add(order);
                    onSelectedItemAdded(order);
                }
            }
        } else {
            iterator = mSelectedOrders.iterator();
            while(iterator.hasNext()) {
                order = iterator.next();
                order.isSelected = false;
                iterator.remove();
                onSelectedItemRemoved(order);
            }
        }
        notifyDataSetChanged();
    }

    private Float mSelectedItemTotalPrice = new Float(0.00);
    public Float getSelectedItemTotalPrice() {
        return mSelectedItemTotalPrice;
    }
    private void onSelectedItemAdded(DeclarationOrder order) {
        mSelectedItemTotalPrice += Float.parseFloat(order.totalMoney);
    }
    private void onSelectedItemRemoved(DeclarationOrder order) {
        mSelectedItemTotalPrice -= Float.parseFloat(order.totalMoney);
    }

    @Override
    public void onItemSelectedChanged(DeclarationOrderView itemView, DeclarationOrder data, boolean selected) {
        if(selected) {
            mSelectedOrders.add(data);
            onSelectedItemAdded(data);
        } else {
            mSelectedOrders.remove(data);
            onSelectedItemRemoved(data);
        }
    }

    @Override
    public int getCount() {
        return mOrderList.size();
    }

    @Override
    public DeclarationOrder getItem(int position) {
        return mOrderList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = View.inflate(mContext, mItemLayoutResId, null);
            if(mItemViewSetter != null) {
                mItemViewSetter.onFinishInflateItemView(convertView, getItem(position));
            }
        }
        if(mItemViewSetter != null) {
            mItemViewSetter.bindingItemViewData(convertView, getItem(position));
        }
        return convertView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        DeclarationOrder order = getItem(position);
        Intent intent = new Intent(mContext, OrderDetailActivity.class);
        intent.putExtra("order", order);
        mContext.startActivity(intent);
    }
}
