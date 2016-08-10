package com.tgf.exhibition.http.msg;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.tgf.exhibition.http.ResponseMessage;
import com.tgf.exhibition.http.json.DeclarationType;

/**
 * Created by jeff on 2016/5/22.
 */
public class DeclarationTypeMessage extends ResponseMessage<DeclarationType[]> {
    @JsonDeserialize(contentAs = DeclarationType.class)
    @Override
    public void setData(DeclarationType[] data) {
        super.setData(data);
    }
}
