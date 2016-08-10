package com.tgf.exhibition.http.json;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jeff on 2016/5/22.
 */
public class AccountYuE {
    @JsonProperty("money")
    public String yuE;

    @JsonProperty("paid_money")
    public String paidMoney;

    @JsonProperty("use_money")
    public String useMoney;

    @JsonProperty("deposit_money")
    public String depositMoney;

    public AccountYuE(){}
}
