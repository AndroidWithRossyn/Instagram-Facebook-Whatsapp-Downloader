package com.allmy.allstatusdownloader.Activity;

import androidx.annotation.Keep;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.allmy.allstatusdownloader.Adapter.UserSearchRecyclerviewAdapter;
import com.allmy.allstatusdownloader.Model.NewSearchUserSetter;
import com.allmy.allstatusdownloader.Others.Constants;
import com.allmy.allstatusdownloader.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
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
@Keep
public class ActivitySearch extends AppCompatActivity {

    public String URL_SEARCH;
    SharedPreferences accountInfoPref;
    SharedPreferences cookiePref;
    List<Cookie> cookies;
    SharedPreferences currentUser;
    public UserSearchRecyclerviewAdapter feedRecyclerviewAdapter;
    public ArrayList feedarray_story;
    public boolean isRefresh = false;
    public boolean isRefreshSwap = false;
    SharedPreferences loginPref;
    public SwipeRefreshLayout mSwipeRefreshLayout = null;
    public String newSearchName;
    public RelativeLayout no_Userfound;
    public RecyclerView recyclerView;
    public boolean searchAble = false;
    String searchText;
    SharedPreferences totalUserInfo;
    public TextView tv_network;
    public TextView txt_toolbar;
    public RelativeLayout upload_progress;
    public String userSearchName;
    public EditText etSearch;
    public ImageView backInsta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        backInsta = (ImageView) findViewById(R.id.backInsta);
        feedarray_story = new ArrayList();
        upload_progress = findViewById(R.id.upload_progress);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        no_Userfound = (RelativeLayout) findViewById(R.id.no_Userfound);
        tv_network = (TextView) findViewById(R.id.tv_network);
        etSearch = (EditText) findViewById(R.id.etSearch);
        etSearch.requestFocus();

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        setAllSearchOption();
        getLoginCookies();


        backInsta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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


