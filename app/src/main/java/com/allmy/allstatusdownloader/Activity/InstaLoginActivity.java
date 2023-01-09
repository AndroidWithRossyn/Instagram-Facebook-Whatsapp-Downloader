package com.allmy.allstatusdownloader.Activity;

import androidx.annotation.Keep;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.allmy.allstatusdownloader.Others.FBhelper;
import com.allmy.allstatusdownloader.R;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
@Keep
public class InstaLoginActivity extends AppCompatActivity {

    String BASE_URL = "https://i.instagram.com/api/v1/";
    private final String COOKIE_PREF_BASE_FLAG = "COOKIE_PREF";
    private final String LOGIN_PREF_BASE_FLAG = "LOGIN";
    List<Cookie> cookies;
    SharedPreferences cookiesPref;
    int count = 0;
    String csrftoken;
    SharedPreferences currentUser;
    public Handler handler = new Handler(new Handler.Callback() {
        public boolean handleMessage(Message message) {
            if (message.what == 1) {
                webViewGoBack();
            }
            return true;
        }
    });
    SharedPreferences loginPref;
    public WebView mWebviewcontainer;
    boolean parsUser = false;
    ProgressBar progressBar1;
    SharedPreferences totalUserInfo;
    String user_id;

    private class MyWebChromeClient extends WebChromeClient {
        private MyWebChromeClient() {
        }

        public boolean onCreateWindow(WebView webView, boolean z, boolean z2, Message message) {
            InstaLoginActivity loginInstagram = InstaLoginActivity.this;
            loginInstagram.mWebviewcontainer = new WebView(loginInstagram);
            WebSettings settings = InstaLoginActivity.this.mWebviewcontainer.getSettings();
            settings.setJavaScriptEnabled(true);
            settings.setSupportZoom(true);
            settings.setBuiltInZoomControls(true);
            settings.setSupportMultipleWindows(true);
            webView.addView(InstaLoginActivity.this.mWebviewcontainer);
            ((WebView.WebViewTransport) message.obj).setWebView(InstaLoginActivity.this.mWebviewcontainer);
            message.sendToTarget();
            InstaLoginActivity.this.mWebviewcontainer.setWebViewClient(new WebClientClass());
            return true;
        }
    }
    public class WebClientClass extends WebViewClient {
        public WebClientClass() {
        }

        @SuppressLint("WrongConstant")
        public void onPageFinished(WebView webView, String str) {
            PrintStream printStream = System.out;
            StringBuilder sb = new StringBuilder();
            sb.append("onPageFinished : ");
            sb.append(str);


            printStream.println(sb.toString());
            String cookie = CookieManager.getInstance().getCookie(str);
            boolean access$100 = InstaLoginActivity.this.isvalidCookies(cookie);
            PrintStream printStream2 = System.out;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("cookies valid onPageFinished : ");
            sb2.append(access$100);
            printStream2.println(sb2.toString());


            if (str.equalsIgnoreCase("https://www.instagram.com")) {
                InstaLoginActivity.this.mWebviewcontainer.setVisibility(4);
                InstaLoginActivity.this.progressBar1.setVisibility(0);
            }
            if (access$100) {
                System.out.println("ccalled parsing on page finish");
                webView.setVisibility(8);
                InstaLoginActivity.this.progressBar1.setVisibility(0);
                InstaLoginActivity.this.parseUserView(webView, cookie);
            } else {
                String cookie2 = CookieManager.getInstance().getCookie(str);

                boolean access$1002 = InstaLoginActivity.this.isvalidCookies(cookie2);


                if (str.contains("https://www.instagram.com/") && access$1002) {
                    webView.setVisibility(8);
                    InstaLoginActivity.this.progressBar1.setVisibility(0);
                    System.out.println("ccalled parsing on page finish else");

                    InstaLoginActivity.this.parseUserView(webView, cookie2);
                }
            }
            super.onPageFinished(webView, str);
        }

