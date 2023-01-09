package com.allmy.allstatusdownloader.Activity;

import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.allmy.allstatusdownloader.Adapter.DrawerfeedAdapt;
import com.allmy.allstatusdownloader.Model.DrawerParseSetter;
import com.allmy.allstatusdownloader.Others.Constants;
import com.allmy.allstatusdownloader.Others.Utils;
import com.allmy.allstatusdownloader.R;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.gson.Gson;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
@Keep
public class ActivitywithDrawer extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences accountInfoPref;
    public ArrayList<DrawerParseSetter> array_drawerParseSetters;
    public TextView btn_retry;
    SharedPreferences cookiePref;
    public List<Cookie> cookies;
    String csrftoken;
    SharedPreferences currentUser;
    public DrawerfeedAdapt drawerfeedAdapt;
    public AlertDialog erroralertBox;
    String fullName;
    public boolean isRefreshSwap = false;
    public boolean isShowprogress = true;
    SharedPreferences loginPref;
    public SwipeRefreshLayout mSwipeRefreshLayout = null;
    public RelativeLayout no_Userfound;
    String profilePic;
    public RecyclerView recyclerView;
    SharedPreferences totalUserInfo;
    public JSONArray tray_array;
    public TextView tv_network;
    public TextView txt_toolbar;
    public TextView txt_toolsub;
    RelativeLayout upload_progress;
    String userId;
    List<IProfile> userLoginProfile;
    String userNameToolBar;
    ImageView ivSearch,ivBack;
    FrameLayout frameLayout;
    Utils utils;
    InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = getWindow();
