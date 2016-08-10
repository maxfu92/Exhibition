package com.tgf.exhibition.http.json;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jeff on 2016/5/22.
 */
public class DeclarationType implements Parcelable {
    @JsonProperty("id")
    public String typeId;

    @JsonProperty("icon")
    public String iconUrl;

    @JsonProperty("title")
    public String displayTitle;

    /**
     * 1为普通计算方式; 2为带面积的计算. 默认为1
     */
    @JsonProperty("count_type")
    public int calcType = 1;

    // TODO: 添加申报类型特征，分计算使用面积 和 不计算使用面积
    public boolean isCalcAreaSize() {
        return calcType == 2;
    }

    public DeclarationType(){}

    protected DeclarationType(Parcel in) {
        typeId = in.readString();
        iconUrl = in.readString();
        displayTitle = in.readString();
        calcType = in.readInt();
    }

    public static final Creator<DeclarationType> CREATOR = new Creator<DeclarationType>() {
        @Override
        public DeclarationType createFromParcel(Parcel in) {
            return new DeclarationType(in);
        }

        @Override
        public DeclarationType[] newArray(int size) {
            return new DeclarationType[size];
        }
    };



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(typeId);
        dest.writeString(iconUrl);
        dest.writeString(displayTitle);
        dest.writeInt(calcType);
    }
}
