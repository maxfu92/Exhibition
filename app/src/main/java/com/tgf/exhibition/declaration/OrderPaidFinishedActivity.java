package com.tgf.exhibition.declaration;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tgf.exhibition.TgfActivity;
import com.tgf.exhibition.R;

public class OrderPaidFinishedActivity extends TgfActivity {
    interface ResultDescripter {
        int getTitleResId();

        int getIconResId();

        int getDesc1();

        int getDesc2();

        int getDesc2ColorResId();

        View.OnClickListener getBtn1ClickListener();

        int getBtn1NameResId();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_paid_finished);

        boolean paidForResult = getIntent().getBooleanExtra("payForResult", false);
        ResultDescripter descripter = paidForResult ? new SuccessDecripter(this) : new FailureDecripter(this);
        setTitle(descripter.getTitleResId());

        ImageView iv = (ImageView) findViewById(android.R.id.icon);
        iv.setImageResource(descripter.getIconResId());

        TextView textView = (TextView) findViewById(android.R.id.text1);
        textView.setText(descripter.getDesc1());

        textView = (TextView)findViewById(android.R.id.text2);
        String string = getString(descripter.getDesc2());
        textView.setText(coloredString(string, string.indexOf("“")+1, string.indexOf("”"), descripter.getDesc2ColorResId()));

        Button btn = (Button) findViewById(android.R.id.button1);
        btn.setText(descripter.getBtn1NameResId());
        btn.setOnClickListener(descripter.getBtn1ClickListener());
        if(!paidForResult) {
            btn.setBackgroundResource(R.color.orange);
        }

        btn = (Button) findViewById(android.R.id.button2);
        btn.setText(R.string.action_goback_usercenter);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTgfDelegate().gotoMainActivity();
            }
        });
    }

    @Override
    protected void onDataRefresh() {
    }

    /**
     * 支付成功描述
     */
    private static class SuccessDecripter implements OrderPaidFinishedActivity.ResultDescripter {
        final OrderPaidFinishedActivity mActivity;
        SuccessDecripter(OrderPaidFinishedActivity activity) {
            mActivity = activity;
        }

        @Override
        public int getTitleResId() {
            return R.string.title_activity_payfor_success;
        }

        @Override
        public int getIconResId() {
            return R.drawable.ic_success;
        }

        @Override
        public int getDesc1() {
            return R.string.tips_payfor_success_1;
        }

        @Override
        public int getDesc2() {
            return R.string.tips_payfor_success_2;
        }

        @Override
        public int getDesc2ColorResId() {
            return R.color.colorAccent;
        }

        @Override
        public View.OnClickListener getBtn1ClickListener() {
            return new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mActivity, DeclarationOrderActivity.class);
                    intent.putExtra(DeclarationOrderActivity.ORDER_CATALOG_KEY, DeclarationOrderActivity.ORDER_CATALOG_DFW);
                    mActivity.startActivity(intent);
                    mActivity.onBackPressed();
                }
            };
        }
        @Override
        public int getBtn1NameResId() {
            return R.string.action_view_declaration;
        }
    }

    /**
     * 支付失败描述
     */
    private static class FailureDecripter implements OrderPaidFinishedActivity.ResultDescripter {
        final OrderPaidFinishedActivity mActivity;
        FailureDecripter(OrderPaidFinishedActivity activity) {
            mActivity = activity;
        }

        @Override
        public int getTitleResId() {
            return R.string.title_activity_payfor_failure;
        }

        @Override
        public int getIconResId() {
            return R.drawable.ic_fail;
        }

        @Override
        public int getDesc1() {
            return R.string.tips_payfor_failure_1;
        }

        @Override
        public int getDesc2() {
            return R.string.tips_payfor_failure_2;
        }

        @Override
        public int getDesc2ColorResId() {
            return R.color.orange;
        }

        @Override
        public View.OnClickListener getBtn1ClickListener() {
            return new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mActivity, DeclarationOrderActivity.class);
                    intent.putExtra(DeclarationOrderActivity.ORDER_CATALOG_KEY, DeclarationOrderActivity.ORDER_CATALOG_DFK);
                    mActivity.startActivity(intent);
                    mActivity.onBackPressed();
                }
            };
        }

        @Override
        public int getBtn1NameResId() {
            return R.string.action_goto_payfor;
        }
    }

}
