package com.allmy.allstatusdownloader.Model;

public class HeighLightParameter {
    String _csrftoken;
    String _uid;
    String _uuid;
    String source;
    String[] user_ids;

    public HeighLightParameter(String str, String str2, String str3, String[] strArr, String str4) {
        this._uuid = str;
        this._uid = str2;
        this._csrftoken = str3;
        this.user_ids = strArr;
        this.source = str4;
    }
}
