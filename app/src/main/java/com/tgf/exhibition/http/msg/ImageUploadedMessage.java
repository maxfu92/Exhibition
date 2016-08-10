package com.tgf.exhibition.http.msg;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.tgf.exhibition.http.ResponseMessage;
import com.tgf.exhibition.http.json.DeclarationType;
import com.tgf.exhibition.http.json.UploadedFile;

/**
 * Created by jeff on 2016/5/27.
 */
public class ImageUploadedMessage extends ResponseMessage<UploadedFile> {
    @JsonDeserialize(as = UploadedFile.class)
    @Override
    public void setData(UploadedFile data) {
        super.setData(data);
    }
}
