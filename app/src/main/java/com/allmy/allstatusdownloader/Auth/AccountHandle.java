package com.allmy.allstatusdownloader.Auth;

import android.content.Context;

import org.apache.http.HttpRequest;

import java.net.HttpURLConnection;
import java.util.Iterator;
import java.util.LinkedHashSet;

public abstract class AccountHandle {
    private LinkedHashSet<AbstractAjaxCallback<?, ?>> callbacks;

    public void applyToken(AbstractAjaxCallback<?, ?> abstractAjaxCallback, HttpURLConnection httpURLConnection) {
    }

    public void applyToken(AbstractAjaxCallback<?, ?> abstractAjaxCallback, HttpRequest httpRequest) {
    }

    public abstract void auth();

    public abstract boolean authenticated();

    public abstract boolean expired(AbstractAjaxCallback<?, ?> abstractAjaxCallback, AjaxStatus ajaxStatus);

    public String getCacheUrl(String str) {
        return str;
    }

    public String getNetworkUrl(String str) {
        return str;
    }

    public abstract boolean reauth(AbstractAjaxCallback<?, ?> abstractAjaxCallback);

    public void unauth() {
    }

    public synchronized void auth(AbstractAjaxCallback<?, ?> abstractAjaxCallback) {
        if (this.callbacks == null) {
            this.callbacks = new LinkedHashSet<>();
            this.callbacks.add(abstractAjaxCallback);
            auth();
        } else {
            this.callbacks.add(abstractAjaxCallback);
        }
    }

    /* access modifiers changed from: protected */
    public synchronized void success(Context context) {
        if (this.callbacks != null) {
            Iterator it = this.callbacks.iterator();
            while (it.hasNext()) {
                ((AbstractAjaxCallback) it.next()).async(context);
            }
            this.callbacks = null;
        }
    }

    /* access modifiers changed from: protected */
    public synchronized void failure(Context context, int i, String str) {
        if (this.callbacks != null) {
            Iterator it = this.callbacks.iterator();
            while (it.hasNext()) {
                ((AbstractAjaxCallback) it.next()).failure(i, str);
            }
            this.callbacks = null;
        }
    }
}
