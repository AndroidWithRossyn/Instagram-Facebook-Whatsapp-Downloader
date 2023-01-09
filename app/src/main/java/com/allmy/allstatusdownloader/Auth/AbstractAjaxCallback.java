package com.allmy.allstatusdownloader.Auth;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build.VERSION;
import android.util.Xml;
import android.view.View;

import com.allmy.allstatusdownloader.Others.AQUtility;
import com.allmy.allstatusdownloader.Others.Common;
import com.allmy.allstatusdownloader.Others.Progress;
import com.allmy.allstatusdownloader.Others.XmlDom;
import com.loopj.android.http.AsyncHttpClient;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import cz.msebera.android.httpclient.cookie.SM;
import cz.msebera.android.httpclient.params.CoreProtocolPNames;
import cz.msebera.android.httpclient.protocol.HTTP;

public abstract class AbstractAjaxCallback<T, K> implements Runnable {
    private static String AGENT = null;
    private static final Class<?>[] DEFAULT_SIG = {String.class, Object.class, AjaxStatus.class};
    private static boolean GZIP = true;
    private static int NETWORK_POOL = 4;
    private static int NET_TIMEOUT = 30000;
    private static boolean REUSE_CLIENT = true;
    private static final String boundary = "*****";
    private static DefaultHttpClient client = null;
    private static ExecutorService fetchExe = null;
    private static int lastStatus = 200;
    private static final String lineEnd = "\r\n";
    private static SocketFactory ssf = null;
    private static Transformer st = null;
    private static final String twoHyphens = "--";
    private boolean abort;
    private WeakReference<Activity> act;
    private AccountHandle ah;
    private boolean blocked;
    private File cacheDir;
    private String callback;
    private boolean completed;
    private Map<String, String> cookies;
    private String encoding = "UTF-8";
    private long expire;
    protected boolean fileCache;
    private Object handler;
    private Map<String, String> headers;
    protected boolean memCache;
    private int method = 4;
    private String networkUrl;
    private Map<String, Object> params;
    private int policy = 0;
    private WeakReference<Object> progress;
    private HttpHost proxy;
    private boolean reauth;
    private boolean refresh;
    private HttpUriRequest request;
    protected T result;
    protected AjaxStatus status;
    private File targetFile;
    private int timeout = 0;
    private Transformer transformer;
    private Class<T> type;
    private boolean uiCallback = true;
    /* access modifiers changed from: private */
    public String url;
    private Reference<Object> whandler;

    private K self() {
        return (K) this;
    }

    public void callback(String str, T t, AjaxStatus ajaxStatus) {
    }

    /* access modifiers changed from: protected */
    public T datastoreGet(String str) {
        return null;
    }

    /* access modifiers changed from: protected */
    public T memGet(String str) {
        return null;
    }

    /* access modifiers changed from: protected */
    public void memPut(String str, T t) {
    }

    /* access modifiers changed from: protected */
    public void skip(String str, T t, AjaxStatus ajaxStatus) {
    }

    private void clear() {
        this.whandler = null;
        this.handler = null;
        this.progress = null;
        this.request = null;
        this.transformer = null;
        this.ah = null;
        this.act = null;
    }

    public static void setTimeout(int i) {
        NET_TIMEOUT = i;
    }

    public static void setAgent(String str) {
        AGENT = str;
    }

    public static void setGZip(boolean z) {
        GZIP = z;
    }

    public static void setTransformer(Transformer transformer2) {
        st = transformer2;
    }

    public Class<T> getType() {
        return this.type;
    }

    public K weakHandler(Object obj, String str) {
        this.whandler = new WeakReference(obj);
        this.callback = str;
        this.handler = null;
        return self();
    }

    public K handler(Object obj, String str) {
        this.handler = obj;
        this.callback = str;
        this.whandler = null;
        return self();
    }

    public K url(String str) {
        this.url = str;
        return self();
    }

    public K networkUrl(String str) {
        this.networkUrl = str;
        return self();
    }

    public K type(Class<T> cls) {
        this.type = cls;
        return self();
    }

    public K method(int i) {
        this.method = i;
        return self();
    }

    public K timeout(int i) {
        this.timeout = i;
        return self();
    }

