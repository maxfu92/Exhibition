package com.tgf.exhibition.http.json;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jeff on 2016/5/22.
 */
public class OrderPaidLog {
    @JsonProperty("object_id")
    public String objectId;

    @JsonProperty("scene_id")
    public String sceneId;

    @JsonProperty("type")
    public String payType;

    @JsonProperty("user_id")
    public String userId;

    @JsonProperty("realname")
    public String realName;

    @JsonProperty("money")
    public String money;

    @JsonProperty("icon")
    public String iconUrl;

    @JsonProperty("title")
    public String title;

    @JsonProperty("create_time")
    public String createTime;

    public OrderPaidLog(){}
}
