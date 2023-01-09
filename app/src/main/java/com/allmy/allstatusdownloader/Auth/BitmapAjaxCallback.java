package com.allmy.allstatusdownloader.Auth;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import com.allmy.allstatusdownloader.Others.AQUtility;
import com.allmy.allstatusdownloader.Others.BitmapCache;
import com.allmy.allstatusdownloader.Others.Common;
import com.allmy.allstatusdownloader.Others.Constant;
import com.allmy.allstatusdownloader.Others.RatioDrawable;

import org.apache.http.HttpHost;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public class BitmapAjaxCallback extends AbstractAjaxCallback<Bitmap, BitmapAjaxCallback> {
    private static int BIG_MAX = 20;
    private static int BIG_PIXELS = 160000;
    private static int BIG_TPIXELS = 1000000;
    private static boolean DELAY_WRITE = false;
    private static final int FADE_DUR = 300;
    private static int SMALL_MAX = 20;
    private static int SMALL_PIXELS = 2500;
    private static Map<String, Bitmap> bigCache;
    private static Bitmap dummy = Bitmap.createBitmap(1, 1, Config.ALPHA_8);
    private static Bitmap empty = Bitmap.createBitmap(1, 1, Config.ALPHA_8);
    private static Map<String, Bitmap> invalidCache;
    private static HashMap<String, WeakHashMap<ImageView, BitmapAjaxCallback>> queueMap = new HashMap<>();
    private static Map<String, Bitmap> smallCache;
    private float anchor = Float.MAX_VALUE;
    private int animation;
    private Bitmap bm;
    private int fallback;
    private File imageFile;
    private boolean invalid;
    private Bitmap preset;
    private float ratio;
    private int round;
    private boolean targetDim = true;
    private int targetWidth;
    private WeakReference<ImageView> v;

    private static boolean fadeIn(int i, int i2) {
        if (i != -3) {
            if (i != -2) {
                if (i == -1) {
                    return true;
                }
                return false;
            }
        } else if (i2 == 3) {
            return true;
        }
        if (i2 == 1) {
            return true;
        }
        return false;
    }

    public BitmapAjaxCallback() {
        ((BitmapAjaxCallback) ((BitmapAjaxCallback) ((BitmapAjaxCallback) type(Bitmap.class)).memCache(true)).fileCache(true)).url("");
    }

    public BitmapAjaxCallback imageView(ImageView imageView) {
        this.v = new WeakReference<>(imageView);
        return this;
    }

    public BitmapAjaxCallback targetWidth(int i) {
        this.targetWidth = i;
        return this;
    }

    public BitmapAjaxCallback file(File file) {
        this.imageFile = file;
        return this;
    }

    public BitmapAjaxCallback preset(Bitmap bitmap) {
        this.preset = bitmap;
        return this;
    }

    public BitmapAjaxCallback bitmap(Bitmap bitmap) {
        this.bm = bitmap;
        return this;
    }

    public BitmapAjaxCallback fallback(int i) {
        this.fallback = i;
        return this;
    }

    public BitmapAjaxCallback animation(int i) {
        this.animation = i;
        return this;
    }

    public BitmapAjaxCallback ratio(float f) {
        this.ratio = f;
        return this;
    }

    public BitmapAjaxCallback anchor(float f) {
        this.anchor = f;
        return this;
    }

    public BitmapAjaxCallback round(int i) {
        this.round = i;
        return this;
    }

    private static Bitmap decode(String str, byte[] bArr, Options options) {
        Bitmap bitmap = str != null ? decodeFile(str, options) : bArr != null ? BitmapFactory.decodeByteArray(bArr, 0, bArr.length, options) : null;
        if (bitmap == null && options != null && !options.inJustDecodeBounds) {
            AQUtility.debug("decode image failed", str);
        }
        return bitmap;
    }

    private static Bitmap decodeFile(String str, Options options) {
        FileInputStream fileInputStream;
        if (options == null) {
            options = new Options();
        }
        options.inInputShareable = true;
        options.inPurgeable = true;
        Bitmap bitmap = null;
        try {
            fileInputStream = new FileInputStream(str);
            try {
                bitmap = BitmapFactory.decodeFileDescriptor(fileInputStream.getFD(), null, options);
            } catch (IOException e) {
            }
        } catch (IOException e) {
            fileInputStream = null;
            try {
                AQUtility.report(e);
                AQUtility.close(fileInputStream);
                return bitmap;
            } catch (Throwable th) {
                th = th;
                AQUtility.close(fileInputStream);
                try {
                    throw th;
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        } catch (Throwable th) {
            fileInputStream = null;
            AQUtility.close(fileInputStream);
            throw th;
        }
        AQUtility.close(fileInputStream);
        return bitmap;
    }

    public static Bitmap getResizedImage(String str, byte[] bArr, int i, boolean z, int i2) {
        Options options;
        Bitmap bitmap = null;
        if (i > 0) {
            Options options2 = new Options();
            options2.inJustDecodeBounds = true;
            decode(str, bArr, options2);
            int i3 = options2.outWidth;
            if (!z) {
                i3 = Math.max(i3, options2.outHeight);
            }
            int sampleSize = sampleSize(i3, i);
            options = new Options();
            options.inSampleSize = sampleSize;
        } else {
            options = null;
        }
        try {
            bitmap = decode(str, bArr, options);
        } catch (OutOfMemoryError e) {
            clearCache();
            AQUtility.report(e);
        }
        return i2 > 0 ? getRoundedCornerBitmap(bitmap, i2) : bitmap;
    }

    private static int sampleSize(int i, int i2) {
        int i3 = 1;
        for (int i4 = 0; i4 < 10 && i >= i2 * 2; i4++) {
            i /= 2;
            i3 *= 2;
        }
        return i3;
    }

    private Bitmap bmGet(String str, byte[] bArr) {
        return getResizedImage(str, bArr, this.targetWidth, this.targetDim, this.round);
    }

    /* access modifiers changed from: protected */
    public File accessFile(File file, String str) {
        File file2 = this.imageFile;
        if (file2 == null || !file2.exists()) {
            return super.accessFile(file, str);
        }
        return this.imageFile;
    }

    /* access modifiers changed from: protected */
    public Bitmap fileGet(String str, File file, AjaxStatus ajaxStatus) {
        return bmGet(file.getAbsolutePath(), null);
    }

    public Bitmap transform(String str, byte[] bArr, AjaxStatus ajaxStatus) {
        File file = ajaxStatus.getFile();
        Bitmap bmGet = bmGet(file != null ? file.getAbsolutePath() : null, bArr);
        if (bmGet == null) {
            int i = this.fallback;
            if (i > 0) {
                bmGet = getFallback();
            } else if (i == -2 || i == -1) {
                bmGet = dummy;
            } else if (i == -3) {
                bmGet = this.preset;
            }
            if (ajaxStatus.getCode() != 200) {
                this.invalid = true;
            }
        }
        return bmGet;
    }

    private Bitmap getFallback() {
        View view = (View) this.v.get();
        if (view == null) {
            return null;
        }
        String num = Integer.toString(this.fallback);
        Bitmap memGet = memGet(num);
        if (memGet != null) {
            return memGet;
        }
        Bitmap decodeResource = BitmapFactory.decodeResource(view.getResources(), this.fallback);
        if (decodeResource == null) {
            return decodeResource;
        }
        memPut(num, decodeResource);
        return decodeResource;
    }

    public static Bitmap getMemoryCached(Context context, int i) {
        String num = Integer.toString(i);
        Bitmap memGet = memGet(num, 0, 0);
        if (memGet == null) {
            memGet = BitmapFactory.decodeResource(context.getResources(), i);
            if (memGet != null) {
                memPut(num, 0, 0, memGet, false);
            }
        }
        return memGet;
    }

    public static Bitmap getEmptyBitmap() {
        return empty;
    }

    public final void callback(String str, Bitmap bitmap, AjaxStatus ajaxStatus) {
        ImageView imageView = (ImageView) this.v.get();
        WeakHashMap weakHashMap = (WeakHashMap) queueMap.remove(str);
        if (weakHashMap == null || !weakHashMap.containsKey(imageView)) {
            checkCb(this, str, imageView, bitmap, ajaxStatus);
        }
        if (weakHashMap != null) {
            for (Object imageView2 : weakHashMap.keySet()) {
                BitmapAjaxCallback bitmapAjaxCallback = (BitmapAjaxCallback) weakHashMap.get(imageView2);
                bitmapAjaxCallback.status = ajaxStatus;
                checkCb(bitmapAjaxCallback, str, (ImageView) imageView2, bitmap, ajaxStatus);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void skip(String str, Bitmap bitmap, AjaxStatus ajaxStatus) {
        queueMap.remove(str);
    }

    private void checkCb(BitmapAjaxCallback bitmapAjaxCallback, String str, ImageView imageView, Bitmap bitmap, AjaxStatus ajaxStatus) {
        if (imageView != null && bitmapAjaxCallback != null) {
            if (str.equals(imageView.getTag(Constant.TAG_URL))) {
                if (imageView instanceof ImageView) {
                    bitmapAjaxCallback.callback(str, imageView, bitmap, ajaxStatus);
                } else {
                    setBitmap(str, imageView, bitmap, false);
                }
            }
            showProgress(false);
        }
    }

    /* access modifiers changed from: protected */
    public void callback(String str, ImageView imageView, Bitmap bitmap, AjaxStatus ajaxStatus) {
        setBitmap(str, imageView, bitmap, false);
    }

    public static void setIconCacheLimit(int i) {
        SMALL_MAX = i;
        clearCache();
    }

    public static void setCacheLimit(int i) {
        BIG_MAX = i;
        clearCache();
    }

    public static void setDelayWrite(boolean z) {
        DELAY_WRITE = z;
    }

    public static void setPixelLimit(int i) {
        BIG_PIXELS = i;
        clearCache();
    }

    public static void setSmallPixel(int i) {
        SMALL_PIXELS = i;
        clearCache();
    }

    public static void setMaxPixelLimit(int i) {
        BIG_TPIXELS = i;
        clearCache();
    }

    public static void clearCache() {
        bigCache = null;
        smallCache = null;
        invalidCache = null;
    }

    protected static void clearTasks() {
        queueMap.clear();
    }

    private static Map<String, Bitmap> getBCache() {
        if (bigCache == null) {
            bigCache = Collections.synchronizedMap(new BitmapCache(BIG_MAX, BIG_PIXELS, BIG_TPIXELS));
        }
        return bigCache;
    }

    private static Map<String, Bitmap> getSCache() {
        if (smallCache == null) {
            smallCache = Collections.synchronizedMap(new BitmapCache(SMALL_MAX, SMALL_PIXELS, 250000));
        }
        return smallCache;
    }

    private static Map<String, Bitmap> getICache() {
        if (invalidCache == null) {
            invalidCache = Collections.synchronizedMap(new BitmapCache(100, BIG_PIXELS, 250000));
        }
        return invalidCache;
    }

    /* access modifiers changed from: protected */
    public Bitmap memGet(String str) {
        Bitmap bitmap = this.bm;
        if (bitmap != null) {
            return bitmap;
        }
        if (!this.memCache) {
            return null;
        }
        return memGet(str, this.targetWidth, this.round);
    }

    public static boolean isMemoryCached(String str) {
        return getBCache().containsKey(str) || getSCache().containsKey(str) || getICache().containsKey(str);
    }

    public static Bitmap getMemoryCached(String str, int i) {
        return memGet(str, i, 0);
    }

    private static Bitmap memGet(String str, int i, int i2) {
        String key = getKey(str, i, i2);
        Bitmap bitmap = (Bitmap) getBCache().get(key);
        if (bitmap == null) {
            bitmap = (Bitmap) getSCache().get(key);
        }
        if (bitmap != null) {
            return bitmap;
        }
        Bitmap bitmap2 = (Bitmap) getICache().get(key);
        if (bitmap2 == null || getLastStatus() != 200) {
            return bitmap2;
        }
        invalidCache = null;
        return null;
    }

    private static String getKey(String str, int i, int i2) {
        String str2 = "#";
        if (i > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append(str);
            sb.append(str2);
            sb.append(i);
            str = sb.toString();
        }
        if (i2 <= 0) {
            return str;
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append(str);
        sb2.append(str2);
        sb2.append(i2);
        return sb2.toString();
    }

    private static void memPut(String str, int i, int i2, Bitmap bitmap, boolean z) {
        Map map;
        if (bitmap != null) {
            int width = bitmap.getWidth() * bitmap.getHeight();
            if (z) {
                map = getICache();
            } else if (width <= SMALL_PIXELS) {
                map = getSCache();
            } else {
                map = getBCache();
            }
            if (i > 0 || i2 > 0) {
                map.put(getKey(str, i, i2), bitmap);
                if (!map.containsKey(str)) {
                    map.put(str, null);
                }
            } else {
                map.put(str, bitmap);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void memPut(String str, Bitmap bitmap) {
        memPut(str, this.targetWidth, this.round, bitmap, this.invalid);
    }

    @SuppressLint("WrongConstant")
    private static Bitmap filter(View view, Bitmap bitmap, int i) {
        if (bitmap != null && bitmap.getWidth() == 1 && bitmap.getHeight() == 1 && bitmap != empty) {
            bitmap = null;
        }
        if (bitmap != null) {
            view.setVisibility(0);
        } else if (i == -2) {
            view.setVisibility(8);
        } else if (i == -1) {
            view.setVisibility(4);
        }
        return bitmap;
    }

    private void presetBitmap(String str, ImageView imageView) {
        if (!str.equals(imageView.getTag(Constant.TAG_URL)) || this.preset != null) {
            imageView.setTag(Constant.TAG_URL, str);
            if (this.preset == null || cacheAvailable(imageView.getContext())) {
                setBitmap(str, imageView, null, true);
            } else {
                setBitmap(str, imageView, this.preset, true);
            }
        }
    }

    private void setBitmap(String str, ImageView imageView, Bitmap bitmap, boolean z) {
        if (bitmap == null) {
            imageView.setImageDrawable(null);
        } else if (z) {
            imageView.setImageDrawable(makeDrawable(imageView, bitmap, this.ratio, this.anchor));
        } else {
            if (this.status != null) {
                setBmAnimate(imageView, bitmap, this.preset, this.fallback, this.animation, this.ratio, this.anchor, this.status.getSource());
            }
        }
    }

    private static Drawable makeDrawable(ImageView imageView, Bitmap bitmap, float f, float f2) {
        if (f <= 0.0f) {
            return new BitmapDrawable(imageView.getResources(), bitmap);
        }
        RatioDrawable ratioDrawable = new RatioDrawable(imageView.getResources(), bitmap, imageView, f, f2);
        return ratioDrawable;
    }

    private static void setBmAnimate(ImageView r0, Bitmap r1, Bitmap r2, int r3, int r4, float r5, float r6, int r7) {
        throw new UnsupportedOperationException("Method not decompiled: com.androidquery.callback.BitmapAjaxCallback.setBmAnimate(android.widget.ImageView, android.graphics.Bitmap, android.graphics.Bitmap, int, int, float, float, int):void");
    }

    public static void async(Activity activity, Context context, ImageView imageView, String str, Object obj, AccountHandle accountHandle, ImageOptions imageOptions, HttpHost httpHost, String str2) {
        ImageOptions imageOptions2 = imageOptions;
        async(activity, context, imageView, str, imageOptions2.memCache, imageOptions2.fileCache, imageOptions2.targetWidth, imageOptions2.fallback, imageOptions2.preset, imageOptions2.animation, imageOptions2.ratio, imageOptions2.anchor, obj, accountHandle, imageOptions2.policy, imageOptions2.round, httpHost, str2);
    }

    public static void async(Activity activity, Context context, ImageView imageView, String str, boolean z, boolean z2, int i, int i2, Bitmap bitmap, int i3, float f, float f2, Object obj, AccountHandle accountHandle, int i4, int i5, HttpHost httpHost, String str2) {
        Activity activity2 = activity;
        ImageView imageView2 = imageView;
        String str3 = str;
        int i6 = i;
        Object obj2 = obj;
        int i7 = i5;
        Bitmap memGet = z ? memGet(str, i, i7) : null;
        if (memGet != null) {
            imageView.setTag(Constant.TAG_URL, str);
            Common.showProgress(obj2, str, false);
            setBmAnimate(imageView, memGet, bitmap, i2, i3, f, f2, 4);
            return;
        }
        BitmapAjaxCallback bitmapAjaxCallback = new BitmapAjaxCallback();
        boolean z3 = z2;
        ((BitmapAjaxCallback) ((BitmapAjaxCallback) ((BitmapAjaxCallback) ((BitmapAjaxCallback) ((BitmapAjaxCallback) ((BitmapAjaxCallback) bitmapAjaxCallback.url(str)).imageView(imageView).memCache(z)).fileCache(z2)).targetWidth(i).fallback(i2).preset(bitmap).animation(i3).ratio(f).anchor(f2).progress(obj2)).auth(accountHandle)).policy(i4)).round(i7).networkUrl(str2);
        if (httpHost != null) {
            bitmapAjaxCallback.proxy(httpHost.getHostName(), httpHost.getPort());
        }
        if (activity2 != null) {
            bitmapAjaxCallback.async(activity);
            return;
        }
        Context context2 = context;
        bitmapAjaxCallback.async(context);
    }

    public void async(Context context) {
        String url = getUrl();
        ImageView imageView = (ImageView) this.v.get();
        if (url == null) {
            showProgress(false);
            setBitmap(url, imageView, null, false);
            return;
        }
        Bitmap memGet = memGet(url);
        if (memGet != null) {
            imageView.setTag(Constant.TAG_URL, url);
            this.status = new AjaxStatus().source(4).done();
            callback(url, memGet, this.status);
            return;
        }
        presetBitmap(url, imageView);
        if (!queueMap.containsKey(url)) {
            addQueue(url, imageView);
            super.async(imageView.getContext());
        } else {
            showProgress(true);
            addQueue(url, imageView);
        }
    }

    /* access modifiers changed from: protected */
    public boolean isStreamingContent() {
        return !DELAY_WRITE;
    }

    private void addQueue(String str, ImageView imageView) {
        WeakHashMap weakHashMap = (WeakHashMap) queueMap.get(str);
        if (weakHashMap != null) {
            weakHashMap.put(imageView, this);
        } else if (queueMap.containsKey(str)) {
            WeakHashMap weakHashMap2 = new WeakHashMap();
            weakHashMap2.put(imageView, this);
            queueMap.put(str, weakHashMap2);
        } else {
            queueMap.put(str, null);
        }
    }

    private static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int i) {
        Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        RectF rectF = new RectF(rect);
        float f = (float) i;
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(-12434878);
        canvas.drawRoundRect(rectF, f, f, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return createBitmap;
    }
}
