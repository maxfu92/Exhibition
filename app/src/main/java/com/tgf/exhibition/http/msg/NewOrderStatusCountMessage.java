package com.tgf.exhibition.http.msg;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.tgf.exhibition.http.ResponseMessage;
import com.tgf.exhibition.http.json.NewOrderStatusCount;

/**
 * Created by jeff on 2016/5/22.
 */
public class NewOrderStatusCountMessage extends ResponseMessage<NewOrderStatusCount> {
    @JsonDeserialize(as = NewOrderStatusCount.class)
    @Override
    public void setData(NewOrderStatusCount data) {
        super.setData(data);
    }
}
