package com.allmy.allstatusdownloader.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.allmy.allstatusdownloader.Adapter.HilightshowerRecyclerviewAdapter;
import com.allmy.allstatusdownloader.Auth.AQuery;
import com.allmy.allstatusdownloader.Model.AppInterface;
import com.allmy.allstatusdownloader.Model.HeighLightParameter;
import com.allmy.allstatusdownloader.Model.Story_setter;
import com.allmy.allstatusdownloader.Others.AllMultiDownloadManager;
import com.allmy.allstatusdownloader.Others.Constant;
import com.allmy.allstatusdownloader.Others.Constants;
import com.allmy.allstatusdownloader.Others.GridSpacingItemDecoration;
import com.allmy.allstatusdownloader.Others.RecyclerItemClickListener;
import com.allmy.allstatusdownloader.R;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
@Keep
public class HeighLightStory_shower extends AppCompatActivity {

    public static boolean highshowtickstory = false;
    public static boolean hilightstoryregister = false;
    private Uri Download_Uri;
    String USER_ID;
    String USER_NAME;
    SharedPreferences accountInfoPref;
    private String alldownloadPath;
    AQuery aq;
    SharedPreferences cookiePref;
    List<Cookie> cookies;
    int counter = 0;
    SharedPreferences currentUser;
    String download_url;
    int file_ptef;
    String highlight_Id;
    String hilight_profilecover;
    String hilight_title;
    ArrayList<Story_setter> hilightstory_data;
    boolean isMultiSelect = false;
    ArrayList<Long> list = new ArrayList<>();
    SharedPreferences loginPref;
    ArrayList<Story_setter> multiselect_list = new ArrayList<>();
    RelativeLayout no_storyfound;
    InterstitialAd mInterstitialAd;
    boolean permissionmenu = false;
    public boolean popupCheck = false;
    int position;
    HashSet<String> preferences;
    RecyclerView recyclerView;
    private long refid = 10;
    RelativeLayout rl_download;
    HilightshowerRecyclerviewAdapter storyshowerRecyclerAdapter;
    private TextView txt_toolbar;
    public RelativeLayout upload_progress;
    String user_FULLNAME;
    CardView ib_im_download;
    ProgressBar progressBar;
    TextView txtProgress, txtDownload;
    LinearLayout linearDownload;
    String dnlUrl, savePath;
    int posOfDownload = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heigh_light_story_shower);

        getIntentData();
        txt_toolbar = findViewById(R.id.txt_toolbar);
        txt_toolbar.setText(hilight_title);

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

        ib_im_download = findViewById(R.id.ib_im_download);
        progressBar = findViewById(R.id.progressBar);
        txtProgress = findViewById(R.id.txtProgress);
        linearDownload = findViewById(R.id.linearDownload);
        txtDownload = findViewById(R.id.txtDownload);

        ImageView backInsta = findViewById(R.id.backInsta);
        backInsta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        file_ptef = getSharedPreferences("DOWNLOAD_FILE_NAME_PREF", 0).getInt("file_value", 0);

        upload_progress = findViewById(R.id.upload_progress);
        no_storyfound = (RelativeLayout) findViewById(R.id.no_storyfound);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        rl_download = (RelativeLayout) findViewById(R.id.rl_download);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, 3, false));
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);
        aq = new AQuery((Activity) this);
        preferences = (HashSet) PreferenceManager.getDefaultSharedPreferences(this).getStringSet("PREF_COOKIES", new HashSet());
        getLoginCookies();

        ib_im_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (multiselect_list != null && multiselect_list.size() > 0) {
                    if (progressBar.getProgress() == 100) {

                        progressBar.setProgress(0);
                        linearDownload.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        txtProgress.setVisibility(View.GONE);


                        Intent intent = new Intent(HeighLightStory_shower.this, CreationActivity.class);
                        startActivity(intent);
                        finish();
                    } else {

                        linearDownload.setVisibility(View.GONE);
                        progressBar.setVisibility(View.VISIBLE);
                        txtProgress.setVisibility(View.VISIBLE);

                        if (mInterstitialAd != null) {
                            mInterstitialAd.show(HeighLightStory_shower.this);

                            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    downloadMultipleStory();
                                }

                                @Override
                                public void onAdFailedToShowFullScreenContent(AdError adError) {
                                    downloadMultipleStory();
                                }

                                @Override
                                public void onAdShowedFullScreenContent() {
                                    mInterstitialAd = null;
                                }
                            });

                        } else {
                            downloadMultipleStory();
                        }
                    }
                } else {
                    if (progressBar.getProgress() == 100) {
                        Intent intent = new Intent(HeighLightStory_shower.this, CreationActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }

            }
        });
    }

    public void onPause() {
        super.onPause();
    }

    public void onResume() {
        super.onResume();
    }

    public void onDestroy() {
        super.onDestroy();
    }

    private void getIntentData() {
        USER_NAME = getIntent().getStringExtra("USER_NAME");
        USER_ID = getIntent().getStringExtra("USER_ID");
        hilight_title = getIntent().getStringExtra("hilight_title");
        user_FULLNAME = getIntent().getStringExtra("user_FULLNAME");
        hilight_profilecover = getIntent().getStringExtra("hilight_profilecover");
        highlight_Id = getIntent().getStringExtra("highlight_Id");
        position = getIntent().getIntExtra("position", 0);
    }

    private void getLoginCookies() {
        String str = "CURRENT_USER";
        currentUser = getSharedPreferences(str, 0);
        String string = currentUser.getString(str, null);
        StringBuilder sb = new StringBuilder();
        sb.append("LOGIN_");
        sb.append(string.toLowerCase(Locale.getDefault()).trim());
        loginPref = getSharedPreferences(sb.toString(), 0);
        StringBuilder sb2 = new StringBuilder();
        sb2.append("COOKIE_PREF_");
        sb2.append(string.toLowerCase(Locale.getDefault()).trim());
        cookiePref = getSharedPreferences(sb2.toString(), 0);
        StringBuilder sb3 = new StringBuilder();
        sb3.append("ACCOUNT_PREF_");
        sb3.append(string.toLowerCase(Locale.getDefault()).trim());
        accountInfoPref = getSharedPreferences(sb3.toString(), 0);
        cookies = new ArrayList();
        int i = cookiePref.getInt("cookie_count", -1);
        for (int i2 = 0; i2 < i; i2++) {
            PrintStream printStream = System.out;
            StringBuilder sb4 = new StringBuilder();
            sb4.append("Cookies from pref : ");
            String str2 = "";
            sb4.append(cookiePref.getString(String.valueOf(i2), str2));
            printStream.println(sb4.toString());
            try {
                cookies.add(Cookie.parse(HttpUrl.get(new URL("https://i.instagram.com/")), cookiePref.getString(String.valueOf(i2), str2)));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        fetchHeighLightStoryReel(highlight_Id);
    }

    private void fetchHeighLightStoryReel(final String str) {

        Log.e("StoryId--)", "" + str);
        String[] strArr = {str};
        String uuid = UUID.randomUUID().toString();
        String str2 = null;
        String string = getSharedPreferences("csrf", 0).getString("csrftoken", null);
        PrintStream printStream = System.out;
        StringBuilder sb = new StringBuilder();
        sb.append("crstoken : uuid :");
        sb.append(string);
        printStream.println(sb.toString());
        HeighLightParameter heighLightParameter = new HeighLightParameter(uuid, USER_ID, string, strArr, "feed_timeline");
        String jsonString = getJsonString(heighLightParameter);
        PrintStream printStream2 = System.out;
        StringBuilder sb2 = new StringBuilder();
        sb2.append("message : ");
        sb2.append(jsonString);
        printStream2.println(sb2.toString());
        try {
            str2 = URLDecoder.decode(hmacSha256(Constants.SECRET_KEY, jsonString));
        } catch (Exception e) {
            e.printStackTrace();
        }
        PrintStream printStream3 = System.out;
        StringBuilder sb3 = new StringBuilder();
        sb3.append("encryptedParameters : ");
        sb3.append(str2);
        printStream3.println(sb3.toString());
        StringBuilder sb4 = new StringBuilder();
        sb4.append("ig_sig_key_version=4&signed_body=");
        sb4.append(str2);
        String str3 = ".";
        sb4.append(str3);
        sb4.append(jsonString);
        sb4.toString();
        OkHttpClient build = new OkHttpClient.Builder().cookieJar(new CookieJar() {
            public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
            }

            public List<Cookie> loadForRequest(HttpUrl httpUrl) {
                return cookies;
            }
        }).build();
        FormBody.Builder add = new FormBody.Builder().add("ig_sig_key_version", "4");
        StringBuilder sb5 = new StringBuilder();
        sb5.append(str2);
        sb5.append(str3);
        sb5.append(jsonString);
        FormBody build2 = add.add("signed_body", sb5.toString()).build();
        String str4 = "https://i.instagram.com/api/v1/feed/reels_media/";
        PrintStream printStream4 = System.out;
        StringBuilder sb6 = new StringBuilder();
        sb6.append("url_Feed :");
        sb6.append(str4);
        printStream4.println(sb6.toString());

        Log.e("Body--)", "" + build2);
        build.newCall(new Request.Builder().url(str4).post(build2).addHeader("User-Agent", Constants.USER_AGENT).addHeader("Connection", "close").addHeader("Accept-Language", "en-US").addHeader("language", "en").addHeader("Accept", "*/*").addHeader("Content-type", "application/x-www-form-urlencoded; charset=UTF-8").build()).enqueue(new Callback() {
            public void onFailure(Call call, IOException iOException) {
                runOnUiThread(new Runnable() {
                    @SuppressLint("WrongConstant")
                    public void run() {
                        upload_progress.setVisibility(4);
                    }
                });
            }

            public void onResponse(Call call, Response response) throws IOException {
                Callback r12;
                String strstr = null;
                String str2;
                String str3;
                String str4;
                Callback r1 = this;
                String str5 = "video_versions";
                String str6 = "caption";
                String str7 = "%.0f";

                if (response != null) {
                    try {
                        String string = response.body().string();
                        Log.e("response--)", "" + string);
                        hilightstory_data = new ArrayList<>();
                        PrintStream printStream = System.out;
                        StringBuilder sb = new StringBuilder();
                        sb.append("Heightlight  story_response-->");
                        sb.append(string);
                        printStream.println(sb.toString());
                        JSONObject jSONObject = new JSONObject(string).getJSONObject("reels").getJSONObject(str);
                        JSONObject jSONObject2 = jSONObject.getJSONObject("user");
                        String string2 = jSONObject2.getString("username");
                        String string3 = jSONObject2.getString("profile_pic_url");
                        JSONArray jSONArray = jSONObject.getJSONArray("items");
                        int i = 0;
                        while (i < jSONArray.length()) {
                            Story_setter story_setter = new Story_setter();
                            JSONObject jSONObject3 = jSONArray.getJSONObject(i);
                            String string4 = jSONObject3.getString("taken_at");
                            String str8 = "";
                            if (!jSONObject3.isNull(str6)) {
                                strstr = jSONObject3.getJSONObject(str6).getString("text");
                                PrintStream printStream2 = System.out;
                                StringBuilder sb2 = new StringBuilder();
                                sb2.append("text : ");
                                sb2.append(str);
                                printStream2.println(sb2.toString());
                            } else {
                                strstr = str8;
                            }
                            String string5 = jSONObject3.getString("code");
                            String string6 = jSONObject3.getString("original_width");
                            String string7 = jSONObject3.getString("original_height");
                            String str9 = str6;
                            JSONArray jSONArray2 = jSONArray;
                            String replace = jSONObject3.getString("id").replace("_", str8);
                            if (file_ptef == 0) {
                                str2 = replace;
                            } else if (file_ptef == 1) {
                                StringBuilder sb3 = new StringBuilder();
                                sb3.append(USER_NAME);
                                sb3.append(replace);
                                str2 = sb3.toString();
                            } else {
                                StringBuilder sb4 = new StringBuilder();
                                sb4.append(replace);
                                sb4.append(USER_NAME);
                                str2 = sb4.toString();
                            }
                            boolean has = jSONObject3.has(str5);
                            int i2 = i;
                            String str10 = "width";
                            String str11 = "height";
                            String str12 = str7;
                            String str13 = "candidates";
                            String str14 = string4;
                            String str15 = "image_versions2";
                            String str16 = string3;
                            String str17 = "url";
                            String str18 = string2;
                            String str19 = " ";
                            if (has) {
                                try {
                                    JSONArray jSONArray3 = jSONObject3.getJSONObject(str15).getJSONArray(str13);
                                    String string8 = jSONArray3.getJSONObject(0).getString(str17);
                                    String str20 = str19;
                                    String string9;
                                    if (jSONArray3.length() > 1) {
                                        string9 = jSONArray3.getJSONObject(1).getString(str17);
                                    } else {
                                        string9 = jSONArray3.getJSONObject(0).getString(str17);
                                    }
                                    String string10 = jSONObject3.getJSONArray(str5).getJSONObject(0).getString(str17);
                                    story_setter.setimageheight(jSONArray3.getJSONObject(0).getString(str11));
                                    story_setter.setimagewidth(jSONArray3.getJSONObject(0).getString(str10));
                                    story_setter.setimageurl(string8);
                                    story_setter.setvideourl(string10);
                                    story_setter.setThimbnail_url(string9);
                                    story_setter.setCode_HdUrl(string5);
                                    story_setter.setOriginal_height(string7);
                                    story_setter.setOriginal_width(string6);
                                    story_setter.setMedia_title(str2);
                                    story_setter.setMedia_id(replace);
                                    story_setter.setCaption_text(str);
                                    str3 = str18;
                                    str4 = str20;
                                } catch (JSONException e) {

                                    Log.e("JSONException--)", "" + e.getMessage());
                                    e = e;
                                    r12 = this;
                                    e.printStackTrace();
                                    runOnUiThread(new Runnable() {
                                        @SuppressLint("WrongConstant")
                                        public void run() {
                                            if (hilightstory_data != null && hilightstory_data.size() > 0) {
                                                showGridstory();
                                                try {
                                                    upload_progress.setVisibility(4);
                                                } catch (Exception unused) {
                                                } catch (Throwable th) {
                                                    upload_progress.setVisibility(4);
                                                    throw th;
                                                }
                                                upload_progress.setVisibility(4);
                                            }
                                        }
                                    });
                                    return;
                                }
                            } else {
                                String str21 = str19;
                                JSONArray jSONArray4 = jSONObject3.getJSONObject(str15).getJSONArray(str13);
                                String string11 = jSONArray4.getJSONObject(0).getString(str17);
                                String string12;
                                if (jSONArray4.length() > 1) {
                                    string12 = jSONArray4.getJSONObject(1).getString(str17);
                                } else {
                                    string12 = jSONArray4.getJSONObject(0).getString(str17);
                                }
                                story_setter.setimageheight(jSONArray4.getJSONObject(0).getString(str11));
                                story_setter.setimagewidth(jSONArray4.getJSONObject(0).getString(str10));
                                story_setter.setimageurl(string11);
                                str4 = str21;
                                story_setter.setvideourl(str4);
                                story_setter.setThimbnail_url(string12);
                                story_setter.setCode_HdUrl(string5);
                                story_setter.setOriginal_height(string7);
                                story_setter.setOriginal_width(string6);
                                story_setter.setMedia_title(str2);
                                story_setter.setMedia_id(replace);
                                story_setter.setCaption_text(str);
                                str3 = str18;
                            }
                            story_setter.setUserName(str3);
                            String str22 = str16;
                            story_setter.setProfile_picture(str22);
                            double longValue = (double) (Long.valueOf(System.currentTimeMillis() / 1000).longValue() - Long.valueOf(Long.parseLong(str14)).longValue());
                            double d = longValue / 3600.0d;
                            String.format("%.2f", new Object[]{Double.valueOf(d)});
                            String str23 = str12;
                            String format = String.format(str23, new Object[]{Double.valueOf(d)});
                            story_setter.setGetDate(str14);
                            double d2 = d / 24.0d;
                            String format2 = String.format(str23, new Object[]{Double.valueOf(d2)});
                            if (d2 > 30.0d) {
                                double d3 = d2 / 30.0d;
                                String format3 = String.format(str23, new Object[]{Double.valueOf(d3)});
                                if (d3 >= 1.5d) {
                                    StringBuilder sb5 = new StringBuilder();
                                    sb5.append(format3);
                                    sb5.append(str4);
                                    r12 = this;
                                    sb5.append(getResources().getString(R.string.months_ago));
                                    story_setter.setTaken_at(sb5.toString());
                                } else {
                                    r12 = this;
                                    StringBuilder sb6 = new StringBuilder();
                                    sb6.append(format3);
                                    sb6.append(str4);
                                    sb6.append(getResources().getString(R.string.month_ago));
                                    story_setter.setTaken_at(sb6.toString());
                                }
                            } else {
                                r12 = this;
                                if (d2 > 7.0d) {
                                    double d4 = d2 / 7.0d;
                                    String format4 = String.format(str23, new Object[]{Double.valueOf(d4)});
                                    if (d4 >= 1.5d) {
                                        StringBuilder sb7 = new StringBuilder();
                                        sb7.append(format4);
                                        sb7.append(str4);
                                        sb7.append(getResources().getString(R.string.weeks_ago));
                                        story_setter.setTaken_at(sb7.toString());
                                    } else {
                                        StringBuilder sb8 = new StringBuilder();
                                        sb8.append(format4);
                                        sb8.append(str4);
                                        sb8.append(getResources().getString(R.string.week_ago));
                                        story_setter.setTaken_at(sb8.toString());
                                    }
                                } else if (d > 24.0d) {
                                    StringBuilder sb9 = new StringBuilder();
                                    sb9.append(format2);
                                    sb9.append(str4);
                                    sb9.append(getResources().getString(R.string.days_ago));
                                    story_setter.setTaken_at(sb9.toString());
                                } else if (d > 2.0d) {
                                    StringBuilder sb10 = new StringBuilder();
                                    sb10.append(format);
                                    sb10.append(str4);
                                    sb10.append(getResources().getString(R.string.hours_ago));
                                    story_setter.setTaken_at(sb10.toString());
                                } else if (d > 1.0d) {
                                    StringBuilder sb11 = new StringBuilder();
                                    sb11.append(format);
                                    sb11.append(str4);
                                    sb11.append(getResources().getString(R.string.hour_ago));
                                    story_setter.setTaken_at(sb11.toString());
                                } else {
                                    double d5 = longValue / 60.0d;
                                    String format5 = String.format(str23, new Object[]{Double.valueOf(d5)});
                                    if (d5 > 2.0d) {
                                        StringBuilder sb12 = new StringBuilder();
                                        sb12.append(format5);
                                        sb12.append(str4);
                                        sb12.append(getResources().getString(R.string.minutes_ago));
                                        story_setter.setTaken_at(sb12.toString());
                                    } else {
                                        story_setter.setTaken_at(getResources().getString(R.string.few_moments_ago));
                                    }
                                    hilightstory_data.add(story_setter);
                                    jSONArray = jSONArray2;
                                    i = i2 + 1;
                                    string2 = str3;
                                    string3 = str22;
                                    str7 = str23;
                                    r1 = r12;
                                    str6 = str9;
                                }
                            }
                            hilightstory_data.add(story_setter);
                            jSONArray = jSONArray2;
                            i = i2 + 1;
                            string2 = str3;
                            string3 = str22;
                            str7 = str23;
                            r1 = r12;
                            str6 = str9;
                        }
                    } catch (JSONException e) {

                        Log.e("Exce--)", "" + e.getMessage());
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @SuppressLint("WrongConstant")
                            public void run() {
                                if (hilightstory_data != null && hilightstory_data.size() > 0) {
                                    showGridstory();
                                    try {
                                        upload_progress.setVisibility(4);
                                    } catch (Exception unused) {
                                    } catch (Throwable th) {
                                        upload_progress.setVisibility(4);
                                        throw th;
                                    }
                                    upload_progress.setVisibility(4);
                                }
                            }
                        });
                        return;
                    }
                    runOnUiThread(new Runnable() {
                        @SuppressLint("WrongConstant")
                        public void run() {
                            if (hilightstory_data != null && hilightstory_data.size() > 0) {
                                showGridstory();
                                try {
                                    upload_progress.setVisibility(4);
                                } catch (Exception unused) {
                                } catch (Throwable th) {
                                    upload_progress.setVisibility(4);
                                    throw th;
                                }
                                upload_progress.setVisibility(4);
                            }
                        }
                    });
                    return;
                }
                Callback r122 = r1;
            }
        });
    }

    private String getJsonString(HeighLightParameter heighLightParameter) {
        return new Gson().toJson((Object) heighLightParameter);
    }

    public static String hmacSha256(String str, String str2) {
        String str3 = "HmacSHA256";
        try {
            Mac instance = Mac.getInstance(str3);
            instance.init(new SecretKeySpec(str.getBytes(), str3));
            return toHexString(instance.doFinal(str2.getBytes()));
        } catch (Exception unused) {
            return null;
        }
    }

    private static String toHexString(byte[] bArr) {
        Formatter formatter = new Formatter();
        for (byte valueOf : bArr) {
            formatter.format("%02x", new Object[]{Byte.valueOf(valueOf)});
        }
        return formatter.toString();
    }

    @SuppressLint("WrongConstant")
    public void showGridstory() {
        if (hilightstory_data.size() < 1) {
            upload_progress.setVisibility(4);
            no_storyfound.setVisibility(0);
        }
        popupCheck = true;
        storyshowerRecyclerAdapter = new HilightshowerRecyclerviewAdapter(hilightstory_data, this, aq, user_FULLNAME);
        recyclerView.setAdapter(storyshowerRecyclerAdapter);
        RecyclerView recyclerView2 = recyclerView;
        recyclerView2.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView2, new RecyclerItemClickListener.OnItemClickListener() {
            public void onItemClick(View view, int i) {
                if (isMultiSelect) {
                    multi_select(i);
                }
            }

            public void onItemLongClick(View view, int i) {
                if (!isMultiSelect) {
                    multiselect_list = new ArrayList<>();
                    isMultiSelect = true;

                    linearDownload.setVisibility(View.VISIBLE);
                    txtDownload.setVisibility(View.VISIBLE);
                    txtProgress.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    rl_download.setVisibility(View.VISIBLE);
                }
                multi_select(i);
                if (!isMultiSelect) {
                    multiselect_list = new ArrayList<>();
                    isMultiSelect = true;
                }
                HeighLightStory_shower.highshowtickstory = true;
                if (storyshowerRecyclerAdapter != null) {
                    storyshowerRecyclerAdapter.notifyDataSetChanged();
                }
            }
        }));
    }

    @SuppressLint("WrongConstant")
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            if (highshowtickstory) {
                rl_download.setVisibility(8);
                highshowtickstory = false;
                HilightshowerRecyclerviewAdapter hilightshowerRecyclerviewAdapter = storyshowerRecyclerAdapter;
                if (hilightshowerRecyclerviewAdapter != null) {
                    hilightshowerRecyclerviewAdapter.notifyDataSetChanged();
                }
                ArrayList<Story_setter> arrayList = multiselect_list;
                if (arrayList != null) {
                    arrayList.clear();
                }
                ArrayList<Long> arrayList2 = list;
                if (arrayList2 != null) {
                    arrayList2.clear();
                    counter = 0;
                }
                for (int i = 0; i < hilightstory_data.size(); i++) {
                    ((Story_setter) hilightstory_data.get(i)).setCheckmultiple(false);
                }
            } else {
                hilightstoryregister = true;
                finish();
            }
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public boolean isNetworkAvailable() {
        @SuppressLint("WrongConstant") NetworkInfo activeNetworkInfo = ((ConnectivityManager) getSystemService("connectivity")).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @SuppressLint("WrongConstant")
    public void onBackPressed() {
        if (highshowtickstory) {
            rl_download.setVisibility(8);
            highshowtickstory = false;
            HilightshowerRecyclerviewAdapter hilightshowerRecyclerviewAdapter = storyshowerRecyclerAdapter;
            if (hilightshowerRecyclerviewAdapter != null) {
                hilightshowerRecyclerviewAdapter.notifyDataSetChanged();
            }
            ArrayList<Story_setter> arrayList = multiselect_list;
            if (arrayList != null) {
                arrayList.clear();
            }
            ArrayList<Long> arrayList2 = list;
            if (arrayList2 != null) {
                arrayList2.clear();
                counter = 0;
            }
            for (int i = 0; i < hilightstory_data.size(); i++) {
                ((Story_setter) hilightstory_data.get(i)).setCheckmultiple(false);
            }
            return;
        }

        hilightstoryregister = true;
        finish();
    }

    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (i != 1 || iArr.length <= 0) {
            if (i != 2 || iArr[0] != 0) {
                if (i != 3 || iArr[0] != 0) {
                    finish();
                }
            }
        } else if (iArr[0] != 0) {
        } else {
            if (permissionmenu) {
                downloadAllMedia();
            } else {
                downloadMultipleStory();
            }
        }
    }

    private void setForceShowIcon(PopupMenu popupMenu) {
        Field[] declaredFields;
        try {
            for (Field field : popupMenu.getClass().getDeclaredFields()) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object obj = field.get(popupMenu);
                    Class.forName(obj.getClass().getName()).getMethod("setForceShowIcon", new Class[]{Boolean.TYPE}).invoke(obj, new Object[]{Boolean.valueOf(true)});
                    return;
                }
            }
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    /* access modifiers changed from: private */
    @SuppressLint("WrongConstant")
    public void downloadMultipleStory() {
        if (highshowtickstory) {
//            rl_download.setVisibility(8);
            highshowtickstory = false;
            storyshowerRecyclerAdapter.notifyDataSetChanged();
            for (int i = 0; i < hilightstory_data.size(); i++) {
                ((Story_setter) hilightstory_data.get(i)).setCheckmultiple(false);
            }
        }
        list.clear();

        startDownloadMulti(0);
    }

    private void startDownloadMulti(int pos) {
        posOfDownload = pos;


        Log.e("Status--)", "" + multiselect_list.get(pos).getvideourl() + "--)" + multiselect_list.get(pos).getimageurl());

        String url = multiselect_list.get(pos).getvideourl();

        if (url.matches(" ") || url == null || url.isEmpty()) {
            new DownloadMultiImage(HeighLightStory_shower.this).execute(multiselect_list.get(pos).getimageurl());
        } else {
            new DownloadMultiVideo(HeighLightStory_shower.this).execute(multiselect_list.get(pos).getvideourl());
        }


    }

    private class DownloadMultiImage extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadMultiImage(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                int fileLength = connection.getContentLength();

                input = connection.getInputStream();

                String strFileName = System.currentTimeMillis() + Constant.IMAGE_EXTENTION;
                String ext = strFileName.substring(strFileName.indexOf(".") + 1);
                File repostfile;
                File file1 = new File(getApplicationContext().getExternalFilesDir(getApplicationContext().getResources().getString(R.string.app_name)).toString());
                if (!file1.exists()) {
                    file1.mkdirs();
                }
                repostfile = new File(file1, strFileName);
                dnlUrl = repostfile.getAbsolutePath();
                output = new FileOutputStream(repostfile);
                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
//            progressDailog.showDailog();

        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false

            Log.e("Progress--)", "" + progress[0]);
            setPr(progress[0]);

        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();

            if (result != null) {
                Toast.makeText(context, "Download Error", Toast.LENGTH_SHORT).show();
            } else {

                scanFile(dnlUrl);
                setPr(100);
                File file1 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), getResources().getString(R.string.app_name));
                if (!file1.exists()) {
                    file1.mkdirs();
                }
                File repostfile = new File(file1, new File(dnlUrl).getName());

                moveFile(dnlUrl, repostfile.getAbsolutePath());
                savePath = repostfile.getAbsolutePath();
                try {
                    if (Build.VERSION.SDK_INT >= 19) {
                        MediaScannerConnection.scanFile(context, new String[]
                                        {savePath},
                                null, (path, uri) -> {

                                });
                    } else {
                        context.sendBroadcast(new Intent("android.intent.action.MEDIA_MOUNTED",
                                Uri.fromFile(new File(savePath))));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.e("Done--)", "Compl--)" + dnlUrl);

                if (multiselect_list.size() > 0) {


                    posOfDownload++;

                    if (posOfDownload < multiselect_list.size()) {

                        startDownloadMulti(posOfDownload);
                    }

                }

            }


        }
    }

    private void moveFile(String inputPath, String outputPath) {
        InputStream in = null;
        OutputStream out = null;
        try {

            in = new FileInputStream(inputPath);
            out = new FileOutputStream(outputPath);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
            new File(inputPath).delete();
            scanFile(outputPath);
        } catch (FileNotFoundException fnfe1) {
        } catch (Exception e) {
        }
    }

    private class DownloadMultiVideo extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadMultiVideo(Context context) {
            this.context = context;


        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                int fileLength = connection.getContentLength();
                input = connection.getInputStream();

                String strFileName = System.currentTimeMillis() + Constant.VIDEO_EXTENTION;


                String ext = strFileName.substring(strFileName.indexOf(".") + 1);
                File repostfile;
                File file1 = new File(getApplicationContext().getExternalFilesDir(getApplicationContext().getResources().getString(R.string.app_name)).toString());
                if (!file1.exists()) {
                    file1.mkdirs();
                }
                repostfile = new File(file1, strFileName);

                dnlUrl = repostfile.getAbsolutePath();
                output = new FileOutputStream(repostfile);
                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
//            progressDailog.showDailog();

        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false

            Log.e("Progress--)", "" + progress[0]);


            setPr(progress[0]);

        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();

            if (result != null) {
                Toast.makeText(context, "Download Error", Toast.LENGTH_SHORT).show();
            } else {

                scanFile(dnlUrl);
                setPr(100);
                Log.e("Done--)", "Compl--)" + dnlUrl);
                File file1 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), getResources().getString(R.string.app_name));
                if (!file1.exists()) {
                    file1.mkdirs();
                }
                File repostfile = new File(file1, new File(dnlUrl).getName());

                moveFile(dnlUrl, repostfile.getAbsolutePath());
                savePath = repostfile.getAbsolutePath();
                try {
                    if (Build.VERSION.SDK_INT >= 19) {
                        MediaScannerConnection.scanFile(context, new String[]
                                        {savePath},
                                null, (path, uri) -> {

                                });
                    } else {
                        context.sendBroadcast(new Intent("android.intent.action.MEDIA_MOUNTED",
                                Uri.fromFile(new File(savePath))));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (multiselect_list.size() > 0) {
                    posOfDownload++;
                    if (posOfDownload < multiselect_list.size()) {

                        startDownloadMulti(posOfDownload);
                    }

                }
            }
        }
    }

    public void setPr(int progress) {
        if (progress == 100) {
            progressBar.setProgress(progress);
            txtProgress.setText("Completed");
            ib_im_download.setEnabled(true);
        } else {
            progressBar.setProgress(progress);
            ib_im_download.setEnabled(false);
            txtProgress.setText("Downloaded.." + progress + "%");
        }
    }

    public long getDurationDialog(File file) {
        long timeInMillisec = 0;
        try {

            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(file.getPath());

            String duration = retriever
                    .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

            timeInMillisec = Long.parseLong(duration);

            return timeInMillisec;
        } catch (Exception e) {

        }
        return timeInMillisec;

    }

    private void scanFile(String path) {
        MediaScannerConnection.scanFile(getApplicationContext(),
                new String[]{path}, null,
                (path1, uri) -> Log.e("TAG--)", "Finished scanning " + path1));
    }

    /* access modifiers changed from: private */
    public void downloadVideoAndImageAll(ArrayList<Story_setter> arrayList, int i) {
        if (i < arrayList.size()) {
            if (((Story_setter) arrayList.get(i)).getvideourl().equalsIgnoreCase(" ")) {
                fullVersionUrlNew(arrayList, i);
            } else {
                String str = ((Story_setter) arrayList.get(i)).getvideourl();
                if (str != null) {
                    String media_title = (arrayList.get(i)).getMedia_title();
                    new AllMultiDownloadManager(HeighLightStory_shower.this, str.trim(), "", media_title + Constant.VIDEO_EXTENTION, new AppInterface.OnDownloadStarted() {
                        @Override
                        public void onDownloadStarted(long requestId) {
                            try {
                                refid = requestId;
                                list.add(Long.valueOf(refid));
                                downloadVideoAndImageAll(arrayList, i + 1);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        }
    }

    private void fullVersionUrlNew(ArrayList<Story_setter> arrayList, int i) {
        final String str = ((Story_setter) arrayList.get(i)).getimageurl();
        String code_HdUrl = ((Story_setter) arrayList.get(i)).getCode_HdUrl();
        if (str != null) {
            final String media_title = ((Story_setter) arrayList.get(i)).getMedia_title();
            StringBuilder sb = new StringBuilder();
            sb.append("https://www.instagram.com/p/");
            sb.append(code_HdUrl);
            sb.append("/?__a=1");
            Call newCall = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
                public Response intercept(Chain chain) throws IOException {
                    return chain.proceed(chain.request().newBuilder().header("User-Agent", Constants.USER_AGENT).header("Connection", "close").header("language", "en").header("Accept", "*/*").header("X-IG-Capabilities", "3QI=").header("Content-type", "application/x-www-form-urlencoded; charset=UTF-8").build());
                }
            }).cookieJar(new CookieJar() {
                public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
                }

                public List<Cookie> loadForRequest(HttpUrl httpUrl) {
                    return cookies;
                }
            }).build().newCall(new Request.Builder().url(sb.toString()).build());
            final ArrayList<Story_setter> arrayList2 = arrayList;
            final int i2 = i;
            Callback r1 = new Callback() {
                public void onFailure(Call call, IOException iOException) {
                    final String message = iOException.getMessage();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            PrintStream printStream = System.out;
                            StringBuilder sb = new StringBuilder();
                            sb.append("onFailure : ");
                            sb.append(message);
                            printStream.println(sb.toString());
                            download_url = str;
                            downloadMediaAllnew(download_url, media_title);
                            downloadVideoAndImageAll(arrayList2, i2 + 1);
                        }
                    });
                }

                public void onResponse(Call call, Response response) throws IOException {
                    final String str = "display_resources";
                    String str2 = "shortcode_media";
                    String str3 = "graphql";
                    runOnUiThread(new Runnable() {
                        public void run() {
                        }
                    });
                    if (response.isSuccessful()) {
                        String str4 = new String(response.body().string());
                        PrintStream printStream = System.out;
                        StringBuilder sb = new StringBuilder();
                        sb.append("response fullversion = :");
                        sb.append(str4);
                        printStream.println(sb.toString());
                        try {
                            if (!isFinishing()) {
                                JSONObject jSONObject = new JSONObject(new String(str4));
                                if (jSONObject.has(str3)) {
                                    JSONObject jSONObject2 = jSONObject.getJSONObject(str3);
                                    if (jSONObject2.has(str2)) {
                                        JSONObject jSONObject3 = jSONObject2.getJSONObject(str2);
                                        if (jSONObject3.has(str)) {
                                            JSONArray jSONArray = jSONObject3.getJSONArray(str);
                                            if (jSONArray.length() > 0) {
                                                JSONObject jSONObject4 = jSONArray.getJSONObject(jSONArray.length() - 1);
                                                download_url = jSONObject4.getString("src");
                                                PrintStream printStream2 = System.out;
                                                StringBuilder sb2 = new StringBuilder();
                                                sb2.append("download_url : ");
                                                sb2.append(download_url);
                                                printStream2.println(sb2.toString());
                                            } else {
                                                download_url = str;
                                            }
                                        } else {
                                            download_url = str;
                                        }
                                    } else {
                                        download_url = str;
                                    }
                                } else {
                                    download_url = str;
                                }
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        downloadMediaAllnew(download_url, media_title);
                                        PrintStream printStream = System.out;
                                        StringBuilder sb = new StringBuilder();
                                        sb.append("download_url 1281 : ");
                                        sb.append(download_url);
                                        printStream.println(sb.toString());
                                        downloadVideoAndImageAll(arrayList2, i2 + 1);
                                    }
                                });
                                PrintStream printStream3 = System.out;
                                StringBuilder sb3 = new StringBuilder();
                                sb3.append("display_url : ");
                                sb3.append(download_url);
                                printStream3.println(sb3.toString());
                                return;
                            }
                            runOnUiThread(new Runnable() {
                                @SuppressLint("WrongConstant")
                                public void run() {
                                    if (!isNetworkAvailable() && HeighLightStory_shower.this != null) {
                                        Toast.makeText(HeighLightStory_shower.this, getResources().getString(R.string.internet_connection), 0).show();
                                    }
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                download_url = str;
                                downloadMediaAllnew(download_url, media_title);
                                PrintStream printStream = System.out;
                                StringBuilder sb = new StringBuilder();
                                sb.append("download_url 1307 : ");
                                sb.append(download_url);
                                printStream.println(sb.toString());
                                downloadVideoAndImageAll(arrayList2, i2 + 1);
                            }
                        });
                    }
                }
            };
            newCall.enqueue(r1);
        }
    }


    public void downloadMediaAllnew(String str, String str2) {
        new AllMultiDownloadManager(HeighLightStory_shower.this, str, "", str2 + Constant.IMAGE_EXTENTION, new AppInterface.OnDownloadStarted() {
            @Override
            public void onDownloadStarted(long requestId) {
                refid = requestId;
                list.add(Long.valueOf(refid));
            }
        });
    }

    public void writeToFile(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.DIRECTORY_DCIM);
        sb.append("/testingFile/");
        File externalStoragePublicDirectory = Environment.getExternalStoragePublicDirectory(sb.toString());
        if (!externalStoragePublicDirectory.exists()) {
            externalStoragePublicDirectory.mkdirs();
        }
        File file = new File(externalStoragePublicDirectory, "hilight.txt");
        try {
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            outputStreamWriter.append(str);
            outputStreamWriter.close();
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("File write failed: ");
            sb2.append(e.toString());
            Log.e("Exception", sb2.toString());
        }
    }

    @SuppressLint("WrongConstant")
    public void downloadAllMedia() {
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStorageDirectory().getPath());
        sb.append("/All Status Downloader");
        alldownloadPath = sb.toString();
        multiselect_list = new ArrayList<>();
        for (int i = 0; i < hilightstory_data.size(); i++) {
            ArrayList<Story_setter> arrayList = hilightstory_data;
            if (!isFilepath(((Story_setter) arrayList.get((arrayList.size() - 1) - i)).getMedia_id())) {
                ArrayList<Story_setter> arrayList2 = multiselect_list;
                ArrayList<Story_setter> arrayList3 = hilightstory_data;
                arrayList2.add(arrayList3.get((arrayList3.size() - 1) - i));
            }
        }
        ArrayList<Story_setter> arrayList4 = multiselect_list;
        if (arrayList4 == null || arrayList4.size() <= 0) {
            Toast.makeText(this, getResources().getString(R.string.select_media), 0).show();
        } else {
            downloadMultipleStory();
        }
    }

    public void multi_select(int i) {

        if (multiselect_list.contains(hilightstory_data.get(i))) {

            hilightstory_data.get(i).setCheckmultiple(false);
            multiselect_list.remove(hilightstory_data.get(i));
        } else {

            hilightstory_data.get(i).setCheckmultiple(true);
            multiselect_list.add(hilightstory_data.get(i));
        }
        if (multiselect_list.size() == 0) {
            isMultiSelect = false;
            multiselect_list.clear();
        }

        refreshAdapter(i);

        if (multiselect_list.size() == 0) {
            rl_download.setVisibility(View.GONE);

            isMultiSelect = false;
        } else {

            rl_download.setVisibility(View.VISIBLE);
            Log.e("Mutilple--)", "" + multiselect_list.size());
            txtDownload.setText("Download (" + multiselect_list.size() + ")");
        }

    }

    private void refreshAdapter(int i) {
        HilightshowerRecyclerviewAdapter hilightshowerRecyclerviewAdapter = storyshowerRecyclerAdapter;
        if (hilightshowerRecyclerviewAdapter != null) {
            hilightshowerRecyclerviewAdapter.notifyItemChanged(i);
        }
    }

    public boolean isFilepath(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStorageDirectory().getPath());
        sb.append("/All Status Downloader");
        alldownloadPath = sb.toString();
        StringBuilder sb2 = new StringBuilder();
        sb2.append(str);
        String str2 = ".mp4";
        sb2.append(str2);
        String sb3 = sb2.toString();
        StringBuilder sb4 = new StringBuilder();
        sb4.append(str);
        String str3 = ".jpg";
        sb4.append(str3);
        String sb5 = sb4.toString();
        StringBuilder sb6 = new StringBuilder();
        sb6.append(alldownloadPath);
        String str4 = "/";
        sb6.append(str4);
        sb6.append(sb3);
        File file = new File(sb6.toString());
        StringBuilder sb7 = new StringBuilder();
        sb7.append(alldownloadPath);
        sb7.append(str4);
        sb7.append(sb5);
        File file2 = new File(sb7.toString());
        StringBuilder sb8 = new StringBuilder();
        sb8.append(str);
        sb8.append(USER_NAME);
        String sb9 = sb8.toString();
        StringBuilder sb10 = new StringBuilder();
        sb10.append(sb9);
        sb10.append(str2);
        String sb11 = sb10.toString();
        StringBuilder sb12 = new StringBuilder();
        sb12.append(sb9);
        sb12.append(str3);
        String sb13 = sb12.toString();
        StringBuilder sb14 = new StringBuilder();
        sb14.append(alldownloadPath);
        sb14.append(str4);
        sb14.append(sb11);
        File file3 = new File(sb14.toString());
        StringBuilder sb15 = new StringBuilder();
        sb15.append(alldownloadPath);
        sb15.append(str4);
        sb15.append(sb13);
        File file4 = new File(sb15.toString());
        StringBuilder sb16 = new StringBuilder();
        sb16.append(USER_NAME);
        sb16.append(str);
        String sb17 = sb16.toString();
        StringBuilder sb18 = new StringBuilder();
        sb18.append(sb17);
        sb18.append(str2);
        String sb19 = sb18.toString();
        StringBuilder sb20 = new StringBuilder();
        sb20.append(sb17);
        sb20.append(str3);
        String sb21 = sb20.toString();
        StringBuilder sb22 = new StringBuilder();
        sb22.append(alldownloadPath);
        sb22.append(str4);
        sb22.append(sb19);
        File file5 = new File(sb22.toString());
        StringBuilder sb23 = new StringBuilder();
        sb23.append(alldownloadPath);
        sb23.append(str4);
        sb23.append(sb21);
        return file.exists() || file2.exists() || file3.exists() || file4.exists() || file5.exists() || new File(sb23.toString()).exists();
    }
}