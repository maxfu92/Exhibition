package com.tgf.exhibition.http.msg;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.tgf.exhibition.http.ResponseMessage;
import com.tgf.exhibition.http.json.PrepayLog;

/**
 * Created by jeff on 2016/5/30.
 */
public class PrepayLogMessage extends ResponseMessage<PrepayLog> {
    @JsonDeserialize(as = PrepayLog.class)
    @Override
    public void setData(PrepayLog data) {
        super.setData(data);
    }
}