    public K transformer(Transformer transformer2) {
        this.transformer = transformer2;
        return self();
    }

    public K fileCache(boolean z) {
        this.fileCache = z;
        return self();
    }

    public K memCache(boolean z) {
        this.memCache = z;
        return self();
    }

    public K policy(int i) {
        this.policy = i;
        return self();
    }

    public K refresh(boolean z) {
        this.refresh = z;
        return self();
    }

    public K uiCallback(boolean z) {
        this.uiCallback = z;
        return self();
    }

    public K expire(long j) {
        this.expire = j;
        return self();
    }

    public K header(String str, String str2) {
        if (this.headers == null) {
            this.headers = new HashMap();
        }
        this.headers.put(str, str2);
        return self();
    }

    public K cookie(String str, String str2) {
        if (this.cookies == null) {
            this.cookies = new HashMap();
        }
        this.cookies.put(str, str2);
        return self();
    }

    public K encoding(String str) {
        this.encoding = str;
        return self();
    }

    public K proxy(String str, int i) {
        this.proxy = new HttpHost(str, i);
        return self();
    }

    public K targetFile(File file) {
        this.targetFile = file;
        return self();
    }

    public K param(String str, Object obj) {
        if (this.params == null) {
            this.params = new HashMap();
        }
        this.params.put(str, obj);
        return self();
    }

    public K params(Map<String, ?> map) {
        this.params = (Map<String, Object>) map;
        return self();
    }

    public K progress(View view) {
        return progress((Object) view);
    }

    public K progress(Dialog dialog) {
        return progress((Object) dialog);
    }

    public K progress(Object obj) {
        if (obj != null) {
            this.progress = new WeakReference<>(obj);
        }
        return self();
    }

    /* access modifiers changed from: 0000 */
    public void callback() {
        showProgress(false);
        this.completed = true;
        if (!isActive()) {
            skip(this.url, this.result, this.status);
        } else if (this.callback != null) {
            Class[] clsArr = {String.class, this.type, AjaxStatus.class};
            AQUtility.invokeHandler(getHandler(), this.callback, true, true, clsArr, DEFAULT_SIG, this.url, this.result, this.status);
        } else {
            try {
                callback(this.url, this.result, this.status);
            } catch (Exception e) {
                AQUtility.report(e);
            }
        }
        filePut();
        if (!this.blocked) {
            this.status.close();
        }
        wake();
        AQUtility.debugNotify();
    }


    private void wake() {

        throw new UnsupportedOperationException("Method not decompiled: com.androidquery.callback.AbstractAjaxCallback.wake():void");
    }

    public void block() {
        if (AQUtility.isUIThread()) {
            throw new IllegalStateException("Cannot block UI thread.");
        } else if (!this.completed) {
            try {
                synchronized (this) {
                    this.blocked = true;
                    wait((long) (NET_TIMEOUT + 5000));
                }
            } catch (Exception unused) {
            }
        }
    }

    /* access modifiers changed from: protected */
    public T fileGet(String str, File file, AjaxStatus ajaxStatus) {
        byte[] bArr;
        try {
            if (isStreamingContent()) {
                ajaxStatus.file(file);
                bArr = null;
            } else {
                bArr = AQUtility.toBytes(new FileInputStream(file));
            }
            return transform(str, bArr, ajaxStatus);
        } catch (Exception e) {
            AQUtility.debug((Throwable) e);
            return null;
        }
    }

    /* access modifiers changed from: protected */
    public void showProgress(final boolean z) {
        WeakReference<Object> weakReference = this.progress;
        final Object obj = weakReference == null ? null : weakReference.get();
        if (obj == null) {
            return;
        }
        if (AQUtility.isUIThread()) {
            Common.showProgress(obj, this.url, z);
        } else {
            AQUtility.post(new Runnable() {
                public void run() {
                    Common.showProgress(obj, AbstractAjaxCallback.this.url, z);
                }
            });
        }
    }

