package com.allmy.allstatusdownloader.Others;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

public class CustomWebView extends WebView {
    boolean viewcustomview;

    public CustomWebView(Context context) {
        super(context);
    }

    public CustomWebView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public CustomWebView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @TargetApi(21)
    public CustomWebView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    @TargetApi(11)
    public CustomWebView(Context context, AttributeSet attributeSet, int i, boolean z) {
        super(context, attributeSet, i, z);
    }

    public void loadUrl(String str) {
        HitTestResult hitTestResult = getHitTestResult();
        if (!str.startsWith("javascript:") || hitTestResult == null || hitTestResult.getType() != 9) {
            if (!str.contains("facebook.com/login/save-device") || this.viewcustomview) {
                super.loadUrl(str);
            } else {
                this.viewcustomview = true;
                super.loadUrl("https://m.facebook.com");
            }
        }
    }
}
