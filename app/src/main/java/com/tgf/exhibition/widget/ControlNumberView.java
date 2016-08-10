package com.tgf.exhibition.widget;

import android.content.Context;
import android.graphics.Rect;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.tgf.exhibition.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeff on 2016/5/18.
 */
public class ControlNumberView extends LinearLayout {
    private ImageView mBtnSubtraction;
    private EditText mEditTextNumber;
    private ImageView mBtnAddition;

    private Float mCurNumber = new Float(1);

    private OnNumberChangedListener mOnNumberChangedListener;

    public interface OnNumberChangedListener {
        void onNumberChangedListener(ControlNumberView controlNumberView, float number);
    }

    public ControlNumberView(Context context) {
        this(context, null);
    }

    public ControlNumberView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ControlNumberView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);
    }

    /**
     * 设置小数位数控制
     */
    private InputFilter dotValueLengthfilter = new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {
            // 删除等特殊字符，直接返回
            if ("".equals(source.toString())) {
                if (dest.length() > 1 && dend == 1 && '.' == dest.charAt(dend)) {
                    return "0";
                }
                return null;
            }
            String dValue = dest.toString();
            String[] splitArray = dValue.split("\\.");
            if (splitArray.length > 1 && splitArray[1].length() == 2) {
                return "";
            }
            return null;
        }
    };

    public void buildView() {
        ViewGroup cn = (ViewGroup) inflate(getContext(), R.layout.widget_control_number, null);
        if (getChildCount() > 0) {
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

    public void setmOnNumberChangedListener(OnNumberChangedListener listener) {
        mOnNumberChangedListener = listener;
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
    }

    private void initViews() {
        mBtnSubtraction = (ImageView) findViewById(R.id.subtraction_btn);
        mEditTextNumber = (EditText) findViewById(R.id.number);
        mBtnAddition = (ImageView) findViewById(R.id.addition_btn);

        mBtnSubtraction.setOnClickListener(mOnClickListener);
        mBtnAddition.setOnClickListener(mOnClickListener);

        mEditTextNumber.setFilters(new InputFilter[]{dotValueLengthfilter});
        setEditTextNumber(mCurNumber);

        mEditTextNumber.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String text = mEditTextNumber.getText().toString();
                    if (text.length() == 0 || Integer.valueOf(text) < 0) {
                        setNumber(0);
                    }
                }
            }
        });

        mEditTextNumber.addTextChangedListener(new TextWatcher() {
               boolean removeZero = false;

               @Override
               public void beforeTextChanged(CharSequence s, int start, int count, int after) {

               }

               @Override
               public void onTextChanged(CharSequence s, int start, int before, int count) {

               }

               @Override
               public void afterTextChanged(Editable ss) {
                   String s = ss.toString();
                   if (TextUtils.isEmpty(s)) {
                       s = "0";
                       ss.append(s);
                   } else if (s.startsWith(".")) {
                       ss.insert(0, "0");
                       s = ss.toString();
                   }

                   //移除前置Zero
                   Float value = Float.parseFloat(s);
                   if (s.length() > 1 && s.startsWith("0") && !s.startsWith("0.")) {
                       String s1 = value.toString();
                       String[] split = s1.split("\\.");
                       if (split.length > 1 && Integer.parseInt(split[1]) == 0) {
                           s1 = split[0];
                       }
                       ss.replace(0, ss.length(), s1, 0, s1.length());
                   }

                   //移除后置Zero
                   String[] split = s.split("\\.");
                   if (split.length > 1 && Integer.parseInt(split[1]) == 0) {
                       if (removeZero) {
                           ss.delete(s.indexOf('.'), ss.length());
                           removeZero = false;
                       } else {
                           removeZero = true;
                       }
                   }

                   setNumber(value);

                   if (mOnNumberChangedListener != null) {
                       mOnNumberChangedListener.onNumberChangedListener(ControlNumberView.this, mCurNumber);
                   }
               }
           }
        );
    }

//    private Rect mScreenRect = new Rect();
//    @Override
//    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        super.onLayout(changed, l, t, r, b);
//        mScreenRect.set(l, t, r, b);
//    }
//
//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        if(!mScreenRect.contains((int)ev.getRawX(), (int)ev.getRawY())) {
//            mEditTextNumber.clearFocus();
//            return true;
//        }
//        return super.onInterceptTouchEvent(ev);
//    }

    public void setNumber(float number) {
        if (mCurNumber == number) {
            return;
        }

        if (number <= 0) {
            mCurNumber = 0f;
            mBtnSubtraction.setEnabled(false);
            mBtnAddition.setEnabled(true);
        } else if (number >= Integer.MAX_VALUE) {
            mCurNumber = Float.valueOf(Integer.MAX_VALUE);
            mBtnAddition.setEnabled(false);
            mBtnSubtraction.setEnabled(true);
        } else {
            mCurNumber = number;
            mBtnSubtraction.setEnabled(true);
            mBtnAddition.setEnabled(true);
        }
    }

    public float getNumber() {
        return mCurNumber;
    }

    private void setEditTextNumber(Float number) {
        String sNumber = number.toString();
        String[] split = sNumber.split("\\.");
        if (split.length > 1 && Integer.parseInt(split[1]) == 0) {
            sNumber = split[0];
        }
        mEditTextNumber.setText(sNumber);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.subtraction_btn) {
                setNumber(mCurNumber - 1);
            } else if (v.getId() == R.id.addition_btn) {
                setNumber(mCurNumber + 1);
            }
            setEditTextNumber(mCurNumber);
        }
    };
}