    /* access modifiers changed from: protected */
    public T transform(String str, byte[] bArr, AjaxStatus ajaxStatus) {
        T t;
        T t2 = null;
        String str2;
        T t3 = null;
        if (this.type == null) {
            return null;
        }
        T file = (T) ajaxStatus.getFile();
        if (bArr != null) {
            if (this.type.equals(Bitmap.class)) {
                return (T) BitmapFactory.decodeByteArray(bArr, 0, bArr.length);
            }
            if (this.type.equals(JSONObject.class)) {
                try {
                    str2 = new String(bArr, this.encoding);
                    try {
                        t2 = (T) new JSONTokener(str2).nextValue();
                    } catch (Exception e) {
                        e = e;
                    }
                } catch (Exception e) {
                    str2 = null;
                    AQUtility.debug((Throwable) e);
                    AQUtility.debug((Object) str2);
                    t2 = null;
                    return t2;
                }
                return t2;
            } else if (this.type.equals(JSONArray.class)) {
                try {
                    t = (T) new JSONTokener(new String(bArr, this.encoding)).nextValue();
                } catch (Exception e3) {
                    AQUtility.debug((Throwable) e3);
                    t = null;
                }
                return t;
            } else if (this.type.equals(String.class)) {
                if (ajaxStatus.getSource() == 1) {
                    AQUtility.debug((Object) "network");
                    t3 = (T) correctEncoding(bArr, this.encoding, ajaxStatus);
                } else {
                    AQUtility.debug((Object) "file");
                    try {
                        t3 = (T) new String(bArr, this.encoding);
                    } catch (Exception e4) {
                        AQUtility.debug((Throwable) e4);
                    }
                }
                return t3;
            } else if (this.type.equals(byte[].class)) {
                return (T) bArr;
            } else {
                Transformer transformer2 = this.transformer;
                if (transformer2 != null) {
                    return transformer2.transform(str, this.type, this.encoding, bArr, ajaxStatus);
                }
                Transformer transformer3 = st;
                if (transformer3 != null) {
                    return transformer3.transform(str, this.type, this.encoding, bArr, ajaxStatus);
                }
            }
        } else if (file != null) {
            if (this.type.equals(File.class)) {
                return file;
            }
            if (this.type.equals(XmlDom.class)) {
                try {
                    FileInputStream fileInputStream = new FileInputStream((File) file);
                    T xmlDom = (T) new XmlDom((InputStream) fileInputStream);
                    ajaxStatus.closeLater(fileInputStream);
                    return xmlDom;
                } catch (Exception e5) {
                    AQUtility.report(e5);
                    return null;
                }
            } else if (this.type.equals(XmlPullParser.class)) {
                T newPullParser = (T) Xml.newPullParser();
                try {
                    FileInputStream fileInputStream2 = new FileInputStream((File) file);
//                    newPullParser.setInput(fileInputStream2, this.encoding);
                    ajaxStatus.closeLater(fileInputStream2);
                    return newPullParser;
                } catch (Exception e6) {
                    AQUtility.report(e6);
                    return null;
                }
            } else if (this.type.equals(InputStream.class)) {
                try {
                    T fileInputStream3 = (T) new FileInputStream(String.valueOf(file));
                    ajaxStatus.closeLater((Closeable) fileInputStream3);
                    return fileInputStream3;
                } catch (Exception e7) {
                    AQUtility.report(e7);
                }
            }
        }
        return null;
    }

    private String getCharset(String str) {
        Matcher matcher = Pattern.compile("<meta [^>]*http-equiv[^>]*\"Content-Type\"[^>]*>", 2).matcher(str);
        if (!matcher.find()) {
            return null;
        }
        return parseCharset(matcher.group());
    }

    private String parseCharset(String str) {
        if (str == null) {
            return null;
        }
        int indexOf = str.indexOf("charset");
        if (indexOf == -1) {
            return null;
        }
        return str.substring(indexOf + 7).replaceAll("[^\\w-]", "");
    }

