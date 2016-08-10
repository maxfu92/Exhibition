package com.tgf.exhibition.http.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Created by jeff on 2016/5/27.
 */
public class UploadedFile {
    @JsonDeserialize(as = UploadedFileDetail.class)
    @JsonProperty("source")
    public UploadedFileDetail source;
    @JsonProperty("savename")
    public String saveName;
    @JsonProperty("url")
    public String url;
}

