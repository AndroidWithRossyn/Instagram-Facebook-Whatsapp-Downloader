package com.allmy.allstatusdownloader.Activity;

import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;

import androidx.annotation.Keep;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.allmy.allstatusdownloader.Others.Constants;
import com.allmy.allstatusdownloader.Others.Utils;
import com.allmy.allstatusdownloader.R;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
@Keep

public class UserProfileActivity extends AppCompatActivity {

    private ImageView ivBack, ivSearch;
    private CircleImageView profileImage;
    private TextView fullName, userName, postCount, followersCount, followingsCount;
    private LinearLayout cardSavedPosts, cardFavourite, openInInstagram;
    private RelativeLayout cardLogout;
    String BASE_URL = "https://i.instagram.com/api/v1/";
    SharedPreferences accountInfoPref;
    SharedPreferences cookiePref;
    List<Cookie> cookies;
    SharedPreferences currentUser;
    SharedPreferences loginPref;
    private String profilePic;
    private Toolbar toolbar;
    SharedPreferences totalUserInfo;
    private TextView txtLogout, txtCancel;
    String userId;
    String fullNames;
    Utils utils;
    String userNameToolBar;
    String str2 = "profile_pic_url";
    String str4 = "profilePic";
    Dialog custLogoutDailog;
    FrameLayout frameBanner;
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
        setContentView(R.layout.activity_user_profile);

        findView();

        profilePic = getIntent().getExtras().getString("profilePic");
        getLoginCookies();
        fetchUserInfo();
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

    public void findView() {
        utils = new Utils(UserProfileActivity.this);
        ivBack = findViewById(R.id.ivBack);
        ivSearch = findViewById(R.id.ivSearch);
        profileImage = findViewById(R.id.profileImage);
        frameBanner = findViewById(R.id.frameBanner);
        fullName = findViewById(R.id.fullName);
        userName = findViewById(R.id.userName);
        postCount = findViewById(R.id.postCount);
        followersCount = findViewById(R.id.followersCount);
        followingsCount = findViewById(R.id.followingsCount);
        cardSavedPosts = findViewById(R.id.cardSavedPosts);
        cardFavourite = findViewById(R.id.cardFavourite);
        openInInstagram = findViewById(R.id.openInInstagram);
        cardLogout = findViewById(R.id.cardLogout);
        custLogoutDailog = new Dialog(UserProfileActivity.this, R.style.UploadDialog);
        custLogoutDailog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        custLogoutDailog.setCancelable(false);
        custLogoutDailog.setCanceledOnTouchOutside(false);
        custLogoutDailog.setContentView(R.layout.logout_dialog);
        custLogoutDailog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        txtLogout = custLogoutDailog.findViewById(R.id.txtLogout);
        txtCancel = custLogoutDailog.findViewById(R.id.txtCancel);
        ivBack.setOnClickListener(view -> finish());

        utils.loadBanner(UserProfileActivity.this, frameBanner);
        txtLogout.setOnClickListener(view -> {

            custLogoutDailog.dismiss();

            logoutUser();
        });

        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    Intent intent = new Intent(UserProfileActivity.this, ActivitySearch.class);
                    intent.setFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    return;
                }
                Toast.makeText(UserProfileActivity.this, getResources().getString(R.string.internet_connection), Toast.LENGTH_SHORT).show();
            }
        });

        txtCancel.setOnClickListener(view -> custLogoutDailog.dismiss());
        cardSavedPosts.setOnClickListener(v -> {

            if (!isNetworkAvailable()) {
                Toast.makeText(UserProfileActivity.this, getResources().getString(R.string.internet_connection), Toast.LENGTH_LONG).show();
                return;
            }
            Intent intent = new Intent(UserProfileActivity.this, SavedFeed.class);
            intent.putExtra("USER_NAME", userNameToolBar);
            intent.putExtra("Full_NAME", fullNames);
            intent.putExtra("USER_ID", userId);
            intent.putExtra(str2, profilePic);
            intent.setFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });

        cardFavourite.setOnClickListener(v -> {

            Intent intent4 = new Intent(UserProfileActivity.this, FavUserActivity.class);
            intent4.putExtra(str4, profilePic);
            startActivity(intent4);

        });

        cardLogout.setOnClickListener(v -> {
            custLogoutDailog.show();
        });


        openInInstagram.setOnClickListener(view -> {
            String string = loginPref.getString("user_name", "");
            StringBuilder sb = new StringBuilder();
            sb.append("http://instagram.com/_u/");
            sb.append(string);
            String str = "android.intent.action.VIEW";
            Intent intent = new Intent(str, Uri.parse(sb.toString()));
            intent.setPackage("com.instagram.android");
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException unused) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("http://instagram.com/");
                sb2.append(string);
                startActivity(new Intent(str, Uri.parse(sb2.toString())));
            }
        });

    }

    @SuppressLint("WrongConstant")
    public void logoutUser() {
        String str = "CURRENT_USER";
        currentUser = getSharedPreferences(str, 0);
        String string = currentUser.getString(str, null);
        if (string != null) {
            totalUserInfo = getSharedPreferences("TOTAL_USER_INFO", 0);
            String sb = "LOGIN_" + string.toLowerCase(Locale.getDefault()).trim();
            loginPref = getSharedPreferences(sb, 0);
            String sb2 = "COOKIE_PREF_" + string.toLowerCase(Locale.getDefault()).trim();
            cookiePref = getSharedPreferences(sb2, 0);
            String sb3 = "ACCOUNT_PREF_" + string.toLowerCase(Locale.getDefault()).trim();
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

                Intent intent = getIntent();
                setResult(RESULT_OK, intent);
                finish();
                return;
            }
            return;
        }
        Toast makeText = Toast.makeText(this, getResources().getString(R.string.error_box), Toast.LENGTH_LONG);
        makeText.setGravity(16, 0, 0);
        makeText.show();
        PrintStream printStream = System.out;
        String sb4 = "error_box :" + getResources().getString(R.string.error_box);
        printStream.println(sb4);