    private String correctEncoding(byte[] bArr, String str, AjaxStatus ajaxStatus) {
        String str2;
        String str3 = "utf-8";
        try {
            if (!str3.equalsIgnoreCase(str)) {
                return new String(bArr, str);
            }
            String parseCharset = parseCharset(ajaxStatus.getHeader("Content-Type"));
            AQUtility.debug("parsing header", parseCharset);
            if (parseCharset != null) {
                return new String(bArr, parseCharset);
            }
            str2 = new String(bArr, str3);
            try {
                String charset = getCharset(str2);
                AQUtility.debug("parsing needed", charset);
                if (charset != null && !str3.equalsIgnoreCase(charset)) {
                    AQUtility.debug("correction needed", charset);
                    String str4 = new String(bArr, charset);
                    try {
                        ajaxStatus.data(str4.getBytes(str3));
                        str2 = str4;
                    } catch (Exception e) {
                        e = e;
                        str2 = str4;
                        AQUtility.report(e);
                        return str2;
                    }
                }
            } catch (Exception e) {
                AQUtility.report(e);
                return str2;
            }
            return str2;
        } catch (Exception e) {
            str2 = null;
            AQUtility.report(e);
            return str2;
        }
    }

    /* access modifiers changed from: protected */
    public void filePut(String str, T t, File file, byte[] bArr) {
        if (file != null && bArr != null) {
            AQUtility.storeAsync(file, bArr, 0);
        }
    }

    /* access modifiers changed from: protected */
    public File accessFile(File file, String str) {
        if (this.expire < 0) {
            return null;
        }
        File existedCacheByUrl = AQUtility.getExistedCacheByUrl(file, str);
        if (existedCacheByUrl == null || this.expire == 0 || System.currentTimeMillis() - existedCacheByUrl.lastModified() <= this.expire) {
            return existedCacheByUrl;
        }
        return null;
    }

    public void async(Activity activity) {
        String str = "Warning";
        if (activity.isFinishing()) {
            AQUtility.warn(str, "Possible memory leak. Calling ajax with a terminated activity.");
        }
        if (this.type == null) {
            AQUtility.warn(str, "type() is not called with response type.");
            return;
        }
        this.act = new WeakReference<>(activity);
        async((Context) activity);
    }

    public void async(Context context) {
        AjaxStatus ajaxStatus = this.status;
        if (ajaxStatus == null) {
            this.status = new AjaxStatus();
            this.status.redirect(this.url).refresh(this.refresh);
        } else if (ajaxStatus.getDone()) {
            this.status.reset();
            this.result = null;
        }
        showProgress(true);
        AccountHandle accountHandle = this.ah;
        if (accountHandle == null || accountHandle.authenticated()) {
            work(context);
            return;
        }
        AQUtility.debug("auth needed", this.url);
        this.ah.auth(this);
    }

    private boolean isActive() {
        WeakReference<Activity> weakReference = this.act;
        if (weakReference == null) {
            return true;
        }
        Activity activity = (Activity) weakReference.get();
        if (activity == null || activity.isFinishing()) {
            return false;
        }
        return true;
    }

    public void failure(int i, String str) {
        AjaxStatus ajaxStatus = this.status;
        if (ajaxStatus != null) {
            ajaxStatus.code(i).message(str);
            callback();
        }
    }

    private void work(Context context) {
        T memGet = memGet(this.url);
        if (memGet != null) {
            this.result = memGet;
            this.status.source(4).done();
            callback();
            return;
        }
        this.cacheDir = AQUtility.getCacheDir(context, this.policy);
        execute(this);
    }

    /* access modifiers changed from: protected */
    public boolean cacheAvailable(Context context) {
        return this.fileCache && AQUtility.getExistedCacheByUrl(AQUtility.getCacheDir(context, this.policy), this.url) != null;
    }

    public void run() {
        if (!this.status.getDone()) {
            try {
                backgroundWork();
            } catch (Throwable th) {
                AQUtility.debug(th);
                this.status.code(AjaxStatus.NETWORK_ERROR).done();
            }
            if (this.status.getReauth()) {
                return;
            }
            if (this.uiCallback) {
                AQUtility.post(this);
            } else {
                afterWork();
            }
        } else {
            afterWork();
        }
    }

    private void backgroundWork() {
        if (!this.refresh && this.fileCache) {
            fileWork();
        }
        if (this.result == null) {
            datastoreWork();
        }
        if (this.result == null) {
            networkWork();
        }
    }

    private String getCacheUrl() {
        AccountHandle accountHandle = this.ah;
        if (accountHandle != null) {
            return accountHandle.getCacheUrl(this.url);
        }
        return this.url;
    }

