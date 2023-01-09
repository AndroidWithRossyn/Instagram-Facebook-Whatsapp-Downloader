package com.allmy.allstatusdownloader.Others;

import android.content.Context;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import java.lang.ref.WeakReference;

public class FBhelper {
    private final WeakReference<Context> weakReference;

    public FBhelper(Context context) {
        this.weakReference = new WeakReference<>(context);
    }

    public void createCookiManger(Context context) {
        if (VERSION.SDK_INT >= 22) {
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
            return;
        }
        CookieSyncManager createInstance = CookieSyncManager.createInstance(context);
        createInstance.startSync();
        CookieManager instance = CookieManager.getInstance();
        instance.removeAllCookie();
        instance.removeSessionCookie();
        createInstance.stopSync();
        createInstance.sync();
    }

    public boolean valadateCooki(String str) {
        return !TextUtils.isEmpty(str) && str.contains("sessionid") && str.contains("ds_user_id") && str.contains("csrftoken");
    }
}
