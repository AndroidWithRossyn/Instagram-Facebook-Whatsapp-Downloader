package com.allmy.allstatusdownloader.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.allmy.allstatusdownloader.Adapter.FragFavRecyclerAdapt;
import com.allmy.allstatusdownloader.Model.FavPrefSetter;
import com.allmy.allstatusdownloader.Model.FavStorySetter;
import com.allmy.allstatusdownloader.Others.FavSharedPreference;
import com.allmy.allstatusdownloader.R;

import org.json.JSONArray;

import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

public class FavUserActivity extends AppCompatActivity {

    SharedPreferences accountInfoPref;
    SharedPreferences cookiePref;
    List<Cookie> cookies;
    SharedPreferences currentUser;
    FragFavRecyclerAdapt fabRecyclerFragAdapt;
    ArrayList<FavStorySetter> fabdarray_story;
    ArrayList<FavPrefSetter> favorites;
    public boolean isRefreshSwap = false;
    SharedPreferences loginPref;
    public SwipeRefreshLayout mSwipeRefreshLayout = null;
    private RelativeLayout no_Userfound;
    public Paint paint;
    private String profilePic;
    public RecyclerView recyclerView;
    FavSharedPreference shrdprefernces;
    private Toolbar toolbar;
    SharedPreferences totalUserInfo;
    private JSONArray tray_array;
    private RelativeLayout upload_progress;
    ImageView ivBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav_user);

        profilePic = getIntent().getExtras().getString("profilePic");

        getLoginCookies();
        initilizeView();
        getPrefValues();
    }
    public void initilizeView() {
        favorites = new ArrayList<>();
        shrdprefernces = new FavSharedPreference();
        paint = new Paint();
        recyclerView = findViewById(R.id.recycler_view);
        upload_progress = findViewById(R.id.upload_progress);
        no_Userfound = findViewById(R.id.no_Userfound);
        ivBack = findViewById(R.id.ivBack);
        TextView tv_network = findViewById(R.id.tv_network);
        mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        ivBack.setOnClickListener(view -> finish());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(this) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        });
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);
    }

    private void getLoginCookies() {
        totalUserInfo = getSharedPreferences("TOTAL_USER_INFO", 0);
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
                cookies.add(Cookie.parse(Objects.requireNonNull(HttpUrl.get(new URL("https://i.instagram.com/"))), cookiePref.getString(String.valueOf(i2), str2)));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("WrongConstant")
    public void getPrefValues() {
        fabdarray_story = new ArrayList<>();
        favorites = shrdprefernces.getFavorites(this);


        ArrayList<FavPrefSetter> arrayList = favorites;
        if (arrayList == null || arrayList.size() <= 0) {
            upload_progress.setVisibility(4);
            no_Userfound.setVisibility(0);
            return;
        }
        no_Userfound.setVisibility(4);
        for (int i = 0; i < favorites.size(); i++) {
            FavPrefSetter favPrefSetter = favorites.get(i);
            String profile_picture = favPrefSetter.getProfile_picture();
            String userID = favPrefSetter.getUserID();
            String userName = favPrefSetter.getUserName();
            String userFullName = favPrefSetter.getUserFullName();
            String latest_reel_media = favPrefSetter.getLatest_reel_media();
            PrintStream printStream = System.out;
            String sb = "Values_FULLNAME : " +
                    userFullName;
            printStream.println(sb);
            FavStorySetter favStorySetter = new FavStorySetter();
            favStorySetter.setProfile_picture(profile_picture);
            favStorySetter.setID(userID);
            favStorySetter.setUserName(userName);
            favStorySetter.setUserFullName(userFullName);
            favStorySetter.setTimeAgo(latest_reel_media);
            fabdarray_story.add(favStorySetter);
        }
        setAdapter();
    }

    @SuppressLint("WrongConstant")
    private void setAdapter() {

        upload_progress.setVisibility(4);
        boolean isRefresh = true;
        fabRecyclerFragAdapt = new FragFavRecyclerAdapt(fabdarray_story, this, true);
        mSwipeRefreshLayout.setRefreshing(false);
        recyclerView.setAdapter(fabRecyclerFragAdapt);
        isRefreshSwap = true;
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            if (!isNetworkAvailable()) {
                FavUserActivity favUserActivity = FavUserActivity.this;
                Toast.makeText(favUserActivity, favUserActivity.getResources().getString(R.string.internet_connection), Toast.LENGTH_LONG).show();
            } else if (isRefreshSwap) {
                getPrefValues();
                isRefreshSwap = false;
                if (mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(true);
                }
            } else if (mSwipeRefreshLayout != null) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
            recyclerView.setAdapter(fabRecyclerFragAdapt);
        });
    }

    public boolean isNetworkAvailable() {
        @SuppressLint("WrongConstant") NetworkInfo activeNetworkInfo = ((ConnectivityManager) getApplicationContext().getSystemService("connectivity")).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}