    private String getNetworkUrl(String str) {
        String str2 = this.networkUrl;
        if (str2 != null) {
            str = str2;
        }
        AccountHandle accountHandle = this.ah;
        return accountHandle != null ? accountHandle.getNetworkUrl(str) : str;
    }

    private void fileWork() {
        File accessFile = accessFile(this.cacheDir, getCacheUrl());
        if (accessFile != null) {
            this.status.source(3);
            this.result = fileGet(this.url, accessFile, this.status);
            if (this.result != null) {
                this.status.time(new Date(accessFile.lastModified())).done();
            }
        }
    }

    private void datastoreWork() {
        this.result = datastoreGet(this.url);
        if (this.result != null) {
            this.status.source(2).done();
        }
    }

    private void networkWork() {
        if (this.url == null) {
            this.status.code(AjaxStatus.NETWORK_ERROR).done();
            return;
        }
        byte[] bArr = null;
        try {
            network();
            if (this.ah != null && this.ah.expired(this, this.status) && !this.reauth) {
                AQUtility.debug("reauth needed", this.status.getMessage());
                this.reauth = true;
                if (this.ah.reauth(this)) {
                    network();
                } else {
                    this.status.reauth(true);
                    return;
                }
            }
            bArr = this.status.getData();
        } catch (Exception e) {
            AQUtility.debug((Throwable) e);
            this.status.code(AjaxStatus.NETWORK_ERROR).message("network error");
        }
        try {
            this.result = transform(this.url, bArr, this.status);
        } catch (Exception e2) {
            AQUtility.debug((Throwable) e2);
        }
        if (this.result == null && bArr != null) {
            this.status.code(AjaxStatus.TRANSFORM_ERROR).message("transform error");
        }
        lastStatus = this.status.getCode();
        this.status.done();
    }

    /* access modifiers changed from: protected */
    public File getCacheFile() {
        return AQUtility.getCacheFile(this.cacheDir, getCacheUrl());
    }

    /* access modifiers changed from: protected */
    public boolean isStreamingContent() {
        return File.class.equals(this.type) || XmlPullParser.class.equals(this.type) || InputStream.class.equals(this.type) || XmlDom.class.equals(this.type);
    }

    private File getPreFile() {
        File file;
        if (isStreamingContent()) {
            file = this.targetFile;
            if (file == null) {
                if (this.fileCache) {
                    file = getCacheFile();
                } else {
                    File tempDir = AQUtility.getTempDir();
                    if (tempDir == null) {
                        tempDir = this.cacheDir;
                    }
                    file = AQUtility.getCacheFile(tempDir, this.url);
                }
            }
        } else {
            file = null;
        }
        if (file != null && !file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (Exception e) {
                AQUtility.report(e);
                return null;
            }
        }
        return file;
    }

    private void filePut() {
        if (this.result != null && this.fileCache) {
            byte[] data = this.status.getData();
            if (data != null) {
                try {
                    if (this.status.getSource() == 1) {
                        File cacheFile = getCacheFile();
                        if (!this.status.getInvalid()) {
                            filePut(this.url, this.result, cacheFile, data);
                        } else if (cacheFile.exists()) {
                            cacheFile.delete();
                        }
                    }
                } catch (Exception e) {
                    AQUtility.debug((Throwable) e);
                }
            }
            this.status.data(null);
        }
    }

    private static String extractUrl(Uri uri) {
        StringBuilder sb = new StringBuilder();
        sb.append(uri.getScheme());
        sb.append("://");
        sb.append(uri.getAuthority());
        sb.append(uri.getPath());
        String sb2 = sb.toString();
        String fragment = uri.getFragment();
        if (fragment == null) {
            return sb2;
        }
        StringBuilder sb3 = new StringBuilder();
        sb3.append(sb2);
        sb3.append("#");
        sb3.append(fragment);
        return sb3.toString();
    }

    private static Map<String, Object> extractParams(Uri uri) {
        HashMap hashMap = new HashMap();
        for (String split : uri.getQuery().split("&")) {
            String[] split2 = split.split("=");
            if (split2.length >= 2) {
                hashMap.put(split2[0], split2[1]);
            } else if (split2.length == 1) {
                hashMap.put(split2[0], "");
            }
        }
        return hashMap;
    }

