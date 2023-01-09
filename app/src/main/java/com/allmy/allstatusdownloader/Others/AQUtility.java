package com.allmy.allstatusdownloader.Others;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AlphaAnimation;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AQUtility {
    private static final int IO_BUFFER_SIZE = 4096;
    private static File cacheDir = null;
    private static Context context = null;
    private static boolean debug = false;
    private static UncaughtExceptionHandler eh;
    private static Handler handler;
    private static File pcacheDir;
    private static ScheduledExecutorService storeExe;
    private static Map<String, Long> times = new HashMap();
    private static Object wait;

    public static void setDebug(boolean z) {
        debug = z;
    }

    public static void debugWait(long j) {
        if (debug) {
            if (wait == null) {
                wait = new Object();
            }
            synchronized (wait) {
                try {
                    wait.wait(j);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void debugNotify() {
        if (debug) {
            Object obj = wait;
            if (obj != null) {
                synchronized (obj) {
                    wait.notifyAll();
                }
            }
        }
    }

    public static void debug(Object obj) {
        if (debug) {
            StringBuilder sb = new StringBuilder();
            sb.append(obj);
            sb.append("");
            Log.w("AQuery", sb.toString());
        }
    }

    public static void warn(Object obj, Object obj2) {
        StringBuilder sb = new StringBuilder();
        sb.append(obj);
        sb.append(":");
        sb.append(obj2);
        Log.w("AQuery", sb.toString());
    }

    public static void debug(Object obj, Object obj2) {
        if (debug) {
            StringBuilder sb = new StringBuilder();
            sb.append(obj);
            sb.append(":");
            sb.append(obj2);
            Log.w("AQuery", sb.toString());
        }
    }

    public static void debug(Throwable th) {
        if (debug) {
            Log.w("AQuery", Log.getStackTraceString(th));
        }
    }

    public static void report(Throwable th) {
        if (th != null) {
            try {
                warn("reporting", Log.getStackTraceString(th));
                if (eh != null) {
                    eh.uncaughtException(Thread.currentThread(), th);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void setExceptionHandler(UncaughtExceptionHandler uncaughtExceptionHandler) {
        eh = uncaughtExceptionHandler;
    }

    public static void time(String str) {
        times.put(str, Long.valueOf(System.currentTimeMillis()));
    }

    public static long timeEnd(String str, long j) {
        Long l = (Long) times.get(str);
        if (l == null) {
            return 0;
        }
        long currentTimeMillis = System.currentTimeMillis() - l.longValue();
        if (j == 0 || currentTimeMillis > j) {
            debug(str, Long.valueOf(currentTimeMillis));
        }
        return currentTimeMillis;
    }

    public static Object invokeHandler(Object obj, String str, boolean z, boolean z2, Class<?>[] clsArr, Object... objArr) {
        return invokeHandler(obj, str, z, z2, clsArr, null, objArr);
    }

    public static Object invokeHandler(Object obj, String str, boolean z, boolean z2, Class<?>[] clsArr, Class<?>[] clsArr2, Object... objArr) {
        try {
            return invokeMethod(obj, str, z, clsArr, clsArr2, objArr);
        } catch (Exception e) {
            if (z2) {
                report(e);
            } else {
                debug((Throwable) e);
            }
            return null;
        }
    }

    private static Object invokeMethod(Object obj, String str, boolean z, Class<?>[] clsArr, Class<?>[] clsArr2, Object... objArr) throws Exception {
        if (!(obj == null || str == null)) {
            if (clsArr == null) {
                clsArr = new Class[0];
            }
            return obj.getClass().getMethod(str, clsArr).invoke(obj, objArr);
        }
        return null;
    }

    public static void transparent(View view, boolean z) {
        setAlpha(view, z ? 0.5f : 1.0f);
    }

    private static void setAlpha(View view, float f) {
        if (f == 1.0f) {
            view.clearAnimation();
            return;
        }
        AlphaAnimation alphaAnimation = new AlphaAnimation(f, f);
        alphaAnimation.setDuration(0);
        alphaAnimation.setFillAfter(true);
        view.startAnimation(alphaAnimation);
    }

    public static void ensureUIThread() {
        if (!isUIThread()) {
            report(new IllegalStateException("Not UI Thread"));
        }
    }

    public static boolean isUIThread() {
        return Looper.getMainLooper().getThread().getId() == Thread.currentThread().getId();
    }

    public static Handler getHandler() {
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }
        return handler;
    }

    public static void post(Runnable runnable) {
        getHandler().post(runnable);
    }

    public static void post(Object obj, String str) {
        post(obj, str, new Class[0], new Object[0]);
    }

    public static void post(final Object obj, final String str, final Class<?>[] clsArr, final Object... objArr) {
        post(new Runnable() {
            public void run() {
                AQUtility.invokeHandler(obj, str, false, true, clsArr, objArr);
            }
        });
    }

    public static void postAsync(Object obj, String str) {
        postAsync(obj, str, new Class[0], new Object[0]);
    }

    public static void postAsync(final Object obj, final String str, final Class<?>[] clsArr, final Object... objArr) {
        getFileStoreExecutor().execute(new Runnable() {
            public void run() {
                AQUtility.invokeHandler(obj, str, false, true, clsArr, objArr);
            }
        });
    }

    public static void removePost(Runnable runnable) {
        getHandler().removeCallbacks(runnable);
    }

    public static void postDelayed(Runnable runnable, long j) {
        getHandler().postDelayed(runnable, j);
    }

//    public static void apply(Editor editor) {
//        Editor editor2 = editor;
//        invokeHandler(editor2, "apply", false, true, null, null);
//        return;
//    }

    private static String getMD5Hex(String str) {
        return new BigInteger(getMD5(str.getBytes())).abs().toString(36);
    }

    private static byte[] getMD5(byte[] bArr) {
        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");
            instance.update(bArr);
            return instance.digest();
        } catch (NoSuchAlgorithmException e) {
            report(e);
            return null;
        }
    }

    public static void copy(InputStream inputStream, OutputStream outputStream) throws IOException {
        copy(inputStream, outputStream, 0, null);
    }

    public static void copy(InputStream inputStream, OutputStream outputStream, int i, Progress progress) throws IOException {
        debug("content header", Integer.valueOf(i));
        if (progress != null) {
            progress.reset();
            progress.setBytes(i);
        }
        byte[] bArr = new byte[4096];
        while (true) {
            int read = inputStream.read(bArr);
            if (read == -1) {
                break;
            }
            outputStream.write(bArr, 0, read);
            if (progress != null) {
                progress.increment(read);
            }
        }
        if (progress != null) {
            progress.done();
        }
    }

    public static byte[] toBytes(InputStream inputStream) {
        byte[] bArr;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            copy(inputStream, byteArrayOutputStream);
            bArr = byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            report(e);
            bArr = null;
        }
        close(inputStream);
        return bArr;
    }

    public static void write(File file, byte[] bArr) {
        try {
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (Exception e) {
                    debug("file create fail", file);
                    report(e);
                }
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(bArr);
            fileOutputStream.close();
        } catch (Exception e2) {
            report(e2);
        }
    }

    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception unused) {
            }
        }
    }

    private static ScheduledExecutorService getFileStoreExecutor() {
        if (storeExe == null) {
            storeExe = Executors.newSingleThreadScheduledExecutor();
        }
        return storeExe;
    }

    public static void storeAsync(File file, byte[] bArr, long j) {
        getFileStoreExecutor().schedule(new Common().method(1, file, bArr), j, TimeUnit.MILLISECONDS);
    }

    public static File getCacheDir(Context context2, int i) {
        if (i != 1) {
            return getCacheDir(context2);
        }
        File file = pcacheDir;
        if (file != null) {
            return file;
        }
        pcacheDir = new File(getCacheDir(context2), "persistent");
        pcacheDir.mkdirs();
        return pcacheDir;
    }

    public static File getCacheDir(Context context2) {
        if (cacheDir == null) {
            cacheDir = new File(context2.getCacheDir(), "aquery");
            cacheDir.mkdirs();
        }
        return cacheDir;
    }

    public static void setCacheDir(File file) {
        cacheDir = file;
        File file2 = cacheDir;
        if (file2 != null) {
            file2.mkdirs();
        }
    }

    private static File makeCacheFile(File file, String str) {
        return new File(file, str);
    }

    private static String getCacheFileName(String str) {
        return getMD5Hex(str);
    }

    public static File getCacheFile(File file, String str) {
        if (str == null) {
            return null;
        }
        if (str.startsWith(File.separator)) {
            return new File(str);
        }
        return makeCacheFile(file, getCacheFileName(str));
    }

    public static File getExistedCacheByUrl(File file, String str) {
        File cacheFile = getCacheFile(file, str);
        if (cacheFile == null || !cacheFile.exists()) {
            return null;
        }
        return cacheFile;
    }

    public static File getExistedCacheByUrlSetAccess(File file, String str) {
        File existedCacheByUrl = getExistedCacheByUrl(file, str);
        if (existedCacheByUrl != null) {
            lastAccess(existedCacheByUrl);
        }
        return existedCacheByUrl;
    }

    private static void lastAccess(File file) {
        file.setLastModified(System.currentTimeMillis());
    }

    public static void store(File file, byte[] bArr) {
        if (file != null) {
            try {
                write(file, bArr);
            } catch (Exception e) {
                report(e);
            }
        }
    }

    public static void cleanCacheAsync(Context context2) {
        cleanCacheAsync(context2, 3000000, 2000000);
    }

    public static void cleanCacheAsync(Context context2, long j, long j2) {
        try {
            File cacheDir2 = getCacheDir(context2);
            getFileStoreExecutor().schedule(new Common().method(2, cacheDir2, Long.valueOf(j), Long.valueOf(j2)), 0, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            report(e);
        }
    }

    public static void cleanCache(File file, long j, long j2) {
        try {
            File[] listFiles = file.listFiles();
            if (listFiles != null) {
                Arrays.sort(listFiles, new Common());
                if (testCleanNeeded(listFiles, j)) {
                    cleanCache(listFiles, j2);
                }
                File tempDir = getTempDir();
                if (tempDir != null && tempDir.exists()) {
                    cleanCache(tempDir.listFiles(), 0);
                }
            }
        } catch (Exception e) {
            report(e);
        }
    }

    public static File getTempDir() {
        File file = new File(Environment.getExternalStorageDirectory(), "aquery/temp");
        file.mkdirs();
        if (!file.exists()) {
            return null;
        }
        return file;
    }

    private static boolean testCleanNeeded(File[] fileArr, long j) {
        long j2 = 0;
        for (File length : fileArr) {
            j2 += length.length();
            if (j2 > j) {
                return true;
            }
        }
        return false;
    }

    private static void cleanCache(File[] fileArr, long j) {
        long j2 = 0;
        int i = 0;
        for (File file : fileArr) {
            if (file.isFile()) {
                j2 += file.length();
                if (j2 >= j) {
                    file.delete();
                    i++;
                }
            }
        }
        debug("deleted", Integer.valueOf(i));
    }

    public static int dip2pixel(Context context2, float f) {
        return (int) TypedValue.applyDimension(1, f, context2.getResources().getDisplayMetrics());
    }

    public static void setContext(Application application) {
        context = application.getApplicationContext();
    }

    public static Context getContext() {
        if (context == null) {
            warn("warn", "getContext with null");
            debug((Throwable) new IllegalStateException());
        }
        return context;
    }
}
