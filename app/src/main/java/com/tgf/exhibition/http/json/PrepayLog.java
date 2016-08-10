package com.tgf.exhibition.http.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Created by jeff on 2016/5/30.
 */
public class PrepayLog {
    @JsonProperty("object_icon")
    public String defaultWorkObjectIcon;
    @JsonProperty("object_title")
    public String defaultWorkObjectTitle;
    @JsonProperty("money")
    public String remaindMoney;
    @JsonProperty("paid_money")
    public String paidMoney;

    @JsonDeserialize(contentAs = WorkObject.class)
    @JsonProperty("objects")
    public WorkObject[] workObjects;

    @JsonDeserialize(contentAs = PrepaySummery.class)
    @JsonProperty("lists")
    public PrepaySummery[] prepaySummeries;
}
