package com.tgf.exhibition.http.json;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jeff on 2016/5/30.
 */
public class PrepaySummery {
    @JsonProperty("id")
    public String id;
    @JsonProperty("object_id")
    public String objectId;
    @JsonProperty("scene_id")
    public String sceneId;
    @JsonProperty("type")
    public String payType; // [prepay|wxzf]
    @JsonProperty("user_id")
    public String userId;
    @JsonProperty("realname")
    public String realname;
    @JsonProperty("icon")
    public String icon;
    @JsonProperty("money")
    public String money;
    @JsonProperty("title")
    public String title;
    @JsonProperty("create_time")
    public String createTime;
    @JsonProperty("update_time")
    public String updateTime;
}
