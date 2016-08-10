package com.tgf.exhibition.widget;

import android.app.FragmentManager;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.datetimepicker.date.DatePickerDialog;
import com.android.datetimepicker.time.RadialPickerLayout;
import com.android.datetimepicker.time.TimePickerDialog;
import com.tgf.exhibition.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by jeff on 2016/5/18.
 */
public class DatetimePickerView extends LinearLayout {
    private FragmentManager mFragmentManager;

    private boolean mIsFullViewClickable = false;
    private String mLabel;
    private TextView mTvLabel;
    private TextView mTvDatetime;

    private final Calendar mCalendar = Calendar.getInstance();;
    private final SimpleDateFormat mDatetimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private OnDatetimeChangedListener mOnDatetimeChangedListener;
    public interface OnDatetimeChangedListener {
        void onDatetimeChangedListener(Date date, String formatedDate);
    }

    public DatetimePickerView(Context context) {
        this(context, null);
    }

    public DatetimePickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DatetimePickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.DatetimePickerView);
        mLabel = a.getString(R.styleable.DatetimePickerView_label);
        mIsFullViewClickable = a.getBoolean(R.styleable.DatetimePickerView_full_view_clickable, false);
        a.recycle();
        init();
    }

    private void init() {
        if(mTvLabel != null && mLabel != null) {
            mTvLabel.setText(mLabel);
        }
        if(mTvDatetime != null) {
            mTvDatetime.setText(mDatetimeFormat.format(mCalendar.getTime()));
        }
    }

    public void buildView() {
        ViewGroup cn = (ViewGroup) inflate(getContext(), R.layout.widget_datetime_picker, null);
        if(getChildCount() > 0) {
            removeAllViews();
        }
        List<View> all = new ArrayList<View>();
        for (int i = 0; i < cn.getChildCount(); i++) {
            all.add(cn.getChildAt(i));
        }
        cn.removeAllViews();
        for (View view : all) {
            addView(view);
        }
    }

    public void setOnDatetimeChangedListener(OnDatetimeChangedListener listener) {
        mOnDatetimeChangedListener = listener;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() != 3) {
            buildView();
        }

        if (getChildCount() == 3) {
            initViews();
        }

        init();
    }

    public void setFragmentManager(FragmentManager mFragmentManager) {
        this.mFragmentManager = mFragmentManager;
    }

    public void setLable(int resId) {
        if(mTvLabel != null) {
            mTvLabel.setText(resId);
        }
    }
    public void setLable(String lable) {
        if(mTvLabel != null) {
            mTvLabel.setText(lable);
        }
    }
    public void setDatetime(Date datetime) {
        setDatetime(datetime, mDatetimeFormat.format(datetime));
    }

    private void setDatetime(Date datetime, String sDatetime) {
        mCalendar.setTime(datetime);
        if(mTvDatetime != null) {
            mTvDatetime.setText(sDatetime);
        }
        if(mOnDatetimeChangedListener != null) {
            mOnDatetimeChangedListener.onDatetimeChangedListener(datetime, sDatetime);
        }
    }

    public void setDatetime(String datetime) {
        try {
            Date date = mDatetimeFormat.parse(datetime);
            setDatetime(date, datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public long getTimeInMillis() {
        return mCalendar.getTimeInMillis();
    }

    public String getTime() {
        return mDatetimeFormat.format(mCalendar.getTime());
    }

    public String getTime(DateFormat dateFormat) {
        return dateFormat.format(mCalendar.getTime());
    }

    private void initViews() {
        mTvLabel = (TextView) findViewById(R.id.tv_datetime_lable);
        mTvDatetime = (TextView) findViewById(R.id.tv_datetime);

        if(mIsFullViewClickable) {
            setClickable(true);
            setOnClickListener(mDatePickerListener);
        } else {
            findViewById(R.id.btn_datetime_picker).setOnClickListener(mDatePickerListener);
        }
    }

    private View.OnClickListener mDatePickerListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if(mFragmentManager != null) {
                DatePickerDialog.newInstance(mOnDateSetListener, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH)).show(mFragmentManager, "datePicker");
            }
        }
    };

    private DatePickerDialog.OnDateSetListener mOnDateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
            mCalendar.set(year, monthOfYear, dayOfMonth);
            post(new Runnable() {
                @Override
                public void run() {
                    TimePickerDialog.newInstance(mOnTimeSetListener, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), true).show(mFragmentManager, "timePicker");
                }
            });
        }
    };

    private TimePickerDialog.OnTimeSetListener mOnTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
            mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            mCalendar.set(Calendar.MINUTE, minute);
            mCalendar.set(Calendar.SECOND, 0);
            setDatetime(mCalendar.getTime());
        }
    };
}
