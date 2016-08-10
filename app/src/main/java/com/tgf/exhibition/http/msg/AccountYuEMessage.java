package com.tgf.exhibition.http.msg;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.tgf.exhibition.http.ResponseMessage;
import com.tgf.exhibition.http.json.AccountYuE;

/**
 * Created by jeff on 2016/5/22.
 */
public class AccountYuEMessage extends ResponseMessage<AccountYuE> {
    @JsonDeserialize(as = AccountYuE.class)
    @Override
    public void setData(AccountYuE data) {
        super.setData(data);
    }
}