    private void setAllSearchOption() {

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = s.toString();
                if (str.length() <= 0) {

                }
                searchAble = true;
                ActivitySearch activitySearch = ActivitySearch.this;
                activitySearch.searchText = str;
                if (activitySearch.isNetworkAvailable()) {
                    userSearchName = str.trim();
                    ActivitySearch activitySearch2 = ActivitySearch.this;
                    String str2 = " ";
                    String str3 = "";
                    activitySearch2.userSearchName = activitySearch2.userSearchName.replace(str2, str3);
                    newSearchName = str.trim();
                    ActivitySearch activitySearch3 = ActivitySearch.this;
                    activitySearch3.newSearchName = activitySearch3.newSearchName.replace(str2, str3);
                    if (!(newSearchName == null || newSearchName.length() == 0 || !searchAble)) {
                        isRefresh = true;
                        searchAble = false;
                        upload_progress.setVisibility(View.GONE);
                        ActivitySearch activitySearch4 = ActivitySearch.this;
                        StringBuilder sb = new StringBuilder();
                        sb.append("https://www.instagram.com/web/search/topsearch/?context=blended&query=");
                        sb.append(newSearchName);
                        activitySearch4.URL_SEARCH = sb.toString();
                        getUserList();


                        newSearchName = str3;
                    }
                } else {
                    ActivitySearch activitySearch5 = ActivitySearch.this;
                    Toast.makeText(activitySearch5, activitySearch5.getResources().getString(R.string.internet_connection), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String str = v.getText().toString();
                    if (str.length() <= 0) {
                        return false;
                    }
                    searchAble = true;
                    ActivitySearch activitySearch = ActivitySearch.this;
                    activitySearch.searchText = str;
                    if (activitySearch.isNetworkAvailable()) {
                        userSearchName = str.trim();
                        ActivitySearch activitySearch2 = ActivitySearch.this;
                        String str2 = " ";
                        String str3 = "";
                        activitySearch2.userSearchName = activitySearch2.userSearchName.replace(str2, str3);
                        newSearchName = str.trim();

                        ActivitySearch activitySearch3 = ActivitySearch.this;
                        activitySearch3.newSearchName = activitySearch3.newSearchName.replace(str2, str3);
                        if (!(newSearchName == null || newSearchName.length() == 0 || !searchAble)) {
                            isRefresh = true;
                            searchAble = false;
                            upload_progress.setVisibility(View.GONE);
                            ActivitySearch activitySearch4 = ActivitySearch.this;
                            StringBuilder sb = new StringBuilder();
                            sb.append("https://www.instagram.com/web/search/topsearch/?context=blended&query=");
                            sb.append(newSearchName);
                            activitySearch4.URL_SEARCH = sb.toString();
                            getUserList();
                            newSearchName = str3;
                        }
                    } else {
                        ActivitySearch activitySearch5 = ActivitySearch.this;
                        Toast.makeText(activitySearch5, activitySearch5.getResources().getString(R.string.internet_connection), Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private void getLoginCookies() {
        totalUserInfo = getSharedPreferences("TOTAL_USER_INFO", 0);
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
    }

    @SuppressLint("WrongConstant")
    public void getUserList() {
        no_Userfound.setVisibility(4);
        new OkHttpClient.Builder().cookieJar(new CookieJar() {
            public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
            }

            public List<Cookie> loadForRequest(HttpUrl httpUrl) {
                return cookies;
            }
        }).build().newCall(new Request.Builder().url(URL_SEARCH).addHeader("User-Agent", Constants.USER_AGENT).addHeader("Connection", "close").addHeader("Accept-Language", "en-US").addHeader("language", "en").addHeader("Accept", "*/*").addHeader("Content-type", "application/x-www-form-urlencoded; charset=UTF-8").build()).enqueue(new Callback() {
            public void onFailure(Call call, IOException iOException) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        upload_progress.setVisibility(4);
                    }
                });
            }

            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    public void run() {
                    }
                });
                String str = new String(response.body().string());


                writeToFile(str);
                if (feedarray_story != null) {
                    feedarray_story.clear();
                }
                try {
                    JSONArray jSONArray = new JSONObject(new String(str)).getJSONArray("users");
                    if (jSONArray != null) {
                        if (jSONArray.length() > 0) {
                            for (int i = 0; i < jSONArray.length(); i++) {
                                JSONObject jSONObject = jSONArray.getJSONObject(i).getJSONObject("user");
                                String string = jSONObject.getString("pk");
                                String string2 = jSONObject.getString("username");
                                String string3 = jSONObject.getString("full_name");
                                String string4 = jSONObject.getString("profile_pic_url");

                                NewSearchUserSetter newSearchUserSetter = new NewSearchUserSetter();
                                newSearchUserSetter.setUserFullName(string3);
                                newSearchUserSetter.setUserName(string2);
                                newSearchUserSetter.setUserID(string);
                                newSearchUserSetter.setProfile_picture(string4);
                                feedarray_story.add(newSearchUserSetter);
                                String optString = jSONObject.optString("latest_reel_media");

                                PrintStream printStream = System.out;
                                StringBuilder sb = new StringBuilder();
                                sb.append("isStoryAvailable : ");
                                sb.append(optString);
                                printStream.println(sb.toString());
                                PrintStream printStream2 = System.out;
                                StringBuilder sb2 = new StringBuilder();
                                sb2.append("user_ID : ");
                                sb2.append(string);
                                printStream2.println(sb2.toString());
                                PrintStream printStream3 = System.out;
                                StringBuilder sb3 = new StringBuilder();
                                sb3.append("user_NAME : ");
                                sb3.append(string2);
                                printStream3.println(sb3.toString());
                                PrintStream printStream4 = System.out;
                                StringBuilder sb4 = new StringBuilder();
                                sb4.append("user_FULLNAME : ");
                                sb4.append(string3);
                                printStream4.println(sb4.toString());
                                PrintStream printStream5 = System.out;
                                StringBuilder sb5 = new StringBuilder();
                                sb5.append("user_PICURL : ");
                                sb5.append(string4);
                                printStream5.println(sb5.toString());
                            }
                        } else {
                            runOnUiThread(new Runnable() {
                                public void run() {

                                    Toast.makeText(ActivitySearch.this, "No Data Found ", 0).show();
                                    feedRecyclerviewAdapter = new UserSearchRecyclerviewAdapter(feedarray_story, ActivitySearch.this, isRefresh);
                                    recyclerView.setAdapter(feedRecyclerviewAdapter);
                                    feedRecyclerviewAdapter.notifyDataSetChanged();
                                    upload_progress.setVisibility(4);
                                    no_Userfound.setVisibility(0);
                                    if (!isNetworkAvailable()) {
                                        tv_network.setVisibility(0);
                                    }
                                }
                            });
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        setAdapter();
                    }
                });
            }
        });
    }

    @SuppressLint("WrongConstant")
    public void setAdapter() {
        ArrayList arrayList = feedarray_story;
        if (arrayList == null) {
            return;
        }
        if (arrayList.size() > 0) {

            mSwipeRefreshLayout.setRefreshing(false);
            upload_progress.setVisibility(4);
            feedRecyclerviewAdapter = new UserSearchRecyclerviewAdapter(feedarray_story, this, isRefresh);
            recyclerView.setAdapter(feedRecyclerviewAdapter);
            mSwipeRefreshLayout.setRefreshing(false);
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    isRefreshSwap = true;
                }
            }, 1000);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                public void onRefresh() {
                    if (!isNetworkAvailable()) {
                        if (mSwipeRefreshLayout != null) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                        ActivitySearch activitySearch = ActivitySearch.this;
                        Toast.makeText(activitySearch, activitySearch.getResources().getString(R.string.internet_connection), 0).show();
                    } else if (isRefreshSwap) {
                        getUserList();
                        if (mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing()) {
                            mSwipeRefreshLayout.setRefreshing(true);
                        }
                    } else if (mSwipeRefreshLayout != null) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }
            });

        }
        feedRecyclerviewAdapter = new UserSearchRecyclerviewAdapter(feedarray_story, this, isRefresh);
        recyclerView.setAdapter(feedRecyclerviewAdapter);
        SwipeRefreshLayout swipeRefreshLayout = mSwipeRefreshLayout;
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
        if (!isNetworkAvailable()) {
            tv_network.setVisibility(0);
        }
    }

    public boolean isNetworkAvailable() {
        @SuppressLint("WrongConstant") NetworkInfo activeNetworkInfo = ((ConnectivityManager) getSystemService("connectivity")).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void writeToFile(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.DIRECTORY_DCIM);
        sb.append("/testingFile/");
        File externalStoragePublicDirectory = Environment.getExternalStoragePublicDirectory(sb.toString());
        if (!externalStoragePublicDirectory.exists()) {
            externalStoragePublicDirectory.mkdirs();
        }
        File file = new File(externalStoragePublicDirectory, "search.txt");
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

        }
    }
}