package com.allmy.allstatusdownloader.Activity;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.allmy.allstatusdownloader.R;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

public class InstaDownloadActivity extends AppCompatActivity {

    ImageView ivBack, ivCreation, ivSetting, iv_image, ib_play;
    EditText et_link;
    LinearLayout btnPaste, lDownloadText, lLoginInsta;
    CardView cardPreview, lDownload;
    ProgressBar progressBar;
    TextView txtProgress, txtDownload;
    ClipboardManager clipboardManager;
    ProgressDialog mPrDialog;
    private String tempIMURL = "";
    public String dnlUrl, savePath;
    private String VideoUrl;
    SharedPreferences loginPref;
    private String baseUrl = "";
    private String type = "";
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
        setContentView(R.layout.activity_insta_download);
        init();
        loadInter();

        String pasteData = getPasteText();
        if (pasteData != null && !pasteData.matches("")) {
            et_link.setText(pasteData);
        } else {
            et_link.setHint("Paste link here..");
        }

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        ivCreation.setOnClickListener(view -> {

            startActivity(new Intent(InstaDownloadActivity.this, CreationActivity.class));
        });

        ivSetting.setOnClickListener(view -> {

            startActivity(new Intent(InstaDownloadActivity.this, SettingActivity.class));
        });

        btnPaste.setOnClickListener(view -> {
            if (!(clipboardManager.hasPrimaryClip())) {
            } else if (!(clipboardManager.getPrimaryClipDescription().hasMimeType(MIMETYPE_TEXT_PLAIN))) {
            } else {
                ClipData.Item item = clipboardManager.getPrimaryClip().getItemAt(0);
                et_link.setText(item.getText().toString());
            }
        });

        lLoginInsta.setOnClickListener(view -> {

            if (isOnline()) {
                String str = "CURRENT_USER";
                SharedPreferences currentUser = getSharedPreferences(str, 0);
                String string = currentUser.getString(str, null);
                if (string != null) {
                    String sb2 = "COOKIE_PREF_" + string.toLowerCase(Locale.getDefault()).trim();
                    String sb3 = "LOGIN_" + string.toLowerCase(Locale.getDefault()).trim();
                    loginPref = getSharedPreferences(sb3, 0);
                    String sb4 = "ACCOUNT_PREF_" + string.toLowerCase(Locale.getDefault()).trim();
                    if (loginPref.getBoolean("IS_LOGIN", false)) {
                        Intent intent = new Intent(InstaDownloadActivity.this, ActivitywithDrawer.class);
                        String str2 = "USER_ID";
                        intent.putExtra(str2, loginPref.getString(str2, null));
                        String str3 = "profile_pic_url";
                        intent.putExtra(str3, loginPref.getString(str3, null));
                        String str4 = "full_name";
                        intent.putExtra(str4, loginPref.getString(str4, null));
                        String str5 = "csrftoken";
                        intent.putExtra(str5, loginPref.getString(str5, null));
                        String str6 = "phone_id";
                        intent.putExtra(str6, loginPref.getString(str6, null));
                        String str7 = "user_name";
                        intent.putExtra(str7, loginPref.getString(str7, null));
                        startActivity(intent);
                        return;
                    }
                } else {
                    startActivity(new Intent(InstaDownloadActivity.this, InstaLoginActivity.class));
                    finish();
                    return;
                }
            }


        });

