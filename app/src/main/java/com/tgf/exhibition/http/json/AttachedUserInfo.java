package com.tgf.exhibition.http.json;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jeff on 2016/5/22.
 */
public class AttachedUserInfo implements Parcelable {
    @JsonProperty("id")
    public String id;
    @JsonProperty("openid")
    public String openId;
    @JsonProperty("user_token")
    public String userToken;
    @JsonProperty("is_bind_wechat")
    public String isBindWeChat;
    @JsonProperty("tel")
    public String phoneNumber;
    @JsonProperty("account_number")
    public String accountNumber;
    @JsonProperty("realname")
    public String realname;
    @JsonProperty("nickname")
    public String nickName;
    @JsonProperty("avatar")
    public String headPictureUrl;
    @JsonProperty("bj_image")
    public String bjImage;
    @JsonProperty("follow_num")
    public String followNumber;
    @JsonProperty("fans_num")
    public String fansNum;
    @JsonProperty("sex")
    public String sex;
    @JsonProperty("sign")
    public String sign;
    @JsonProperty("star")
    public String star;
    @JsonProperty("status")
    public String status;
    @JsonProperty("register_latitude")
    public String registerLatitude;
    @JsonProperty("register_longitude")
    public String registerLongitude;
    @JsonProperty("dev")
    public String dev;
    @JsonProperty("current_tel_city")
    public String currentTelCity;
    @JsonProperty("current_city")
    public String currentCity;
    @JsonProperty("last_login_ip")
    public String lastLoginIp;
    @JsonProperty("register_ip")
    public String registerIp;
    @JsonProperty("last_login_time")
    public String lastLoginTime;
    @JsonProperty("update_time")
    public String updateTime;
    @JsonProperty("create_time")
    public String createTime;

    public AttachedUserInfo() {
    }

    protected AttachedUserInfo(Parcel in) {
        id = in.readString();
        openId = in.readString();
        userToken = in.readString();
        phoneNumber = in.readString();
        accountNumber = in.readString();
        nickName = in.readString();
        headPictureUrl = in.readString();
        bjImage = in.readString();
        followNumber = in.readString();
        fansNum = in.readString();
        sex = in.readString();
        sign = in.readString();
        star = in.readString();
        status = in.readString();
        registerLatitude = in.readString();
        registerLongitude = in.readString();
        dev = in.readString();
        currentTelCity = in.readString();
        currentCity = in.readString();
        lastLoginIp = in.readString();
        registerIp = in.readString();
        lastLoginTime = in.readString();
        updateTime = in.readString();
        createTime = in.readString();
    }

    public static final Creator<AttachedUserInfo> CREATOR = new Creator<AttachedUserInfo>() {
        @Override
        public AttachedUserInfo createFromParcel(Parcel in) {
            return new AttachedUserInfo(in);
        }

        @Override
        public AttachedUserInfo[] newArray(int size) {
            return new AttachedUserInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(openId);
        dest.writeString(userToken);
        dest.writeString(phoneNumber);
        dest.writeString(accountNumber);
        dest.writeString(nickName);
        dest.writeString(headPictureUrl);
        dest.writeString(bjImage);
        dest.writeString(followNumber);
        dest.writeString(fansNum);
        dest.writeString(sex);
        dest.writeString(sign);
        dest.writeString(star);
        dest.writeString(status);
        dest.writeString(registerLatitude);
        dest.writeString(registerLongitude);
        dest.writeString(dev);
        dest.writeString(currentTelCity);
        dest.writeString(currentCity);
        dest.writeString(lastLoginIp);
        dest.writeString(registerIp);
        dest.writeString(lastLoginTime);
        dest.writeString(updateTime);
        dest.writeString(createTime);
    }
}
