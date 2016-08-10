package com.tgf.exhibition.declaration2;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.tgf.exhibition.R;
import com.tgf.exhibition.declaration.TgfOrderDetailActivity;
import com.tgf.exhibition.http.json.DeclarationOrderDetail;
import com.tgf.exhibition.http.json.OnsiteDocument;
import com.tgf.exhibition.util.AnimateFirstDisplayListener;
import com.tgf.exhibition.util.BitmapUtils;
import com.tgf.exhibition.util.SystemInvoker;

import java.io.IOException;

public class OrderDetailActivity extends TgfOrderDetailActivity {
    private AnimateFirstDisplayListener animateFirstDisplayListener = new AnimateFirstDisplayListener();
    private DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
            .cacheInMemory(true)                        // 设置下载的图片是否缓存在内存中
            .displayer(new RoundedBitmapDisplayer(20))  // 设置成圆角图片
            .imageScaleType(ImageScaleType.EXACTLY)
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onDataRefresh() {
    }

    @Override
    protected void onOrderDetailRetreived(DeclarationOrderDetail orderDetail) {
        getTgfDelegate().setTextViewText((TextView) findViewById(R.id.tv_order_status), mOrder.getServiceOrderStatusDisplayName(this));

        getTgfDelegate().setTextViewText((TextView) findViewById(R.id.tv_person_title), getString(R.string.label_person_title2));
        bindImageViewUrlString((ImageView) findViewById(R.id.iv_person_photo), orderDetail.avatar);
        getTgfDelegate().setTextViewText((TextView) findViewById(R.id.tv_person_name), orderDetail.realname);
        findViewById(R.id.action_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SystemInvoker.launchDailPage(OrderDetailActivity.this, mOrderDetail.tel);
            }
        });

        buildCertificatesContent(orderDetail);

        if ("signin".equals(orderDetail.status) && "Y".equals(mOrder.isComment)) {
            buildStarContent(orderDetail);
        } else {
            Button button = (Button) findViewById(R.id.btn_status);
            button.setText(mOrder.getServiceOrderActionDisplayName());
            if ("starting".equals(mOrder.orderStatus)) { // TODO: use orderDetail? Should be the same thing
                button.setEnabled(true);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(OrderDetailActivity.this, OrderServiceFinishedActivity.class);
                        intent.putExtra("order", mOrder);
                        startActivity(intent);
                    }
                });
            } else {
                button.setEnabled(false);
            }
        }
    }

    private void buildStarContent(DeclarationOrderDetail orderDetail) {
        View starPanel = View.inflate(this, R.layout.order_service_evaluation, null);
        RatingBar ratingBar = (RatingBar) starPanel.findViewById(R.id.rating_bar);
        ratingBar.setEnabled(false);
        ratingBar.setRating(Integer.valueOf(orderDetail.starCount));
        getTgfDelegate().setTextViewText((TextView) starPanel.findViewById(R.id.rating_num), orderDetail.starCount);
        getTgfDelegate().setTextViewText((TextView) starPanel.findViewById(R.id.tv_user_comments), orderDetail.comment);
        getTgfDelegate().setTextViewText((TextView) starPanel.findViewById(R.id.tv_comments_time), orderDetail.signTime);

        ViewGroup actionContainer = (ViewGroup) findViewById(R.id.action_pannel_container);
        actionContainer.setVisibility(View.GONE);

        LinearLayout rootView = (LinearLayout) findViewById(R.id.parent_panel);
        LinearLayout.LayoutParams itemParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        itemParam.topMargin = BitmapUtils.dp2px(this, 16);
        rootView.addView(starPanel, itemParam);
    }

    private void buildCertificatesContent(DeclarationOrderDetail orderDetail) {
        ViewGroup certificates = (ViewGroup) View.inflate(this, R.layout.order_service_certificates, null);
        certificates.setPadding(BitmapUtils.dp2px(this, 16), 0, BitmapUtils.dp2px(this, 16), 0);

        // Setup Comments
        boolean isShowComments = true;
        TextView staffComments = (TextView) certificates.findViewById(R.id.tv_staff_comments);
        if (!TextUtils.isEmpty(orderDetail.serviceMessage)) {
            getTgfDelegate().setTextViewText(staffComments, orderDetail.serviceMessage);
        } else {
            isShowComments = false;
            staffComments.setVisibility(View.GONE);
        }

        // Setup Pictures
        boolean isShowDocumentFiles = true;
        OnsiteDocument[] onsiteDocuments = parseOnsiteDocument(orderDetail.serviceFile);
        if (onsiteDocuments != null && onsiteDocuments.length > 0) {
            LinearLayout linearLayout = (LinearLayout) certificates.findViewById(R.id.linear_container);
            LinearLayout.LayoutParams itemParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, BitmapUtils.dp2px(this, 60));
            for (OnsiteDocument onsiteDocument : onsiteDocuments) {
                View itemView = View.inflate(this, R.layout.list_item2, null);
                bindingDocumentData(itemView, onsiteDocument);
                linearLayout.addView(itemView, itemParam);
            }
        } else {
            isShowDocumentFiles = false;
        }

        // Setup Title
        if (!isShowComments && !isShowDocumentFiles) {
            findViewById(R.id.tv_detail_lable).setVisibility(View.GONE);
        } else {
            getTgfDelegate().setTextViewText((TextView) findViewById(R.id.tv_detail_lable), getString(R.string.label_detail_title2));
        }

        // Combined views
        LinearLayout container = (LinearLayout) findViewById(R.id.order_progress_container);
        ViewGroup rootView = (ViewGroup) findViewById(R.id.parent_panel);
        int indexOfContainer = rootView.indexOfChild(container);
        rootView.removeViewAt(indexOfContainer);
        if (isShowComments || isShowDocumentFiles) {
            rootView.addView(certificates, indexOfContainer);
        }
    }

    private String getDocumentFileUrl(String urls) {
        return urls.split(",")[0];
    }

    private void bindingDocumentData(View itemView, OnsiteDocument onsiteDocument) {
        ImageView icon = (ImageView) itemView.findViewById(android.R.id.icon);
        ImageLoader.getInstance().displayImage(getDocumentFileUrl(onsiteDocument.url), icon, displayImageOptions, animateFirstDisplayListener);

        TextView textView = (TextView) itemView.findViewById(android.R.id.title);
        getTgfDelegate().setTextViewText(textView, onsiteDocument.filename);
        textView = (TextView) itemView.findViewById(android.R.id.text1);
        textView.setVisibility(View.GONE);
        textView = (TextView) itemView.findViewById(android.R.id.summary);
        getTgfDelegate().setTextViewText(textView, onsiteDocument.create_time);
        textView = (TextView) itemView.findViewById(android.R.id.text2);
        getTgfDelegate().setTextViewText(textView, onsiteDocument.size);
        itemView.setTag(onsiteDocument);
        itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                OnsiteDocument onsiteDocument = (OnsiteDocument) v.getTag();
                Intent intent = new Intent(OrderDetailActivity.this, PicturePreviewActivity.class);
                intent.putExtra("url", getDocumentFileUrl(onsiteDocument.url));
                ImageView icon = (ImageView) v.findViewById(android.R.id.icon);
                icon.setDrawingCacheEnabled(true); // TODO: where to recycle the caching bitmap?
                intent.putExtra("smallBitmap", icon.getDrawingCache());
//                icon.setDrawingCacheEnabled(false); // NOTICE: this will be result app crash to recycle here
                // intent.putExtra("smallPath", getSmallPath());
                // intent.putExtra("indentify", getIdentify());
                startActivity(intent);
            }
        });
    }

    private OnsiteDocument[] parseOnsiteDocument(String jsonString) {
        if(TextUtils.isEmpty(jsonString)) {
            return null;
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(jsonString, OnsiteDocument[].class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
