package com.tgf.exhibition.http.json;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jeff on 2016/5/27.
 */
public class UploadedFileDetail {
    @JsonProperty("name")
    public String name;
    @JsonProperty("type")
    public String type;
    @JsonProperty("size")
    public String size;
    @JsonProperty("key")
    public String key;
    @JsonProperty("ext")
    public String ext;
    @JsonProperty("md5")
    public String md5;
    @JsonProperty("sha1")
    public String sha1;
    @JsonProperty("savename")
    public String saveName;
    @JsonProperty("savepath")
    public String savePath;
}