    private void network() throws IOException {
        String str = this.url;
        Map<String, Object> map = this.params;
        if (map == null && str.length() > 2000) {
            Uri parse = Uri.parse(str);
            String extractUrl = extractUrl(parse);
            map = extractParams(parse);
            str = extractUrl;
        }
        String networkUrl2 = getNetworkUrl(str);
        int i = this.method;
        if (2 == i) {
            httpDelete(networkUrl2, this.headers, this.status);
        } else if (3 == i) {
            httpPut(networkUrl2, this.headers, map, this.status);
        } else {
            if (1 == i && map == null) {
                map = new HashMap<>();
            }
            if (map == null) {
                httpGet(networkUrl2, this.headers, this.status);
            } else if (isMultiPart(map)) {
                httpMulti(networkUrl2, this.headers, map, this.status);
            } else {
                httpPost(networkUrl2, this.headers, map, this.status);
            }
        }
    }

    private void afterWork() {
        String str = this.url;
        if (str != null && this.memCache) {
            memPut(str, this.result);
        }
        callback();
        clear();
    }

    public static void execute(Runnable runnable) {
        if (fetchExe == null) {
            fetchExe = Executors.newFixedThreadPool(NETWORK_POOL);
        }
        fetchExe.execute(runnable);
    }

    public static void setNetworkLimit(int i) {
        NETWORK_POOL = Math.max(1, Math.min(25, i));
        fetchExe = null;
        AQUtility.debug("setting network limit", Integer.valueOf(NETWORK_POOL));
    }

    public static void cancel() {
        ExecutorService executorService = fetchExe;
        if (executorService != null) {
            executorService.shutdownNow();
            fetchExe = null;
        }
        BitmapAjaxCallback.clearTasks();
    }

    private static String patchUrl(String str) {
        return str.replaceAll(" ", "%20").replaceAll("\\|", "%7C");
    }

    private void httpGet(String str, Map<String, String> map, AjaxStatus ajaxStatus) throws IOException {
        AQUtility.debug("get", str);
        String patchUrl = patchUrl(str);
//        httpDo(new HttpGet(patchUrl), patchUrl, map, ajaxStatus);
    }

    private void httpDelete(String str, Map<String, String> map, AjaxStatus ajaxStatus) throws IOException {
        AQUtility.debug("get", str);
        String patchUrl = patchUrl(str);
//        httpDo(new HttpDelete(patchUrl), patchUrl, map, ajaxStatus);
    }

    private void httpPost(String str, Map<String, String> map, Map<String, Object> map2, AjaxStatus ajaxStatus) throws ClientProtocolException, IOException {
        AQUtility.debug("post", str);
        httpEntity(str, new HttpPost(str), map, map2, ajaxStatus);
    }

    private void httpPut(String str, Map<String, String> map, Map<String, Object> map2, AjaxStatus ajaxStatus) throws ClientProtocolException, IOException {
        AQUtility.debug("put", str);
        httpEntity(str, new HttpPut(str), map, map2, ajaxStatus);
    }

    private void httpEntity(String str, HttpEntityEnclosingRequestBase httpEntityEnclosingRequestBase, Map<String, String> map, Map<String, Object> map2, AjaxStatus ajaxStatus) throws ClientProtocolException, IOException {
        HttpEntity httpEntity;
        httpEntityEnclosingRequestBase.getParams().setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
        Object obj = map2.get("%entity");
        if (obj instanceof HttpEntity) {
            httpEntity = (HttpEntity) obj;
        } else {
            ArrayList arrayList = new ArrayList();
            for (Entry entry : map2.entrySet()) {
                Object value = entry.getValue();
                if (value != null) {
                    arrayList.add(new BasicNameValuePair((String) entry.getKey(), value.toString()));
                }
            }
            httpEntity = new UrlEncodedFormEntity(arrayList, "UTF-8");
        }
        if (map != null) {
            String str2 = "Content-Type";
            if (!map.containsKey(str2)) {
                map.put(str2, "application/x-www-form-urlencoded;charset=UTF-8");
            }
        }
        httpEntityEnclosingRequestBase.setEntity(httpEntity);
//        httpDo(httpEntityEnclosingRequestBase, str, map, ajaxStatus);
    }

