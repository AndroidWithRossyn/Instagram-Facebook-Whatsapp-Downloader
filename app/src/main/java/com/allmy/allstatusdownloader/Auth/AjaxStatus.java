package com.allmy.allstatusdownloader.Auth;
import com.allmy.allstatusdownloader.Others.AQUtility;

import org.apache.http.Header;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;

import java.io.Closeable;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class AjaxStatus {
    public static final int AUTH_ERROR = -102;
    public static final int DATASTORE = 2;
    public static final int DEVICE = 5;
    public static final int FILE = 3;
    public static final int MEMORY = 4;
    public static final int NETWORK = 1;
    public static final int NETWORK_ERROR = -101;
    public static final int TRANSFORM_ERROR = -103;
    private DefaultHttpClient client;
    private Closeable close;
    private int code = 200;
    private HttpContext context;
    private byte[] data;
    private boolean done;
    private long duration;
    private String error;
    private File file;
    private Header[] headers;
    private boolean invalid;
    private String message = "OK";
    private boolean reauth;
    private String redirect;
    private boolean refresh;
    private int source = 1;
    private long start = System.currentTimeMillis();
    private Date time = new Date();

    public AjaxStatus() {
    }

    public AjaxStatus(int i, String str) {
        this.code = i;
        this.message = str;
    }

    /* access modifiers changed from: protected */
    public AjaxStatus source(int i) {
        this.source = i;
        return this;
    }

    public AjaxStatus code(int i) {
        this.code = i;
        return this;
    }

    /* access modifiers changed from: protected */
    public AjaxStatus error(String str) {
        this.error = str;
        return this;
    }

    public AjaxStatus message(String str) {
        this.message = str;
        return this;
    }

    /* access modifiers changed from: protected */
    public AjaxStatus redirect(String str) {
        this.redirect = str;
        return this;
    }

    /* access modifiers changed from: protected */
    public AjaxStatus context(HttpContext httpContext) {
        this.context = httpContext;
        return this;
    }

    /* access modifiers changed from: protected */
    public AjaxStatus time(Date date) {
        this.time = date;
        return this;
    }

    /* access modifiers changed from: protected */
    public AjaxStatus refresh(boolean z) {
        this.refresh = z;
        return this;
    }

    /* access modifiers changed from: protected */
    public AjaxStatus reauth(boolean z) {
        this.reauth = z;
        return this;
    }

    /* access modifiers changed from: protected */
    public AjaxStatus client(DefaultHttpClient defaultHttpClient) {
        this.client = defaultHttpClient;
        return this;
    }

    /* access modifiers changed from: protected */
    public AjaxStatus headers(Header[] headerArr) {
        this.headers = headerArr;
        return this;
    }

    public AjaxStatus done() {
        this.duration = System.currentTimeMillis() - this.start;
        this.done = true;
        this.reauth = false;
        return this;
    }

    /* access modifiers changed from: protected */
    public AjaxStatus reset() {
        this.duration = System.currentTimeMillis() - this.start;
        this.done = false;
        close();
        return this;
    }

    /* access modifiers changed from: protected */
    public void closeLater(Closeable closeable) {
        this.close = closeable;
    }

    public void close() {
        AQUtility.close(this.close);
        this.close = null;
    }

    /* access modifiers changed from: protected */
    public AjaxStatus data(byte[] bArr) {
        this.data = bArr;
        return this;
    }

    /* access modifiers changed from: protected */
    public AjaxStatus file(File file2) {
        this.file = file2;
        return this;
    }

    public AjaxStatus invalidate() {
        this.invalid = true;
        return this;
    }

    /* access modifiers changed from: protected */
    public boolean getDone() {
        return this.done;
    }

    /* access modifiers changed from: protected */
    public boolean getReauth() {
        return this.reauth;
    }

    /* access modifiers changed from: protected */
    public boolean getInvalid() {
        return this.invalid;
    }

    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    public String getRedirect() {
        return this.redirect;
    }

    /* access modifiers changed from: protected */
    public byte[] getData() {
        return this.data;
    }

    /* access modifiers changed from: protected */
    public File getFile() {
        return this.file;
    }

    public Date getTime() {
        return this.time;
    }

    public boolean getRefresh() {
        return this.refresh;
    }

    public DefaultHttpClient getClient() {
        return this.client;
    }

    public long getDuration() {
        return this.duration;
    }

    public int getSource() {
        return this.source;
    }

    public String getError() {
        return this.error;
    }

    public boolean expired(long j) {
        return System.currentTimeMillis() - this.time.getTime() > j && getSource() != 1;
    }

    public List<Cookie> getCookies() {
        HttpContext httpContext = this.context;
        if (httpContext == null) {
            return Collections.emptyList();
        }
        CookieStore cookieStore = (CookieStore) httpContext.getAttribute("http.cookie-store");
        if (cookieStore == null) {
            return Collections.emptyList();
        }
        return cookieStore.getCookies();
    }

    public List<Header> getHeaders() {
        Header[] headerArr = this.headers;
        if (headerArr == null) {
            return Collections.emptyList();
        }
        return Arrays.asList(headerArr);
    }

    public String getHeader(String str) {
        if (this.headers == null) {
            return null;
        }
        int i = 0;
        while (true) {
            Header[] headerArr = this.headers;
            if (i >= headerArr.length) {
                return null;
            }
            if (str.equalsIgnoreCase(headerArr[i].getName())) {
                return this.headers[i].getValue();
            }
            i++;
        }
    }
}
