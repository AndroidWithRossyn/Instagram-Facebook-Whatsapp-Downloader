package com.allmy.allstatusdownloader.Activity;

import static android.content.Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.allmy.allstatusdownloader.Others.ConnectionDetector;
import com.allmy.allstatusdownloader.Others.CustomWebView;
import com.allmy.allstatusdownloader.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import cz.msebera.android.httpclient.HttpHost;
import cz.msebera.android.httpclient.protocol.HTTP;

public class FacebookLoginActivity extends AppCompatActivity {

    String base_url = "https://www.facebook.com/";
    BottomNavigationView bottomAppBar;
    ConnectionDetector connectionDetector;
    public Handler handler = new Handler(message -> {
        if (message.what == 1) {
            webViewGoBack();
        }
        return true;
    });
    public String dnlUrl,savePath;
    boolean isvideo = true;
    public CustomWebView mWebView;
    SharedPreferences prefs;
    ProgressBar progressBar1;
    RelativeLayout reload_view;
    TextView reloadviewtxtx, txtProgress;
    boolean restart_view;
    String stringFileName;
    String showview = "yes";
    String subStringUrl1;
    private RelativeLayout coordinatorLayout;
    private ProgressDialog dialog;
    private LinearLayout lBtnHome, lBtnRefresh, lBtnExit, lBtnLogout, lDownload, linearDownload;
    private LinearLayout ivBackPage, ivForwardPage;
    CardView ib_im_download;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_login);

        dialog = new ProgressDialog(FacebookLoginActivity.this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        lBtnHome = findViewById(R.id.lBtnHome);
        lBtnRefresh = findViewById(R.id.lBtnRefresh);
        lBtnExit = findViewById(R.id.lBtnExit);
        lBtnLogout = findViewById(R.id.lBtnLogout);
        ivBackPage = findViewById(R.id.ivBackPage);
        lDownload = findViewById(R.id.lDownload);
        ib_im_download = findViewById(R.id.ib_im_download);
        ivForwardPage = findViewById(R.id.ivForwardPage);
        reload_view = findViewById(R.id.reload_view);
        reloadviewtxtx = findViewById(R.id.reloadviewtxtx);
        progressBar = findViewById(R.id.progressBar);
        txtProgress = findViewById(R.id.txtProgress);
        linearDownload = findViewById(R.id.linearDownload);

        reload_view.setVisibility(View.GONE);
        reloadviewtxtx.setVisibility(View.GONE);

        restart_view = false;
        isvideo = true;
        connectionDetector = new ConnectionDetector(this);
        getIntentValue();
        initlizeView();
        initlizeObject();
        setUpClient(savedInstanceState);

        bottomAppBar = findViewById(R.id.btbar);

        lBtnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWebView.loadUrl("https://www.facebook.com/");
            }
        });

        lBtnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        lBtnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mWebView.clearCache(true);
                mWebView.clearHistory();
                android.webkit.CookieManager.getInstance().removeAllCookie();
                finish();
            }
        });

        lBtnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWebView.reload();
            }
        });

        ivBackPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWebView.goBack();
            }
        });

        ivForwardPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mWebView.canGoForward()) {
                    mWebView.goForward();
                }
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    public class WebClientClass extends WebViewClient {
        public WebClientClass() {
        }

        @SuppressLint({"SetJavaScriptEnabled"})
        public boolean shouldOverrideUrlLoading(WebView webView, String str) {
            String str2 = "&source";
            String str3 = "src=";
            if (!str.contains(".mp4")) {
                webView.loadUrl(str);
            } else if (isvideo) {
                isvideo = false;
                try {
                    String decode = URLDecoder.decode(str, HTTP.ASCII);
                    if (decode.contains(str3) && decode.contains(str2)) {
                        subStringUrl1 = decode.substring(decode.indexOf(str3), decode.indexOf(str2));
                        subStringUrl1 = subStringUrl1.substring(subStringUrl1.indexOf("https:"));
                        showMyDialog(webView);
                    }
                    return true;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return false;
                }
            }
            return false;
        }

        public void onLoadResource(WebView webView, String str) {
            String str2 = ".mp4";
            if (str.contains(str2) && isvideo) {
                isvideo = false;
                try {
                    String decode = URLDecoder.decode(str, HTTP.ASCII);
                    if (decode.contains(str2)) {
                        subStringUrl1 = decode;
                        showMyDialog(webView);
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }

        @SuppressLint("WrongConstant")
        public void onReceivedError(WebView webView, WebResourceRequest webResourceRequest, WebResourceError webResourceError) {
            if (connectionDetector.isConnectingToInternet()) {
                webView.loadUrl("https://www.facebook.com/");
                return;
            }
            reloadviewtxtx.setVisibility(0);
            reload_view.setVisibility(0);
            mWebView.setVisibility(8);
            reload_view.setOnClickListener(view -> {
                reload_view.setVisibility(8);
                if (connectionDetector.isConnectingToInternet()) {
                    reloadviewtxtx.setVisibility(8);
                    mWebView.loadUrl("https://www.facebook.com/");
                    mWebView.setVisibility(0);
                    return;
                }
                mWebView.setVisibility(8);
                Toast.makeText(FacebookLoginActivity.this, "Please check internet connection.", Toast.LENGTH_LONG).show();
                reload_view.setVisibility(0);
                reloadviewtxtx.setVisibility(0);
            });
        }

        public void onPageFinished(WebView webView, String str) {
            super.onPageFinished(webView, str);
        }

        public WebResourceResponse shouldInterceptRequest(WebView webView, WebResourceRequest webResourceRequest) {
            return super.shouldInterceptRequest(webView, webResourceRequest);
        }
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        mWebView.saveState(bundle);
    }

    private void getIntentValue() {
        if (getIntent() != null) {
            base_url = getIntent().getStringExtra("url");
            showview = getIntent().getStringExtra("showview");
            String str = showview;
            if (str == null || str.isEmpty()) {
                showview = "yes";
            }
            String str2 = base_url;
            if (str2 == null || str2.isEmpty()) {
                base_url = "https://www.facebook.com/";
            }
            base_url = base_url.trim();
            try {
                base_url = URLDecoder.decode(base_url, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    protected void onRestart() {
        super.onRestart();
    }

    @SuppressLint({"WrongConstant", "SetJavaScriptEnabled"})
    private void setUpClient(Bundle bundle) {
        getWindow().setFlags(FLAG_ACTIVITY_PREVIOUS_IS_TOP, FLAG_ACTIVITY_PREVIOUS_IS_TOP);
        mWebView.setWebViewClient(new WebClientClass());
        WebSettings settings = mWebView.getSettings();
        settings.setCacheMode(2);
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setPluginState(WebSettings.PluginState.ON);
        settings.setJavaScriptEnabled(true);
        settings.setUserAgentString("Mozilla/5.0 (Linux; U; Android 2.0; en-us; Droid Build/ESD20) AppleWebKit/530.17 (KHTML, like Gecko) Version/4.0 Mobile Safari/530.17");
        settings.setDomStorageEnabled(true);
        settings.setBuiltInZoomControls(true);
        settings.setSupportZoom(true);
        Configuration configuration = getResources().getConfiguration();
        settings.setTextZoom((int) (configuration.fontScale * 100.0f));
        if (Build.VERSION.SDK_INT >= 19) {
            mWebView.setLayerType(2, null);
        } else {
            mWebView.setLayerType(1, null);
        }
        if (bundle == null) {
            mWebView.loadUrl(base_url);
            mWebView.setInitialScale(0);
        } else {
            mWebView.restoreState(bundle);
        }
        mWebView.requestFocus();
        mWebView.requestFocusFromTouch();
        mWebView.setOnKeyListener((view, i, keyEvent) -> {
            if (keyEvent.getAction() == 0) {
                WebView webView = (WebView) view;
                if (i == 4 && webView.canGoBack()) {
                    webView.goBack();
                    return true;
                }
            }
            return false;
        });
    }

    @SuppressLint("WrongConstant")
    private void initlizeView() {
        progressBar1 = findViewById(R.id.progressBar1);
        mWebView = findViewById(R.id.webView);
        mWebView.setLayerType(1, null);
        mWebView.setOnKeyListener((view, i, keyEvent) -> {
            if (i != 4 || !mWebView.canGoBack()) {
                return false;
            }
            handler.sendEmptyMessage(1);
            return true;
        });
    }

    public void showInterstitialDownload() {
        String str = subStringUrl1;

        Log.e("subStringUrl1--)", "" + subStringUrl1);

        if (str != null && str.contains(".mp4")) {

            linearDownload.setVisibility(View.GONE);
            txtProgress.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            downloadVideo(subStringUrl1);

        }
    }

    protected void onPause() {
        super.onPause();
    }

    protected void onStop() {
        super.onStop();
    }

    public void showInterstitialShare() {
        share();
    }

    private void downloadVideo(String str) {
        String strFileName;
        if (str.matches("^(https|ftp)://.*$")) {
            String substring = str.substring(5);
            str = HttpHost.DEFAULT_SCHEME_NAME + substring;
        }

        Timestamp timestamp = new Timestamp((int) System.currentTimeMillis());
        String currentTimeStamp = String.valueOf(System.currentTimeMillis());
        if (currentTimeStamp != null) {
            strFileName = currentTimeStamp;
        } else {
            strFileName = timestamp.toString();
        }
        Log.e("strFileName--)", "" + strFileName);
        stringFileName = strFileName;
        Log.e("strFileName1--)", "" + strFileName);

        new DownloadTask(getApplicationContext()).execute(str);
    }

    private class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                Log.e("TAG PD", "Doin back");
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM)
                {
                    Log.e("TAG PD", "Doin back trying move perm redirect");
                    String redirect = connection.getHeaderField("Location");

                    if (redirect != null){
                        connection = (HttpURLConnection) new URL(redirect).openConnection();
                    }
                    connection.connect();
                    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        Log.e("TAG PD", "Doin back redirect fail");
                        return "Server returned HTTP " + connection.getResponseCode()
                                + " " + connection.getResponseMessage();
                    }
                }
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e("TAG PD", "Doin back redirect fail2");
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }
                int fileLength = connection.getContentLength();

                input = connection.getInputStream();
                File file = new File(getExternalFilesDir(getApplicationContext().getResources().getString(R.string.app_name)).toString());

                if (!file.exists()) {
                    file.mkdirs();
                }

                File repostfile = new File(file, System.currentTimeMillis() + ".mp4");

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
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {

                Log.e("TAG PD", e+" " + e.getMessage());
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
            Log.e("TAG PD", "post exc result"+result);
            if (result != null) {
                Toast.makeText(context, "Download Error", Toast.LENGTH_SHORT).show();
            } else {
                setPr(100);
                ib_im_download.setEnabled(true);

                File file1 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), getResources().getString(R.string.app_name));
                if (!file1.exists()) {
                    file1.mkdirs();
                }
                File repostfile = new File(file1, System.currentTimeMillis() + ".mp4");

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
                    Log.e("TAG PD", "post exe catch exception "+e);
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
            Log.e("TAG PD", "movefile exception "+fnfe1);
        } catch (Exception e) {
            Log.e("TAG PD", "move file exception "+e);
        }
    }


    @SuppressLint("WrongConstant")
    private void initlizeObject() {
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //mgr = (DownloadManager) getSystemService("download");
    }

    private void scanFile(String path) {
        MediaScannerConnection.scanFile(FacebookLoginActivity.this,
                new String[]{path}, null,
                (path1, uri) -> Log.e("TAG--)", "Finished scanning " + path1));
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

    public static String getCurrentTimeStamp() {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void ifPermissionRevokeone() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You need to give permission to download video");
        builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel());
        builder.setPositiveButton("Give Permission", (dialogInterface, i) -> {
            dialogInterface.cancel();
            ActivityCompat.requestPermissions(FacebookLoginActivity.this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 7);
        });
        builder.show();
    }

    private void ifPermissionRevoke() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You need to give permission to download video");
        builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel());
        builder.setPositiveButton("Give Permission", (dialogInterface, i) -> {
            dialogInterface.cancel();
            ActivityCompat.requestPermissions(FacebookLoginActivity.this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 14);
        });
        builder.show();
    }

    public void onRequestPermissionsResult(int i, @NonNull String[] strArr, @NonNull int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        String str = "android.permission.WRITE_EXTERNAL_STORAGE";
        if (i != 7) {
            if (i != 14 || iArr.length <= 0 || !strArr[0].equals(str)) {
                return;
            }
            if (iArr[0] == 0) {
                String str2 = subStringUrl1;
                if (str2 != null && !str2.isEmpty()) {
                    showInterstitialShare();
                    return;
                }
                return;
            }
            ifPermissionRevoke();
        } else if (iArr.length > 0 && strArr[0].equals(str)) {
            if (iArr[0] == 0) {
                String str3 = subStringUrl1;
                if (str3 != null && !str3.isEmpty()) {
                    showInterstitialDownload();
                    return;
                }
                return;
            }
            ifPermissionRevokeone();
        }
    }

    public void webViewGoBack() {
        CustomWebView customWebView = mWebView;
        if (customWebView != null) {
            customWebView.goBack();
        }
    }

    public void showMyDialog(final WebView webView) {

        lDownload.setVisibility(View.VISIBLE);



        ib_im_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (progressBar.getProgress() == 100) {

                    linearDownload.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    txtProgress.setVisibility(View.GONE);
                    progressBar.setProgress(0);

                    startActivity(new Intent(FacebookLoginActivity.this, CreationActivity.class));
                    finish();
                } else {
                    String str = "android.permission.WRITE_EXTERNAL_STORAGE";
                    isvideo = true;
                    webView.onResume();
                    if (Build.VERSION.SDK_INT >= 23) {
                        requestPermissions(new String[]{str}, 7);
                    } else {
                        showInterstitialDownload();
                    }
                }


            }
        });

    }

    private void share() {
        String str = subStringUrl1;
        if (str != null && str.contains(".mp4")) {
            String str2 = null;
            if (subStringUrl1.matches("^(https|ftp)://.*$")) {
                String substring = subStringUrl1.substring(5);
                str2 = HttpHost.DEFAULT_SCHEME_NAME + substring;
            }
            String[] strArr = new String[3];
            strArr[1] = getCurrentTimeStamp();
            strArr[2] = "video";
            strArr[0] = str2;
        }
    }

    protected void onDestroy() {
        super.onDestroy();


    }
}