package com.tgf.exhibition.http.msg;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.tgf.exhibition.http.ResponseMessage;
import com.tgf.exhibition.http.json.LoginData;

/**
 * Created by jeff on 2016/5/21.
 */ /// Http Response Messages ///
public final class LoginResponseMessage extends ResponseMessage<LoginData> {
    @JsonDeserialize(as = LoginData.class)
    @Override
    public void setData(LoginData data) {
        super.setData(data);
    }
}