        public void onPageStarted(WebView webView, String str, Bitmap bitmap) {
            super.onPageStarted(webView, str, bitmap);
        }

        public void onReceivedError(WebView webView, int i, String str, String str2) {
            super.onReceivedError(webView, i, str, str2);
        }

        public void onReceivedError(WebView webView, WebResourceRequest webResourceRequest, WebResourceError webResourceError) {
            super.onReceivedError(webView, webResourceRequest, webResourceError);
        }

        public boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest webResourceRequest) {
            if (Build.VERSION.SDK_INT >= 21) {
                return m11431a(webView, webResourceRequest.getUrl().toString());
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return m11431a(webView, webResourceRequest.getUrl().toString());
            }
            return false;
        }

        @Deprecated
        public boolean shouldOverrideUrlLoading(WebView webView, String str) {
            return m11431a(webView, str);
        }

        @SuppressLint("WrongConstant")
        private boolean m11431a(WebView webView, String str) {
            PrintStream printStream = System.out;
            StringBuilder sb = new StringBuilder();
            sb.append("shouldOverrideUrlLoading : ");
            sb.append(str);
            printStream.println(sb.toString());
            String cookie = CookieManager.getInstance().getCookie(str);
            boolean access$100 = InstaLoginActivity.this.isvalidCookies(cookie);
            PrintStream printStream2 = System.out;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("cookies valid shouldOverrideUrlLoading : ");
            sb2.append(access$100);
            printStream2.println(sb2.toString());
            if (access$100) {
                webView.setVisibility(8);
                InstaLoginActivity.this.progressBar1.setVisibility(0);
                System.out.println("ccalled parsing on shouldOverrideUrlLoading");
                InstaLoginActivity.this.parseUserView(webView, cookie);
            } else {
                String cookie2 = CookieManager.getInstance().getCookie(str);
                boolean access$1002 = InstaLoginActivity.this.isvalidCookies(cookie2);
                PrintStream printStream3 = System.out;
                StringBuilder sb3 = new StringBuilder();
                sb3.append("cookies valid shouldOverrideUrlLoading else: ");
                sb3.append(access$1002);
                printStream3.println(sb3.toString());
                if (access$1002) {
                    webView.setVisibility(8);
                    InstaLoginActivity.this.progressBar1.setVisibility(0);
                    InstaLoginActivity.this.parseUserView(webView, cookie2);
                } else {
                    String str2 = "https://www.instagram.com/";
                    if (!str.startsWith("http") && !str.startsWith("https")) {
                        webView.loadUrl(str2);
                    } else if (str.contains("https://www.instagram.com/#reactivated")) {
                        webView.loadUrl(str2);
                    } else {
                        webView.loadUrl(str);
                    }
                }
            }
            return true;
        }
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        this.mWebviewcontainer.saveState(bundle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insta_login);

        this.parsUser = false;
        this.cookies = new ArrayList();

