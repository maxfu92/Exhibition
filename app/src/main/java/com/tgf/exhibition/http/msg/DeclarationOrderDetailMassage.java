package com.tgf.exhibition.http.msg;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.tgf.exhibition.http.ResponseMessage;
import com.tgf.exhibition.http.json.DeclarationOrder;
import com.tgf.exhibition.http.json.DeclarationOrderDetail;

import java.util.List;

/**
 * Created by jeff on 2016/5/22.
 */
public class DeclarationOrderDetailMassage extends ResponseMessage<DeclarationOrderDetail> {
    @JsonDeserialize(as = DeclarationOrderDetail.class)
    @Override
    public void setData(DeclarationOrderDetail data) {
        super.setData(data);
    }
}
