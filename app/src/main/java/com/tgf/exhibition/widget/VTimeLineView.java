package com.tgf.exhibition.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tgf.exhibition.R;

/**
 * Created by jeff on 2016/5/25.
 */
public class VTimeLineView extends FrameLayout {
    private View mUperLine;
    private ImageView mNoder;
    private View mLowerLine;
    private TextView mTvTitle;
    private TextView mTvTitle2;
    private TextView mTvSummery;
    private View mDividingLine;

    public VTimeLineView(Context context) {
        this(context, null);
    }

    public VTimeLineView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VTimeLineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void buildChildrenViews() {
        View view = inflate(getContext(), R.layout.time_line_view, null);
        mUperLine = view.findViewById(R.id.time_line_up);
        mNoder = (ImageView) view.findViewById(R.id.time_line_noder);
        mLowerLine = view.findViewById(R.id.time_line_lower);
        mDividingLine = view.findViewById(R.id.divider);

        mTvTitle = (TextView) view.findViewById(android.R.id.title);
        mTvTitle2 = (TextView) view.findViewById(android.R.id.text1);
        mTvSummery = (TextView) view.findViewById(android.R.id.summary);
        addView(view);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        buildChildrenViews();
    }

    public void setDividingLineDisplay(boolean display) {
        mDividingLine.setVisibility(display?VISIBLE:INVISIBLE);
    }

    public void setActived(boolean actived) {
        mNoder.setImageResource(actived ? R.drawable.ic_p_actived : R.drawable.ic_p);
    }

    public void setUperLineDisplay(boolean display) {
        mUperLine.setVisibility(display?VISIBLE:INVISIBLE);
    }

    public void setLowerLineDisplay(boolean display) {
        mLowerLine.setVisibility(display?VISIBLE:INVISIBLE);
    }

    public void setTitle(String title) {
        mTvTitle.setText(title);
    }

    public void setTitle2(String title2) {
        mTvTitle2.setText(title2);
    }

    public void setSummery(String summery) {
        mTvSummery.setText(summery);
    }
}
