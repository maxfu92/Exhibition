package com.tgf.exhibition.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tgf.exhibition.R;
import com.tgf.exhibition.http.json.DeclarationOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeff on 2016/5/23.
 */
public class DeclarationOrderView extends FrameLayout {
    private ImageLoader mImageLoader = ImageLoader.getInstance();
    private DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
            .cacheInMemory(true)                        // 设置下载的图片是否缓存在内存中
            .cacheOnDisc(true)                          // 设置下载的图片是否缓存在SD卡中
            .build();

    private CheckBox mCheckBox;

    private TextView mTvOrderNumber;
    private TextView mTvOrderStatus;
    private ImageView mIvWorkObject;
    private TextView mTvWorkObject;
    private ImageView mIvDecalareItem;
    private TextView mTvDecalareItemDsc;
    private TextView mTvPayCount;
    private TextView mTvStartTime;
    private TextView mTvEndTime;
    private TextView mTvTotalPrice1;
    private TextView mTvTotalPrice2;

    private int actionPanelLayoutId;
    private boolean showCheckBoxView;
    private boolean showEndtiemView;

    private OnItemSelectedChangedListener mOnItemSelectedChangedListener;
    public interface OnItemSelectedChangedListener {
        void onItemSelectedChanged(DeclarationOrderView itemView, DeclarationOrder data, boolean selected);
    }

    public interface OrderItemActionDescripter {
        int getActionViewResId();
        void setupActionView(View actionView);
        void onActionViewDataRefreshed(View actionView, DeclarationOrder data);
        void onActionClick(DeclarationOrderView itemView, DeclarationOrder data);
    }

    public DeclarationOrderView(Context context) {
        this(context, null);
    }

    public DeclarationOrderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DeclarationOrderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DeclarationOrderView);
        actionPanelLayoutId = a.getResourceId(R.styleable.DeclarationOrderView_action_panel_layout, 0);
        showCheckBoxView = a.getBoolean(R.styleable.DeclarationOrderView_show_checkbox, true);
        showEndtiemView = a.getBoolean(R.styleable.DeclarationOrderView_show_endtime, false);
        a.recycle();

        buildChildren();
    }

    private void buildChildren() {
        addView(inflate(getContext(), R.layout.order_item, null));
        FrameLayout actionContainer = (FrameLayout) findViewById(R.id.action_pannel_container);
        if(actionPanelLayoutId > 0) {
            View mActionPanel = inflate(getContext(), actionPanelLayoutId, null);
            actionContainer.addView(mActionPanel);
        } else {
            actionContainer.setVisibility(GONE);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initViews();
    }

    private void initViews() {
        mCheckBox = (CheckBox) findViewById(R.id.iv_order_checked);
        if(showCheckBoxView) {
            mCheckBox.setVisibility(VISIBLE);
        } else {
            mCheckBox.setVisibility(GONE);
        }

        mTvOrderNumber = (TextView) findViewById(R.id.tv_order_num);
        mTvOrderStatus = (TextView) findViewById(R.id.tv_order_status);
        mIvWorkObject = (ImageView) findViewById(R.id.iv_obj_icon);
        mTvWorkObject = (TextView) findViewById(R.id.tv_obj_name);
        mIvDecalareItem = (ImageView) findViewById(R.id.iv_decalare_item_icon);
        mTvDecalareItemDsc = (TextView) findViewById(R.id.tv_decalare_item_desc);
        mTvPayCount = (TextView) findViewById(R.id.tv_pay_count);
        mTvStartTime = (TextView) findViewById(R.id.tv_start_time);
        mTvEndTime = (TextView) findViewById(R.id.tv_end_time);
        if(showEndtiemView) {
            findViewById(R.id.tv_end_time_lable).setVisibility(VISIBLE);
            mTvEndTime.setVisibility(VISIBLE);
        } else {
            findViewById(R.id.tv_end_time_lable).setVisibility(GONE);
            mTvEndTime.setVisibility(View.GONE);
        }
        mTvTotalPrice1 = (TextView) findViewById(R.id.tv_total_all_price_1);
        mTvTotalPrice2 = (TextView) findViewById(R.id.tv_total_all_price_2);

        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setItemSelected(isChecked);
            }
        });
    }

    private List<View> mActionViews = new ArrayList<View>();
    public void addOrderItemActionDescripter(OrderItemActionDescripter descripter) {
        if(descripter == null) {
            return;
        }
        View actionView = findViewById(descripter.getActionViewResId());
        if(actionView != null) {
            actionView.setTag(descripter);
            mActionViews.add(actionView);
            descripter.setupActionView(actionView);
            actionView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    OrderItemActionDescripter descripter = (OrderItemActionDescripter) v.getTag();
                    if(descripter != null) {
                        descripter.onActionClick(DeclarationOrderView.this, (DeclarationOrder) DeclarationOrderView.this.getTag());
                    }
                }
            });
        }
    }

    public void setOnItemSelectedChangedListener(OnItemSelectedChangedListener listener) {
        mOnItemSelectedChangedListener = listener;
    }

    private void setItemSelected(boolean selected) {
        DeclarationOrder data = (DeclarationOrder) getTag();
        if(data.isSelected == selected) return;
        data.isSelected = selected;
        if(mOnItemSelectedChangedListener != null) {
            mOnItemSelectedChangedListener.onItemSelectedChanged(this, data, selected);
        }
    }

    private void setTvText(TextView tvText, CharSequence value) {
        if(tvText != null) {
            tvText.setText(value);
        }
    }

    public void bindDeclarationOrderData(DeclarationOrder data) {
        setTag(data);
        mCheckBox.setChecked(data.isSelected);
        mImageLoader.displayImage(data.objectIcon, mIvWorkObject);
        mImageLoader.displayImage(data.typeIcon, mIvDecalareItem);

        setTvText(mTvOrderNumber, data.getOrderNumber());
        setTvText(mTvOrderStatus, data.getOrderStatusDisplayName(getContext()));
        setTvText(mTvWorkObject, data.objectAddress);
        setTvText(mTvDecalareItemDsc, data.itemTitle);
        setTvText(mTvPayCount, data.number);
        setTvText(mTvStartTime, data.startTime);
        setTvText(mTvEndTime, "Nothing for the end");

        String[] split = data.totalMoney.split("\\.");
        if(split.length==1){
            setTvText(mTvTotalPrice2, ".00");
        } else {
            setTvText(mTvTotalPrice2, "." + split[1]);
        }

        setTvText(mTvTotalPrice1, split[0]);

        for (View actionView : mActionViews) {
            OrderItemActionDescripter descripter = (OrderItemActionDescripter) actionView.getTag();
            descripter.onActionViewDataRefreshed(actionView, data);
        }
    }
}
