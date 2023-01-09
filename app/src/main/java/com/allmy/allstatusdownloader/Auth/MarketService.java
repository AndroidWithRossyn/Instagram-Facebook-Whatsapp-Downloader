package com.allmy.allstatusdownloader.Auth;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.Html;
import android.text.Html.TagHandler;

import androidx.core.app.NotificationCompat;

import com.allmy.allstatusdownloader.Model.Constants;
import com.allmy.allstatusdownloader.Others.AQUtility;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.xml.sax.XMLReader;

import java.util.Locale;

import cz.msebera.android.httpclient.cookie.ClientCookie;

public class MarketService {
    private static final String BULLET = "•";
    public static final int MAJOR = 2;
    public static final int MINOR = 1;
    public static final int REVISION = 0;
    private static final String SKIP_VERSION = "aqs.skip";
    private static ApplicationInfo ai;
    private static PackageInfo pi;
    /* access modifiers changed from: private */
    public Activity act;
    /* access modifiers changed from: private */
    public AQuery aq;
    /* access modifiers changed from: private */
    public boolean completed;
    private long expire = 720000;
    /* access modifiers changed from: private */
    public boolean fetch;
    private boolean force;
    private Handler handler;
    private int level = 0;
    private String locale;
    /* access modifiers changed from: private */
    public int progress;
    /* access modifiers changed from: private */
    public String rateUrl;
    /* access modifiers changed from: private */
    public String updateUrl;
    /* access modifiers changed from: private */
    public String version;

    private class Handler implements OnClickListener, TagHandler {
        private Handler() {
        }

        public void marketCb(String str, JSONObject jSONObject, AjaxStatus ajaxStatus) {
            if (!MarketService.this.act.isFinishing()) {
                if (jSONObject != null) {
                    String optString = jSONObject.optString(NotificationCompat.CATEGORY_STATUS);
                    if ("1".equals(optString)) {
                        if (jSONObject.has("dialog")) {
                            cb(str, jSONObject, ajaxStatus);
                        }
                        if (!MarketService.this.fetch && jSONObject.optBoolean("fetch", false) && ajaxStatus.getSource() == 1) {
                            MarketService.this.fetch = true;
                            String optString2 = jSONObject.optString("marketUrl", null);
                            AjaxCallback ajaxCallback = new AjaxCallback();
                            ((AjaxCallback) ((AjaxCallback) ajaxCallback.url(optString2)).type(String.class)).handler(this, "detailCb");
                            ((AQuery) MarketService.this.aq.progress(MarketService.this.progress)).ajax(ajaxCallback);
                        }
                    } else if ("0".equals(optString)) {
                        ajaxStatus.invalidate();
                    } else {
                        cb(str, jSONObject, ajaxStatus);
                    }
                } else {
                    cb(str, jSONObject, ajaxStatus);
                }
            }
        }

        private void cb(String str, JSONObject jSONObject, AjaxStatus ajaxStatus) {
            if (!MarketService.this.completed) {
                MarketService.this.completed = true;
                MarketService.this.progress = 0;
                MarketService.this.callback(str, jSONObject, ajaxStatus);
            }
        }

        public void detailCb(String str, String str2, AjaxStatus ajaxStatus) {
            if (str2 != null && str2.length() > 1000) {
                String access$600 = MarketService.this.getQueryUrl();
                AjaxCallback ajaxCallback = new AjaxCallback();
                ((AjaxCallback) ((AjaxCallback) ajaxCallback.url(access$600)).type(JSONObject.class)).handler(this, "marketCb");
                ajaxCallback.param("html", str2);
                ((AQuery) MarketService.this.aq.progress(MarketService.this.progress)).ajax(ajaxCallback);
            }
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            if (i == -3) {
                MarketService.setSkipVersion(MarketService.this.act, MarketService.this.version);
            } else if (i == -2) {
                MarketService.openUrl(MarketService.this.act, MarketService.this.updateUrl);
            } else if (i == -1) {
                MarketService.openUrl(MarketService.this.act, MarketService.this.rateUrl);
            }
        }

        public void handleTag(boolean z, String str, Editable editable, XMLReader xMLReader) {
            if (!"li".equals(str)) {
                return;
            }
            if (z) {
                String str2 = "  ";
                editable.append(str2);
                editable.append(MarketService.BULLET);
                editable.append(str2);
                return;
            }
            editable.append(IOUtils.LINE_SEPARATOR_UNIX);
        }
    }

    private String getHost() {
        return "https://androidquery.appspot.com";
    }

