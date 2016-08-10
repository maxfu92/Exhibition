package com.tgf.exhibition.http.json;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jeff on 2016/5/22.
 */
public class WorkObject  implements Parcelable {
    @JsonProperty("id")
    public String objectId;
    @JsonProperty("name")
    public String title;
    @JsonProperty("owner_icon")
    public String owner_icon;

    /* 预付款管理中使用 */
    @JsonProperty("money")
    public String money;
    @JsonProperty("icon")
    public String icon;
    @JsonProperty("is_selected")
    public String isSelected;
    @JsonProperty("object_title")
    public String objectTitle;

    @JsonProperty("square")
    public float usedSize = 0.00f;

    public WorkObject() {
    }

    protected WorkObject(Parcel in) {
        objectId = in.readString();
        title = in.readString();
        owner_icon = in.readString();
        money = in.readString();
        icon = in.readString();
        isSelected = in.readString();
        objectTitle = in.readString();
        usedSize = in.readFloat();
    }

    public static final Creator<WorkObject> CREATOR = new Creator<WorkObject>() {
        @Override
        public WorkObject createFromParcel(Parcel in) {
            return new WorkObject(in);
        }

        @Override
        public WorkObject[] newArray(int size) {
            return new WorkObject[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(objectId);
        dest.writeString(title);
        dest.writeString(owner_icon);
        dest.writeString(money);
        dest.writeString(icon);
        dest.writeString(isSelected);
        dest.writeString(objectTitle);
        dest.writeFloat(usedSize);
    }
}
