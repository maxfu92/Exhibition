package com.tgf.exhibition.http.json;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Created by jeff on 2016/5/22.
 */
public class DeclarationOrderDetail {
    @JsonProperty("id")
    public String id;
    @JsonProperty("show_staff")
    public String showStaff;
    @JsonProperty("show_guest")
    public String showGuest;
    @JsonProperty("show_service")
    public String showService;
    @JsonProperty("show_comment")
    public String showComment;
    @JsonProperty("order_num")
    public String orderNumber;
    @JsonProperty("title")
    public String title;
    @JsonProperty("item_title")
    public String itemTitle;
    @JsonProperty("item_id")
    public String itemId;
    @JsonProperty("type_icon")
    public String typeIcon;
    @JsonProperty("realname")
    public String realname;
    @JsonProperty("tel")
    public String tel;
    @JsonProperty("user_id")
    public String userId;
    @JsonProperty("avatar")
    public String avatar;
    @JsonProperty("staff_user_id")
    public String staffUserId;
    @JsonProperty("staff_avatar")
    public String staffAvatar;
    @JsonProperty("staff_realname")
    public String staffRealname;
    @JsonProperty("staff_tel")
    public String staffTel;
    @JsonProperty("checker_user_id")
    public String checkerUserId;
    @JsonProperty("checker_realname")
    public String checkerRealname;
    @JsonProperty("checker_avatar")
    public String checkerAvatar;
    @JsonProperty("checker_tel")
    public String checkerTel;
    @JsonProperty("apply_type_id")
    public String applyTypeId;
    @JsonProperty("object_id")
    public String objectId;
    @JsonProperty("object_icon")
    public String objectIcon;
    @JsonProperty("object_address")
    public String objectAddress;
    @JsonProperty("square")
    public float objectUsedSize = 0.00f;
    @JsonProperty("num")
    public String number;
    @JsonProperty("price")
    public String price;
    @JsonProperty("deposit_money")
    public String depositMoney;
    @JsonProperty("total_money")
    public String totalMoney;
    @JsonProperty("total_use_money")
    public String totalUseMoney;
    @JsonProperty("total_deposit_money")
    public String totalDepositMoney;
    @JsonProperty("status")
    public String status;
    @JsonProperty("pay_type")
    public String payType;
    @JsonProperty("task_status")
    public String taskStatus;
    @JsonProperty("service_message")
    public String serviceMessage;
    @JsonProperty("service_file")
    public String serviceFile;
    @JsonProperty("star")
    public String starCount;
    @JsonProperty("comment")
    public String comment;
    @JsonProperty("is_comment")
    public String isComment;
    @JsonProperty("scene_id")
    public String sceneId;
    @JsonDeserialize(contentAs = OrderProgress.class)
    @JsonProperty("progress")
    public OrderProgress[] progresses;
    @JsonProperty("create_time")
    public String createTime;
    @JsonProperty("update_time")
    public String updateTime;
    @JsonProperty("paid_time")
    public String paidTime;
    @JsonProperty("cancel_time")
    public String cancelTime;
    @JsonProperty("start_time")
    public String startTime;
    @JsonProperty("end_time")
    public String endTime;
    @JsonProperty("sign_time")
    public String signTime;
    @JsonProperty("finish_time")
    public String finishTime;

    @JsonProperty("is_drawing")
    public String isDrawing;
    @JsonProperty("drawing_time")
    public String drawingTime;
    @JsonProperty("drawing_username")
    public String drawingUsername;
    @JsonProperty("is_balance")
    public String isBalance;
    @JsonProperty("balance_time")
    public String balanceTime;
    @JsonProperty("balance_username")
    public String balanceUsername;
    @JsonProperty("is_client_delete")
    public String isClientDelete;
    @JsonProperty("client_delete_time")
    public String clientDeleteTime;
    @JsonProperty("role")
    public String role;

    public String getOrderNumber() {
        if(!TextUtils.isEmpty(orderNumber) && orderNumber.length() > 9) {
            return orderNumber.substring(orderNumber.length() - 10, orderNumber.length() - 1);
        }
        return orderNumber;
    }
}