        lDownload.setOnClickListener(view -> {

            if (et_link.getText() == null || !et_link.getText().toString().contains("www.instagram.com")) {
                Toast.makeText(getApplicationContext(), "Invalid URL", Toast.LENGTH_SHORT).show();
                return;
            }
            baseUrl = et_link.getText().toString();

            if (baseUrl.contains("reel") || baseUrl.contains("Reel")) {
                Toast.makeText(getApplicationContext(), "Require Login to Instagram", Toast.LENGTH_SHORT).show();
            } else {
                if (txtProgress.getText().toString().matches("Completed")) {
                    txtProgress.setText("0%");
                    cardPreview.setVisibility(View.GONE);
                    ib_play.setVisibility(View.GONE);
                    lDownloadText.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    txtProgress.setVisibility(View.GONE);

                    startActivity(new Intent(InstaDownloadActivity.this, CreationActivity.class));
                } else {

                    if (mInterstitialAd != null) {
                        mInterstitialAd.show(InstaDownloadActivity.this);

                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                new RetrieveFeedTask().execute(et_link.getText().toString());

                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                new RetrieveFeedTask().execute(et_link.getText().toString());
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                mInterstitialAd = null;
                            }
                        });

                    } else {
                        new RetrieveFeedTask().execute(et_link.getText().toString());
                    }
                }

            }

        });
        ib_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(savePath));
                    intent.setDataAndType(Uri.parse(savePath), "video/mp4");
                    startActivity(intent);
                } catch (Exception e) {

                }
            }
        });
    }

    public void loadInter() {
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
    }
    public boolean isOnline() {
        @SuppressLint("WrongConstant") NetworkInfo activeNetworkInfo = ((ConnectivityManager) getSystemService("connectivity")).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public void init() {
        ivCreation = findViewById(R.id.ivCreation);
        ivSetting = findViewById(R.id.ivSetting);
        ivBack = findViewById(R.id.ivBack);
        btnPaste = findViewById(R.id.btnPaste);
        iv_image = findViewById(R.id.iv_image);
        ib_play = findViewById(R.id.ib_play);
        lLoginInsta = findViewById(R.id.lLoginInsta);
        lDownloadText = findViewById(R.id.lDownloadText);
        et_link = findViewById(R.id.et_link);
        cardPreview = findViewById(R.id.cardPreview);
        lDownload = findViewById(R.id.lDownload);
        progressBar = findViewById(R.id.progressBar);
        txtProgress = findViewById(R.id.txtProgress);
        txtDownload = findViewById(R.id.txtDownload);

        mPrDialog = new ProgressDialog(InstaDownloadActivity.this);
        mPrDialog.setMessage("Please Wait..");
        clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
    }

    class RetrieveFeedTask extends AsyncTask<String, Void, String> {


        RetrieveFeedTask() {
        }

        @SuppressLint("WrongConstant")
        protected void onPreExecute() {
            super.onPreExecute();
            lDownload.setEnabled(false);
            mPrDialog.show();

        }

        protected String doInBackground(String... urls) {
            try {
                Elements metaOgTitle1;
                Document doc = Jsoup.connect(urls[0]).get();
                Elements metaOgTitle = doc.select("meta[property=og:type]");
                if (metaOgTitle != null) {
                    type = metaOgTitle.attr("content");
                } else {
                    type = doc.title();
                }
                if (type.contains("video") || type.contains("instapp:video")) {
                    metaOgTitle1 = doc.select("meta[property=og:video]");
                    if (metaOgTitle1 != null) {
                        tempIMURL = metaOgTitle1.attr("content");
                    } else {
                        tempIMURL = doc.title();
                    }
                } else if (type.contains("photo") || type.contains("instapp:photo") || type.contains("image") || type.contains("instapp:image")) {
                    metaOgTitle1 = doc.select("meta[property=og:image]");
                    if (metaOgTitle1 != null) {
                        tempIMURL = metaOgTitle1.attr("content");
                    } else {
                        tempIMURL = doc.title();
                    }
                }

                Log.e("type--)", "" + type + "--)" + tempIMURL);
                metaOgTitle1 = doc.select("meta[property=og:image]");
                if (metaOgTitle1 != null) {
                    return metaOgTitle1.attr("content");
                }
                return doc.title();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @SuppressLint("WrongConstant")
        protected void onPostExecute(String feed) {
//            pb.setVisibility(8);
            if (mPrDialog != null && mPrDialog.isShowing()) {
                mPrDialog.dismiss();
            }
            cardPreview.setVisibility(View.VISIBLE);
            ib_play.setVisibility(View.VISIBLE);
            lDownloadText.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            txtProgress.setVisibility(View.VISIBLE);

            Glide.with(InstaDownloadActivity.this).load(feed).override(720, 720).into(iv_image);
            if (type.contains("video") || type.contains("instapp:video")) {
                ib_play.setVisibility(0);
            } else {
                ib_play.setVisibility(8);
            }

            downloadFile(tempIMURL);

        }
    }

    public void downloadFile(String uRl) {

        try {
            if (type.contains("video") || type.contains("instapp:video")) {
                try {
                    new DownloadTask(getApplicationContext()).execute(uRl);
                } catch (Exception e) {
                    Log.e("ExceptionUrl--)", "" + e.getMessage());
                }
            } else if (type.contains("photo") || type.contains("instapp:photo") || type.contains("image") || type.contains("instapp:image")) {
                try {
                    new DownloadTask(getApplicationContext()).execute(uRl);
                } catch (Exception e) {
                    Log.e("ExceptionUrl--)", "" + e.getMessage());
                }
            } else {
                Toast.makeText(getApplicationContext(), "URL Invalid!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {

        }

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
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                int fileLength = connection.getContentLength();

                input = connection.getInputStream();

                String strFileName = null;
                if (type.contains("video") || type.contains("instapp:video")) {
                    strFileName = System.currentTimeMillis() + ".mp4";
                } else if (type.contains("photo") || type.contains("instapp:photo") || type.contains("image") || type.contains("instapp:image")) {
                    strFileName = System.currentTimeMillis() + ".jpg";
                }

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
            lDownload.setEnabled(true);
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
            }


        }
    }

    public void setPr(int progress) {
        if (progress == 100) {

            progressBar.setProgress(progress);
            txtProgress.setText("Completed");
        } else {
            progressBar.setProgress(progress);
            txtProgress.setText(progress + "%");
        }
    }

    private String getPasteText() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        String pasteData = "";
        if (!(clipboard.hasPrimaryClip())) {

        } else if (!(clipboard.getPrimaryClipDescription().hasMimeType(MIMETYPE_TEXT_PLAIN))) {

        } else {
            ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
            pasteData = item.getText().toString();
        }
        return pasteData;
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

    private void scanFile(String path) {
        MediaScannerConnection.scanFile(InstaDownloadActivity.this,
                new String[]{path}, null,
                (path1, uri) -> Log.e("TAG--)", "Finished scanning " + path1));
    }
}