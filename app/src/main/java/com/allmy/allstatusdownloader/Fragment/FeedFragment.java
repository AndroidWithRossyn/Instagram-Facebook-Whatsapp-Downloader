package com.allmy.allstatusdownloader.Fragment;

import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.allmy.allstatusdownloader.Activity.AlbumActivity;
import com.allmy.allstatusdownloader.Activity.CreationActivity;
import com.allmy.allstatusdownloader.Activity.ImageShower;
import com.allmy.allstatusdownloader.Activity.ShowAllData;
import com.allmy.allstatusdownloader.Activity.VideoShower;
import com.allmy.allstatusdownloader.Adapter.FeedDataAdapter;
import com.allmy.allstatusdownloader.Model.Album;
import com.allmy.allstatusdownloader.Model.AppInterface;
import com.allmy.allstatusdownloader.Model.Bean;
import com.allmy.allstatusdownloader.Model.Downloadlist;
import com.allmy.allstatusdownloader.Model.FeedOnBackPressed;
import com.allmy.allstatusdownloader.Others.AllMultiDownloadManager;
import com.allmy.allstatusdownloader.Others.Constant;
import com.allmy.allstatusdownloader.Others.Constants;
import com.allmy.allstatusdownloader.Others.DetectScrollToEnd;
import com.allmy.allstatusdownloader.Others.GridSpacingItemDecoration;
import com.allmy.allstatusdownloader.Others.RecyclerItemClickListener;
import com.allmy.allstatusdownloader.R;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import org.jetbrains.annotations.NotNull;
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
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FeedFragment extends Fragment implements FeedOnBackPressed {
    public static boolean showtick = false;
    String API_URL = "https://i.instagram.com/api/v1/";
    private Uri Download_Uri;
    SharedPreferences accountInfoPref;
    List<Album> arr_album;
    List<Downloadlist> arr_downloadlists;
    String baseurl = "https://i.instagram.com/api/v1/feed/timeline/";
    SharedPreferences cookiePref;
    List<Cookie> cookies;
    int counter = 0;
    String csrfTocken;
    SharedPreferences currentUser;
    String download_url;
    FeedDataAdapter feeddataadapter;
    int file_ptef;
    private GridLayoutManager gridLayoutManager;
    public ArrayList<Bean> imagePath;
    private boolean isFragmentLoadedfeed = false;
    boolean isMultiSelect = false;
    ArrayList<Long> list = new ArrayList<>();
    int listsize;
    SharedPreferences loginPref;
    public static ArrayList<Bean> multiselect_list = new ArrayList<>();
    public String next_url = null;
    RelativeLayout no_storyfound;
    boolean isCallReload = false;
    boolean permissionmenu = false;
    int position;
    RecyclerView recycler_feedlist;
    private long refid;
    String response_feed;
    RelativeLayout rl_download;
    CardView ib_im_download;
    ProgressBar progressBar;
    TextView txtProgress, txtDownload, txt_toolbar;
    LinearLayout linearDownload;
    String dnlUrl, savePath;
    int posOfDownload = 0;
    InterstitialAd mInterstitialAd;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    public View onCreateView(@NotNull LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {

        position = getArguments().getInt("position");

        InterstitialAd.load(getContext(), getString(R.string.interstitial_ID), new AdRequest.Builder().build(), new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                mInterstitialAd = interstitialAd;
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                mInterstitialAd = null;

            }
        });
        isMultiSelect = false;
        setHasOptionsMenu(true);
        if (getActivity() != null) {
            file_ptef = getActivity().getSharedPreferences("DOWNLOAD_FILE_NAME_PREF", 0).getInt("file_value", 0);
            PrintStream printStream = System.out;
            String sb = "file_ptef : " +
                    file_ptef;
            printStream.println(sb);
        } else {
            file_ptef = 0;
        }
        return layoutInflater.inflate(R.layout.show_feedfragment, viewGroup, false);
    }

    public void onViewCreated(@NotNull View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        getViewId(view);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener((view1, i, keyEvent) -> {
            if (i == 4 && keyEvent.getAction() == 1) {
                if (getActivity() == null || !FeedFragment.showtick) {
                    System.out.println("onBackPressed  else");
                } else {
                    rl_download.animate().alpha(0.0f).setDuration(200);
                    rl_download.setVisibility(View.GONE);
                    FeedFragment.showtick = false;
                    if (multiselect_list != null) {
                        multiselect_list.clear();
                    }
                    if (list != null) {
                        list.clear();
                        counter = 0;
                    }
                    if (imagePath != null && imagePath.size() > 0) {
                        for (int i2 = 0; i2 < imagePath.size(); i2++) {
                            imagePath.get(i2).setCheckmultiple(false);
                        }
                    }
                    if (feeddataadapter != null) {
                        feeddataadapter.notifyDataSetChanged();
                    }
                    return true;
                }
            }
            return false;
        });
    }

    @SuppressLint("WrongConstant")
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        ((ShowAllData) getActivity()).setFeedOnBackPressedListener(this);

        if (isNetworkAvailable()) {
            getLoginCookies();
        } else if (getActivity() != null) {
            Toast.makeText(getActivity(), getResources().getString(R.string.internet_connection), 0).show();
        }
    }

    private void getLoginCookies() {
        if (getActivity() != null) {
            String str = "CURRENT_USER";
            currentUser = getActivity().getSharedPreferences(str, 0);
            String string = currentUser.getString(str, null);
            FragmentActivity activity = getActivity();
            String sb = "LOGIN_" +
                    string.toLowerCase(Locale.getDefault()).trim();
            loginPref = activity.getSharedPreferences(sb, 0);
            FragmentActivity activity2 = getActivity();
            String sb2 = "COOKIE_PREF_" +
                    string.toLowerCase(Locale.getDefault()).trim();
            cookiePref = activity2.getSharedPreferences(sb2, 0);
            FragmentActivity activity3 = getActivity();
            String sb3 = "ACCOUNT_PREF_" +
                    string.toLowerCase(Locale.getDefault()).trim();
            accountInfoPref = activity3.getSharedPreferences(sb3, 0);
            cookies = new ArrayList();
            int i = cookiePref.getInt("cookie_count", -1);
            for (int i2 = 0; i2 < i; i2++) {
                PrintStream printStream = System.out;
                String str2 = "";
                String sb4 = "Cookies from pref : " +
                        cookiePref.getString(String.valueOf(i2), str2);
                printStream.println(sb4);
                try {
                    Cookie parse = Cookie.parse(HttpUrl.get(new URL("https://i.instagram.com/")), cookiePref.getString(String.valueOf(i2), str2));
                    cookies.add(parse);
                    if (parse.toString().contains("csrftoken")) {
                        csrfTocken = parse.toString().split(";")[0].split("=")[1];
                        PrintStream printStream2 = System.out;
                        String sb5 = "csrfTocken  :" +
                                csrfTocken;
                        printStream2.println(sb5);
                    }
                    PrintStream printStream3 = System.out;
                    String sb6 = "MYTestcookie  :" +
                            parse;
                    printStream3.println(sb6);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
            loadFeed("https://i.instagram.com/api/v1/feed/timeline");
        }
    }

    @SuppressLint("WrongConstant")
    private void getViewId(View view) {
        recycler_feedlist = view.findViewById(R.id.recycler_feedlist);
        rl_download = view.findViewById(R.id.rl_download);
        no_storyfound = view.findViewById(R.id.no_storyfound);

        ib_im_download = view.findViewById(R.id.ib_im_download);
        progressBar = view.findViewById(R.id.progressBar);
        txtProgress = view.findViewById(R.id.txtProgress);
        linearDownload = view.findViewById(R.id.linearDownload);
        txtDownload = view.findViewById(R.id.txtDownload);

        recycler_feedlist.addItemDecoration(new GridSpacingItemDecoration(3, 3, false));

        gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            public int getSpanSize(int i) {


                switch (feeddataadapter.getItemViewType(i)) {
                    case 22:
                        return 1;
                    case 11:
                        return 3;
                    default:
                        return 3;
                }
            }
        });
        recycler_feedlist.setLayoutManager(gridLayoutManager);

        ib_im_download.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (multiselect_list != null && multiselect_list.size() > 0) {

                    if (progressBar.getProgress() == 100) {

                        progressBar.setProgress(0);
                        linearDownload.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        txtProgress.setVisibility(View.GONE);

                        Intent intent = new Intent(getContext(), CreationActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    } else {

                        if (mInterstitialAd != null) {
                            mInterstitialAd.show(getActivity());

                            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    linearDownload.setVisibility(View.GONE);
                                    progressBar.setVisibility(View.VISIBLE);
                                    txtProgress.setVisibility(View.VISIBLE);
                                    downloadMedia();
                                }

                                @Override
                                public void onAdFailedToShowFullScreenContent(AdError adError) {
                                    linearDownload.setVisibility(View.GONE);
                                    progressBar.setVisibility(View.VISIBLE);
                                    txtProgress.setVisibility(View.VISIBLE);
                                    downloadMedia();
                                }

                                @Override
                                public void onAdShowedFullScreenContent() {
                                    mInterstitialAd = null;
                                }
                            });

                        } else {
                            linearDownload.setVisibility(View.GONE);
                            progressBar.setVisibility(View.VISIBLE);
                            txtProgress.setVisibility(View.VISIBLE);
                            downloadMedia();
                        }
                    }

                } else {
                    if (progressBar.getProgress() == 100) {
                        Intent intent = new Intent(getContext(), CreationActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                }


                Log.e("arr_downloadlists--)", "" + multiselect_list.size() + "--)" + progressBar.getProgress());


            }
        });

    }

    private void loadFeed(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append(API_URL);
        sb.append("feed/user/");
        sb.append(ShowAllData.USER_ID);
        sb.append("/?rank_token=");
        sb.append(getRankToken());
        sb.append("&ranked_content=true&");
        String sb2 = sb.toString();
        PrintStream printStream = System.out;
        StringBuilder sb3 = new StringBuilder();
        sb3.append("media_Url : ");
        sb3.append(sb2);
        printStream.println(sb3.toString());
        new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            public Response intercept(Chain chain) throws IOException {
                return chain.proceed(chain.request().newBuilder().header("User-Agent", Constants.USER_AGENT).header("Connection", "close").header("language", "en").header("Accept", "*/*").header("X-IG-Capabilities", "3QI=").header("Content-type", "application/x-www-form-urlencoded; charset=UTF-8").build());
            }
        }).cookieJar(new CookieJar() {
            public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
            }

            public List<Cookie> loadForRequest(HttpUrl httpUrl) {
                return cookies;
            }
        }).build().newCall(new Request.Builder().url(sb2).build()).enqueue(new Callback() {
            public void onFailure(Call call, IOException iOException) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
//                            progressBarfeed.setVisibility(0);
                        }
                    });
                }
            }

            @SuppressLint("WrongConstant")
            public void onResponse(Call call, Response response) throws IOException {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
//                            progressBarfeed.setVisibility(0);
                        }
                    });
                }
                response_feed = response.body().string();
                PrintStream printStream = System.out;
                StringBuilder sb = new StringBuilder();
                sb.append("response_feed : ");
                sb.append(response_feed);
                printStream.println(sb.toString());
                if (getActivity() != null) {
                    if (response_feed != null) {
                        String str = "message";
                        if (response_feed.contains(str) && !response_feed.contains("items")) {
                            try {
                                JSONObject jSONObject = new JSONObject(response_feed);
                                if (getActivity() != null) {
                                    final String string = jSONObject.getString(str);
                                    getActivity().runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(getActivity(), string + "::new", Toast.LENGTH_SHORT).show();
                                            PrintStream printStream = System.out;
                                            StringBuilder sb = new StringBuilder();
                                            sb.append("messages : ");
                                            sb.append(string);
                                            printStream.println(sb.toString());
//                                            progressBarfeed.setVisibility(4);
                                        }
                                    });
                                    if (string.contains("login_required")) {
                                        return;
                                    }
                                }
                                return;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    if (response_feed == null) {
                        Toast.makeText(getActivity(), "Please check your internet ", 0).show();
                    } else if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                showFeed(response_feed);
                            }
                        });
                    }
                }
            }
        });
    }

    private String getDate(long j) {
        Calendar instance = Calendar.getInstance(Locale.ENGLISH);
        instance.setTimeInMillis(j * 1000);
        int i = Calendar.getInstance().get(1);
        int parseInt = Integer.parseInt(DateFormat.format("yyyy", instance).toString());
        PrintStream printStream = System.out;
        StringBuilder sb = new StringBuilder();
        sb.append("postYear : currentYear : ");
        sb.append(parseInt);
        sb.append(":");
        sb.append(i);
        printStream.println(sb.toString());
        double longValue = (double) (Long.valueOf(System.currentTimeMillis() / 1000).longValue() - Long.valueOf(j).longValue());
        double d = longValue / 3600.0d;
        String str = "%.0f";
        String format = String.format(str, Double.valueOf(d));
        if (d <= 23.0d) {
            String str2 = " ";
            if (d > 2.0d) {
                if (getActivity() != null) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append(format);
                    sb2.append(str2);
                    sb2.append(getResources().getString(R.string.hours_ago));
                    return sb2.toString();
                }
            } else if (d <= 1.0d) {
                double d2 = longValue / 60.0d;
                String format2 = String.format(str, Double.valueOf(d2));
                if (d2 > 2.0d) {
                    if (getActivity() != null) {
                        StringBuilder sb3 = new StringBuilder();
                        sb3.append(format2);
                        sb3.append(str2);
                        sb3.append(getResources().getString(R.string.minutes_ago));
                        return sb3.toString();
                    }
                } else if (getActivity() != null) {
                    return getResources().getString(R.string.few_moments_ago);
                }
            } else if (getActivity() != null) {
                StringBuilder sb4 = new StringBuilder();
                sb4.append(format);
                sb4.append(str2);
                sb4.append(getResources().getString(R.string.hour_ago));
                return sb4.toString();
            }
            return null;
        } else if (i == parseInt) {
            return DateFormat.format("MMM dd", instance).toString();
        } else {
            return DateFormat.format("MMM dd yyyy", instance).toString();
        }
    }

    /* access modifiers changed from: private */
    @SuppressLint("WrongConstant")
    public void showFeed(String str) {
        String str2;
        JSONArray jSONArray;
        String str3;
        String str4;
        String str5;
        String str6;
        String str7 = null;
        String str8;
        String str9;
        String str10;
        String str11;
        String str12 = null;
        String str13 = null;
        String str14;
        String str15 = null;
        String str16 = null;
        String str17;
        String str18 = null;
        String str19;
        String str20;
        String str21;
        String str22;
        PrintStream printStream = null;
        StringBuilder sb = null;
        String str23;
        String str24 = str;
        String str25 = "comment_count";
        String str26 = "like_count";
        String str27 = "user";
        String str28 = "next_max_id";
        String str29 = "media_type";
        imagePath = new ArrayList<>();
        try {
            JSONObject jSONObject = new JSONObject(str24);
            JSONArray jSONArray2 = jSONObject.getJSONArray("items");
            if (!jSONObject.isNull(str28)) {
                next_url = jSONObject.getString(str28);
            } else {
                next_url = null;
            }
            String str30 = null;
            String str31 = null;
            int i = 0;
            while (i < jSONArray2.length()) {
                arr_album = new ArrayList();
                try {
                    JSONObject jSONObject2 = jSONArray2.getJSONObject(i);
                    String string = jSONObject2.getString("code");
                    String string2 = jSONObject2.getString("taken_at");
                    long parseLong = Long.parseLong(string2);
                    PrintStream printStream2 = System.out;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("dateString : ");
                    sb2.append(getDate(parseLong));
                    printStream2.println(sb2.toString());
                    String date = getDate(parseLong);
                    if (jSONObject2.has(str27)) {
                        String str32 = "0";
                        String string3 = jSONObject2.has(str26) ? jSONObject2.getString(str26) : str32;
                        String string4 = jSONObject2.has(str25) ? jSONObject2.getString(str25) : str32;
                        String string5 = jSONObject2.has("original_height") ? jSONObject2.getString("original_height") : str32;
                        String string6 = jSONObject2.has("original_width") ? jSONObject2.getString("original_width") : str32;
                        if (jSONObject2.has(str29)) {
                            str10 = jSONObject2.getString(str29);
                            str5 = str25;
                        } else {
                            str5 = str25;
                            str10 = str32;
                        }
                        try {
                            String string7 = jSONObject2.has("has_liked") ? jSONObject2.getString("has_liked") : str32;
                            String string8 = jSONObject2.has("device_timestamp") ? jSONObject2.getString("device_timestamp") : "00000";
                            JSONObject jSONObject3 = jSONObject2.getJSONObject(str27);
                            String string9 = jSONObject3.getString("full_name");
                            String string10 = jSONObject3.getString("username");
                            String string11 = jSONObject3.getString("pk");
                            str4 = str26;
                            str3 = str27;
                            try {
                                String replace = jSONObject2.getString("id").replace("_", "");
                                if (file_ptef == 0) {
                                    str11 = replace;
                                } else {
                                    if (file_ptef == 1) {
                                        StringBuilder sb3 = new StringBuilder();
                                        sb3.append(ShowAllData.USER_NAME);
                                        sb3.append(replace);
                                        str23 = sb3.toString();
                                    } else {
                                        StringBuilder sb4 = new StringBuilder();
                                        sb4.append(replace);
                                        sb4.append(ShowAllData.USER_NAME);
                                        str23 = sb4.toString();
                                    }
                                    str11 = str23;
                                }
                                String string12 = jSONObject3.getString("profile_pic_url");
                                if (!jSONObject2.isNull("caption")) {
                                    try {
                                        str12 = jSONObject2.getJSONObject("caption").getString("text");
                                        printStream = System.out;
                                        sb = new StringBuilder();
                                        jSONArray = jSONArray2;
                                    } catch (JSONException e) {
                                        e = e;
                                        jSONArray = jSONArray2;
                                        str2 = str29;
                                        str8 = str30;
                                        str6 = str31;
                                        e.printStackTrace();
                                        str30 = str7;
                                        str31 = str6;
                                        i++;
                                        String str33 = str;
                                        str25 = str5;
                                        str26 = str4;
                                        str27 = str3;
                                        jSONArray2 = jSONArray;
                                        str29 = str2;
                                    }
                                    sb.append("text : ");
                                    sb.append(str12);
                                    printStream.println(sb.toString());
                                } else {
                                    jSONArray = jSONArray2;
                                    str12 = "";
                                }
                                String str34 = str12;
                                boolean contains = str10.contains("8");
                                String str35 = "candidates";
                                String str36 = "image_versions2";
                                String str37 = "url";
                                if (contains) {
                                    str14 = "album";
                                    try {
                                        JSONArray jSONArray3 = jSONObject2.getJSONArray("carousel_media");
                                        str7 = str30;
                                        int i2 = 0;
                                        String str38 = null;
                                        String str39 = null;
                                        String str40 = null;
                                        while (i2 < jSONArray3.length()) {
                                            try {
                                                JSONObject jSONObject4 = jSONArray3.getJSONObject(i2);
                                                JSONArray jSONArray4 = jSONArray3;
                                                str6 = str31;
                                                try {
                                                    String str41 = jSONObject4.getString("id").split("_")[0];
                                                    if (file_ptef == 0) {
                                                        str19 = str41;
                                                    } else {
                                                        if (file_ptef == 1) {
                                                            StringBuilder sb5 = new StringBuilder();
                                                            sb5.append(ShowAllData.USER_NAME);
                                                            sb5.append(str41);
                                                            str22 = sb5.toString();
                                                        } else {
                                                            StringBuilder sb6 = new StringBuilder();
                                                            sb6.append(str41);
                                                            sb6.append(ShowAllData.USER_NAME);
                                                            str22 = sb6.toString();
                                                        }
                                                        str19 = str22;
                                                    }
                                                    if (jSONObject4.getString(str29) == ExifInterface.GPS_MEASUREMENT_2D) {
                                                        String str42 = "video";
                                                        String string13 = jSONObject4.getJSONArray("video_versions").getJSONObject(0).getString(str37);
                                                        if (i2 == 0) {
                                                            str38 = jSONObject4.getJSONObject(str36).getJSONArray(str35).getJSONObject(0).getString(str37);
                                                        }
                                                        str20 = str42;
                                                        str21 = str40;
                                                        str39 = string13;
                                                    } else {
                                                        String str43 = "image";
                                                        JSONObject jSONObject5 = jSONObject4.getJSONObject(str36).getJSONArray(str35).getJSONObject(0);
                                                        str21 = jSONObject5.getString(str37);
                                                        if (i2 == 0) {
                                                            str20 = str43;
                                                            str38 = jSONObject5.getString(str37);
                                                        } else {
                                                            str20 = str43;
                                                        }
                                                    }
                                                    Album album = new Album(str21, str39, str20, str41, str19);
                                                    arr_album.add(album);
                                                    i2++;
                                                    String str44 = str;
                                                    str40 = str21;
                                                    jSONArray3 = jSONArray4;
                                                    str31 = str6;
                                                } catch (JSONException e) {
                                                    str2 = str29;
                                                    e.printStackTrace();
                                                    str30 = str7;
                                                    str31 = str6;
                                                    i++;
                                                    String str3322 = str;
                                                    str25 = str5;
                                                    str26 = str4;
                                                    str27 = str3;
                                                    jSONArray2 = jSONArray;
                                                    str29 = str2;
                                                }
                                            } catch (JSONException e) {
                                                str6 = str31;
                                                str2 = str29;
                                                e.printStackTrace();
                                                str30 = str7;
                                                str31 = str6;
                                                i++;
                                                String str33222 = str;
                                                str25 = str5;
                                                str26 = str4;
                                                str27 = str3;
                                                jSONArray2 = jSONArray;
                                                str29 = str2;
                                            }
                                        }
                                        str2 = str29;
                                        str9 = string5;
                                        str16 = string6;
                                        str15 = str38;
                                        str13 = null;
                                    } catch (JSONException e) {
                                        str7 = str30;
                                        str6 = str31;
                                        str2 = str29;
                                        e.printStackTrace();
                                        str30 = str7;
                                        str31 = str6;
                                        i++;
                                        String str332222 = str;
                                        str25 = str5;
                                        str26 = str4;
                                        str27 = str3;
                                        jSONArray2 = jSONArray;
                                        str29 = str2;
                                    }
                                } else {
                                    str7 = str30;
                                    str6 = str31;
                                    String str45 = "width";
                                    String str46 = "height";
                                    if (str10.contains(ExifInterface.GPS_MEASUREMENT_2D)) {
                                        String str47 = "video";
                                        JSONObject jSONObject6 = jSONObject2.getJSONObject(str36);
                                        JSONObject jSONObject7 = jSONObject2.getJSONArray("video_versions").getJSONObject(0);
                                        String string14 = jSONObject7.getString(str37);
                                        jSONObject7.getString(str46);
                                        jSONObject7.getString(str45);
                                        str15 = jSONObject6.getJSONArray(str35).getJSONObject(0).getString(str37);
                                        str14 = str47;
                                        str2 = str29;
                                        str9 = string5;
                                        str13 = string14;
                                        str16 = string6;
                                    } else {
                                        String str48 = "image";
                                        JSONArray jSONArray5 = jSONObject2.getJSONObject(str36).getJSONArray(str35);
                                        String str49 = str48;
                                        str31 = str6;
                                        int i3 = 0;
                                        String str50 = null;
                                        while (i3 < jSONArray5.length()) {
                                            try {
                                                JSONObject jSONObject8 = jSONArray5.getJSONObject(i3);
                                                str2 = str29;
                                                try {
                                                    String string15 = jSONObject8.getString(str46);
                                                    str17 = str31;
                                                    try {
                                                        String string16 = jSONObject8.getString(str45);
                                                        if (!string15.contains(string5) || !string16.contains(string6)) {
                                                            str18 = str45;
                                                            str31 = str17;
                                                        } else {
                                                            String string17 = jSONObject8.getString(str37);
                                                            String string18 = jSONObject8.getString(str46);
                                                            try {
                                                                str31 = jSONObject8.getString(str45);
                                                                PrintStream printStream3 = System.out;
                                                                str18 = str45;
                                                                StringBuilder sb7 = new StringBuilder();
                                                                String str51 = string17;
                                                                sb7.append("Height : width = ");
                                                                sb7.append(string18);
                                                                sb7.append(" : ");
                                                                sb7.append(str31);
                                                                printStream3.println(sb7.toString());
                                                                str7 = string18;
                                                                str50 = str51;
                                                            } catch (JSONException e) {
                                                                str7 = string18;
                                                                str6 = str17;
                                                                e.printStackTrace();
                                                                str30 = str7;
                                                                str31 = str6;
                                                                i++;
//                                                                String str33222222 = str;
                                                                str25 = str5;
                                                                str26 = str4;
                                                                str27 = str3;
                                                                jSONArray2 = jSONArray;
                                                                str29 = str2;
                                                            }
                                                        }
                                                        i3++;
                                                        str45 = str18;
                                                        str29 = str2;
                                                    } catch (JSONException e) {
                                                        str6 = str17;
                                                        e.printStackTrace();
                                                        str30 = str7;
                                                        str31 = str6;
                                                        i++;
                                                        String str332222222 = str;
                                                        str25 = str5;
                                                        str26 = str4;
                                                        str27 = str3;
                                                        jSONArray2 = jSONArray;
                                                        str29 = str2;
                                                    }
                                                } catch (JSONException e) {
                                                    str17 = str31;
                                                    str6 = str17;
                                                    e.printStackTrace();
                                                    str30 = str7;
                                                    str31 = str6;
                                                    i++;
                                                    String str3322222222 = str;
                                                    str25 = str5;
                                                    str26 = str4;
                                                    str27 = str3;
                                                    jSONArray2 = jSONArray;
                                                    str29 = str2;
                                                }
                                            } catch (JSONException e) {
                                                str2 = str29;
                                                str17 = str31;
                                                str6 = str17;
                                                e.printStackTrace();
                                                str30 = str7;
                                                str31 = str6;
                                                i++;
                                                String str33222222222 = str;
                                                str25 = str5;
                                                str26 = str4;
                                                str27 = str3;
                                                jSONArray2 = jSONArray;
                                                str29 = str2;
                                            }
                                        }
                                        str2 = str29;
                                        str17 = str31;
                                        if (str50 == null) {
                                            str9 = string5;
                                            str16 = string6;
                                            str14 = str49;
                                            str13 = null;
                                            str15 = jSONArray5.getJSONObject(0).getString(str37);
                                        } else {
                                            str16 = str17;
                                            str9 = str7;
                                            str13 = null;
                                            str14 = str49;
                                            str15 = str50;
                                        }
                                    }
                                }
                                //                                    str7 = str9;
                                Bean bean = new Bean(string4, string3, str15, str14, string10, string12, string9, string11, replace, str34, string8, string7, str13, null, str7, str16, false, string, string6, string5, date, str11, str10, string2, arr_album);
                                imagePath.add(bean);
                                str6 = str16;
                            } catch (JSONException e) {
                                str2 = str29;
                                jSONArray = jSONArray2;
//                                str8 = str30;
                                str6 = str31;
                                e.printStackTrace();
                                str30 = str7;
                                str31 = str6;
                                i++;
                                String str3322222222222 = str;
                                str25 = str5;
                                str26 = str4;
                                str27 = str3;
                                jSONArray2 = jSONArray;
                                str29 = str2;
                            }
                        } catch (JSONException e) {
                            str4 = str26;
                            str3 = str27;
                            str2 = str29;
                            jSONArray = jSONArray2;
                            str8 = str30;
                            str6 = str31;
                            e.printStackTrace();
                            str30 = str7;
                            str31 = str6;
                            i++;
                            String str33222222222222 = str;
                            str25 = str5;
                            str26 = str4;
                            str27 = str3;
                            jSONArray2 = jSONArray;
                            str29 = str2;
                        }
                    } else {
                        str5 = str25;
                        str4 = str26;
                        str3 = str27;
                        str2 = str29;
                        jSONArray = jSONArray2;
                        str7 = str30;
                        str6 = str31;
//                        str9 = str7;
                    }
//                    str30 = str9;
                } catch (JSONException e) {
                    str5 = str25;
                    str4 = str26;
                    str3 = str27;
                    str2 = str29;
                    jSONArray = jSONArray2;
                    str6 = str31;
                    e.printStackTrace();
                    i++;
                    String str332222222222222 = str;
                }
                str31 = str6;
                i++;
                String str3322222222222222 = str;
                str25 = str5;
                str26 = str4;
                str27 = str3;
                jSONArray2 = jSONArray;
                str29 = str2;
            }
        } catch (JSONException e15) {
            e15.printStackTrace();
        }
        if (str == null || imagePath.size() <= 0) {
            no_storyfound.setVisibility(0);
            recycler_feedlist.setVisibility(8);
//            progressBarfeed.setVisibility(8);
            return;
        }
        feeddataadapter = new FeedDataAdapter(getActivity(), (ShowAllData) getActivity(), imagePath, ShowAllData.user_FULLNAME);
        if (getActivity() != null) {
            recycler_feedlist.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recycler_feedlist, new RecyclerItemClickListener.OnItemClickListener() {
                public void onItemClick(View view, int i) {
                    if (isMultiSelect) {
                        multi_select(i);
                    } else {
                        if (isNetworkAvailable()) {
                            String type = (imagePath.get(i)).getType();
                            String str = "full_name";
                            if (type.equalsIgnoreCase("album")) {

                                Intent intent = new Intent(getContext(), AlbumActivity.class);
                                intent.putExtra("current_position", i);
                                intent.putExtra("url", (imagePath.get(i)).getImageUrl());
                                intent.putExtra("height", (imagePath.get(i)).getImageheight());
                                intent.putExtra("widht", Integer.parseInt((imagePath.get(i)).getImagewidth()));
                                intent.putExtra("user_image_url", (imagePath.get(i)).getProfile_picture());
                                intent.putExtra("user_name", (imagePath.get(i)).getUserName());
                                intent.putExtra(str, ShowAllData.user_FULLNAME);
                                intent.putExtra("position", i);
                                intent.putExtra("code_HdUrl", (imagePath.get(i)).getCode_HdUrl());
                                intent.putExtra("original_height", (imagePath.get(i)).getOriginal_height());
                                intent.putExtra("original_width", (imagePath.get(i)).getOriginal_width());
                                intent.putExtra("media_Title", (imagePath.get(i)).getMedia_title());
                                intent.putExtra("media_id", (imagePath.get(i)).getMedia_id());
                                intent.putExtra("USER_ID", (imagePath.get(i)).getUser_id());
                                intent.putExtra("taken_at", (imagePath.get(i)).getTaken_at());
                                intent.putExtra("caption", (imagePath.get(i)).getText());
                                intent.putExtra("isFeed", true);
                                intent.setFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                                startActivity(intent);
                                return;
                            }
                            String str2 = "profileUrl";
                            String str3 = "imgUrl";
                            String str4 = "id";
                            String str5 = "title";
                            if (type.equalsIgnoreCase("video")) {
                                Intent intent2 = new Intent(getContext(), VideoShower.class);
                                intent2.putExtra(str, ShowAllData.user_FULLNAME);
                                intent2.putExtra(str5, (imagePath.get(i)).getMedia_title());
                                intent2.putExtra(str4, (imagePath.get(i)).getMedia_id());
                                intent2.putExtra(str3, (imagePath.get(i)).getImageUrl());
                                intent2.putExtra(str2, (imagePath.get(i)).getProfile_picture());
                                intent2.putExtra("videoUrl", (imagePath.get(i)).getVideoUrl());
                                intent2.setFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                                startActivity(intent2);
                                return;
                            }
                            Intent intent3 = new Intent(getContext(), ImageShower.class);
                            intent3.putExtra(str, ShowAllData.user_FULLNAME);
                            intent3.putExtra(str5, (imagePath.get(i)).getMedia_title());
                            intent3.putExtra(str4, (imagePath.get(i)).getMedia_id());
                            intent3.putExtra(str3, (imagePath.get(i)).getImageUrl());
                            intent3.putExtra(str2, (imagePath.get(i)).getProfile_picture());
                            intent3.setFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                            startActivity(intent3);
                            return;
                        }
                    }
                }

                public void onItemLongClick(View view, int i) {

                    Log.e("isMultiSelect--)", "" + isMultiSelect);
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
//                    }
                    if (!isMultiSelect) {
                        multiselect_list = new ArrayList<>();
                        isMultiSelect = true;
                    }
                    FeedFragment.showtick = true;
                    feeddataadapter.notifyDataSetChanged();
                }
            }));
        }
        recycler_feedlist.addOnScrollListener(new DetectScrollToEnd(gridLayoutManager, 5) {
            public void onLoadMore() {

                Log.e("next_url--)", "" + next_url + "--)" + isCallReload);
                if (next_url != null) {
                    if (isCallReload) {
                        isCallReload = false;
                        FeedFragment feedFragment = FeedFragment.this;
                        feedFragment.getreloadedData(feedFragment.baseurl, next_url);
                    }
                }
            }
        });
        recycler_feedlist.setAdapter(feeddataadapter);


        isCallReload = true;
    }

    public void all_select(int i) {
        isMultiSelect = true;
        for (int i2 = 0; i2 < imagePath.size(); i2++) {
            multiselect_list.add(imagePath.get(i2));
            (imagePath.get(i2)).setCheckmultiple(true);
        }
        feeddataadapter.notifyDataSetChanged();
    }

    @SuppressLint("WrongConstant")
    public void downloadAllMedia() {
        multiselect_list = new ArrayList<>();
        for (int i = 0; i < imagePath.size(); i++) {
            if (!isFilepath((imagePath.get(i)).getMedia_id())) {
                multiselect_list.add(imagePath.get(i));
            }
        }
        ArrayList<Bean> arrayList = multiselect_list;
        if (arrayList == null || arrayList.size() <= 0) {
            Toast.makeText(getActivity(), getResources().getString(R.string.select_media), 0).show();
        } else {
            downloadMultipleStory();
        }
    }

    public void multi_select(int i) {

        if (multiselect_list.contains(imagePath.get(i))) {

            imagePath.get(i).setCheckmultiple(false);
            multiselect_list.remove(imagePath.get(i));
        } else {

            imagePath.get(i).setCheckmultiple(true);
            multiselect_list.add(imagePath.get(i));
        }
        if (multiselect_list.size() == 0) {
            isMultiSelect = false;
            multiselect_list.clear();
        }
        FeedDataAdapter feedDataAdapter = feeddataadapter;
        if (feedDataAdapter != null) {
            feedDataAdapter.notifyItemChanged(i);
        }

        Log.e("multiselect_list--)", "" + multiselect_list.size());
        if (multiselect_list.size() == 0) {
            rl_download.setVisibility(View.GONE);

            isMultiSelect = false;
        } else {

            rl_download.setVisibility(View.VISIBLE);
            txtDownload.setText("Download (" + multiselect_list.size() + ")");
        }

    }

    private void refreshAdapter(int i) {
        FeedDataAdapter feedDataAdapter = feeddataadapter;
        if (feedDataAdapter != null) {
            feedDataAdapter.notifyItemChanged(i);
        }
    }

    private void downloadMedia() {

        if (multiselect_list != null && multiselect_list.size() > 0) {
            isMultiSelect = false;
            if (VERSION.SDK_INT > 22) {
                String str = "android.permission.WRITE_EXTERNAL_STORAGE";
                if (getActivity().checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    shouldShowRequestPermissionRationale(str);
                    requestPermissions(new String[]{str}, 1);
                    return;
                }
                downloadMultipleStory();
                return;
            }
            downloadMultipleStory();
        } else if (getActivity() != null) {
            Toast.makeText(getActivity(), getResources().getString(R.string.select_media), Toast.LENGTH_SHORT).show();
        }


    }

    private void scanFile(String path) {
        MediaScannerConnection.scanFile(getContext(),
                new String[]{path}, null,
                (path1, uri) -> Log.e("TAG--)", "Finished scanning " + path1));
    }

    public void setPr(int progress) {
        if (progress == 100) {
            ib_im_download.setEnabled(true);
            progressBar.setProgress(progress);
            txtProgress.setText("Completed");
//            Snackbar.make(coordinatorLayout, "Download Completed!", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
        } else {
            ib_im_download.setEnabled(false);
            progressBar.setProgress(progress);
            txtProgress.setText("Downloaded.." + progress + "%");
        }
    }

    public String GenerateUUID() {
        return UUID.randomUUID().toString();
    }

    public String getRankToken() {
        String GenerateUUID = GenerateUUID();
        String str = ShowAllData.USER_NAME;
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append("_");
        sb.append(GenerateUUID);
        return sb.toString();
    }

    public void getreloadedData(String str, String str2) {
        String sb = API_URL +
                "feed/user/" +
                ShowAllData.USER_ID +
                "/?rank_token=" +
                getRankToken() +
                "&max_id=" +
                str2;
        new OkHttpClient.Builder().addInterceptor(chain -> chain.proceed(chain.request().newBuilder().header("User-Agent", Constants.USER_AGENT).header("Connection", "close").header("language", "en").header("Accept", "*/*").header("X-IG-Capabilities", "3QI=").header("Content-type", "application/x-www-form-urlencoded; charset=UTF-8").build())).cookieJar(new CookieJar() {
            public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> list) {
            }

            @NotNull
            public List<Cookie> loadForRequest(HttpUrl httpUrl) {
                return cookies;
            }
        }).build().newCall(new Request.Builder().url(sb).build()).enqueue(new Callback() {
            public void onFailure(Call call, IOException iOException) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                        }
                    });
                }
            }

            @SuppressLint("WrongConstant")
            public void onResponse(Call call, Response response) throws IOException {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                        }
                    });
                }
                response_feed = response.body().string();
                if (getActivity() != null) {
                    if (response_feed != null) {
                        String str = "message";
                        if (response_feed.contains(str) && !response_feed.contains("item")) {
                            try {
                                final String string = new JSONObject(response_feed).getString(str);
                                if (getActivity() != null) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @SuppressLint("WrongConstant")
                                        public void run() {
                                            Toast.makeText(getActivity(), string, 0).show();
                                        }
                                    });
                                }
                                if (string.contains("login_required")) {
                                    return;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    if (response_feed != null) {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {


                                    loadMoreAgain(response_feed);
                                }
                            });
                        }
                    } else if (getActivity() != null) {
                        Toast.makeText(getActivity(), "Please check your internet", 0).show();
                    }
                }
            }
        });
    }

    @SuppressLint("WrongConstant")
    public void loadMoreAgain(String str) {

        Log.e("Load--)", "" + str);
        int i;
        String str2 = null;
        int i2 = 0;
        String str3;
        String str4;
        String str5;
        String str6;
        String str7;
        String str8 = null;
        String str9 = null;
        String str10 = null;
        String str11 = null;
        String str12 = null;
        String str13 = null;
        String str14;
        String str15;
        String str16;
        String str17;
        String str18;
        String str19;
        String str20 = "caption";
        String str21 = "";
        String str22 = "_";
        String str23 = "id";
        String str24 = "media_type";
        String str25 = "comment_count";
        String str26 = "user";
        String str27 = "next_max_id";
        int size = imagePath.size();
        try {
            JSONObject jSONObject = new JSONObject(str);
            JSONArray jSONArray = jSONObject.getJSONArray("items");
            if (!jSONObject.isNull(str27)) {
                next_url = jSONObject.getString(str27);
            } else {
                next_url = null;
            }
            String str28 = null;
            String str29 = null;
            int i3 = 0;
            while (i3 < jSONArray.length()) {
                JSONObject jSONObject2 = jSONArray.getJSONObject(i3);
                String string = jSONObject2.getString("code");
                String string2 = jSONObject2.getString("taken_at");
                int i4 = size;
                try {
                    long parseLong = Long.parseLong(string2);
                    JSONArray jSONArray2 = jSONArray;
                    PrintStream printStream = System.out;
                    String str30 = str28;
                    StringBuilder sb = new StringBuilder();
                    String str31 = str29;
                    sb.append("dateString : ");
                    sb.append(getDate(parseLong));
                    printStream.println(sb.toString());
                    String date = getDate(parseLong);
                    if (jSONObject2.has(str26)) {
                        String strZero = "0";
                        String string3 = jSONObject2.has(str25) ? jSONObject2.getString(str25) : strZero;
                        /*String string4 = jSONObject2.getString("original_height");
                        String string5 = jSONObject2.getString("original_width");*/
                        String string4 = jSONObject2.has("original_height") ? jSONObject2.getString("original_height") : strZero;
                        String string5 = jSONObject2.has("original_width") ? jSONObject2.getString("original_width") : strZero;
                        String string6 = jSONObject2.getString(str24);
                        String string7 = jSONObject2.getString("has_liked");
                        String string8 = jSONObject2.getString("like_count");
                        String string9 = jSONObject2.getString("device_timestamp");
                        JSONObject jSONObject3 = jSONObject2.getJSONObject(str26);
                        str6 = str25;
                        String string10 = jSONObject3.getString("full_name");
                        String string11 = jSONObject3.getString("username");
                        String string12 = jSONObject3.getString("pk");
                        String replace = jSONObject2.getString(str23).replace(str22, str21);
                        str5 = str21;
                        str4 = str26;
                        if (file_ptef == 0) {
                            str7 = replace;
                        } else {
                            if (file_ptef == 1) {
                                StringBuilder sb2 = new StringBuilder();
                                sb2.append(ShowAllData.USER_NAME);
                                sb2.append(replace);
                                str19 = sb2.toString();
                            } else {
                                StringBuilder sb3 = new StringBuilder();
                                sb3.append(replace);
                                sb3.append(ShowAllData.USER_NAME);
                                str19 = sb3.toString();
                            }
                            str7 = str19;
                        }
                        String string13 = jSONObject3.getString("profile_pic_url");
                        String string14 = !jSONObject2.isNull(str20) ? jSONObject2.getJSONObject(str20).getString("text") : str5;
                        boolean contains = string6.contains("8");
                        String str32 = ExifInterface.GPS_MEASUREMENT_2D;
                        String str33 = "candidates";
                        str3 = str20;
                        String str34 = "image_versions2";
                        i = i4;
                        String str35 = "url";
                        if (contains) {
                            String str36 = "album";
                            try {
                                JSONArray jSONArray3 = jSONObject2.getJSONArray("carousel_media");
                                str9 = replace;
                                i2 = i3;
                                JSONObject jSONObject4 = null;
                                int i5 = 0;
                                String str37 = null;
                                String str38 = null;
                                while (i5 < jSONArray3.length()) {
                                    jSONObject4 = jSONArray3.getJSONObject(i5);
                                    String str39 = jSONObject4.getString(str23).split(str22)[0];
                                    JSONArray jSONArray4 = jSONArray3;
                                    if (file_ptef == 0) {
                                        str14 = str22;
                                        str15 = str39;
                                    } else {
                                        str14 = str22;
                                        if (file_ptef == 1) {
                                            StringBuilder sb4 = new StringBuilder();
                                            sb4.append(ShowAllData.USER_NAME);
                                            sb4.append(str39);
                                            str18 = sb4.toString();
                                        } else {
                                            StringBuilder sb5 = new StringBuilder();
                                            sb5.append(str39);
                                            sb5.append(ShowAllData.USER_NAME);
                                            str18 = sb5.toString();
                                        }
                                        str15 = str18;
                                    }
                                    if (jSONObject4.getString(str24) == str32) {
                                        str17 = jSONObject4.getJSONArray("video_versions").getJSONObject(0).getString(str35);
                                        str16 = "video";
                                    } else {
                                        str16 = "image";
                                        str37 = jSONObject4.getJSONObject(str34).getJSONArray(str33).getJSONObject(0).getString(str35);
                                        str17 = str38;
                                    }
                                    Album album = new Album(str37, str17, str16, str39, str15);
                                    arr_album.add(album);
                                    i5++;
                                    str38 = str17;
                                    jSONArray3 = jSONArray4;
                                    str22 = str14;
                                }
                                str2 = str22;
                                str11 = jSONObject4.getJSONObject(str34).getJSONArray(str33).getJSONObject(0).getString(str35);
                                str13 = string4;
                                str12 = string5;
                                str10 = str36;
                            } catch (JSONException e) {
                                e = e;
                                e.printStackTrace();
                                if (feeddataadapter != null) {
                                }
                            }
                        } else {
                            str2 = str22;
                            str9 = replace;
                            i2 = i3;
                            if (string6.contains(str32)) {
                                JSONObject jSONObject5 = jSONObject2.getJSONObject(str34);
                                String string15 = jSONObject2.getJSONArray("video_versions").getJSONObject(0).getString(str35);
                                str11 = jSONObject5.getJSONArray(str33).getJSONObject(0).getString(str35);
                                str10 = "video";
                                str8 = string15;
                                str13 = string4;
                                str12 = string5;
                                /*Bean bean = new Bean(string3, string8, str11, str10, string11, string13, string10, string12, str9, string14, string9, string7, str8, null, str13, str12, false, string, string5, string4, date, str7, string6, string2, arr_album);
                                imagePath.add(bean);*/
                                str28 = str13;
                                str29 = str12;
                            } else {
                                String str40 = "image";
                                JSONArray jSONArray5 = jSONObject2.getJSONObject(str34).getJSONArray(str33);
                                String str41 = null;
                                for (int i6 = 0; i6 < jSONArray5.length(); i6++) {
                                    JSONObject jSONObject6 = jSONArray5.getJSONObject(i6);
                                    String string16 = jSONObject6.getString("height");
                                    String string17 = jSONObject6.getString("width");
                                    if (string16.contains(string4) && string17.contains(string5)) {
                                        str41 = jSONObject6.getString(str35);
                                        String string18 = jSONObject6.getString("height");
                                        str31 = jSONObject6.getString("width");
                                        str30 = string18;
                                    }
                                }
                                if (str41 == null) {
                                    str11 = jSONArray5.getJSONObject(0).getString(str35);
                                    str10 = str40;
                                    str13 = string4;
                                    str12 = string5;
                                } else {
                                    str10 = str40;
                                    str11 = str41;
                                    str13 = str30;
                                    str12 = str31;
                                }
                            }
                        }
                        //str8 = null;
                        Bean bean2 = new Bean(string3, string8, str11, str10, string11, string13, string10, string12, str9, string14, string9, string7, str8, null, str13, str12, false, string, string5, string4, date, str7, string6, string2, arr_album);
                        imagePath.add(bean2);
                        str28 = str13;
                        str29 = str12;
                    } else {
                        str3 = str20;
                        str5 = str21;
                        str2 = str22;
                        str6 = str25;
                        str4 = str26;
                        i2 = i3;
                        i = i4;
                        str28 = str30;
                        str29 = str31;
                    }
                    i3 = i2 + 1;
                    jSONArray = jSONArray2;
                    str25 = str6;
                    str21 = str5;
                    str26 = str4;
                    str20 = str3;
                    size = i;
                    str22 = str2;
                } catch (JSONException e) {
//                    i = i4;
                    e.printStackTrace();
                    if (feeddataadapter != null) {
                        return;
                    }
                }
            }
            i = size;
        } catch (JSONException e) {
            i = size;
            e.printStackTrace();
            if (feeddataadapter != null) {
            }
        }


        Log.e("PosLoad--)", "" + i);
        if (feeddataadapter != null && recycler_feedlist.getVisibility() == 0) {
//            progressBarbottom.setVisibility(8);
            feeddataadapter.notifyItemRangeChanged(i, imagePath.size());
        }

        feeddataadapter.notifyDataSetChanged();
        isCallReload = true;
    }

    /* access modifiers changed from: private */
    @SuppressLint("WrongConstant")
    public void downloadMultipleStory() {
        if (showtick) {
//            rl_download.animate().alpha(0.0f).setDuration(200);
//            rl_download.setVisibility(8);
            showtick = false;
            feeddataadapter.notifyDataSetChanged();
            for (int i = 0; i < imagePath.size(); i++) {
                imagePath.get(i).setCheckmultiple(false);
            }
        }
        list.clear();
        arr_downloadlists = new ArrayList();
        for (int i2 = 0; i2 < multiselect_list.size(); i2++) {
            if (multiselect_list.get(i2).getType().equalsIgnoreCase("album")) {
                List arr_album2 = multiselect_list.get(i2).getArr_album();
                for (int i3 = 0; i3 < arr_album2.size(); i3++) {
                    Album album = (Album) arr_album2.get(i3);
                    Downloadlist downloadlist = new Downloadlist(album.getImageUrl(), album.getVideoUrl(), album.getType(), album.getCode_HdUrl(), album.getMedia_title());
                    arr_downloadlists.add(downloadlist);
                }
            } else {
                Downloadlist downloadlist2 = new Downloadlist(multiselect_list.get(i2).getImageUrl(), multiselect_list.get(i2).getVideoUrl(), multiselect_list.get(i2).getType(), multiselect_list.get(i2).getCode_HdUrl(), multiselect_list.get(i2).getMedia_title());
                arr_downloadlists.add(downloadlist2);
            }
        }
//        downloadVideoAndImageAll((ArrayList) arr_downloadlists, 0);


        Log.e("S--)", "" + arr_downloadlists.size());
        startDownloadMulti(0);


        Log.e("Donwload--)", "" + arr_downloadlists.get(0).getCode_HdUrl());
        Log.e("Donwload11--)", "" + arr_downloadlists.get(0).getVideoUrl());
        Log.e("Donwload12--)", "" + arr_downloadlists.get(0).getImageUrl());
        Log.e("Donwload13--)", "" + arr_downloadlists.get(0).getType());
    }

    private void startDownloadMulti(int pos) {
        posOfDownload = pos;


        Log.e("S1--)", "" + arr_downloadlists.get(pos).getType());
        if (arr_downloadlists.get(pos).getType().matches("image")) {


            new DownloadMultiImage(getContext()).execute(arr_downloadlists.get(pos).getImageUrl());
        } else {
            new DownloadMultiVideo(getContext()).execute(arr_downloadlists.get(pos).getVideoUrl());
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

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();

                String strFileName = System.currentTimeMillis() + Constant.IMAGE_EXTENTION;


                String ext = strFileName.substring(strFileName.indexOf(".") + 1);
                File repostfile;

                File file1 = new File(getContext().getExternalFilesDir(getContext().getResources().getString(R.string.app_name)).toString());
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
                    if (VERSION.SDK_INT >= 19) {
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

                if (arr_downloadlists.size() > 0) {


                    posOfDownload++;

                    if (posOfDownload < arr_downloadlists.size()) {

                        startDownloadMulti(posOfDownload);
                    }
                    ib_im_download.setEnabled(true);

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

                // download the file
                input = connection.getInputStream();

                String strFileName = System.currentTimeMillis() + Constant.VIDEO_EXTENTION;


                String ext = strFileName.substring(strFileName.indexOf(".") + 1);
                File repostfile;

                File file1 = new File(getContext().getExternalFilesDir(getContext().getResources().getString(R.string.app_name)).toString());
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
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();

        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);

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
                    if (VERSION.SDK_INT >= 19) {
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

                if (arr_downloadlists.size() > 0) {
                    posOfDownload++;
                    if (posOfDownload < arr_downloadlists.size()) {

                        startDownloadMulti(posOfDownload);
                    }

                }

            }


        }
    }

    /* access modifiers changed from: private */
    public void downloadVideoAndImageAll(ArrayList<Downloadlist> arrayList, int i) {
        if (i < arrayList.size()) {
            if (!arrayList.get(i).getType().equalsIgnoreCase("video")) {
                fullVersionUrlNew(arrayList, i);
            } else {
                String videoUrl = arrayList.get(i).getVideoUrl();
                if (videoUrl != null) {
                    String media_title = arrayList.get(i).getMedia_title();
                    //Download_Uri = Uri.parse(videoUrl);
                    new AllMultiDownloadManager(getActivity(), videoUrl, "", media_title + Constant.VIDEO_EXTENTION, new AppInterface.OnDownloadStarted() {
                        @Override
                        public void onDownloadStarted(long requestId) {
                            refid = requestId;
                            list.add(Long.valueOf(refid));
                            downloadVideoAndImageAll(arrayList, i + 1);
                        }
                    });
                }
            }
        }
    }

    private void fullVersionUrlNew(ArrayList<Downloadlist> arrayList, int i) {
        final String imageUrl = arrayList.get(i).getImageUrl();
        String code_HdUrl = arrayList.get(i).getCode_HdUrl();
        if (imageUrl != null) {
            final String media_title = arrayList.get(i).getMedia_title();
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
            final ArrayList<Downloadlist> arrayList2 = arrayList;
            final int i2 = i;
            Callback r1 = new Callback() {
                public void onFailure(Call call, IOException iOException) {
                    final String message = iOException.getMessage();

                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                PrintStream printStream = System.out;
                                StringBuilder sb = new StringBuilder();
                                sb.append("onFailure : ");
                                sb.append(message);
                                printStream.println(sb.toString());
                                download_url = imageUrl;
                                downloadMediaAllnew(download_url, media_title);
                                downloadVideoAndImageAll(arrayList2, i2 + 1);
                            }
                        });
                    }

                }

                public void onResponse(Call call, Response response) throws IOException {
                    String str = "display_resources";
                    String str2 = "shortcode_media";
                    String str3 = "graphql";
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                            }
                        });
                    }
                    if (response.isSuccessful()) {
                        String str4 = response.body().string();
                        PrintStream printStream = System.out;
                        StringBuilder sb = new StringBuilder();
                        sb.append("response fullversion = :");
                        sb.append(str4);
                        printStream.println(sb.toString());
                        try {
                            if (getActivity() != null && !getActivity().isFinishing()) {
                                JSONObject jSONObject = new JSONObject(str4);
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
                                                download_url = imageUrl;
                                            }
                                        } else {
                                            download_url = imageUrl;
                                        }
                                    } else {
                                        download_url = imageUrl;
                                    }
                                } else {
                                    download_url = imageUrl;
                                }
                                if (getActivity() != null) {
                                    getActivity().runOnUiThread(new Runnable() {
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
                                }
                                PrintStream printStream3 = System.out;
                                StringBuilder sb3 = new StringBuilder();
                                sb3.append("display_url : ");
                                sb3.append(download_url);
                                printStream3.println(sb3.toString());
                            } else if (getActivity() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @SuppressLint("WrongConstant")
                                    public void run() {
                                        if (!isNetworkAvailable() && getActivity() != null) {
                                            Toast.makeText(getActivity(), getResources().getString(R.string.internet_connection), 0).show();
                                        }
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                download_url = imageUrl;
                                downloadMediaAllnew(download_url, media_title);
                                downloadVideoAndImageAll(arrayList2, i2 + 1);
                            }
                        });
                    }
                }
            };
            newCall.enqueue(r1);
        }
    }

    /* access modifiers changed from: private */
    public void downloadMediaAllnew(String str, String str2) {
        new AllMultiDownloadManager(getActivity(), str, "", str2 + Constant.IMAGE_EXTENTION, new AppInterface.OnDownloadStarted() {
            @Override
            public void onDownloadStarted(long requestId) {
                refid = requestId;
                list.add(Long.valueOf(refid));
            }
        });
    }

    public boolean isNetworkAvailable() {
        if (getActivity() == null) {
            return false;
        }
        @SuppressLint("WrongConstant") NetworkInfo activeNetworkInfo = ((ConnectivityManager) getActivity().getSystemService("connectivity")).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @SuppressLint("WrongConstant")
    public boolean onBackPressedfeed() {
        if (getActivity() == null || !showtick) {
            getActivity().finish();
            return false;
        }
        rl_download.animate().alpha(0.0f).setDuration(200);
        rl_download.setVisibility(8);
        showtick = false;
        FeedDataAdapter feedDataAdapter = feeddataadapter;
        if (feedDataAdapter != null) {
            feedDataAdapter.notifyDataSetChanged();
        }
        ArrayList<Bean> arrayList = multiselect_list;
        if (arrayList != null) {
            arrayList.clear();
        }
        ArrayList<Long> arrayList2 = list;
        if (arrayList2 != null) {
            arrayList2.clear();
            counter = 0;
        }
        for (int i = 0; i < imagePath.size(); i++) {
            imagePath.get(i).setCheckmultiple(false);
        }
        return true;
    }

    @Override
    public void registterBReciver() {

    }

    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        if (i != 1 || iArr.length <= 0) {
            if (i != 2 || iArr[0] != 0) {
                if (i != 3 || iArr[0] != 0) {
                    getActivity().finish();
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


    public void setUserVisibleHint(boolean z) {
        super.setUserVisibleHint(z);
        if ((!z || isFragmentLoadedfeed) && isFragmentLoadedfeed && getActivity() != null) {
            try {
                isFragmentLoadedfeed = false;
//                getActivity().unregisterReceiver(onComplete);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    public void onPause() {
        super.onPause();
    }

    public boolean isFilepath(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStorageDirectory().getPath());
        sb.append("/All Status Downloader");
        String alldownloadPath = sb.toString();
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
        sb8.append(ShowAllData.USER_NAME);
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
        sb16.append(ShowAllData.USER_NAME);
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

    public void onResume() {
        super.onResume();
        if (AlbumActivity.albumregister && getActivity() != null) {
            AlbumActivity.albumregister = false;
        }
    }
}
