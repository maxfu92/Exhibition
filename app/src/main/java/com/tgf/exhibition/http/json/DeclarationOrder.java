package com.tgf.exhibition.http.json;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tgf.exhibition.R;

/**
 * Created by jeff on 2016/5/22.
 */
public class DeclarationOrder implements Parcelable{
    @JsonProperty("order_num")
    public String orderNumber;

    @JsonProperty("task_status")
    public String orderStatus;

    @JsonProperty("is_comment")
    public String isComment ;

    @JsonProperty("object_id")
    public String objectId;

    @JsonProperty("object_icon")
    public String objectIcon;

    @JsonProperty("object_address")
    public String objectAddress;

    @JsonProperty("type_icon")
    public String typeIcon ;

    @JsonProperty("item_title")
    public String itemTitle ;

    @JsonProperty("num")
    public String number;

    @JsonProperty("start_time")
    public String startTime;

    @JsonProperty("service_start_time")
    public String serviceStartTime;

    @JsonProperty("total_money")
    public String totalMoney;

    @JsonProperty("staff_user_id")
    public String staffUserId ;

    @JsonProperty("staff_realname")
    public String staffRealname;

    @JsonProperty("staff_tel")
    public String staffTel;

    @JsonProperty("user_id")
    public String userId;

    @JsonProperty("realname")
    public String userRealName;

    @JsonProperty("tel")
    public String userTel;

    @JsonProperty("checker_user_id")
    public String checkerUserId;

    @JsonProperty("checker_realname")
    public String checkerRealName;

    @JsonProperty("checker_tel")
    public String checkerTel;

    @JsonProperty("star")
    public String starCount;

    public DeclarationOrder(){}

    /////////////////////////////////////
    /// The following is not for JSON ///
    @JsonIgnore
    public boolean isSelected = false;

    public static final Creator<DeclarationOrder> CREATOR = new Creator<DeclarationOrder>() {
        @Override
        public DeclarationOrder createFromParcel(Parcel in) {
            return new DeclarationOrder(in);
        }

        @Override
        public DeclarationOrder[] newArray(int size) {
            return new DeclarationOrder[size];
        }
    };

    public String getOrderNumber() {
        if(!TextUtils.isEmpty(orderNumber) && orderNumber.length() > 9) {
            return orderNumber.substring(orderNumber.length() - 10, orderNumber.length() - 1);
        }
        return orderNumber;
    }

    public final String getServiceOrderActionDisplayName() {
        if("starting".equals(orderStatus)) {
            return "完成服务";
        } else if("finish".equals(orderStatus)) {
            return "等待签收";
        } else if("signin".equals(orderStatus)) {
            if("N".equals(isComment)) {
                return "等待评论";
            } else if("Y".equals(isComment)) {
                return "交易成功";
            }
        }
        return "等待付款";
    }

    public final SpannableStringBuilder getServiceOrderStatusDisplayName(Context context) {
        int color = context.getResources().getColor(R.color.orange);
        if("starting".equals(orderStatus)) {
            color = context.getResources().getColor(R.color.colorAccent);
            return coloredString("付款成功", color);
        } else if("finish".equals(orderStatus)) {
            color = context.getResources().getColor(R.color.colorAccent);
            return coloredString("完成服务", color);
        } else if("signin".equals(orderStatus)) {
            if("N".equals(isComment)) {
                color = context.getResources().getColor(R.color.colorAccent);
                return coloredString("交易成功", color);
            } else if("Y".equals(isComment)) {
                return coloredString(starCount, color).append(" 分");
            }
        }
        return coloredString("未付款", color);
    }

    public final SpannableStringBuilder getOrderStatusDisplayName(Context context) {
        int color;
        if("pending".equals(orderStatus)) {
            color = context.getResources().getColor(R.color.orange);
            return coloredString("待付款", color);
        } else if("starting".equals(orderStatus)) {
            color = context.getResources().getColor(R.color.colorAccent);
            return coloredString("付款成功", color);
        } else if("finish".equals(orderStatus)) {
            color = context.getResources().getColor(R.color.orange);
            return coloredString("等待确认", color);
        } else if("signin".equals(orderStatus)) {
            color = context.getResources().getColor(R.color.colorAccent);
            return coloredString("交易成功", color);
        }
        return new SpannableStringBuilder("");
    }

    private SpannableStringBuilder coloredString(String str, int color) {
        if(TextUtils.isEmpty(str)) {
            return new SpannableStringBuilder("");
        }
        SpannableStringBuilder style=new SpannableStringBuilder(str);
        style.setSpan(new ForegroundColorSpan(color), 0, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );
        return style;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeclarationOrder that = (DeclarationOrder) o;
        return orderNumber.equals(that.orderNumber);

    }

    @Override
    public int hashCode() {
        return orderNumber.hashCode();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected DeclarationOrder(Parcel in) {
        orderNumber = in.readString();
        orderStatus = in.readString();
        isComment = in.readString();
        objectId = in.readString();
        objectIcon = in.readString();
        objectAddress = in.readString();
        typeIcon = in.readString();
        itemTitle = in.readString();
        number = in.readString();
        startTime = in.readString();
        serviceStartTime = in.readString();
        totalMoney = in.readString();
        staffUserId = in.readString();
        staffRealname = in.readString();
        staffTel = in.readString();
        userId = in.readString();
        userRealName = in.readString();
        userTel = in.readString();
        checkerUserId = in.readString();
        checkerRealName = in.readString();
        checkerTel = in.readString();
        isSelected = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(orderNumber);
        dest.writeString(orderStatus);
        dest.writeString(isComment);
        dest.writeString(objectId);
        dest.writeString(objectIcon);
        dest.writeString(objectAddress);
        dest.writeString(typeIcon);
        dest.writeString(itemTitle);
        dest.writeString(number);
        dest.writeString(startTime);
        dest.writeString(serviceStartTime);
        dest.writeString(totalMoney);
        dest.writeString(staffUserId);
        dest.writeString(staffRealname);
        dest.writeString(staffTel);
        dest.writeString(userId);
        dest.writeString(userRealName);
        dest.writeString(userTel);
        dest.writeString(checkerUserId);
        dest.writeString(checkerRealName);
        dest.writeString(checkerTel);
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }
}