    public MarketService(Activity activity) {
        this.act = activity;
        this.aq = new AQuery(activity);
        this.handler = new Handler();
        this.locale = Locale.getDefault().toString();
        this.rateUrl = getMarketUrl();
        this.updateUrl = this.rateUrl;
    }

    public MarketService rateUrl(String str) {
        this.rateUrl = str;
        return this;
    }

    public MarketService level(int i) {
        this.level = i;
        return this;
    }

    public MarketService updateUrl(String str) {
        this.updateUrl = str;
        return this;
    }

    public MarketService locale(String str) {
        this.locale = str;
        return this;
    }

    public MarketService progress(int i) {
        this.progress = i;
        return this;
    }

    public MarketService force(boolean z) {
        this.force = z;
        return this;
    }

    public MarketService expire(long j) {
        this.expire = j;
        return this;
    }

    private ApplicationInfo getApplicationInfo() {
        if (ai == null) {
            ai = this.act.getApplicationInfo();
        }
        return ai;
    }

    private PackageInfo getPackageInfo() {
        if (pi == null) {
            try {
                pi = this.act.getPackageManager().getPackageInfo(getAppId(), 0);
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return pi;
    }

    /* access modifiers changed from: private */
    public String getQueryUrl() {
        String appId = getAppId();
        StringBuilder sb = new StringBuilder();
        sb.append(getHost());
        sb.append("/api/market?app=");
        sb.append(appId);
        sb.append("&locale=");
        sb.append(this.locale);
        sb.append("&version=");
        sb.append(getVersion());
        sb.append("&code=");
        sb.append(getVersionCode());
        sb.append("&aq=");
        sb.append(Constants.VERSION);
        String sb2 = sb.toString();
        if (!this.force) {
            return sb2;
        }
        StringBuilder sb3 = new StringBuilder();
        sb3.append(sb2);
        sb3.append("&force=true");
        return sb3.toString();
    }

    private String getAppId() {
        return getApplicationInfo().packageName;
    }

    private Drawable getAppIcon() {
        return getApplicationInfo().loadIcon(this.act.getPackageManager());
    }

    private String getVersion() {
        return getPackageInfo().versionName;
    }

    private int getVersionCode() {
        return getPackageInfo().versionCode;
    }

    public void checkVersion() {
        String queryUrl = getQueryUrl();
        AjaxCallback ajaxCallback = new AjaxCallback();
        ((AjaxCallback) ((AjaxCallback) ((AjaxCallback) ((AjaxCallback) ajaxCallback.url(queryUrl)).type(JSONObject.class)).handler(this.handler, "marketCb")).fileCache(!this.force)).expire(this.expire);
        ((AQuery) this.aq.progress(this.progress)).ajax(ajaxCallback);
    }

    /* access modifiers changed from: private */
    public static boolean openUrl(Activity activity, String str) {
        if (str == null) {
            return false;
        }
        try {
            activity.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(str)));
            return true;
        } catch (Exception unused) {
            return false;
        }
    }

    private String getMarketUrl() {
        String appId = getAppId();
        StringBuilder sb = new StringBuilder();
        sb.append("market://details?id=");
        sb.append(appId);
        return sb.toString();
    }

    /* access modifiers changed from: protected */
    public void callback(String str, JSONObject jSONObject, AjaxStatus ajaxStatus) {
        if (jSONObject != null) {
            String str2 = ClientCookie.VERSION_ATTR;
            String optString = jSONObject.optString(str2, "0");
            int optInt = jSONObject.optInt("code", 0);
            StringBuilder sb = new StringBuilder();
            sb.append(getVersion());
            String str3 = "->";
            sb.append(str3);
            sb.append(optString);
            sb.append(":");
            sb.append(getVersionCode());
            sb.append(str3);
            sb.append(optInt);
            AQUtility.debug(str2, sb.toString());
            AQUtility.debug("outdated", Boolean.valueOf(outdated(optString, optInt)));
            if (this.force || outdated(optString, optInt)) {
                showUpdateDialog(jSONObject);
            }
        }
    }

    private boolean outdated(String str, int i) {
        if (str.equals(getSkipVersion(this.act))) {
            return false;
        }
        String version2 = getVersion();
        int versionCode = getVersionCode();
        if (version2.equals(str) || versionCode > i) {
            return false;
        }
        return requireUpdate(version2, str, this.level);
    }

