package com.tgf.exhibition.http.json;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jeff on 2016/5/21.
 */
public class LoginData implements Parcelable {
    @JsonProperty("id")
    public String userId;

    @JsonProperty("status")
    public String regStatus;

    @JsonProperty("isBind")
    public String isBind;

    @JsonProperty("user_token")
    public String userToken;

    @JsonIgnore
    public String phoneNumber;

    @JsonIgnore
    public String loginSceneId;

    public LoginData() {
    }

    protected LoginData(Parcel in) {
        userId = in.readString();
        regStatus = in.readString();
        isBind = in.readString();
        userToken = in.readString();
        phoneNumber = in.readString();
        loginSceneId = in.readString();
    }

    public static final Creator<LoginData> CREATOR = new Creator<LoginData>() {
        @Override
        public LoginData createFromParcel(Parcel in) {
            return new LoginData(in);
        }

        @Override
        public LoginData[] newArray(int size) {
            return new LoginData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(regStatus);
        dest.writeString(isBind);
        dest.writeString(userToken);
        dest.writeString(phoneNumber);
        dest.writeString(loginSceneId);
    }
}