        initlizeView();
        setUpClient(savedInstanceState);
    }
    private void setUpClient(Bundle bundle) {
        new FBhelper(this).createCookiManger(this);
        this.mWebviewcontainer.setWebViewClient(new WebClientClass());
        this.mWebviewcontainer.setWebChromeClient(new MyWebChromeClient());
        if (Build.VERSION.SDK_INT >= 19) {
            this.mWebviewcontainer.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            this.mWebviewcontainer.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        WebSettings settings = this.mWebviewcontainer.getSettings();
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setPluginState(WebSettings.PluginState.ON);
        settings.setJavaScriptEnabled(true);
        settings.setSupportMultipleWindows(true);
        settings.setDomStorageEnabled(true);
        settings.setBuiltInZoomControls(true);
        settings.setSupportZoom(true);
        Configuration configuration = getResources().getConfiguration();
        if (Build.VERSION.SDK_INT > 14) {
            settings.setTextZoom((int) (configuration.fontScale * 100.0f));
        }
        if (Build.VERSION.SDK_INT >= 21) {
            settings.setMixedContentMode(0);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(this.mWebviewcontainer, true);
        }
        if (bundle == null) {
            this.mWebviewcontainer.loadUrl(" https://www.instagram.com/accounts/login/");
            this.mWebviewcontainer.setInitialScale(0);
            this.mWebviewcontainer.requestFocus();
            this.mWebviewcontainer.requestFocusFromTouch();
            return;
        }
        this.mWebviewcontainer.restoreState(bundle);
        this.mWebviewcontainer.requestFocus();
        this.mWebviewcontainer.requestFocusFromTouch();
    }

    public void webViewGoBack() {
        this.mWebviewcontainer.goBack();
    }

    private void initlizeView() {
        this.mWebviewcontainer = findViewById(R.id.webView);
        this.progressBar1 = findViewById(R.id.progressBar1);

        this.mWebviewcontainer.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i != 4 || !InstaLoginActivity.this.mWebviewcontainer.canGoBack()) {
                    return false;
                }
                InstaLoginActivity.this.handler.sendEmptyMessage(1);
                return true;
            }
        });
    }

    private void fetchUserInfo(String str) {
        String sb = this.BASE_URL + "users/" + str + "/info/";

        new OkHttpClient.Builder().addInterceptor(chain -> {
            try {
                return chain.proceed(chain.request().newBuilder().header("User-Agent", "Instagram 10.3.2 Android (18/4.3; 320dpi; 720x1280; Xiaomi; HM 1SW; armani; qcom; en_US").header("Connection", "close").header("language", "en").header("Accept", "*/*").header("X-IG-Capabilities", "3QI=").header("Content-type", "application/x-www-form-urlencoded; charset=UTF-8").build());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }).cookieJar(new CookieJar() {
            public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
            }

            public List<Cookie> loadForRequest(HttpUrl httpUrl) {
                return InstaLoginActivity.this.cookies;
            }
        }).build().newCall(new Request.Builder().url(sb).build()).enqueue(new okhttp3.Callback() {
            public void onFailure(Call call, IOException iOException) {
            }

            public void onResponse(Call call, Response response) {
                String str = "CURRENT_USER";
                String str2 = "TOTAL_USER";
                String string = "";
                try {
                    string = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                PrintStream printStream = System.out;
                StringBuilder sb = new StringBuilder();
                sb.append("response  : ");
                sb.append(string);
                printStream.println(sb.toString());
                count++;

                try {
                    JSONObject jSONObject = new JSONObject(string).getJSONObject("user");


                    String string2 = jSONObject.getString("pk");
                    String string3 = jSONObject.getString("profile_pic_url");
                    jSONObject.getInt("media_count");
                    jSONObject.getInt("follower_count");
                    jSONObject.getInt("following_count");
                    String string4 = jSONObject.getString("username");
                    String string5 = jSONObject.getString("full_name");
                    PrintStream printStream3 = System.out;
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("user : username");
                    sb3.append(jSONObject);
                    sb3.append(" : ");
                    sb3.append(string4);
                    printStream3.println(sb3.toString());
                    InstaLoginActivity.this.currentUser = InstaLoginActivity.this.getSharedPreferences(str, 0);
                    SharedPreferences.Editor edit = InstaLoginActivity.this.currentUser.edit();
                    edit.putString(str, string4.toLowerCase(Locale.getDefault()).trim());
                    edit.commit();
                    InstaLoginActivity.this.totalUserInfo = InstaLoginActivity.this.getSharedPreferences("TOTAL_USER_INFO", 0);
                    SharedPreferences.Editor edit2 = InstaLoginActivity.this.totalUserInfo.edit();
                    String string6 = InstaLoginActivity.this.totalUserInfo.getString(str2, null);
                    if (string6 != null) {
                        Gson gson = new Gson();
                        String[] strArr = gson.fromJson(string6, String[].class);
                        ArrayList arrayList = new ArrayList();
                        Collections.addAll(arrayList, strArr);
                        if (arrayList.contains(string4)) {
                            InstaLoginActivity loginInstagram = InstaLoginActivity.this;
                            InstaLoginActivity loginInstagram2 = InstaLoginActivity.this;
                            StringBuilder sb4 = new StringBuilder();
                            sb4.append("LOGIN_");
                            sb4.append(string4.toLowerCase(Locale.getDefault()).trim());
                            loginInstagram.loginPref = loginInstagram2.getSharedPreferences(sb4.toString(), 0);
                            InstaLoginActivity.this.intentNextActivity();
                            return;
                        }
                        arrayList.add(string4.toLowerCase(Locale.getDefault()).trim());
                        String json = gson.toJson(arrayList);
                        edit2.putString(str2, json);
                        edit2.commit();
                        PrintStream printStream4 = System.out;
                        StringBuilder sb5 = new StringBuilder();
                        sb5.append("jsonText : ");
                        sb5.append(json);
                        printStream4.println(sb5.toString());
                        InstaLoginActivity.this.saveData(string4, string2, string3, string5);
                        return;
                    }
                    ArrayList arrayList2 = new ArrayList();
                    arrayList2.add(string4.toLowerCase(Locale.getDefault()).trim());
                    String json2 = new Gson().toJson(arrayList2);
                    edit2.putString(str2, json2);
                    edit2.commit();
                    PrintStream printStream5 = System.out;
                    StringBuilder sb6 = new StringBuilder();
                    sb6.append("jsonText Old : ");
                    sb6.append(json2);
                    printStream5.println(sb6.toString());
                    InstaLoginActivity.this.saveData(string4, string2, string3, string5);
                } catch (JSONException e) {
                    e.printStackTrace();


                }
            }
        });
    }

    public void saveData(String str, String str2, String str3, String str4) {

        Cookie cookie;
        StringBuilder sb = new StringBuilder();
        sb.append("LOGIN_");
        sb.append(str.toLowerCase(Locale.getDefault()).trim());
        this.loginPref = getSharedPreferences(sb.toString(), 0);
        SharedPreferences.Editor edit = this.loginPref.edit();
        edit.putBoolean("IS_LOGIN", true);
        edit.putString("USER_ID", str2);
        edit.putString("profile_pic_url", str3);
        edit.putString("full_name", str4);
        edit.putString("uuid", UUID.randomUUID().toString());
        edit.putString("csrftoken", this.csrftoken);
        edit.putString("phone_id", UUID.randomUUID().toString());
        edit.putString("user_name", str);

        Log.e("UserId--)",""+str+"--)"+str3+"--)"+str4);
        edit.commit();
        try {
            HttpUrl httpUrl = HttpUrl.get(new URL("https://i.instagram.com/"));
            StringBuilder sb2 = new StringBuilder();
            sb2.append("ds_user=");
            sb2.append(str);
            sb2.append("; path=/");
            cookie = Cookie.parse(httpUrl, sb2.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            cookie = null;
        }
        this.cookies.add(cookie);
        StringBuilder sb3 = new StringBuilder();
        sb3.append("COOKIE_PREF_");
        sb3.append(str.toLowerCase(Locale.getDefault()).trim());
        this.cookiesPref = getSharedPreferences(sb3.toString(), 0);
        SharedPreferences.Editor edit2 = this.cookiesPref.edit();
        PrintStream printStream = System.out;
        StringBuilder sb4 = new StringBuilder();
        sb4.append("size : ");
        sb4.append(this.cookies.size());
        printStream.println(sb4.toString());
        for (int i = 0; i < this.cookies.size(); i++) {
            edit2.putString(String.valueOf(i), this.cookies.get(i).toString());
        }
        edit2.putInt("cookie_count", this.cookies.size());
        edit2.commit();
        myPrefrences();

        runOnUiThread(new Runnable() {
            public void run() {
                intentNextActivity();
            }
        });

    }
    public void intentNextActivity() {
        Intent intent = new Intent(this, com.allmy.allstatusdownloader.Activity.ActivitywithDrawer.class);
        String str = "USER_ID";
        intent.putExtra(str, this.loginPref.getString(str, null));
        String str2 = "profile_pic_url";
        intent.putExtra(str2, this.loginPref.getString(str2, null));
        String str3 = "full_name";
        intent.putExtra(str3, this.loginPref.getString(str3, null));
        String str4 = "csrftoken";
        intent.putExtra(str4, this.loginPref.getString(str4, null));
        String str5 = "phone_id";
        intent.putExtra(str5, this.loginPref.getString(str5, null));
        String str6 = "user_name";
        intent.putExtra(str6, this.loginPref.getString(str6, null));
        startActivity(intent);
        finish();
    }
    private void goClass() {
        myPrefrences();
        Intent intent = new Intent(this, com.allmy.allstatusdownloader.Activity.ActivitywithDrawer.class);
        String str = "USER_ID";
        intent.putExtra(str, this.loginPref.getString(str, null));
        String str2 = "profile_pic_url";
        intent.putExtra(str2, this.loginPref.getString(str2, null));
        String str3 = "full_name";
        intent.putExtra(str3, this.loginPref.getString(str3, null));
        String str4 = "csrftoken";
        intent.putExtra(str4, this.loginPref.getString(str4, null));
        String str5 = "phone_id";
        intent.putExtra(str5, this.loginPref.getString(str5, null));
        String str6 = "user_name";
        intent.putExtra(str6, this.loginPref.getString(str6, null));
        startActivity(intent);
        finish();
    }

    private void myPrefrences() {
        SharedPreferences.Editor edit = getSharedPreferences("csrf", 0).edit();
        edit.putString("csrftoken", this.csrftoken);
        edit.commit();
        String str = "phoneid";
        SharedPreferences.Editor edit2 = getSharedPreferences(str, 0).edit();
        edit2.putString(str, this.loginPref.getString("phone_id", null));
        edit2.apply();
        String str2 = "USER_NAME";
        SharedPreferences.Editor edit3 = getSharedPreferences(str2, 0).edit();
        edit3.putString(str2, this.loginPref.getString("user_name", null));
        edit3.apply();
        String str3 = "PROFILE_PICK_URL";
        SharedPreferences.Editor edit4 = getSharedPreferences(str3, 0).edit();
        edit4.putString(str3, this.loginPref.getString("profile_pic_url", null));
        edit4.apply();
    }

    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, com.allmy.allstatusdownloader.Activity.InstaDownloadActivity.class));
    }

    public boolean isvalidCookies(String str) {
        return new FBhelper(this).valadateCooki(str);
    }
    public void parseUserView(WebView webView, String str) {
        String[] split;
        String str2 = "csrftoken";
        if (!this.parsUser) {
            this.parsUser = true;
            webView.stopLoading();
            for (String str3 : str.split(";")) {
                PrintStream printStream = System.out;
                StringBuilder sb = new StringBuilder();
                sb.append("parse  : ");
                sb.append(str3);
                printStream.println(sb.toString());

                try {
                    Cookie parse = Cookie.parse(HttpUrl.get(new URL("https://i.instagram.com/")), str3);
                    if (!this.cookies.contains(str2)) {
                        PrintStream printStream2 = System.out;
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("csrftoken : ");
                        sb2.append(parse.toString());
                        printStream2.println(sb2.toString());
                        this.cookies.add(parse);
                    }
                } catch (MalformedURLException e) {

                    e.printStackTrace();
                }
                String str4 = "ds_user_id=";
                String str5 = " ";
                String str6 = "";
                if (str3.contains(str4)) {
                    this.user_id = str3.replace(str4, str6);
                    this.user_id = this.user_id.replace(str5, str6);
                    PrintStream printStream3 = System.out;
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("user id : ");
                    sb3.append(this.user_id);
                    printStream3.println(sb3.toString());
                }
                if (str3.contains(str2)) {
                    this.csrftoken = str3.replace("csrftoken=", str6);
                    this.csrftoken = this.csrftoken.replace(str5, str6);
                }
            }

            fetchUserInfo(this.user_id);
        }
    }
}