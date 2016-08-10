package com.tgf.exhibition.http.json;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jeff on 2016/5/25.
 */
public class OnsiteDocument {
    @JsonProperty("filename")
    public String filename;
    @JsonProperty("url")
    public String url;
    @JsonProperty("create_time")
    public String create_time;
    @JsonProperty("size")
    public String size;
}
