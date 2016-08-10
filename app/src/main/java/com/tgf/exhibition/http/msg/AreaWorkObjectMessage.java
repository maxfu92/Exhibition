package com.tgf.exhibition.http.msg;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.tgf.exhibition.http.ResponseMessage;
import com.tgf.exhibition.http.json.AreaWorkObject;

/**
 * Created by jeff on 2016/5/22.
 */
public class AreaWorkObjectMessage extends ResponseMessage<AreaWorkObject[]> {
    @JsonDeserialize(contentAs = AreaWorkObject.class)
    @Override
    public void setData(AreaWorkObject[] data) {
        super.setData(data);
    }
}
