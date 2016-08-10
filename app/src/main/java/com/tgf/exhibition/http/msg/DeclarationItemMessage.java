package com.tgf.exhibition.http.msg;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.tgf.exhibition.http.ResponseMessage;
import com.tgf.exhibition.http.json.DeclarationItem;

/**
 * Created by jeff on 2016/5/22.
 */
public class DeclarationItemMessage extends ResponseMessage<DeclarationItem[]> {
    @JsonDeserialize(contentAs = DeclarationItem.class)
    @Override
    public void setData(DeclarationItem[] data) {
        super.setData(data);
    }
}