    public static void setSSF(SocketFactory socketFactory) {
        ssf = socketFactory;
        client = null;
    }

    public static void setReuseHttpClient(boolean z) {
        REUSE_CLIENT = z;
        client = null;
    }

    private static DefaultHttpClient getClient() {
        if (client == null || !REUSE_CLIENT) {
            AQUtility.debug((Object) "creating http client");
            BasicHttpParams basicHttpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(basicHttpParams, NET_TIMEOUT);
            HttpConnectionParams.setSoTimeout(basicHttpParams, NET_TIMEOUT);
            ConnManagerParams.setMaxConnectionsPerRoute(basicHttpParams, new ConnPerRouteBean(25));
            HttpConnectionParams.setSocketBufferSize(basicHttpParams, 8192);
            SchemeRegistry schemeRegistry = new SchemeRegistry();
            schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            SocketFactory socketFactory = ssf;
            if (socketFactory == null) {
                socketFactory = SSLSocketFactory.getSocketFactory();
            }
            schemeRegistry.register(new Scheme("https", socketFactory, 443));
            client = new DefaultHttpClient(new ThreadSafeClientConnManager(basicHttpParams, schemeRegistry), basicHttpParams);
        }
        return client;
    }

    private String getEncoding(HttpEntity httpEntity) {
        if (httpEntity == null) {
            return null;
        }
        Header contentEncoding = httpEntity.getContentEncoding();
        if (contentEncoding == null) {
            return null;
        }
        return contentEncoding.getValue();
    }

    private void copy(InputStream inputStream, OutputStream outputStream, String str, int i) throws IOException {
        if (AsyncHttpClient.ENCODING_GZIP.equalsIgnoreCase(str)) {
            inputStream = new GZIPInputStream(inputStream);
        }
        WeakReference<Object> weakReference = this.progress;
        Progress progress2 = null;
        Object obj = weakReference != null ? weakReference.get() : null;
        if (obj != null) {
            progress2 = new Progress(obj);
        }
        AQUtility.copy(inputStream, outputStream, i, progress2);
    }

    public K auth(Activity activity, String str, String str2) {
        if (VERSION.SDK_INT >= 5 && str.startsWith("g.")) {
            this.ah = new GoogleHandle(activity, str, str2);
        }
        return self();
    }

    public K auth(AccountHandle accountHandle) {
        this.ah = accountHandle;
        return self();
    }

    public String getUrl() {
        return this.url;
    }

    public Object getHandler() {
        Object obj = this.handler;
        if (obj != null) {
            return obj;
        }
        Reference<Object> reference = this.whandler;
        if (reference == null) {
            return null;
        }
        return reference.get();
    }

    public String getCallback() {
        return this.callback;
    }

    protected static int getLastStatus() {
        return lastStatus;
    }

    public T getResult() {
        return this.result;
    }

    public AjaxStatus getStatus() {
        return this.status;
    }

    public String getEncoding() {
        return this.encoding;
    }

    public void abort() {
        this.abort = true;
        HttpUriRequest httpUriRequest = this.request;
        if (httpUriRequest != null && !httpUriRequest.isAborted()) {
            this.request.abort();
        }
    }

    private static boolean isMultiPart(Map<String, Object> map) {
        for (Entry entry : map.entrySet()) {
            Object value = entry.getValue();
            AQUtility.debug(entry.getKey(), value);
            if ((value instanceof File) || (value instanceof byte[])) {
                return true;
            }
            if (value instanceof InputStream) {
                return true;
            }
        }
        return false;
    }

