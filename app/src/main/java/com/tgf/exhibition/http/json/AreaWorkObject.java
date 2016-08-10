package com.tgf.exhibition.http.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Created by jeff on 2016/5/22.
 */
public class AreaWorkObject {
    @JsonProperty("area_name")
    public String areaTitle;

    @JsonDeserialize(contentAs = WorkObject.class)
    @JsonProperty("objects")
    public WorkObject[] workObjects;

    public AreaWorkObject(){}
}
