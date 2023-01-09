package com.allmy.allstatusdownloader.Activity;

import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.allmy.allstatusdownloader.Adapter.SavedFeedAdapter;
import com.allmy.allstatusdownloader.Auth.AQuery;
import com.allmy.allstatusdownloader.Model.Album;
import com.allmy.allstatusdownloader.Model.AppInterface;
import com.allmy.allstatusdownloader.Model.Downloadlist;
import com.allmy.allstatusdownloader.Model.SavedFeed_setter;
import com.allmy.allstatusdownloader.Others.AllMultiDownloadManager;
import com.allmy.allstatusdownloader.Others.Constants;
import com.allmy.allstatusdownloader.Others.DetectScrollToEnd;
import com.allmy.allstatusdownloader.Others.GridSpacingItemDecoration;
import com.allmy.allstatusdownloader.Others.RecyclerItemClickListener;
import com.allmy.allstatusdownloader.Others.Utils;
import com.allmy.allstatusdownloader.R;

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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SavedFeed extends AppCompatActivity {

    public static boolean savedfeedhowtick = false;
    public static boolean savedfeedregister = false;
    String API_URL = "https://i.instagram.com/api/v1/";
    String USER_ID;
    String USER_NAME;
    SharedPreferences accountInfoPref;
    AQuery aq;
    ArrayList<Album> arr_album;
    ArrayList<Downloadlist> arr_downloadlists;
    SharedPreferences cookiePref;
    List<Cookie> cookies;
    int counter = 0;
    SharedPreferences currentUser;
    String download_url;
    int file_ptef;
    private GridLayoutManager gridLayoutManager;
    boolean isMultiSelect = false;
    public ImageView iv_download;
    ArrayList<Long> list = new ArrayList<>();
    int listsize;
    SharedPreferences loginPref;
    public ArrayList<SavedFeed_setter> multiselect_list = new ArrayList<>();
    String next_Url_Id;
    RelativeLayout no_storyfound;
    boolean isCallReload = false;
    boolean permissionmenu = false;
    public boolean popupCheck = false;
    HashSet<String> preferences;
    String profilePic;
    RecyclerView recyclerView;
    private long refid = 10;
    RelativeLayout rl_download;
    ArrayList<SavedFeed_setter> savedfeed_data;
    SavedFeedAdapter storyshowerRecyclerAdapter;
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
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = getWindow();
//            Drawable background = getResources().getDrawable(R.drawable.bg_status);
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(getResources().getColor(android.R.color.transparent));
//            window.setNavigationBarColor(getResources().getColor(android.R.color.transparent));
//            window.setBackgroundDrawable(background);
//        }
        setContentView(R.layout.activity_saved_feed);

        getIntentData();

        file_ptef = getSharedPreferences("DOWNLOAD_FILE_NAME_PREF", 0).getInt("file_value", 0);
        ImageView back_arroe = findViewById(R.id.ivBack);
        iv_download = findViewById(R.id.iv_download);
//        iv_download.setVisibility(0);

        CircleImageView imgToolbar = findViewById(R.id.profile);
        imgToolbar.setVisibility(View.VISIBLE);
        upload_progress = findViewById(R.id.rl_bg_progress);
        no_storyfound = findViewById(R.id.no_storyfound);
        recyclerView = findViewById(R.id.recycler_view);
        rl_download = findViewById(R.id.rl_download);

        ib_im_download = findViewById(R.id.ib_im_download);
        progressBar = findViewById(R.id.progressBar);
        txtProgress = findViewById(R.id.txtProgress);
        linearDownload = findViewById(R.id.linearDownload);
        txtDownload = findViewById(R.id.txtDownload);

        aq = new AQuery(this);
        preferences = (HashSet) PreferenceManager.getDefaultSharedPreferences(this).getStringSet("PREF_COOKIES", new HashSet());
        getLoginCookies();


        Glide.with(this)
                .load(profilePic)
                .placeholder(R.drawable.applogo)
                .into(imgToolbar);

        iv_download.setOnClickListener(view ->
                showDownloadAll(SavedFeed.this));

        back_arroe.setOnClickListener(view -> {
            if (SavedFeed.savedfeedhowtick) {
                rl_download.setVisibility(View.INVISIBLE);
                SavedFeed.savedfeedhowtick = false;
                if (storyshowerRecyclerAdapter != null) {
                    storyshowerRecyclerAdapter.notifyDataSetChanged();
                }
                if (multiselect_list != null) {
                    multiselect_list.clear();
                }
                if (list != null) {
                    list.clear();
                    counter = 0;
                }
                for (int i = 0; i < savedfeed_data.size(); i++) {
                    savedfeed_data.get(i).setCheckmultiple(false);
                }
                return;
            }

            SavedFeed.savedfeedregister = false;
            finish();
        });

        ib_im_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (multiselect_list != null && multiselect_list.size() > 0) {

                    if (progressBar.getProgress() == 100) {

                        progressBar.setProgress(0);
                        linearDownload.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        txtProgress.setVisibility(View.GONE);
                        Intent intent = new Intent(SavedFeed.this, CreationActivity.class);
                        startActivity(intent);
                        finish();
                    } else {

                        linearDownload.setVisibility(View.GONE);
                        progressBar.setVisibility(View.VISIBLE);
                        txtProgress.setVisibility(View.VISIBLE);
                        downloadMultipleStory();
                    }


                } else {

                    Log.e("Click--)", "" + progressBar.getProgress());
                    if (progressBar.getProgress() == 100) {
                        Intent intent = new Intent(SavedFeed.this, CreationActivity.class);
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

    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void getIntentData() {
        USER_NAME = getIntent().getStringExtra("USER_NAME");
        USER_ID = getIntent().getStringExtra("USER_ID");
        profilePic = getIntent().getStringExtra("profile_pic_url");
        user_FULLNAME = getIntent().getStringExtra("Full_NAME");

    }

    public void showDownloadAll(Activity activity) {
        final Dialog dialog = new Dialog(activity, R.style.UploadDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_all_download);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView txtCancel = dialog.findViewById(R.id.txtCancelDownload);
        TextView txtDownload = dialog.findViewById(R.id.txtDownloadAll);

        txtDownload.setOnClickListener(v -> {
            showPopupDownload(iv_download);

            dialog.dismiss();
        });
        txtCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void getLoginCookies() {
        String str = "CURRENT_USER";
        currentUser = getSharedPreferences(str, 0);
        String string = currentUser.getString(str, null);
        String sb = "LOGIN_" +
                string.toLowerCase(Locale.getDefault()).trim();
        loginPref = getSharedPreferences(sb, 0);
        String sb2 = "COOKIE_PREF_" +
                string.toLowerCase(Locale.getDefault()).trim();
        cookiePref = getSharedPreferences(sb2, 0);
        String sb3 = "ACCOUNT_PREF_" +
                string.toLowerCase(Locale.getDefault()).trim();
        accountInfoPref = getSharedPreferences(sb3, 0);
        cookies = new ArrayList();
        int i = cookiePref.getInt("cookie_count", -1);
        for (int i2 = 0; i2 < i; i2++) {
            PrintStream printStream = System.out;
            String str2 = "";
            String sb4 = "Cookies from pref : " +
                    cookiePref.getString(String.valueOf(i2), str2);
            printStream.println(sb4);
            try {
                cookies.add(Cookie.parse(HttpUrl.get(new URL("https://i.instagram.com/")), cookiePref.getString(String.valueOf(i2), str2)));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        fetchSavedFeed();
    }

    @SuppressLint("WrongConstant")
    private void fetchSavedFeed() {
        upload_progress.setVisibility(0);


        OkHttpClient build = new OkHttpClient.Builder().cookieJar(new CookieJar() {
            public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
            }

            public List<Cookie> loadForRequest(HttpUrl httpUrl) {
                return SavedFeed.this.cookies;
            }
        }).build();
        String str = this.API_URL + "feed/saved/";
        System.out.println("url_Feed :" + str);


        build.newCall(new Request.Builder().url(str).addHeader("User-Agent", Constants.USER_AGENT).addHeader("Connection", "close").addHeader("Accept-Language", "en-US").addHeader("language", "en").addHeader("Accept", "*/*").addHeader("Content-type", "application/x-www-form-urlencoded; charset=UTF-8").build()).enqueue(new Callback() {
            public void onFailure(Call call, IOException iOException) {
                SavedFeed.this.runOnUiThread(new Runnable() {
                    public void run() {
                        SavedFeed.this.upload_progress.setVisibility(4);
                    }
                });
            }

            public void onResponse(Call call, Response response) throws IOException {
                if (response != null) {
                    try {
                        String string = response.body().string();
                        SavedFeed.this.writeToFile(string);
                        PrintStream printStream = System.out;
                        printStream.println("vidfeed_response-->" + string);


                        Log.e("string--)", "" + string);
                        if (new JSONObject(string).getJSONArray("items").length() > 0) {
                            SavedFeed.this.parsesavedPosts(string);
                        } else {
                            SavedFeed.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    SavedFeed.this.upload_progress.setVisibility(4);
                                    SavedFeed.this.no_storyfound.setVisibility(0);
                                }
                            });
                        }
                    } catch (JSONException e) {

                        e.printStackTrace();
                    }
                } else {
                    System.out.println("No response :");
                }
            }
        });
    }

    @SuppressLint("DefaultLocale")
    public void parsesavedPosts(String str) {
        Activity r1 = null;
        SavedFeed r12 = null;
        SavedFeed r13;
        String str2;
        int i;
        JSONArray jSONArray;
        String str3;
        String str4;
        SavedFeed r14;
        String str5;
        String str6;
        String str7;
        SavedFeed r15;
        double d;
        String str8 = null;
        SavedFeed r16 = null;
        String str9;
        String str10;
        String str11;
        String str12;
        String str13;
        String str14;
        String str15;
        SavedFeed r17 = this;
        String str16 = "media";
        String str17 = "next_max_id";
        String str18 = "";
        String str19 = "%.0f";
        try {
            r17.savedfeed_data = new ArrayList<>();
            JSONObject jSONObject = new JSONObject(str);
            JSONArray jSONArray2 = jSONObject.getJSONArray("items");
            String str20 = null;
            if (!jSONObject.isNull(str17)) {
                r17.next_Url_Id = jSONObject.getString(str17);
                PrintStream printStream = System.out;
                String sb = "next_Url_Id :" +
                        r17.next_Url_Id;
                printStream.println(sb);
            } else {
                r17.next_Url_Id = "empty";
                PrintStream printStream2 = System.out;
                String sb2 = "next_Url_Id reload :" +
                        r17.next_Url_Id;
                printStream2.println(sb2);
            }
            if (jSONArray2.length() > 0) {
                int i2 = 0;
                SavedFeed r18 = r17;
                while (i2 < jSONArray2.length()) {
                    r18.arr_album = new ArrayList<>();
                    SavedFeed_setter savedFeed_setter = new SavedFeed_setter();
                    JSONObject jSONObject2 = jSONArray2.getJSONObject(i2);
                    if (jSONObject2.has(str16)) {
                        JSONObject jSONObject3 = jSONObject2.getJSONObject(str16);
                        String string = jSONObject3.getString("media_type");
                        String string2 = jSONObject3.getString("taken_at");
                        if (!jSONObject3.isNull(str18)) {
                            str5 = jSONObject3.getJSONObject("caption").getString("text");
                            PrintStream printStream3 = System.out;
                            String sb3 = "text : " +
                                    str5;
                            printStream3.println(sb3);
                        } else {
                            str5 = str18;
                        }
                        String string3 = jSONObject3.has("code") ? jSONObject3.getString("code") : str20;
                        String replace = jSONObject3.getString("id").replace("_", str18);
                        if (r18.file_ptef == 0) {
                            str6 = replace;
                        } else if (r18.file_ptef == 1) {
                            str6 = r18.USER_NAME + replace;
                        } else {
                            str6 = replace + r18.USER_NAME;
                        }
                        boolean contains = string.contains("8");
                        String str21 = "width";
                        str3 = str16;
                        String str22 = "height";
                        jSONArray = jSONArray2;
                        String str23 = "original_height";
                        i = i2;
                        String str24 = "original_width";
                        String str25 = str19;
                        String str26 = "candidates";
                        String str28 = "image_versions2";
                        String str29 = "url";
                        String str30 = str5;
                        String str31 = " ";
                        if (contains) {
                            str8 = "album";
                            JSONArray jSONArray3 = jSONObject3.getJSONArray("carousel_media");
                            String str35 = null;
                            JSONObject jSONObject4 = null;
                            int i3 = 0;
                            String str36 = null;
                            String str37 = null;
                            while (i3 < jSONArray3.length()) {
                                jSONObject4 = jSONArray3.getJSONObject(i3);
                                JSONArray jSONArray4 = jSONArray3;
                                String str38 = jSONObject4.getString("id").split("_")[0];
                                if (r18.file_ptef == 0) {
                                    str10 = str35;
                                    str9 = str38;
                                } else {
                                    str10 = str35;
                                    if (r18.file_ptef == 1) {
                                        str15 = r18.USER_NAME + str38;
                                    } else {
                                        str15 = str38 + r18.USER_NAME;
                                    }
                                    str9 = str15;
                                }
                                if (jSONObject4.getString("media_type").equals(ExifInterface.GPS_MEASUREMENT_2D)) {
                                    String str39 = "video";
                                    String string4 = jSONObject4.getJSONArray("video_versions").getJSONObject(0).getString(str29);
                                    if (i3 == 0) {
                                        str14 = string4;
                                        str10 = jSONObject4.getJSONObject(str28).getJSONArray(str26).getJSONObject(0).getString(str29);
                                    } else {
                                        str14 = string4;
                                    }
                                    str11 = str39;
                                    str13 = str37;
                                    str12 = str10;
                                    str36 = str14;
                                } else {
                                    String str40 = "image";
                                    JSONObject jSONObject5 = jSONObject4.getJSONObject(str28).getJSONArray(str26).getJSONObject(0);
                                    str13 = jSONObject5.getString(str29);
                                    if (i3 == 0) {
                                        str11 = str40;
                                        str12 = jSONObject5.getString(str29);
                                    } else {
                                        str11 = str40;
                                        str12 = str10;
                                    }
                                }
                                Album album = new Album(str13, str36, str11, str38, str9);
                                r18.arr_album.add(album);
                                i3++;
                                jSONArray3 = jSONArray4;
                                String str41 = str12;
                                str37 = str13;
                                str35 = str41;
                            }
                            String str42 = str35;
                            String string5 = jSONObject4.getString(str24);
                            String string6 = jSONObject4.getString(str23);
                            JSONObject jSONObject6 = jSONObject4.getJSONObject(str28).getJSONArray(str26).getJSONObject(0);
                            savedFeed_setter.setimageheight(jSONObject6.getString(str22));
                            savedFeed_setter.setimagewidth(jSONObject6.getString(str21));
                            savedFeed_setter.setimageurl(str18);
                            savedFeed_setter.setvideourl(str31);
                            savedFeed_setter.setThumbnail_url(str42);
                            savedFeed_setter.setCode_HdUrl(string3);
                            savedFeed_setter.setOriginal_height(string6);
                            savedFeed_setter.setOriginal_width(string5);
                            savedFeed_setter.setMedia_title(str6);
                            savedFeed_setter.setMedia_id(replace);
                            savedFeed_setter.setCaption_text(str30);

                            savedFeed_setter.setArr_album(r18.arr_album);
                            str2 = str18;
                            r16 = r18;
                        } else {
                            str2 = str18;
                            try {
                                if (string.contains(ExifInterface.GPS_MEASUREMENT_2D)) {
                                    String string7 = jSONObject3.getString(str24);
                                    String string8 = jSONObject3.getString(str23);
                                    JSONArray jSONArray5 = jSONObject3.getJSONObject(str28).getJSONArray(str26);
                                    String string9 = jSONArray5.getJSONObject(0).getString(str29);
                                    str8 = "video";

                                    String string10 = "";
                                    if (jSONArray5.length() > 1) {
                                        string10 = jSONArray5.getJSONObject(1).getString(str29);
                                    } else {
                                        string10 = jSONArray5.getJSONObject(0).getString(str29);
                                    }
                                    String string11 = jSONObject3.getJSONArray("video_versions").getJSONObject(0).getString(str29);
                                    savedFeed_setter.setimageheight(jSONArray5.getJSONObject(0).getString(str22));
                                    savedFeed_setter.setimagewidth(jSONArray5.getJSONObject(0).getString(str21));
                                    savedFeed_setter.setimageurl(string9);
                                    savedFeed_setter.setvideourl(string11);
                                    savedFeed_setter.setThumbnail_url(string10);
                                    savedFeed_setter.setCode_HdUrl(string3);
                                    savedFeed_setter.setOriginal_height(string8);
                                    savedFeed_setter.setOriginal_width(string7);
                                    savedFeed_setter.setMedia_title(str6);
                                    savedFeed_setter.setMedia_id(replace);
                                    savedFeed_setter.setCaption_text(str30);
                                    r16 = this;
                                } else {
                                    String string12 = jSONObject3.getString(str24);
                                    String string13 = jSONObject3.getString(str23);
                                    str7 = "image";
                                    str8 = null;
                                    JSONArray jSONArray6 = jSONObject3.getJSONObject(str28).getJSONArray(str26);
                                    String string14 = jSONArray6.getJSONObject(0).getString(str29);
                                    String string15;
                                    if (jSONArray6.length() > 1) {
                                        string15 = jSONArray6.getJSONObject(1).getString(str29);
                                    } else {
                                        string15 = string14;
                                    }
                                    savedFeed_setter.setimageheight(jSONArray6.getJSONObject(0).getString(str22));
                                    savedFeed_setter.setimagewidth(jSONArray6.getJSONObject(0).getString(str21));
                                    savedFeed_setter.setimageurl(string14);
                                    savedFeed_setter.setvideourl(str31);
                                    savedFeed_setter.setThumbnail_url(string15);
                                    savedFeed_setter.setCode_HdUrl(string3);
                                    savedFeed_setter.setOriginal_height(string13);
                                    savedFeed_setter.setOriginal_width(string12);
                                    savedFeed_setter.setMedia_title(str6);
                                    savedFeed_setter.setMedia_id(replace);
                                    savedFeed_setter.setCaption_text(str30);
                                    r15 = this;
                                    savedFeed_setter.setUserName(r15.USER_NAME);
                                    savedFeed_setter.setProfile_picture(r15.profilePic);
                                    savedFeed_setter.setType(str7);
                                    savedFeed_setter.setUserID(r15.USER_ID);
                                    savedFeed_setter.setGetDate(string2);
                                    if (string2 != null) {
                                        savedFeed_setter.setTaken_at(Utils.covertTimeToText(string2));
                                    }

                                    r15.savedfeed_data.add(savedFeed_setter);
                                }
                            } catch (JSONException e) {
                                r12 = this;
                                e.printStackTrace();
                                r1 = r12;
                                r1.runOnUiThread(() -> {
                                    if (savedfeed_data != null && savedfeed_data.size() > 0) {
                                        showGridstory();
                                        try {
                                            upload_progress.setVisibility(View.INVISIBLE);
                                        } catch (Exception ignored) {
                                            ignored.printStackTrace();
                                        } catch (Throwable th) {
                                            upload_progress.setVisibility(View.INVISIBLE);
                                            throw th;
                                        }
                                        upload_progress.setVisibility(View.INVISIBLE);
                                    }
                                });
                            }
                        }
                        str7 = str8;
                        r15 = r16;
                        if (r15 != null && str7 != null) {
                            savedFeed_setter.setUserName(r15.USER_NAME);
                            savedFeed_setter.setProfile_picture(r15.profilePic);
                            savedFeed_setter.setType(str7);
                            savedFeed_setter.setUserID(r15.USER_ID);

                            double longValue2 = (double) (System.currentTimeMillis() / 1000 - Long.parseLong(string2));
                            double d22 = longValue2 / 3600.0d;
                            String.format("%.2f", d22);
                            str4 = str25;
                            String format6 = String.format(str4, d22);
                            savedFeed_setter.setGetDate(string2);
                            d = d22 / 24.0d;
                            String format22 = String.format(str4, d);
                            if (d <= 30.0d) {
                            }
                            r15.savedfeed_data.add(savedFeed_setter);
                            r14 = r15;
                        } else {
                            str3 = str16;
                            str2 = str18;
                            i = i2;
                            jSONArray = jSONArray2;
                            str4 = str19;
                            r14 = r18;
                        }
                        i2 = i + 1;
                        str19 = str4;
                        str16 = str3;
                        jSONArray2 = jSONArray;
                        str18 = str2;
                        str20 = null;
                        r18 = r14;
                    }
                    r1 = r18;
                }
            } else {
                r17.runOnUiThread(new Runnable() {
                    @SuppressLint("WrongConstant")
                    public void run() {
                        upload_progress.setVisibility(4);
                        no_storyfound.setVisibility(0);
                    }
                });
                r1 = r17;
            }
        } catch (JSONException e) {
//            r12 = r13;
            e.printStackTrace();
            r1 = r12;
            r1.runOnUiThread(new Runnable() {
                @SuppressLint("WrongConstant")
                public void run() {
                    if (savedfeed_data != null && savedfeed_data.size() > 0) {
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
        }


        Log.e("savedfeed_data--)", "" + savedfeed_data.size());
        if (!isCallReload) {
            isCallReload = false;
            SavedFeed savedFeed = SavedFeed.this;
            savedFeed.getreloadedData(savedFeed.next_Url_Id);
        }

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
            formatter.format("%02x", Byte.valueOf(valueOf));
        }
        return formatter.toString();
    }

    @SuppressLint("WrongConstant")
    public void showGridstory() {
        if (savedfeed_data.size() < 1) {
            upload_progress.setVisibility(4);
            no_storyfound.setVisibility(0);
        }
        popupCheck = true;

        SavedFeedAdapter savedFeedAdapter = new SavedFeedAdapter(savedfeed_data, this, aq, user_FULLNAME);
        storyshowerRecyclerAdapter = savedFeedAdapter;
        recyclerView.setAdapter(storyshowerRecyclerAdapter);
        gridLayoutManager = new GridLayoutManager(this, 3);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            public int getSpanSize(int i) {
                switch (storyshowerRecyclerAdapter.getItemViewType(i)) {
                    case 22:
                        return 1;
                    case 11:
                        return 3;
                    default:
                        return 3;
                }
            }
        });
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, 3, false));
        RecyclerView recyclerView2 = recyclerView;
        recyclerView2.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView2, new RecyclerItemClickListener.OnItemClickListener() {
            public void onItemClick(View view, int i) {

                Log.e("Select--)", "" + isMultiSelect + "--)" + savedfeed_data.get(i).isCheckmultiple());
                if (isMultiSelect) {
                    multi_select(i);
                } else {
                    String thumbnail_url = savedfeed_data.get(i).getThumbnail_url();
                    String thumbnail_url_video = savedfeed_data.get(i).getvideourl();
                    Log.e("Click--)", "" + thumbnail_url);
                    Log.e("Click--)", "" + thumbnail_url_video);
                    String type1 = savedfeed_data.get(i).getType();
                    String str = "full_name";
                    if (type1.equalsIgnoreCase("album")) {

                        if (savedfeed_data != null) {
                            Intent intent = new Intent(SavedFeed.this, AlbumActivity.class);
                            intent.putExtra("current_position", i);
                            intent.putExtra("url", savedfeed_data.get(i).getimageurl());
                            if (!TextUtils.isEmpty(savedfeed_data.get(i).getimageheight())) {
                                intent.putExtra("height", Integer.valueOf(savedfeed_data.get(i).getimageheight()));
                            } else {
                                intent.putExtra("height", 0);
                            }
                            if (!TextUtils.isEmpty(savedfeed_data.get(i).getimagewidth())) {
                                intent.putExtra("widht", Integer.valueOf(savedfeed_data.get(i).getimagewidth()));
                            } else {
                                intent.putExtra("widht", 0);
                            }

                            Log.e("onBindViewHolderUser: ", "" + savedfeed_data.get(i).getUserID());

                            intent.putExtra("user_image_url", savedfeed_data.get(i).getProfile_picture());
                            intent.putExtra("user_name", savedfeed_data.get(i).getUserName());
                            intent.putExtra(str, user_FULLNAME);
                            intent.putExtra("position", i);
                            intent.putExtra("code_HdUrl", savedfeed_data.get(i).getCode_HdUrl());
                            intent.putExtra("original_height", savedfeed_data.get(i).getOriginal_height());
                            intent.putExtra("original_width", savedfeed_data.get(i).getOriginal_width());
                            intent.putExtra("media_Title", savedfeed_data.get(i).getMedia_title());
                            intent.putExtra("media_id", savedfeed_data.get(i).getMedia_id());
                            intent.putExtra("USER_ID", savedfeed_data.get(i).getUserID());
                            intent.putExtra("taken_at", savedfeed_data.get(i).getGetDate());
                            intent.putExtra("caption", savedfeed_data.get(i).getCaption_text());
                            intent.setFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                            startActivity(intent);
                        }
                        return;
                    }
                    String str2 = "profileUrl";
                    String str3 = "imgUrl";
                    String str4 = "id";
                    String str5 = "title";
                    if (type1.equalsIgnoreCase("image")) {
                        Intent intent2 = new Intent(SavedFeed.this, ImageShower.class);
                        intent2.putExtra(str, user_FULLNAME);
                        intent2.putExtra(str5, savedfeed_data.get(i).getMedia_title());
                        intent2.putExtra(str4, savedfeed_data.get(i).getMedia_id());
                        intent2.putExtra(str3, savedfeed_data.get(i).getimageurl());
                        intent2.putExtra(str2, savedfeed_data.get(i).getProfile_picture());
                        intent2.setFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(intent2);
                        return;
                    }
                    Intent intent3 = new Intent(SavedFeed.this, VideoShower.class);
                    intent3.putExtra(str, user_FULLNAME);
                    intent3.putExtra(str5, savedfeed_data.get(i).getMedia_title());
                    intent3.putExtra(str4, savedfeed_data.get(i).getMedia_id());
                    intent3.putExtra(str3, savedfeed_data.get(i).getimageurl());
                    intent3.putExtra(str2, savedfeed_data.get(i).getProfile_picture());
                    intent3.putExtra("videoUrl", savedfeed_data.get(i).getvideourl());
                    intent3.setFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent3);
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
//                SavedFeed.savedfeedhowtick = true;
                if (storyshowerRecyclerAdapter != null) {
                    storyshowerRecyclerAdapter.notifyDataSetChanged();
                }


            }
        }));
        recyclerView.addOnScrollListener(new DetectScrollToEnd(gridLayoutManager, 5) {
            public void onLoadMore() {
                if (next_Url_Id == null) {
                    return;
                }

            }
        });
    }

    public void getreloadedData(String str) {

        StringBuilder sb = new StringBuilder();
        sb.append(API_URL);
        sb.append("feed/saved/?max_id=");
        sb.append(str);
        String sb2 = sb.toString();
        PrintStream printStream = System.out;
        StringBuilder sb3 = new StringBuilder();
        sb3.append(" getreloadedData icon_feed url : ");
        sb3.append(sb2);
        printStream.println(sb3.toString());
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.addInterceptor(new Interceptor() {
            public Response intercept(Chain chain) throws IOException {
                return chain.proceed(chain.request().newBuilder().addHeader("User-Agent", Constants.USER_AGENT).addHeader("Connection", "close").addHeader("language", "en").addHeader("Accept", "*/*").addHeader("X-IG-Capabilities", "3QI=").addHeader("Content-type", "application/x-www-form-urlencoded; charset=UTF-8").build());
            }
        });

        builder.cookieJar(new CookieJar() {
            public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
            }

            public List<Cookie> loadForRequest(HttpUrl httpUrl) {
                return cookies;
            }
        });
        builder.build().newCall(new Request.Builder().url(sb2).build()).enqueue(new Callback() {
            public void onFailure(Call call, IOException iOException) {
                runOnUiThread(new Runnable() {
                    public void run() {
                    }
                });
            }

            public void onResponse(Call call, Response response) throws IOException {
                if (response != null) {
                    try {
                        String string = response.body().string();
                        PrintStream printStream = System.out;
                        StringBuilder sb = new StringBuilder();
                        sb.append("reload_response-->");
                        sb.append(string);

                        Log.e("string--)", "" + string);
                        printStream.println(sb.toString());
                        if (new JSONObject(string).getJSONArray("items").length() > 0) {
                            parsereloadsavedPosts(string);
                        } else {
                            runOnUiThread(new Runnable() {
                                @SuppressLint("WrongConstant")
                                public void run() {
//                                    progressBarbottom.setVisibility(8);
                                    no_storyfound.setVisibility(0);
                                }
                            });
                        }
                    } catch (JSONException e) {

                        Log.e("EXSAVED--)", "" + e.getMessage());
                        e.printStackTrace();
                    }
                } else {

                    Log.e("EXSAVED--)", "No response :");
                    System.out.println("No response :");
                }
            }
        });
    }

    @SuppressLint("DefaultLocale")
    public void parsereloadsavedPosts(String str) {
        Activity r1;
        SavedFeed r12;
        SavedFeed r13 = null;
        int i;
        JSONArray jSONArray;
        String str2;
        String str3;
        String str4;
        Object r14;
        String str5;
        String str6;
        String str7 = null;
        SavedFeed r15;
        double d;
        String str8 = null;
        SavedFeed r16 = null;
        String str9;
        String str10;
        String str11;
        String str12;
        String str13;
        SavedFeed r17 = this;
        String str14 = "media";
        String str15 = "next_max_id";
        String str16 = "";
        String str17 = "%.0f";
        int size = savedfeed_data.size();

        Log.e("sizSave--)", "" + size);
        try {
            r13 = r17;
            JSONObject jSONObject = new JSONObject(str);
            JSONArray jSONArray2 = jSONObject.getJSONArray("items");
            String str18 = null;
            if (!jSONObject.isNull(str15)) {
                r17.next_Url_Id = jSONObject.getString(str15);
                PrintStream printStream = System.out;
                StringBuilder sb = new StringBuilder();
                sb.append("next_Url_Id reload :");
                sb.append(r17.next_Url_Id);
                printStream.println(sb.toString());
            } else {
                r17.next_Url_Id = "empty";
                PrintStream printStream2 = System.out;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("next_Url_Id reload else :");
                sb2.append(r17.next_Url_Id);
                printStream2.println(sb2.toString());
            }
            if (jSONArray2.length() > 0) {
                int i2 = 0;
                SavedFeed r18 = r17;
                while (i2 < jSONArray2.length()) {

                    int i4 = size;
                    r18.arr_album = new ArrayList<>();
                    SavedFeed_setter savedFeed_setter = new SavedFeed_setter();
                    JSONObject jSONObject2 = jSONArray2.getJSONObject(i2);
                    if (jSONObject2.has(str14)) {
                        JSONObject jSONObject3 = jSONObject2.getJSONObject(str14);
                        String string = jSONObject3.getString("media_type");
                        String string2 = jSONObject3.getString("taken_at");
                        if (!jSONObject3.isNull(str16)) {
                            str5 = jSONObject3.getJSONObject("caption").getString("text");
                            PrintStream printStream3 = System.out;
                            StringBuilder sb3 = new StringBuilder();
                            sb3.append("text : ");
                            sb3.append(str5);
                            printStream3.println(sb3.toString());
                        } else {
                            str5 = str16;
                        }
                        String string3 = jSONObject3.has("code") ? jSONObject3.getString("code") : str18;
                        String replace = jSONObject3.getString("id").replace("_", str16);
                        if (r18.file_ptef == 0) {
                            str6 = replace;
                        } else if (r18.file_ptef == 1) {
                            StringBuilder sb4 = new StringBuilder();
                            sb4.append(r18.USER_NAME);
                            sb4.append(replace);
                            str6 = sb4.toString();
                        } else {
                            StringBuilder sb5 = new StringBuilder();
                            sb5.append(replace);
                            sb5.append(r18.USER_NAME);
                            str6 = sb5.toString();
                        }
                        boolean contains = string.contains("8");
                        String str19 = "width";
                        str3 = str14;
                        String str20 = "height";
                        str2 = str16;
                        String str21 = "original_height";
                        jSONArray = jSONArray2;
                        String str22 = "original_width";
                        i = i2;
                        String str23 = "candidates";
                        String str24 = str17;
                        String str25 = "image_versions2";
                        String str26 = string2;
                        String str27 = "url";
                        String r22 = string;
                        String str28 = " ";
                        if (contains) {
                            str8 = "album";
                            r13 = r18;
                            JSONArray jSONArray3 = jSONObject3.getJSONArray("carousel_media");
                            String str29 = str5;
                            String str30 = replace;
                            JSONObject jSONObject4 = null;
                            int i3 = 0;
                            String str31 = null;
                            String str32 = null;
                            while (i3 < jSONArray3.length()) {
                                jSONObject4 = jSONArray3.getJSONObject(i3);
                                JSONArray jSONArray4 = jSONArray3;
                                String str33 = jSONObject4.getString("id").split("_")[0];
                                if (r18.file_ptef == 0) {
                                    str9 = str6;
                                    str10 = str33;
                                } else {
                                    str9 = str6;
                                    if (r18.file_ptef == 1) {
                                        StringBuilder sb6 = new StringBuilder();
                                        sb6.append(r18.USER_NAME);
                                        sb6.append(str33);
                                        str13 = sb6.toString();
                                    } else {
                                        StringBuilder sb7 = new StringBuilder();
                                        sb7.append(str33);
                                        sb7.append(r18.USER_NAME);
                                        str13 = sb7.toString();
                                    }
                                    str10 = str13;
                                }
                                if (jSONObject4.getString("media_type").equals(ExifInterface.GPS_MEASUREMENT_2D)) {
                                    str11 = "video";
                                    str32 = jSONObject4.getJSONArray("video_versions").getJSONObject(0).getString(str27);
                                    str12 = str31;
                                } else {
                                    str12 = jSONObject4.getJSONObject(str25).getJSONArray(str23).getJSONObject(0).getString(str27);
                                    str11 = "image";
                                }
                                Album album = new Album(str12, str32, str11, str33, str10);
                                r18.arr_album.add(album);
                                i3++;
                                str31 = str12;
                                jSONArray3 = jSONArray4;
                                str6 = str9;
                            }
                            String str34 = str6;
                            String string4 = jSONObject4.getString(str22);
                            String string5 = jSONObject4.getString(str21);
                            JSONArray jSONArray5 = jSONObject4.getJSONObject(str25).getJSONArray(str23);
                            JSONObject jSONObject5;
                            if (jSONArray5.length() > 1) {
                                jSONObject5 = jSONArray5.getJSONObject(1);
                            } else {
                                jSONObject5 = jSONArray5.getJSONObject(0);
                            }
                            String string7 = jSONObject5.getString(str27);
                            savedFeed_setter.setimageheight(jSONObject5.getString(str20));
                            savedFeed_setter.setimagewidth(jSONObject5.getString(str19));
                            savedFeed_setter.setThumbnail_url(string7);
                            //Log.e("Reload JSON5",jSONArray5.toString());
                            String string6 = jSONArray5.getJSONObject(0).getString(str27);
                            savedFeed_setter.setimageurl(string6);
                            savedFeed_setter.setvideourl(str28);
                            savedFeed_setter.setCode_HdUrl(string3);
                            savedFeed_setter.setOriginal_height(string5);
                            savedFeed_setter.setOriginal_width(string4);
                            savedFeed_setter.setMedia_title(str34);
                            savedFeed_setter.setMedia_id(str30);
                            savedFeed_setter.setCaption_text(str29);
                            savedFeed_setter.setArr_album(r18.arr_album);
                            r16 = r18;
                            /*if(jSONArray5.length() > 1){
                                JSONObject jSONObject5 = jSONArray5.getJSONObject(1);
                                String string6 = jSONArray5.getJSONObject(0).getString(str27);
                                String string7 = jSONObject5.getString(str27);
                                savedFeed_setter.setimageheight(jSONObject5.getString(str20));
                                savedFeed_setter.setimagewidth(jSONObject5.getString(str19));
                                savedFeed_setter.setimageurl(string6);
                                savedFeed_setter.setvideourl(str28);
                                savedFeed_setter.setThumbnail_url(string7);
                                savedFeed_setter.setCode_HdUrl(string3);
                                savedFeed_setter.setOriginal_height(string5);
                                savedFeed_setter.setOriginal_width(string4);
                                savedFeed_setter.setMedia_title(str34);
                                savedFeed_setter.setMedia_id(str30);
                                savedFeed_setter.setCaption_text(str29);
                                savedFeed_setter.setArr_album(r18.arr_album);
                                r16 = r18;
                            }*/
                        } else {
                            String str35 = replace;
                            String str36 = str5;
                            String str37 = str35;
                            String r19 = r22;
                            try {
//                                r13 = r19;
                                if (r19.contains(ExifInterface.GPS_MEASUREMENT_2D)) {
                                    String string8 = jSONObject3.getString(str22);
                                    String string9 = jSONObject3.getString(str21);
                                    JSONArray jSONArray6 = jSONObject3.getJSONObject(str25).getJSONArray(str23);
                                    String string10 = jSONArray6.getJSONObject(0).getString(str27);
                                    str8 = "video";
                                    String string11 = "";
                                    if (jSONArray6.length() > 1) {
                                        string10 = jSONArray6.getJSONObject(1).getString(str27);
                                    } else {
                                        string10 = jSONArray6.getJSONObject(0).getString(str27);
                                    }
                                    //String string11 = jSONArray6.getJSONObject(1).getString(str27);
                                    String string12 = jSONObject3.getJSONArray("video_versions").getJSONObject(0).getString(str27);
                                    savedFeed_setter.setimageheight(jSONArray6.getJSONObject(0).getString(str20));
                                    savedFeed_setter.setimagewidth(jSONArray6.getJSONObject(0).getString(str19));
                                    savedFeed_setter.setimageurl(string10);
                                    savedFeed_setter.setvideourl(string12);
                                    savedFeed_setter.setThumbnail_url(string11);
                                    savedFeed_setter.setCode_HdUrl(string3);
                                    savedFeed_setter.setOriginal_height(string9);
                                    savedFeed_setter.setOriginal_width(string8);
                                    savedFeed_setter.setMedia_title(str6);
                                    savedFeed_setter.setMedia_id(str37);
                                    savedFeed_setter.setCaption_text(str36);
                                    r16 = this;
                                } else {
                                    String string13 = jSONObject3.getString(str22);
                                    String string14 = jSONObject3.getString(str21);
                                    str7 = "image";
                                    str8 = null;
//                                    str8 = "image";
                                    JSONArray jSONArray7 = jSONObject3.getJSONObject(str25).getJSONArray(str23);
                                    String string15 = jSONArray7.getJSONObject(0).getString(str27);
                                    String string16;
                                    if (jSONArray7.length() > 1) {
                                        string16 = jSONArray7.getJSONObject(1).getString(str27);
                                    } else {
                                        string16 = string15;
                                    }
                                    savedFeed_setter.setimageheight(jSONArray7.getJSONObject(0).getString(str20));
                                    savedFeed_setter.setimagewidth(jSONArray7.getJSONObject(0).getString(str19));
                                    savedFeed_setter.setimageurl(string15);
                                    savedFeed_setter.setvideourl(str28);
                                    savedFeed_setter.setThumbnail_url(string16);
                                    savedFeed_setter.setCode_HdUrl(string3);
                                    savedFeed_setter.setOriginal_height(string14);
                                    savedFeed_setter.setOriginal_width(string13);
                                    savedFeed_setter.setMedia_title(str6);
                                    savedFeed_setter.setMedia_id(str37);
                                    savedFeed_setter.setCaption_text(str36);
                                    r15 = this;
                                    r13 = r15;
                                    savedFeed_setter.setUserName(r15.USER_NAME);
                                    savedFeed_setter.setProfile_picture(r15.profilePic);
                                    savedFeed_setter.setType(str7);
                                    savedFeed_setter.setUserID(r15.USER_ID);
                                    savedFeed_setter.setGetDate(str26);
                                    savedFeed_setter.setTaken_at(Utils.covertTimeToText(str26));
                                    r15.savedfeed_data.add(savedFeed_setter);
                                }
                            } catch (JSONException e) {

                                r12 = this;
                                e.printStackTrace();
                                r1 = r12;
                                r1.runOnUiThread(() -> {
                                    if (savedfeed_data != null && savedfeed_data.size() > 0) {
                                        storyshowerRecyclerAdapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        }
                        str7 = str8;
                        r15 = r16;
                        r13 = r15;
                        if (r15 != null && str7 != null) {
                            savedFeed_setter.setUserName(r15.USER_NAME);
                            savedFeed_setter.setProfile_picture(r15.profilePic);
                            savedFeed_setter.setType(str7);
                            savedFeed_setter.setUserID(r15.USER_ID);
                            double longValue2 = (double) (System.currentTimeMillis() / 1000 - Long.parseLong(str26));
                            double d22 = longValue2 / 3600.0d;
                            String.format("%.2f", d22);
                            str4 = str24;
                            String format6 = String.format(str4, d22);
                            savedFeed_setter.setGetDate(str26);
                            d = d22 / 24.0d;
                            String format22 = String.format(str4, d);
                            if (d <= 30.0d) {
                            }
                            r15.savedfeed_data.add(savedFeed_setter);
                        } else {
                            str3 = str14;
                            str2 = str16;
                            i = i2;
                            jSONArray = jSONArray2;
                            str4 = str17;
                        }
                        i2 = i + 1;
                        str17 = str4;
                        str14 = str3;
                        str16 = str2;
                        jSONArray2 = jSONArray;
                        str18 = null;
                    }
                }
                r1 = r18;
            } else {
                r17.runOnUiThread(() -> {
                    no_storyfound.setVisibility(View.VISIBLE);
                });
                r1 = r17;
            }
        } catch (JSONException e) {
            r12 = r13;
            e.printStackTrace();
            r1 = r12;
            r1.runOnUiThread(() -> {
                if (savedfeed_data != null && savedfeed_data.size() > 0) {
                    storyshowerRecyclerAdapter.notifyDataSetChanged();
                }
            });
        }
//        r1.runOnUiThread(() -> {

//            if (savedfeed_data != null && savedfeed_data.size() > 0) {
//                showGridstory();
//                try {
//                    upload_progress.setVisibility(4);
//                } catch (Exception unused) {
//                } catch (Throwable th) {
//                    upload_progress.setVisibility(4);
//                    throw th;
//                }
//                upload_progress.setVisibility(4);
//            }


        r1.runOnUiThread(new Runnable() {
            @SuppressLint("WrongConstant")
            public void run() {

                Log.e("nextUrl--)", "" + next_Url_Id);
                if (next_Url_Id.matches("empty")) {
                    if (savedfeed_data != null && savedfeed_data.size() > 0) {
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
                } else {
                    SavedFeed savedFeed = SavedFeed.this;
                    savedFeed.getreloadedData(savedFeed.next_Url_Id);
                }


            }
        });

//            if (savedfeed_data != null && savedfeed_data.size() > 0) {
//
//////                for (int ii=size;ii<savedfeed_data.size();ii+=ITEM_PER_AD)
//////                {
//////                    savedfeed_data.add(ii,new SavedFeed_setter(""));
//////                }
//
//                storyshowerRecyclerAdapter.notifyDataSetChanged();
//
//
//            }
//        });
//        isCallReload = true;


        Log.e("savedfeed_data--)", "" + savedfeed_data.size());


    }

    @SuppressLint("WrongConstant")
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            if (savedfeedhowtick) {
                rl_download.setVisibility(4);
                savedfeedhowtick = false;
                SavedFeedAdapter savedFeedAdapter = storyshowerRecyclerAdapter;
                if (savedFeedAdapter != null) {
                    savedFeedAdapter.notifyDataSetChanged();
                }
                ArrayList<SavedFeed_setter> arrayList = multiselect_list;
                if (arrayList != null) {
                    arrayList.clear();
                }
                ArrayList<Long> arrayList2 = list;
                if (arrayList2 != null) {
                    arrayList2.clear();
                    counter = 0;
                }
                for (int i = 0; i < savedfeed_data.size(); i++) {
                    savedfeed_data.get(i).setCheckmultiple(false);
                }
            } else {
                savedfeedregister = false;
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
        if (savedfeedhowtick) {
            savedfeedhowtick = false;
            SavedFeedAdapter savedFeedAdapter = storyshowerRecyclerAdapter;
            if (savedFeedAdapter != null) {
                savedFeedAdapter.notifyDataSetChanged();
            }
            ArrayList<SavedFeed_setter> arrayList = multiselect_list;
            if (arrayList != null) {
                arrayList.clear();
            }
            ArrayList<Long> arrayList2 = list;
            if (arrayList2 != null) {
                arrayList2.clear();
                counter = 0;
            }
            for (int i = 0; i < savedfeed_data.size(); i++) {
                savedfeed_data.get(i).setCheckmultiple(false);
            }
            return;
        }

        savedfeedregister = false;
        finish();
    }

    @SuppressLint("WrongConstant")
    public void showPopupDownload(View view) {
        ArrayList<SavedFeed_setter> arrayList = savedfeed_data;
        if (arrayList != null && arrayList.size() > 0) {
            ArrayList<SavedFeed_setter> arrayList2 = multiselect_list;
            if (arrayList2 != null) {
                arrayList2.clear();
            }
            ArrayList<Long> arrayList3 = list;
            if (arrayList3 != null) {
                arrayList3.clear();
                counter = 0;
            }
            rl_download.setVisibility(0);
            savedfeedhowtick = true;
            isMultiSelect = true;
            SavedFeedAdapter savedFeedAdapter = storyshowerRecyclerAdapter;
            if (savedFeedAdapter != null) {
                savedFeedAdapter.notifyDataSetChanged();
            }
        }

        isMultiSelect = false;
        if (Build.VERSION.SDK_INT > 22) {
            String str = "android.permission.WRITE_EXTERNAL_STORAGE";
            if (checkSelfPermission(str) != 0) {
                shouldShowRequestPermissionRationale(str);
                requestPermissions(new String[]{str}, 1);
            } else {
                downloadAllMedia();
            }
        } else {
            downloadAllMedia();
        }
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

    @SuppressLint("WrongConstant")
    public void downloadMultipleStory() {
        if (savedfeedhowtick) {
            savedfeedhowtick = false;
            storyshowerRecyclerAdapter.notifyDataSetChanged();
            for (int i = 0; i < savedfeed_data.size(); i++) {
                savedfeed_data.get(i).setCheckmultiple(false);
            }
        }
        list.clear();
        arr_downloadlists = new ArrayList<>();
        for (int i2 = 0; i2 < multiselect_list.size(); i2++) {
            if (multiselect_list.get(i2).getType().equalsIgnoreCase("album")) {
                ArrayList arr_album2 = multiselect_list.get(i2).getArr_album();
                PrintStream printStream = System.out;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("size : ");
                sb2.append(arr_album2.size());
                printStream.println(sb2.toString());
                for (int i3 = 0; i3 < arr_album2.size(); i3++) {
                    Album album = (Album) arr_album2.get(i3);
                    Downloadlist downloadlist = new Downloadlist(album.getImageUrl(), album.getVideoUrl(), album.getType(), album.getCode_HdUrl(), album.getMedia_title());
                    arr_downloadlists.add(downloadlist);
                }
            } else {
                Downloadlist downloadlist2 = new Downloadlist(multiselect_list.get(i2).getimageurl(), multiselect_list.get(i2).getvideourl(), multiselect_list.get(i2).getType(), multiselect_list.get(i2).getCode_HdUrl(), multiselect_list.get(i2).getMedia_title());
                arr_downloadlists.add(downloadlist2);
            }
        }

        Log.e("arrdownload--)", "" + arr_downloadlists.size());
        startDownloadMulti(0);
    }

    private void startDownloadMulti(int pos) {
        posOfDownload = pos;
        if (arr_downloadlists.get(pos).getType().matches("image")) {
            new DownloadMultiImage(getApplicationContext()).execute(arr_downloadlists.get(pos).getImageUrl());
        } else {
            new DownloadMultiVideo(getApplicationContext()).execute(arr_downloadlists.get(pos).getVideoUrl());
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

                String strFileName = System.currentTimeMillis() + ".jpg";


                String ext = strFileName.substring(strFileName.indexOf(".") + 1);
                File repostfile;
                File file = new File(getExternalFilesDir(getApplicationContext().getResources().getString(R.string.app_name)).toString());

                if (!file.exists()) {
                    file.mkdirs();
                }
                repostfile = new File(file, strFileName);
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
                input = connection.getInputStream();

                String strFileName = System.currentTimeMillis() + ".mp4";


                String ext = strFileName.substring(strFileName.indexOf(".") + 1);
                File repostfile;
                File file = new File(getExternalFilesDir(getApplicationContext().getResources().getString(R.string.app_name)).toString());

                repostfile = new File(file, strFileName);
                dnlUrl = repostfile.getAbsolutePath();
                output = new FileOutputStream(repostfile);
                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    if (fileLength > 0)
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

                if (arr_downloadlists.size() > 0) {
                    posOfDownload++;
                    if (posOfDownload < arr_downloadlists.size()) {

                        startDownloadMulti(posOfDownload);
                    }

                }
            }


        }
    }


    public void downloadVideoAndImageAll(ArrayList<Downloadlist> arrayList, int i) {
        if (i < arrayList.size()) {
            if (!arrayList.get(i).getType().equalsIgnoreCase("video")) {
                fullVersionUrlNew(arrayList, i);
            } else {
                String videoUrl = arrayList.get(i).getVideoUrl();
                if (videoUrl != null) {
                    String media_title = arrayList.get(i).getMedia_title();
                    //Log.e("Video All", " Type >> " + ((Downloadlist) arrayList.get(i)).getType() + " Url >>> " + videoUrl);
                    new AllMultiDownloadManager(SavedFeed.this, videoUrl, "", media_title + ".mp4", new AppInterface.OnDownloadStarted() {
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
                    runOnUiThread(new Runnable() {
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

                public void onResponse(Call call, Response response) throws IOException {
                    String str = "display_resources";
                    String str2 = "shortcode_media";
                    String str3 = "graphql";
                    runOnUiThread(new Runnable() {
                        public void run() {
                        }
                    });
                    if (response.isSuccessful()) {
                        String str4 = response.body().string();
                        PrintStream printStream = System.out;
                        StringBuilder sb = new StringBuilder();
                        sb.append("response fullversion = :");
                        sb.append(str4);
                        printStream.println(sb.toString());
                        try {
                            if (!isFinishing()) {
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
                                    if (!isNetworkAvailable() && SavedFeed.this != null) {
                                        Toast.makeText(SavedFeed.this, getResources().getString(R.string.internet_connection), 0).show();
                                    }
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                download_url = imageUrl;
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
        new AllMultiDownloadManager(SavedFeed.this, str, "", str2 + ".jpg", new AppInterface.OnDownloadStarted() {
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
        File file = new File(externalStoragePublicDirectory, "fsaveddfeed.txt");
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
            //Log.e("Exception", sb2.toString());
        }
    }

    @SuppressLint("WrongConstant")
    public void downloadAllMedia() {
        multiselect_list = new ArrayList<>();
        for (int i = 0; i < savedfeed_data.size(); i++) {
            if (!isFilepath(savedfeed_data.get(i).getMedia_id())) {
                multiselect_list.add(savedfeed_data.get(i));
            }
        }
        ArrayList<SavedFeed_setter> arrayList = multiselect_list;
        if (arrayList == null || arrayList.size() <= 0) {
            Toast.makeText(this, getResources().getString(R.string.select_media), 0).show();
        } else {

            rl_download.setVisibility(View.VISIBLE);
            txtDownload.setText("Download (" + savedfeed_data.size() + ")");

            if (progressBar.getProgress() == 100) {
                Intent intent = new Intent(SavedFeed.this, CreationActivity.class);
                startActivity(intent);
                finish();
            } else {

                linearDownload.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                txtProgress.setVisibility(View.VISIBLE);
                downloadMultipleStory();
            }


        }

    }

    public void multi_select(int i) {
        savedfeed_data.get(i).getMedia_id();
        if (multiselect_list.contains(savedfeed_data.get(i))) {
            multiselect_list.remove(savedfeed_data.get(i));
            savedfeed_data.get(i).setCheckmultiple(false);
        } else {
            multiselect_list.add(savedfeed_data.get(i));
            savedfeed_data.get(i).setCheckmultiple(true);
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
            txtDownload.setText("Download (" + multiselect_list.size() + ")");
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

    public void setPr(int progress) {

        if (progress == 100) {
            ib_im_download.setEnabled(true);
            progressBar.setProgress(progress);
            txtProgress.setText("Completed");
        } else {
            ib_im_download.setEnabled(false);
            progressBar.setProgress(progress);
            txtProgress.setText("Downloaded.." + progress + "%");
        }
    }

    private void refreshAdapter(int i) {
        SavedFeedAdapter savedFeedAdapter = storyshowerRecyclerAdapter;
        if (savedFeedAdapter != null) {
            savedFeedAdapter.notifyItemChanged(i);
        }
    }

    public boolean isFilepath(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStorageDirectory().getPath());
        sb.append("/All Status Downloader");
        //    private AdView adView;
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