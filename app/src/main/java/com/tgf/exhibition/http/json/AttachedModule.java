package com.tgf.exhibition.http.json;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jeff on 2016/5/22.
 */
public class AttachedModule implements Parcelable {
    @JsonProperty("icon")
    public String icon;

    @JsonProperty("title")
    public String title;

    @JsonProperty("name")
    public String name;

    @JsonProperty("message_count")
    public String msgCount;

    public AttachedModule(){}

    protected AttachedModule(Parcel in) {
        icon = in.readString();
        title = in.readString();
        name = in.readString();
        msgCount = in.readString();
    }

    public static final Creator<AttachedModule> CREATOR = new Creator<AttachedModule>() {
        @Override
        public AttachedModule createFromParcel(Parcel in) {
            return new AttachedModule(in);
        }

        @Override
        public AttachedModule[] newArray(int size) {
            return new AttachedModule[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(icon);
        dest.writeString(title);
        dest.writeString(name);
        dest.writeString(msgCount);
    }
}
