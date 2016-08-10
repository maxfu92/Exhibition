package com.tgf.exhibition.http.json;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jeff on 2016/5/22.
 */
public class NewOrderStatusCount implements Parcelable {
    @JsonProperty("pending_pay_count")
    public String penddingPayCount;

    @JsonProperty("pending_server_count")
    public String penddingServiceCount;

    @JsonProperty("pending_confirm_count")
    public String penddingConfirmCount;

    @JsonProperty("pending_comment_count")
    public String penddingCommentCount;

    public NewOrderStatusCount(){}

    protected NewOrderStatusCount(Parcel in) {
        penddingPayCount = in.readString();
        penddingServiceCount = in.readString();
        penddingConfirmCount = in.readString();
        penddingCommentCount = in.readString();
    }

    public static final Creator<NewOrderStatusCount> CREATOR = new Creator<NewOrderStatusCount>() {
        @Override
        public NewOrderStatusCount createFromParcel(Parcel in) {
            return new NewOrderStatusCount(in);
        }

        @Override
        public NewOrderStatusCount[] newArray(int size) {
            return new NewOrderStatusCount[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(penddingPayCount);
        dest.writeString(penddingServiceCount);
        dest.writeString(penddingConfirmCount);
        dest.writeString(penddingCommentCount);
    }
}
