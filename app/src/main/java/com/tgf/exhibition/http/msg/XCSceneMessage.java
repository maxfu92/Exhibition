package com.tgf.exhibition.http.msg;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.tgf.exhibition.http.ResponseMessage;
import com.tgf.exhibition.http.json.XCScene;

/**
 * Created by jeff on 2016/5/22.
 */
public class XCSceneMessage extends ResponseMessage<XCScene> {
    @JsonDeserialize(as = XCScene.class)
    @Override
    public void setData(XCScene data) {
        super.setData(data);
    }
}
