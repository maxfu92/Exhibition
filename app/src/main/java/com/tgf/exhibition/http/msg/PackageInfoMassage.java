package com.tgf.exhibition.http.msg;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.tgf.exhibition.http.ResponseMessage;
import com.tgf.exhibition.http.json.PackageInfo;

/**
 * Created by jeff on 2016/5/30.
 */
public class PackageInfoMassage  extends ResponseMessage<PackageInfo> {
    @JsonDeserialize(as = PackageInfo.class)
    @Override
    public void setData(PackageInfo data) {
        super.setData(data);
    }
}