//            Drawable background = getResources().getDrawable(R.drawable.bg_status);
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(getResources().getColor(android.R.color.transparent));
//            window.setNavigationBarColor(getResources().getColor(android.R.color.transparent));
//            window.setBackgroundDrawable(background);
//        }
        setContentView(R.layout.activitywith_drawer);
        InterstitialAd.load(this, getString(R.string.interstitial_ID), new AdRequest.Builder().build(), new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                mInterstitialAd = interstitialAd;
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                mInterstitialAd = null;

            }
        });
        getIntentData();
        getLoginCookies();
        setupToolbar();
        initilizeView();
        setupProfile();

        if (!isNetworkAvailable()) {
            Toast.makeText(this, getResources().getString(R.string.internet_connection), Toast.LENGTH_SHORT).show();
            tv_network.setText(getResources().getString(R.string.internet_connection));
            tv_network.setVisibility(View.VISIBLE);
            btn_retry.setVisibility(View.VISIBLE);
            upload_progress.setVisibility(View.INVISIBLE);
            ArrayList<DrawerParseSetter> arrayList = array_drawerParseSetters;
            if (arrayList != null) {
                arrayList.clear();
            }
            drawerfeedAdapt = new DrawerfeedAdapt(this, array_drawerParseSetters, this);
            recyclerView.setAdapter(drawerfeedAdapt);
            upload_progress.setVisibility(View.INVISIBLE);
        } else if (!isRefreshSwap) {
            isRefreshSwap = true;
            fetchStory();
            tv_network.setVisibility(View.INVISIBLE);
            btn_retry.setVisibility(View.INVISIBLE);
        }
    }

     public void onPause() {
        super.onPause();
    }

    private void getIntentData() {
        userLoginProfile = new ArrayList<>();
        Intent intent = getIntent();
        userId = intent.getStringExtra("USER_ID");
        profilePic = intent.getStringExtra("profile_pic_url");
        fullName = intent.getStringExtra("full_name");
        csrftoken = intent.getStringExtra("csrftoken");
        userNameToolBar = intent.getStringExtra("user_name");
        PrintStream printStream = System.out;
        String sb = "csrftoken nn:" +
                csrftoken;
        printStream.println(sb);
    }

    private void initilizeView() {
        recyclerView = findViewById(R.id.recycler_view);
        ImageView imgProfile = findViewById(R.id.imgProfile);
        upload_progress = findViewById(R.id.upload_progress);
        no_Userfound = findViewById(R.id.no_Userfound);
        tv_network = findViewById(R.id.tv_network);
        btn_retry = findViewById(R.id.btn_retry);
        frameLayout = findViewById(R.id.frameBanner);
        ivBack = findViewById(R.id.ivBack);

        utils = new Utils(ActivitywithDrawer.this);
        utils.loadBanner(ActivitywithDrawer.this, frameLayout);
        mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        Glide.with(this)
                .load(profilePic)
                .placeholder(R.drawable.applogo)
                .into(imgProfile);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        array_drawerParseSetters = new ArrayList<>();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        btn_retry.setOnClickListener(view -> {
            if (!isNetworkAvailable()) {
                Toast.makeText(ActivitywithDrawer.this, getResources().getString(R.string.internet_connection), Toast.LENGTH_SHORT).show();
                tv_network.setText(getResources().getString(R.string.internet_connection));
                tv_network.setVisibility(View.VISIBLE);
                btn_retry.setVisibility(View.VISIBLE);
                upload_progress.setVisibility(View.INVISIBLE);
                return;
            }
            fetchStory();
            tv_network.setVisibility(View.INVISIBLE);
            btn_retry.setVisibility(View.INVISIBLE);
        });
    }

    @SuppressLint("WrongConstant")
    private void setupToolbar() {
        txt_toolbar = findViewById(R.id.txt_toolbar);
        txt_toolsub = findViewById(R.id.txt_toolsub);
        ivSearch = findViewById(R.id.ivSearch);
        txt_toolbar.setText(fullName);
        txt_toolsub.setText(userNameToolBar);
        txt_toolsub.setVisibility(View.VISIBLE);
        txt_toolbar.setVisibility(View.VISIBLE);

        ivSearch.setVisibility(View.VISIBLE);
        ivSearch.setOnClickListener(view -> {
            if (isNetworkAvailable()) {
                Intent intent = new Intent(ActivitywithDrawer.this, ActivitySearch.class);
                intent.setFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return;
            }
            Toast.makeText(ActivitywithDrawer.this, getResources().getString(R.string.internet_connection), Toast.LENGTH_SHORT).show();
        });
    }

    private void setupProfile() {
        String string = totalUserInfo.getString("TOTAL_USER", null);
        if (string != null) {
            String[] strArr = new Gson().fromJson(string, String[].class);
            ArrayList<String> arrayList = new ArrayList<>();
            Collections.addAll(arrayList, strArr);
            String string2 = currentUser.getString("CURRENT_USER", null);
            Iterator<String> it = arrayList.iterator();
            while (it.hasNext()) {
                if (it.next().equals(string2)) {
                    it.remove();
                }
            }
            arrayList.add(0, string2);
            for (int i = 0; i < arrayList.size(); i++) {
                String str = arrayList.get(i);
                String sb = "LOGIN_" + str.toLowerCase(Locale.getDefault()).trim();
                SharedPreferences sharedPreferences = getSharedPreferences(sb, 0);
                String str2 = "";
                String string3 = sharedPreferences.getString("USER_ID", str2);
                String string4 = sharedPreferences.getString("full_name", str2);
                String string5 = sharedPreferences.getString("profile_pic_url", str2);
                PrintStream printStream = System.out;
                String str3 = ":";
                String sb2 = "userId :userFullName :userProfilePicture-->" + string3 + str3 + string4 + str3 + string5;
                printStream.println(sb2);
            }
        }
    }

    public void getLoginCookies() {
        String str = "CURRENT_USER";
        currentUser = getSharedPreferences(str, 0);
        String string = currentUser.getString(str, null);

        if (string != null) {
            totalUserInfo = getSharedPreferences("TOTAL_USER_INFO", 0);
            String sb = "LOGIN_" +
                    string.toLowerCase(Locale.getDefault()).trim();
            loginPref = getSharedPreferences(sb, 0);
            String sb2 = "COOKIE_PREF_" +
                    string.toLowerCase(Locale.getDefault()).trim();
            cookiePref = getSharedPreferences(sb2, 0);
            String sb3 = "ACCOUNT_PREF_" +
                    string.toLowerCase(Locale.getDefault()).trim();
            accountInfoPref = getSharedPreferences(sb3, 0);
            cookies = new ArrayList<>();
            int i = cookiePref.getInt("cookie_count", -1);
            PrintStream printStream = System.out;
            String sb4 = "count  :" +
                    i;
            printStream.println(sb4);
            for (int i2 = 0; i2 < i; i2++) {
                PrintStream printStream2 = System.out;
                String str2 = "";
                String sb5 = "Cookies from pref : " +
                        cookiePref.getString(String.valueOf(i2), str2);
                printStream2.println(sb5);
                try {
                    Cookie parse = Cookie.parse(HttpUrl.get(new URL("https://i.instagram.com/")), cookiePref.getString(String.valueOf(i2), str2));
                    cookies.add(parse);
                    PrintStream printStream3 = System.out;
                    String sb6 = "Logincookies :" +
                            parse.toString();
                    printStream3.println(sb6);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @SuppressLint("WrongConstant")
    public void fetchStory() {
        if (isShowprogress) {
            upload_progress.setVisibility(0);
        } else {
            upload_progress.setVisibility(4);
        }
        array_drawerParseSetters = new ArrayList<>();
        new OkHttpClient.Builder().cookieJar(new CookieJar() {
            public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> list) {
            }

            @NotNull
            public List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
                return cookies;
            }
        }).build().newCall(new Request.Builder().url("https://i.instagram.com/api/v1/feed/reels_tray").header("User-Agent", Constants.USER_AGENT).header("Connection", "close").header("language", "en").header("Accept", "*/*").header("X-IG-Capabilities", "3QI=").header("Content-type", "application/x-www-form-urlencoded; charset=UTF-8").build()).enqueue(new Callback() {
            public void onFailure(@NotNull Call call, @NotNull final IOException iOException) {
                ActivitywithDrawer activitywithDrawer = ActivitywithDrawer.this;
                if (activitywithDrawer != null) {
                    activitywithDrawer.runOnUiThread(() -> {
                        isRefreshSwap = false;
                        tv_network.setText(iOException.getMessage());
                        tv_network.setVisibility(0);
                        btn_retry.setVisibility(0);
                        upload_progress.setVisibility(4);
                        if (array_drawerParseSetters != null) {
                            array_drawerParseSetters.clear();

                            Log.e("Res--)", "3--)" + array_drawerParseSetters.size());
                            drawerfeedAdapt = new DrawerfeedAdapt(ActivitywithDrawer.this, array_drawerParseSetters, ActivitywithDrawer.this);
                            recyclerView.setAdapter(drawerfeedAdapt);
                        }
                    });
                }
            }

            @SuppressLint("DefaultLocale")
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String str = "hide_from_feed_unit";
                String str2 = "tray";
                String str3 = "message";
                try {
                    ResponseBody responseBody = response.body();
                    if (responseBody == null) {
                        return;
                    }
                    String string = responseBody.string();
                    PrintStream printStream = System.out;
                    String sb = "feedstory_response-->" + string;
                    printStream.println(sb);
                    JSONObject jSONObject = new JSONObject(string);
                    JSONArray jsonArray = jSONObject.getJSONArray("tray");


                    String string2 = jSONObject.getString(NotificationCompat.CATEGORY_STATUS);
                    if (jSONObject.has(str3) && string2.contains("fail")) {
                        final String string3 = jSONObject.getString(str3);
                        if (string3.contains("login_required")) {
                            runOnUiThread(() -> {
                                logOutErrorDialog(string3);
                                upload_progress.setVisibility(4);
                            });
                            return;
                        }
                    }
                    jSONObject.has(str2);
                    tray_array = jSONObject.getJSONArray(str2);
                    if (tray_array.length() > 0) {
                        if (array_drawerParseSetters == null) {
                            array_drawerParseSetters = new ArrayList<>();
                        }
                        for (int i = 0; i < tray_array.length(); i++) {
                            String str4 = "latest_reel_media";
                            String str5 = "id";
                            String str6 = "New!";
                            String str7 = "full_name";
                            String str8 = "username";
                            String str9 = "%.0f";
                            String str10 = "user";
                            String str11 = " ";
                            if (!tray_array.getJSONObject(i).has(str)) {
                                String string4 = tray_array.getJSONObject(i).getJSONObject(str10).getString(str8);
                                String string5 = tray_array.getJSONObject(i).getJSONObject(str10).getString(str7);
                                if (!string4.equalsIgnoreCase(str6)) {
                                    String string6 = tray_array.getJSONObject(i).getString(str5);
                                    String string7 = tray_array.getJSONObject(i).getString(str4);
                                    double longValue = (double) (System.currentTimeMillis() / 1000 - Long.parseLong(string7));
                                    double d = longValue / 3600.0d;
                                    String.format("%.2f", d);
                                    @SuppressLint("DefaultLocale") String format = String.format(str9, d);
                                    String string8 = tray_array.getJSONObject(i).getJSONObject(str10).getString("profile_pic_url");
                                    DrawerParseSetter drawerParseSetter = new DrawerParseSetter();
                                    if (d > 2.0d) {
                                        String sb2 = format +
                                                str11 +
                                                getResources().getString(R.string.hours_ago);
                                        drawerParseSetter.setLatest_reel_media(sb2);
                                    } else if (d > 1.0d) {
                                        String sb3 = format +
                                                str11 +
                                                getResources().getString(R.string.hour_ago);
                                        drawerParseSetter.setLatest_reel_media(sb3);
                                    } else {
                                        double d2 = longValue / 60.0d;
                                        String format2 = String.format(str9, d2);
                                        if (d2 > 2.0d) {
                                            String sb4 = format2 +
                                                    str11 +
                                                    getResources().getString(R.string.minutes_ago);
                                            drawerParseSetter.setLatest_reel_media(sb4);
                                        } else {
                                            String sb5 = str11 +
                                                    getResources().getString(R.string.few_moments_ago);
                                            drawerParseSetter.setLatest_reel_media(sb5);
                                        }
                                    }
                                    drawerParseSetter.setUserFullName(string5);
                                    drawerParseSetter.setGetDate(string7);
                                    drawerParseSetter.setUserName(string4);
                                    drawerParseSetter.setUserID(string6);
                                    drawerParseSetter.setProfile_picture(string8);

                                    if (array_drawerParseSetters != null) {
                                        array_drawerParseSetters.add(drawerParseSetter);
                                    }

                                }
                            } else if (!tray_array.getJSONObject(i).getBoolean(str)) {
                                String string9 = tray_array.getJSONObject(i).getJSONObject(str10).getString(str8);
                                String string10 = tray_array.getJSONObject(i).getJSONObject(str10).getString(str7);
                                if (!string9.equalsIgnoreCase(str6)) {
                                    String string11 = tray_array.getJSONObject(i).getString(str5);
                                    String string12 = tray_array.getJSONObject(i).getString(str4);
                                    double longValue2 = (double) (System.currentTimeMillis() / 1000 - Long.parseLong(string12));
                                    double d3 = longValue2 / 3600.0d;
                                    String.format("%.2f", d3);
                                    String format3 = String.format(str9, d3);
                                    String string13 = tray_array.getJSONObject(i).getJSONObject(str10).getString("profile_pic_url");
                                    DrawerParseSetter drawerParseSetter2 = new DrawerParseSetter();
                                    if (d3 > 2.0d) {
                                        String sb6 = format3 +
                                                str11 +
                                                getResources().getString(R.string.hours_ago);
                                        drawerParseSetter2.setLatest_reel_media(sb6);
                                    } else if (d3 > 1.0d) {
                                        String sb7 = format3 +
                                                str11 +
                                                getResources().getString(R.string.hour_ago);
                                        drawerParseSetter2.setLatest_reel_media(sb7);
                                    } else {
                                        double d4 = longValue2 / 60.0d;
                                        String format4 = String.format(str9, d4);
                                        if (d4 > 2.0d) {
                                            String sb8 = format4 +
                                                    str11 +
                                                    getResources().getString(R.string.minutes_ago);
                                            drawerParseSetter2.setLatest_reel_media(sb8);
                                        } else {
                                            String sb9 = str11 +
                                                    getResources().getString(R.string.few_moments_ago);
                                            drawerParseSetter2.setLatest_reel_media(sb9);
                                        }
                                    }
                                    drawerParseSetter2.setUserFullName(string10);
                                    drawerParseSetter2.setGetDate(string12);
                                    drawerParseSetter2.setUserName(string9);
                                    drawerParseSetter2.setUserID(string11);
                                    drawerParseSetter2.setProfile_picture(string13);
                                    if (array_drawerParseSetters != null) {
                                        array_drawerParseSetters.add(drawerParseSetter2);
                                    }
                                }
                            }
                        }
                        if (ActivitywithDrawer.this != null) {
                            runOnUiThread(() -> no_Userfound.setVisibility(4));
                        }
                    } else if (ActivitywithDrawer.this != null) {
                        runOnUiThread(() -> {
                            ActivitywithDrawer activitywithDrawer = ActivitywithDrawer.this;
                            String sb1 = "" +
                                    getResources().getString(R.string.no_data_found);
                            Toast.makeText(activitywithDrawer, sb1, Toast.LENGTH_SHORT).show();
                            isRefreshSwap = false;
                            upload_progress.setVisibility(4);
                            no_Userfound.setVisibility(0);
                            if (!isNetworkAvailable()) {
                                tv_network.setVisibility(0);
                                btn_retry.setVisibility(0);
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                runOnUiThread(() -> {
                    if (array_drawerParseSetters == null || tray_array == null) {
                        no_Userfound.setVisibility(0);
                        upload_progress.setVisibility(4);
                        if (!isNetworkAvailable()) {
                            tv_network.setVisibility(0);
                            btn_retry.setVisibility(0);
                            return;
                        }
                        return;
                    }
                    upload_progress.setVisibility(4);
                    tv_network.setVisibility(4);
                    btn_retry.setVisibility(4);
                    recyclerView.getRecycledViewPool().clear();
                    mSwipeRefreshLayout.setRefreshing(false);
                    drawerfeedAdapt = new DrawerfeedAdapt(ActivitywithDrawer.this, array_drawerParseSetters, ActivitywithDrawer.this);
                    mSwipeRefreshLayout.setRefreshing(false);
                    new Handler().postDelayed(() -> {
                        isRefreshSwap = false;
                        isShowprogress = true;
                    }, 2000);
                    mSwipeRefreshLayout.setOnRefreshListener(() -> {
                        if (!isNetworkAvailable()) {
                            if (mSwipeRefreshLayout != null) {
                                mSwipeRefreshLayout.setRefreshing(false);
                            }
                            if (array_drawerParseSetters != null) {
                                array_drawerParseSetters.clear();
                            }
                            tv_network.setVisibility(0);
                            btn_retry.setVisibility(0);
                            upload_progress.setVisibility(4);

                            drawerfeedAdapt = new DrawerfeedAdapt(ActivitywithDrawer.this, array_drawerParseSetters, ActivitywithDrawer.this);
                            recyclerView.setAdapter(drawerfeedAdapt);
                            Toast.makeText(ActivitywithDrawer.this, getResources().getString(R.string.internet_connection), Toast.LENGTH_SHORT).show();
                        } else if (!isRefreshSwap) {
                            isRefreshSwap = true;
                            isShowprogress = false;
                            fetchStory();
                            if (mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing()) {
                                mSwipeRefreshLayout.setRefreshing(true);
                            }
                        } else if (mSwipeRefreshLayout != null) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    });

                    recyclerView.setAdapter(drawerfeedAdapt);
                });
            }
        });
    }

    public void updateData() {
        drawerfeedAdapt = new DrawerfeedAdapt(ActivitywithDrawer.this, array_drawerParseSetters, ActivitywithDrawer.this);
        recyclerView.setAdapter(drawerfeedAdapt);
    }

    @SuppressLint("WrongConstant")
    public void logoutUser() {
        String str = "CURRENT_USER";
        currentUser = getSharedPreferences(str, 0);
        String string = currentUser.getString(str, null);
        if (string != null) {
            totalUserInfo = getSharedPreferences("TOTAL_USER_INFO", 0);
            String sb = "LOGIN_" +
                    string.toLowerCase(Locale.getDefault()).trim();
            loginPref = getSharedPreferences(sb, 0);
            String sb2 = "COOKIE_PREF_" +
                    string.toLowerCase(Locale.getDefault()).trim();
            cookiePref = getSharedPreferences(sb2, 0);
            String sb3 = "ACCOUNT_PREF_" +
                    string.toLowerCase(Locale.getDefault()).trim();
            accountInfoPref = getSharedPreferences(sb3, 0);
            loginPref.edit().clear().apply();
            cookiePref.edit().clear().apply();
            accountInfoPref.edit().clear().apply();
            String str2 = "TOTAL_USER";
            String string2 = totalUserInfo.getString(str2, null);
            if (string2 != null) {
                Gson gson = new Gson();
                String[] strArr = gson.fromJson(string2, String[].class);
                ArrayList<String> arrayList = new ArrayList<>();
                Collections.addAll(arrayList, strArr);
                for (int i = 0; i < arrayList.size(); i++) {
                }
                arrayList.remove(string);
                String json = gson.toJson(arrayList);
                SharedPreferences.Editor edit = totalUserInfo.edit();
                edit.putString(str2, json);
                edit.apply();
                if (arrayList.size() > 0) {
                    currentUser.edit().putString(str, arrayList.get(0)).apply();
                } else {
                    currentUser.edit().clear().apply();
                    totalUserInfo.edit().clear().apply();
                }
                finish();
                return;
            }
            return;
        }
        Toast makeText = Toast.makeText(this, getResources().getString(R.string.error_box), Toast.LENGTH_LONG);
        makeText.setGravity(16, 0, 0);
        makeText.show();
        PrintStream printStream = System.out;
        String sb4 = "error_box :" +
                getResources().getString(R.string.error_box);
        printStream.println(sb4);
        @SuppressLint("SdCardPath") File[] listFiles = new File("/data/data/com.aioi.statusdownloader/shared_prefs/").listFiles();
        if (listFiles != null && listFiles.length > 0) {
            for (File file : listFiles) {
                if (file.exists()) {
                    file.delete();
                }
            }
        }
        ((AlarmManager) getSystemService(NotificationCompat.CATEGORY_ALARM)).set(1, System.currentTimeMillis() + 1000, PendingIntent.getActivity(this, 4965, new Intent(this, InstaDownloadActivity.class), 268435456));
        System.exit(0);
        finish();
    }

    public boolean isNetworkAvailable() {
        @SuppressLint("WrongConstant") NetworkInfo activeNetworkInfo = ((ConnectivityManager) getSystemService("connectivity")).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void onDestroy() {
        super.onDestroy();
        if (array_drawerParseSetters != null) {
            array_drawerParseSetters = null;
        }
        if (drawerfeedAdapt != null) {
            drawerfeedAdapt = null;
        }
        Runtime.getRuntime().gc();
    }

    public void logOutErrorDialog(String str) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View inflate = getLayoutInflater().inflate(R.layout.logoutbubblealertview, null);
        builder.setView(inflate);
        ((TextView) inflate.findViewById(R.id.txtError)).setText(str);
        inflate.findViewById(R.id.txtDialogTitle);
        inflate.findViewById(R.id.txtContent);
        TextView textView = inflate.findViewById(R.id.btnOk);
        TextView textView2 = inflate.findViewById(R.id.btnCancel);
        erroralertBox = builder.create();
        erroralertBox.setCancelable(false);
        if (!erroralertBox.isShowing() && !isFinishing()) {
            erroralertBox.show();
        }
        textView.setOnClickListener(view -> {
            ActivitywithDrawer activitywithDrawer = ActivitywithDrawer.this;
            if (activitywithDrawer != null && !erroralertBox.isShowing() && !isFinishing()) {
                erroralertBox.cancel();
            }
            logoutUser();
        });
        textView2.setOnClickListener(view -> {
            ActivitywithDrawer activitywithDrawer = ActivitywithDrawer.this;
            if (activitywithDrawer != null && !erroralertBox.isShowing() && !isFinishing()) {
                erroralertBox.cancel();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void Logout(View view) {
        String str2 = "profile_pic_url";
        String str4 = "profilePic";

        if (mInterstitialAd != null) {
            mInterstitialAd.show(ActivitywithDrawer.this);

            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    if (isNetworkAvailable()) {
                        Intent intent5 = new Intent(ActivitywithDrawer.this, UserProfileActivity.class);
                        intent5.putExtra(str4, profilePic);
                        intent5.putExtra(str2, profilePic);
                        intent5.putExtra("USER_NAME", userNameToolBar);
                        intent5.putExtra("Full_NAME", fullName);
                        intent5.putExtra("USER_ID", userId);
                        intent5.setFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivityForResult(intent5, 1);
                    }
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    if (isNetworkAvailable()) {
                        Intent intent5 = new Intent(ActivitywithDrawer.this, UserProfileActivity.class);
                        intent5.putExtra(str4, profilePic);
                        intent5.putExtra(str2, profilePic);
                        intent5.putExtra("USER_NAME", userNameToolBar);
                        intent5.putExtra("Full_NAME", fullName);
                        intent5.putExtra("USER_ID", userId);
                        intent5.setFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivityForResult(intent5, 1);
                    }
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    mInterstitialAd = null;
                }
            });

        } else {
            if (isNetworkAvailable()) {
                Intent intent5 = new Intent(ActivitywithDrawer.this, UserProfileActivity.class);
                intent5.putExtra(str4, profilePic);
                intent5.putExtra(str2, profilePic);
                intent5.putExtra("USER_NAME", userNameToolBar);
                intent5.putExtra("Full_NAME", fullName);
                intent5.putExtra("USER_ID", userId);
                intent5.setFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivityForResult(intent5, 1);
            }
        }



    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        String str2 = "profile_pic_url";
        String str4 = "profilePic";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == 1 && resultCode == RESULT_OK) {
                finish();
            }
        } catch (Exception e) {

        }
    }
}