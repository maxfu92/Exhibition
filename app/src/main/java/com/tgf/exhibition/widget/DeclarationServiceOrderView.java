package com.tgf.exhibition.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tgf.exhibition.R;
import com.tgf.exhibition.http.json.DeclarationOrder;
import com.tgf.exhibition.util.SystemInvoker;

/**
 * Created by jeff on 2016/5/25.
 */
public class DeclarationServiceOrderView extends FrameLayout {
    private DeclarationOrder mOrder;

    private TextView mTvOrderStatus;
    private ImageView mIvWorkObject;
    private TextView mTvWorkObject;

    private ImageView mIvDecalareItem;
    private TextView mTvDecalareItemDsc;
    private TextView mTvDeclarerName;

    private TextView mTvPayCount;
    private TextView mTvStartTime;

    private RatingBar mRatingBar;
    private TextView ratingNum;

    private Button mFinishedButton;


    public DeclarationServiceOrderView(Context context) {
        this(context, null);
    }

    public DeclarationServiceOrderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DeclarationServiceOrderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // TODO: Add some attributs if needed
        ViewGroup mRootView = (ViewGroup)inflate(context, R.layout.order_item2, null);
        initViews(mRootView);
        addView(mRootView);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    private void initViews(View rootView) {
        mTvOrderStatus = (TextView) rootView.findViewById(R.id.tv_order_status);
        setTvText(mTvOrderStatus, "");
        mIvWorkObject = (ImageView) rootView.findViewById(R.id.iv_obj_icon);
        mTvWorkObject = (TextView) rootView.findViewById(R.id.tv_obj_name);
        setTvText(mTvWorkObject, "");
        mIvDecalareItem = (ImageView) rootView.findViewById(R.id.iv_decalare_item_icon);
        mTvDecalareItemDsc = (TextView) rootView.findViewById(R.id.tv_decalare_item_desc);
        setTvText(mTvDecalareItemDsc, "");
        mTvPayCount = (TextView) rootView.findViewById(R.id.tv_pay_count);
        setTvText(mTvPayCount, "x");

        mTvDeclarerName  = (TextView) rootView.findViewById(R.id.tv_declarer_name);
        setTvText(mTvDeclarerName, "");

        mTvStartTime = (TextView) rootView.findViewById(R.id.tv_start_time);
        setTvText(mTvStartTime, "");

        mRatingBar = (RatingBar) rootView.findViewById(R.id.rating_bar);
        mRatingBar.setEnabled(false);
        ratingNum = (TextView) rootView.findViewById(R.id.rating_num);

        rootView.findViewById(R.id.iv_action_contact).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SystemInvoker.launchDailPage(getContext(), mOrder.userTel);
            }
        });

        mFinishedButton = (Button) rootView.findViewById(R.id.btn_finish_serice);

    }

    public interface OnFinishedActionPressedListener {
        void onFinishedActionPressed(DeclarationOrder order);
    }
    private OnFinishedActionPressedListener mListener;
    public void setOrderFihishedAction(OnFinishedActionPressedListener listener) {
        mListener = listener;
    }

    private void setTvText(TextView tvText, CharSequence value) {
        if(tvText != null) {
            tvText.setText(value);
        }
    }

    private View.OnClickListener mFinishAction = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(mListener != null) {
                mListener.onFinishedActionPressed((DeclarationOrder)DeclarationServiceOrderView.this.getTag());
            }
        }
    };

    public void bindDeclarationOrderData(DeclarationOrder data) {
        mOrder = data;
        mFinishedButton.setText(data.getServiceOrderActionDisplayName());
        if("starting".equals(data.orderStatus)) {
            mFinishedButton.setEnabled(true);
            mFinishedButton.setOnClickListener(mFinishAction);
        } else {
            mFinishedButton.setEnabled(false);
        }

        setTag(data);
        setTvText(mTvOrderStatus, data.getServiceOrderStatusDisplayName(getContext()));
        setTvText(mTvWorkObject, data.objectAddress);
        setTvText(mTvDecalareItemDsc, data.itemTitle);
        setTvText(mTvPayCount, "x"+ data.number);
        setTvText(mTvDeclarerName, data.userRealName);
        setTvText(mTvStartTime, data.startTime);
        ImageLoader.getInstance().displayImage(data.objectIcon, mIvWorkObject);
        ImageLoader.getInstance().displayImage(data.typeIcon, mIvDecalareItem);
        if("Y".equals(data.isComment)) {
            findViewById(R.id.rating_pannel).setVisibility(VISIBLE);
            mRatingBar.setRating(Float.valueOf(data.starCount));
            ratingNum.setText(data.starCount);
        } else {
            findViewById(R.id.rating_pannel).setVisibility(GONE);
        }
    }

}