    /* JADX WARNING: Removed duplicated region for block: B:23:0x004f A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0050 A[RETURN] */
    private boolean requireUpdate(String r6, String r7, int r8) {
        /*
            r5 = this;
            java.lang.String r0 = "\\."
            boolean r1 = r6.equals(r7)
            r2 = 0
            if (r1 == 0) goto L_0x000a
            return r2
        L_0x000a:
            r1 = 1
            java.lang.String[] r6 = r6.split(r0)     // Catch:{ Exception -> 0x0052 }
            java.lang.String[] r7 = r7.split(r0)     // Catch:{ Exception -> 0x0052 }
            int r0 = r6.length     // Catch:{ Exception -> 0x0052 }
            r3 = 3
            if (r0 < r3) goto L_0x0051
            int r0 = r7.length     // Catch:{ Exception -> 0x0052 }
            if (r0 >= r3) goto L_0x001b
            goto L_0x0051
        L_0x001b:
            r0 = 2
            if (r8 == 0) goto L_0x0023
            if (r8 == r1) goto L_0x0032
            if (r8 == r0) goto L_0x0041
            return r1
        L_0x0023:
            int r8 = r6.length     // Catch:{ Exception -> 0x0052 }
            int r8 = r8 - r1
            r8 = r6[r8]     // Catch:{ Exception -> 0x0052 }
            int r4 = r7.length     // Catch:{ Exception -> 0x0052 }
            int r4 = r4 - r1
            r4 = r7[r4]     // Catch:{ Exception -> 0x0052 }
            boolean r8 = r8.equals(r4)     // Catch:{ Exception -> 0x0052 }
            if (r8 != 0) goto L_0x0032
            return r1
        L_0x0032:
            int r8 = r6.length     // Catch:{ Exception -> 0x0052 }
            int r8 = r8 - r0
            r8 = r6[r8]     // Catch:{ Exception -> 0x0052 }
            int r4 = r7.length     // Catch:{ Exception -> 0x0052 }
            int r4 = r4 - r0
            r0 = r7[r4]     // Catch:{ Exception -> 0x0052 }
            boolean r8 = r8.equals(r0)     // Catch:{ Exception -> 0x0052 }
            if (r8 != 0) goto L_0x0041
            return r1
        L_0x0041:
            int r8 = r6.length     // Catch:{ Exception -> 0x0052 }
            int r8 = r8 - r3
            r6 = r6[r8]     // Catch:{ Exception -> 0x0052 }
            int r8 = r7.length     // Catch:{ Exception -> 0x0052 }
            int r8 = r8 - r3
            r7 = r7[r8]     // Catch:{ Exception -> 0x0052 }
            boolean r6 = r6.equals(r7)     // Catch:{ Exception -> 0x0052 }
            if (r6 != 0) goto L_0x0050
            return r1
        L_0x0050:
            return r2
        L_0x0051:
            return r1
        L_0x0052:
            r6 = move-exception
            com.androidquery.util.AQUtility.report(r6)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.androidquery.service.MarketService.requireUpdate(java.lang.String, java.lang.String, int):boolean");
    }

    /* access modifiers changed from: protected */
    public void showUpdateDialog(JSONObject jSONObject) {
        if (jSONObject != null && this.version == null && isActive()) {
            JSONObject optJSONObject = jSONObject.optJSONObject("dialog");
            String optString = optJSONObject.optString("update", "Update");
            String optString2 = optJSONObject.optString("skip", "Skip");
            String optString3 = optJSONObject.optString("rate", "Rate");
            String str = "wbody";
            String optString4 = optJSONObject.optString(str, "");
            String optString5 = optJSONObject.optString("title", "Update Available");
            AQUtility.debug(str, optString4);
            this.version = jSONObject.optString(ClientCookie.VERSION_ATTR, null);
            AlertDialog create = new Builder(this.act).setIcon(getAppIcon()).setTitle(optString5).setPositiveButton(optString3, this.handler).setNeutralButton(optString2, this.handler).setNegativeButton(optString, this.handler).create();
            create.setMessage(Html.fromHtml(patchBody(optString4), null, this.handler));
            this.aq.show(create);
        }
    }

    private static String patchBody(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append("<small>");
        sb.append(str);
        sb.append("</small>");
        return sb.toString();
    }

    /* access modifiers changed from: private */
    public static void setSkipVersion(Context context, String str) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(SKIP_VERSION, str).commit();
    }

    private static String getSkipVersion(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(SKIP_VERSION, null);
    }

    private boolean isActive() {
        return !this.act.isFinishing();
    }
}
