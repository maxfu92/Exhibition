package com.tgf.exhibition.declaration;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.tgf.exhibition.TgfActivity;
import com.tgf.exhibition.R;
import com.tgf.exhibition.http.handler.DeclarationRequestHandler;
import com.tgf.exhibition.http.handler.JsonResponseHandler;
import com.tgf.exhibition.http.json.DeclarationOrder;
import com.tgf.exhibition.http.json.LoginData;
import com.tgf.exhibition.http.msg.StringMessage;
import com.tgf.exhibition.widget.LoadingDialog;

public class ServiceEvaluationActivity extends TgfActivity {
    private LoginData mLoginData;
    private DeclarationOrder mOrder;
    private RatingBar mRatingBar;
    private EditText mComments;
    private TextView mTvCharCount;
    private View mSubmitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_evaluation);

        mLoginData = getTgfDelegate().getLoginData();
        mOrder = getIntent().getParcelableExtra("order");

        mSubmitButton = findViewById(R.id.button);
        ;
        mSubmitButton.setEnabled(false);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canSubmitEvaluation()) return;

                getTgfDelegate().getDeclarationRequestHandler()
                        .setOrderNumber(mOrder.orderNumber)
                        .setServiceStarLevel(String.valueOf(mRatingBar.getRating()))
                        .setServiceComments(mComments.getText().toString())
                        .setSceneId(mLoginData.loginSceneId)
                        .setIRequestUrl(DeclarationRequestHandler.DeclarationURL.ORDER_PUSH_COMMENT)
                        .doHttpRequest(new SubmitEvaluationHandler());
            }
        });

        mRatingBar = (RatingBar) findViewById(R.id.rating_bar);
        mRatingBar.setRating(0);
        mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                TextView textView = (TextView) findViewById(R.id.rating_num);
                textView.setText(String.valueOf(ratingBar.getRating()));
                canSubmitEvaluation();
            }
        });


        mTvCharCount = (TextView) findViewById(R.id.tv_char_count);
        mComments = (EditText) findViewById(R.id.et_comments);
        mComments.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                canSubmitEvaluation();
                updatedCharCount(mComments.length());
            }
        });
        updatedCharCount(0);

        mSubmitButton = findViewById(R.id.button);
        ;
        mSubmitButton.setEnabled(false);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canSubmitEvaluation()) return;

                getTgfDelegate().getDeclarationRequestHandler()
                        .setOrderNumber(mOrder.orderNumber)
                        .setServiceStarLevel(String.valueOf(mRatingBar.getRating()))
                        .setServiceComments(mComments.getText().toString())
                        .setUserToken(mLoginData.userToken)
                        .setSceneId(mLoginData.loginSceneId)
                        .setIRequestUrl(DeclarationRequestHandler.DeclarationURL.ORDER_PUSH_COMMENT)
                        .doHttpRequest(new SubmitEvaluationHandler());
            }
        });
    }

    @Override
    protected void onDataRefresh() {

    }

    private boolean canSubmitEvaluation() {
        mSubmitButton.setEnabled(!(mRatingBar.getRating() == 0F && mComments.length() == 0));
        return mSubmitButton.isEnabled();
    }

    private void updatedCharCount(int cnt) {
        if (cnt <= 150) {
            String tips = String.format(getString(R.string.label_char_count), cnt, 150);
            mTvCharCount.setText(tips);
        }
    }

    private class SubmitEvaluationHandler extends JsonResponseHandler<StringMessage> {

        public SubmitEvaluationHandler() {
            super(StringMessage.class);
        }

        @Override
        public void onFinish() {
            super.onFinish();
            dismissLoadingDialog();
        }

        @Override
        public void onStart() {
            setLoadingDialog(new LoadingDialog(ServiceEvaluationActivity.this));
            super.onStart();
        }

        @Override
        public void onSuccess(StringMessage jsonObj, String rawJsonResponse) {
            if (jsonObj.statusCode == 0) {
                getTgfDelegate().postTipsMassage(R.string.tips_submit_success);
            } else {
                getTgfDelegate().postTipsMassage(jsonObj.statusMessage);
            }
            startActivity(new Intent(ServiceEvaluationActivity.this, DeclarationActivity.class));
        }

        @Override
        public void onFailure(int statusCode, Throwable throwable) {
            getTgfDelegate().postTipsMassage(R.string.tips_network_error);
            getTgfDelegate().gotoMainActivity();
        }
    }
}