    private void httpMulti(String str, Map<String, String> map, Map<String, Object> map2, AjaxStatus ajaxStatus) throws IOException {
        String str2;
        byte[] bArr;
        AQUtility.debug("multipart", str);
        HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(str).openConnection();
        httpURLConnection.setInstanceFollowRedirects(false);
        httpURLConnection.setConnectTimeout(NET_TIMEOUT * 4);
        httpURLConnection.setDoInput(true);
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setUseCaches(false);
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty("Connection", HTTP.CONN_KEEP_ALIVE);
        httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data;charset=utf-8;boundary=*****");
        if (map != null) {
            for (String str3 : map.keySet()) {
                httpURLConnection.setRequestProperty(str3, (String) map.get(str3));
            }
        }
        String makeCookie = makeCookie();
        if (makeCookie != null) {
            httpURLConnection.setRequestProperty(SM.COOKIE, makeCookie);
        }
        AccountHandle accountHandle = this.ah;
        if (accountHandle != null) {
            accountHandle.applyToken(this, httpURLConnection);
        }
        DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
        for (Entry entry : map2.entrySet()) {
            writeObject(dataOutputStream, (String) entry.getKey(), entry.getValue());
        }
        dataOutputStream.writeBytes("--*****--\r\n");
        dataOutputStream.flush();
        dataOutputStream.close();
        httpURLConnection.connect();
        int responseCode = httpURLConnection.getResponseCode();
        String responseMessage = httpURLConnection.getResponseMessage();
        String contentEncoding = httpURLConnection.getContentEncoding();
        if (responseCode < 200 || responseCode >= 300) {
            str2 = new String(toData(contentEncoding, httpURLConnection.getErrorStream()), "UTF-8");
            AQUtility.debug("error", str2);
            bArr = null;
        } else {
            bArr = toData(contentEncoding, httpURLConnection.getInputStream());
            str2 = null;
        }
        AQUtility.debug("response", Integer.valueOf(responseCode));
        if (bArr != null) {
            AQUtility.debug(Integer.valueOf(bArr.length), str);
        }
        ajaxStatus.code(responseCode).message(responseMessage).redirect(str).time(new Date()).data(bArr).error(str2).client(null);
    }

    private byte[] toData(String str, InputStream inputStream) throws IOException {
        return AQUtility.toBytes(AsyncHttpClient.ENCODING_GZIP.equalsIgnoreCase(str) ? new GZIPInputStream(inputStream) : inputStream);
    }

    private static void writeObject(DataOutputStream dataOutputStream, String str, Object obj) throws IOException {
        if (obj != null) {
            if (obj instanceof File) {
                File file = (File) obj;
                writeData(dataOutputStream, str, file.getName(), new FileInputStream(file));
            } else if (obj instanceof byte[]) {
                writeData(dataOutputStream, str, str, new ByteArrayInputStream((byte[]) obj));
            } else if (obj instanceof InputStream) {
                writeData(dataOutputStream, str, str, (InputStream) obj);
            } else {
                writeField(dataOutputStream, str, obj.toString());
            }
        }
    }

    private static void writeData(DataOutputStream dataOutputStream, String str, String str2, InputStream inputStream) throws IOException {
        dataOutputStream.writeBytes("--*****\r\n");
        StringBuilder sb = new StringBuilder();
        sb.append("Content-Disposition: form-data; name=\"");
        sb.append(str);
        sb.append("\";");
        sb.append(" filename=\"");
        sb.append(str2);
        sb.append("\"");
        String str3 = "\r\n";
        sb.append(str3);
        dataOutputStream.writeBytes(sb.toString());
        dataOutputStream.writeBytes(str3);
        AQUtility.copy(inputStream, dataOutputStream);
        dataOutputStream.writeBytes(str3);
    }

    private static void writeField(DataOutputStream dataOutputStream, String str, String str2) throws IOException {
        dataOutputStream.writeBytes("--*****\r\n");
        StringBuilder sb = new StringBuilder();
        sb.append("Content-Disposition: form-data; name=\"");
        sb.append(str);
        sb.append("\"");
        dataOutputStream.writeBytes(sb.toString());
        String str3 = "\r\n";
        dataOutputStream.writeBytes(str3);
        dataOutputStream.writeBytes(str3);
        dataOutputStream.write(str2.getBytes("UTF-8"));
        dataOutputStream.writeBytes(str3);
    }

    private String makeCookie() {
        Map<String, String> map = this.cookies;
        if (map == null || map.size() == 0) {
            return null;
        }
        Iterator it = this.cookies.keySet().iterator();
        StringBuilder sb = new StringBuilder();
        while (it.hasNext()) {
            String str = (String) it.next();
            String str2 = (String) this.cookies.get(str);
            sb.append(str);
            sb.append("=");
            sb.append(str2);
            if (it.hasNext()) {
                sb.append("; ");
            }
        }
        return sb.toString();
    }
}
