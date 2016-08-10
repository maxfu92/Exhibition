package com.tgf.exhibition.http.json;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Created by jeff on 2016/5/22.
 */
public class XCScene implements Parcelable {
    @JsonProperty("status")
    public String status;

    @JsonProperty("scene_id")
    public String sceneId;

    @JsonProperty("scene_title")
    public String sceneTitle;

    @JsonProperty("scene_icon")
    public String sceneIconUrl;

    @JsonProperty("role")
    public String role;

    @JsonProperty("message")
    public String message;

    @JsonDeserialize(as = AttachedUserInfo.class)
    @JsonProperty("userinfo")
    public AttachedUserInfo userInfo;

    @JsonDeserialize(contentAs = Scene.class)
    @JsonProperty("scenes")
    public Scene[] scenes;

    @JsonDeserialize(contentAs = AttachedModule.class)
    @JsonProperty("actions")
    public AttachedModule[] attachedModules;

    public XCScene(){
        super();
    }

    public Scene getSelectedScene() {
        if(scenes != null) {
            for (Scene scene : scenes) {
                if ("Y".equals(scene.isSelected)) {
                    return scene;
                }
            }
        }
        return null;
    }

    protected XCScene(Parcel in) {
        status = in.readString();
        sceneId = in.readString();
        sceneTitle = in.readString();
        sceneIconUrl = in.readString();
        role = in.readString();
        userInfo = in.readParcelable(AttachedUserInfo.class.getClassLoader());
        scenes = in.createTypedArray(Scene.CREATOR);
        attachedModules = in.createTypedArray(AttachedModule.CREATOR);
    }

    public static final Creator<XCScene> CREATOR = new Creator<XCScene>() {
        @Override
        public XCScene createFromParcel(Parcel in) {
            return new XCScene(in);
        }

        @Override
        public XCScene[] newArray(int size) {
            return new XCScene[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(status);
        dest.writeString(sceneId);
        dest.writeString(sceneTitle);
        dest.writeString(sceneIconUrl);
        dest.writeString(role);
        dest.writeParcelable(userInfo, flags);
        dest.writeTypedArray(scenes, flags);
        dest.writeTypedArray(attachedModules, flags);
    }
}