//        @SuppressLint("SdCardPath") File[] listFiles = new File("/data/data/com.aioi.statusdownloader/shared_prefs/").listFiles();
        @SuppressLint("SdCardPath") File[] listFiles = new File("/data/data/com.np.allstatusdownloader/shared_prefs/").listFiles();
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

    private void getLoginCookies() {
        try {
            String str = "CURRENT_USER";
            currentUser = getSharedPreferences(str, 0);

            String string = currentUser.getString(str, null);
            if (string != null) {
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
                totalUserInfo = getSharedPreferences("TOTAL_USER_INFO", 0);
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
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void fetchUserInfo() {
        String str = "";
        String string = loginPref.getString("USER_ID", str);
        String string2 = loginPref.getString("uuid", str);
        StringBuilder sb = new StringBuilder();
        sb.append(string);
        sb.append("_");
        sb.append(string2);
        sb.toString();
        StringBuilder sb2 = new StringBuilder();
        sb2.append(BASE_URL);
        sb2.append("users/");
        sb2.append(string);
        sb2.append("/info/");

        String str2 = "User-Agent";
        new OkHttpClient.Builder().cookieJar(new CookieJar() {
            public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
            }

            public List<Cookie> loadForRequest(HttpUrl httpUrl) {
                return cookies;
            }
        }).build().newCall(new Request.Builder().url(sb2.toString()).header(str2, Constants.USER_AGENT).header("Connection", "close").header("language", "en").header("Accept", "*/*").header("X-IG-Capabilities", "3QI=").header("Content-type", "application/x-www-form-urlencoded; charset=UTF-8").build()).enqueue(new Callback() {
            public void onFailure(Call call, IOException iOException) {
            }

            public void onResponse(Call call, Response response) {
                try {
                    JSONObject jSONObject = new JSONObject(response.body().string()).getJSONObject("user");
                    final String string = jSONObject.getString("profile_pic_url");
                    final int i = jSONObject.getInt("media_count");
                    final int i2 = jSONObject.getInt("follower_count");
                    final int i3 = jSONObject.getInt("following_count");
                    final String string2 = jSONObject.getString("username");
                    final String string3 = jSONObject.getString("full_name");
                    final String string4 = jSONObject.getString("biography");
                    final String string5 = jSONObject.getString("external_url");
                    final String string6 = jSONObject.getString("pk");

                    userNameToolBar = string2;
                    fullNames = string3;
                    userId = string6;
                    Runnable r0 = () -> {
                        if (!isFinishing()) {
                            Glide.with(getApplicationContext()).load(string).apply(((new RequestOptions().override(300, 300)).centerCrop()).skipMemoryCache(false)).listener(new RequestListener<Drawable>() {
                                public boolean onLoadFailed(GlideException glideException, Object obj, Target<Drawable> target, boolean z) {
                                    return true;
                                }

                                @SuppressLint("WrongConstant")
                                public boolean onResourceReady(Drawable drawable, Object obj, Target<Drawable> target, DataSource dataSource, boolean z) {
                                    profileImage.setVisibility(0);
                                    return false;
                                }
                            }).into(profileImage);
                        }

                        if (!isFinishing()) {
                            Glide.with(UserProfileActivity.this)
                                    .load(string)
                                    .centerCrop()
                                    .into(profileImage);

                        }

                        postCount.setText(String.valueOf(i));
                        followersCount.setText(String.valueOf(i2));
                        followingsCount.setText(String.valueOf(i3));

                        String sb1 = "@" + string2;
                        userName.setText(sb1);
                        fullName.setText(string3);

                    };
                    runOnUiThread(r0);
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

}