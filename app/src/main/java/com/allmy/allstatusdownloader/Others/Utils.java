package com.allmy.allstatusdownloader.Others;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.FrameLayout;

import androidx.core.app.ActivityCompat;

import com.allmy.allstatusdownloader.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

public class Utils {
    private static SharedPreferences pref;
    private static SharedPreferences.Editor editor;
    Activity mContext;
    public static final String PREF_NAME = "allstatus";
    public static final String TWITTER_KEY = "Sg4eZBsqktgYo8DG7RdpyNsWT";
    public static final String TWITTER_SECRET = "MxLc2FVnQ2MhnXE841KLpT9KQQT1fe4r81oJNmo6dxnHz5bOqK";
    public static final String IMG_TAG = "IMG-";
    public static final String VID_TAG = "VID";
    public static final String VID_EXT = ".mp4";
    public static final String GIF_EXT = ".gif";

    public static final String VIDEO_EXTENTION = ".mp4";
    public static final String IMAGE_EXTENTION = ".jpg";
    public static boolean isEmpty(String s) {
        return s == null || s.equals("null") || s.length() == 0 || s.trim().equals("") || s.isEmpty();
    }


    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public Utils(Activity context) {
        mContext = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void setWAUri(String spin) {
        editor.putString("waUri", spin);
        editor.commit();

    }
    public String getWAUri() {
        return pref.getString("waUri", "null");
    }

    public void setWBUri(String spin) {
        editor.putString("wbUri", spin);
        editor.commit();

    }
    public String getWBUri() {
        return pref.getString("wbUri", "null");
    }

    public void setGBUri(String spin) {
        editor.putString("GbUri", spin);
        editor.commit();

    }
    public String getGBUri() {
        return pref.getString("GbUri", "null");
    }

    public static String covertTimeToText(String strPasTime) {
        try {
            String convTime = "";
            String prefix = "";
            String suffix = "ago";
            try {
                if (!isEmpty(strPasTime)) {
                    long pasTime = Long.parseLong(strPasTime);
                    long dateDiff = (System.currentTimeMillis()) - (pasTime * 1000);
                    long second = TimeUnit.MILLISECONDS.toSeconds(dateDiff);
                    long minute = TimeUnit.MILLISECONDS.toMinutes(dateDiff);
                    long hour = TimeUnit.MILLISECONDS.toHours(dateDiff);
                    long day = TimeUnit.MILLISECONDS.toDays(dateDiff);

                    if (second < 60) {
                        convTime = second + " few moments " + suffix;
                    } else if (minute < 60) {
                        convTime = minute + " minutes " + suffix;
                    } else if (hour < 24) {
                        convTime = hour + " hours " + suffix;
                    } else if (day >= 7) {
                        if (day > 360) {
                            convTime = (day / 360) + " years " + suffix;
                        } else if (day > 30) {
                            convTime = (day / 30) + " months " + suffix;
                        } else {
                            convTime = (day / 7) + " week " + suffix;
                        }
                    } else if (day < 7) {
                        convTime = day + " days " + suffix;
                    }
                } else {
                    convTime = "";
                }


            } catch (ParseException e) {
                e.printStackTrace();
                Log.e("ConvTimeE", e.getMessage());
            }

            return convTime;
        } catch (Exception e) {

        }


        return null;
    }

    public void loadBanner(Activity activity, FrameLayout frameBanner) {
        if (frameBanner != null) {
            frameBanner.setVisibility(View.VISIBLE);
            AdView adView = new AdView(activity);
            adView.setAdUnitId(activity.getResources().getString(R.string.banner_ID));

            frameBanner.addView(adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            AdSize adSize = getAdSize(activity);
            adView.setAdSize(adSize);
            adView.loadAd(adRequest);

        }
    }
    private AdSize getAdSize(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;
        int adWidth = (int) (widthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth);
    }
    @SuppressLint("SimpleDateFormat")
    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    public static void deleteFileIfExists(Activity activity, File file) {
        try {
            if (file != null && isWriteStoragePermissionGranted(activity)) {
                if (file.exists()) {
                    deleteRecursive(file);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static boolean isWriteStoragePermissionGranted(Activity activity) {
        if (Build.VERSION.SDK_INT < 23 || activity.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        ActivityCompat.requestPermissions(activity, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 2);
        return false;
    }
    public static void deleteRecursive(File file) {
        if (file.isDirectory()) {
            for (File deleteRecursive : file.listFiles()) {
                deleteRecursive(deleteRecursive);
            }
        }
        file.delete();
    }
}
