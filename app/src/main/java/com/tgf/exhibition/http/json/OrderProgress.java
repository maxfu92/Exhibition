package com.tgf.exhibition.http.json;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jeff on 2016/5/25.
 */
public class OrderProgress {
    @JsonProperty("id")
    public String id;
    @JsonProperty("type")
    public String type;
    @JsonProperty("title")
    public String title;
    @JsonProperty("message")
    public String message;
    @JsonProperty("scene_id")
    public String scene_id;
    @JsonProperty("create_time")
    public String create_time;
}
