package com.tgf.exhibition.http.json;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jeff on 2016/5/22.
 */
public class Scene implements Parcelable {

    @JsonProperty("id")
    public String sceneId;

    @JsonProperty("title")
    public String sceneTitle;

    @JsonProperty("icon")
    public String sceneIconUrl;

    @JsonProperty("start_time")
    public String startTime;

    @JsonProperty("is_selected")
    public String isSelected;

    public Scene(){}

    protected Scene(Parcel in) {
        sceneId = in.readString();
        sceneTitle = in.readString();
        sceneIconUrl = in.readString();
        isSelected = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sceneId);
        dest.writeString(sceneTitle);
        dest.writeString(sceneIconUrl);
        dest.writeString(isSelected);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Scene> CREATOR = new Creator<Scene>() {
        @Override
        public Scene createFromParcel(Parcel in) {
            return new Scene(in);
        }

        @Override
        public Scene[] newArray(int size) {
            return new Scene[size];
        }
    };
}
