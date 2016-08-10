package com.tgf.exhibition.http.msg;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.tgf.exhibition.http.ResponseMessage;
import com.tgf.exhibition.http.json.DeclarationOrder;

import java.util.List;

/**
 * Created by jeff on 2016/5/22.
 */
public class DeclarationOrderMassage extends ResponseMessage<List<DeclarationOrder>> {
    @JsonDeserialize(contentAs = DeclarationOrder.class)
    @Override
    public void setData(List<DeclarationOrder> data) {
        super.setData(data);
    }
}
