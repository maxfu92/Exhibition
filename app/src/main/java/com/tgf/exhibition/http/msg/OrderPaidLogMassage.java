package com.tgf.exhibition.http.msg;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.tgf.exhibition.http.ResponseMessage;
import com.tgf.exhibition.http.json.OrderPaidLog;

/**
 * Created by jeff on 2016/5/22.
 */
public class OrderPaidLogMassage extends ResponseMessage<OrderPaidLog[]> {
    @JsonDeserialize(contentAs = OrderPaidLog.class)
    @Override
    public void setData(OrderPaidLog[] data) {
        super.setData(data);
    }
}
