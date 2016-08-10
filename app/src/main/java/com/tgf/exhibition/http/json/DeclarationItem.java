package com.tgf.exhibition.http.json;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jeff on 2016/5/22.
 */
public class DeclarationItem {
    @JsonProperty("id")
    public String itemId;

    @JsonProperty("title")
    public String displayTitle;

    @JsonProperty("price")
    public String price;

    @JsonProperty("unit")
    public String unit ;

    @JsonProperty("deposit_money")
    public String deposit;

    @JsonProperty("intro")
    public String description;

    @JsonProperty("order_title")
    public String orderTitle;

    @JsonProperty("scene_id")
    public String sceneId ;

    public DeclarationItem(){}
}
