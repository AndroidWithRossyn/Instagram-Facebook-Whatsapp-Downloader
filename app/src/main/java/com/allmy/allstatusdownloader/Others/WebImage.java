package com.allmy.allstatusdownloader.Others;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Picture;
import android.os.Build.VERSION;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebView.PictureListener;
import android.webkit.WebViewClient;

public class WebImage extends WebViewClient {
    private static final String DOUBLE_TAP_TOAST_COUNT = "double_tap_toast_count";
    private static final String PREF_FILE = "WebViewSettings";
    private static String template;
    private int color;
    private boolean control;
    private Object progress;
    private String url;
    public WebView wv;
    private boolean zoom;

    public void onScaleChanged(WebView webView, float f, float f2) {
    }

    private static String getSource(Context context) {
        if (template == null) {
            try {
                template = new String(AQUtility.toBytes(context.getClassLoader().getResourceAsStream("com/androidquery/util/web_image.html")));
            } catch (Exception e) {
                AQUtility.debug((Throwable) e);
            }
        }
        return template;
    }

    private static void fixWebviewTip(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE, 0);
        String str = DOUBLE_TAP_TOAST_COUNT;
        if (sharedPreferences.getInt(str, 1) > 0) {
            sharedPreferences.edit().putInt(str, 0).commit();
        }
    }

    public WebImage(WebView webView, String str, Object obj, boolean z, boolean z2, int i) {
        this.wv = webView;
        this.url = str;
        this.progress = obj;
        this.zoom = z;
        this.control = z2;
        this.color = i;
    }

    public void load() {
        if (!this.url.equals(this.wv.getTag(1090453505))) {
            this.wv.setTag(1090453505, this.url);
            if (VERSION.SDK_INT <= 10) {
                this.wv.setDrawingCacheEnabled(true);
            }
            fixWebviewTip(this.wv.getContext());
            WebSettings settings = this.wv.getSettings();
            settings.setSupportZoom(this.zoom);
            settings.setBuiltInZoomControls(this.zoom);
            if (!this.control) {
                disableZoomControl(this.wv);
            }
            settings.setJavaScriptEnabled(true);
            this.wv.setBackgroundColor(this.color);
            Object obj = this.progress;
            if (obj != null) {
                Common.showProgress(obj, this.url, true);
            }
            if (this.wv.getWidth() > 0) {
                setup();
            } else {
                delaySetup();
            }
        }
    }

    private void delaySetup() {
        this.wv.setPictureListener(new PictureListener() {
            public void onNewPicture(WebView webView, Picture picture) {
                WebImage.this.wv.setPictureListener(null);
                WebImage.this.setup();
            }
        });
        this.wv.loadData("<html></html>", "text/html", "utf-8");
        this.wv.setBackgroundColor(this.color);
    }

    public void setup() {
        String str = "@color";
        String replace = getSource(this.wv.getContext()).replace("@src", this.url).replace(str, Integer.toHexString(this.color));
        this.wv.setWebViewClient(this);
        this.wv.loadDataWithBaseURL(null, replace, "text/html", "utf-8", null);
        this.wv.setBackgroundColor(this.color);
    }

    @SuppressLint("WrongConstant")
    private void done(WebView webView) {
        if (this.progress != null) {
            webView.setVisibility(0);
            Common.showProgress(this.progress, this.url, false);
        }
        webView.setWebViewClient(null);
    }

    public void onPageFinished(WebView webView, String str) {
        done(webView);
    }

    public void onReceivedError(WebView webView, int i, String str, String str2) {
        done(webView);
    }

    private static void disableZoomControl(WebView webView) {
        if (VERSION.SDK_INT >= 11) {
            AQUtility.invokeHandler(webView.getSettings(), "setDisplayZoomControls", false, false, new Class[]{Boolean.TYPE}, Boolean.valueOf(false));
        }
    }
}
