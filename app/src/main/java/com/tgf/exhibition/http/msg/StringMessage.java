package com.tgf.exhibition.http.msg;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.tgf.exhibition.http.ResponseMessage;

/**
 * Created by jeff on 2016/5/22.
 */
public final class StringMessage extends ResponseMessage<String> {
    @JsonDeserialize(as = String.class)
    @Override
    public void setData(String data) {
        super.setData(data);
    }
}
