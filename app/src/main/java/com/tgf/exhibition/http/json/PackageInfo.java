package com.tgf.exhibition.http.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jeff on 2016/5/30.
 */
public class PackageInfo {
    @JsonProperty("package_name")
    public String packageName;
    @JsonProperty("version_name")
    public String versionName;
    @JsonProperty("version_code")
    public String versionCode;
    @JsonProperty("download_url")
    public String downloadUrl;

    @JsonIgnore
    public String localePath;
}
