package com.allmy.allstatusdownloader.Activity;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class FBDownloadActivity extends AppCompatActivity {

    ImageView ivBack, ivCreation, ivSetting, iv_image, ib_play;
    EditText et_link;
    LinearLayout btnPaste, lDownloadText, lLoginFB;
    CardView cardPreview, lDownload;
    ProgressBar progressBar;
    TextView txtProgress, txtDownload;
    ClipboardManager clipboardManager;
    ProgressDialog mPrDialog;
    private String tempIMURL = "";
    public String dnlUrl, savePath;
    private String VideoUrl;
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
        setContentView(R.layout.activity_fbdownload);

        init();
        loadAds();

        ivBack.setOnClickListener(view -> {
            finish();
        });

        ivCreation.setOnClickListener(view -> startActivity(new Intent(FBDownloadActivity.this, CreationActivity.class)));

        ivSetting.setOnClickListener(view -> {
            startActivity(new Intent(FBDownloadActivity.this, SettingActivity.class));
        });

        String pasteData = getPasteText();
        if (pasteData != null && !pasteData.matches("")) {
            et_link.setText(pasteData);
        } else {
            et_link.setHint("Paste link here..");
        }

        lLoginFB.setOnClickListener(view -> {
            startActivity(new Intent(FBDownloadActivity.this, FacebookLoginActivity.class));
        });

        lDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (txtProgress.getText().toString().matches("Completed")) {
                    cardPreview.setVisibility(View.GONE);
                    ib_play.setVisibility(View.GONE);
                    txtProgress.setText("0%");
                    lDownloadText.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    txtProgress.setVisibility(View.GONE);

                    startActivity(new Intent(FBDownloadActivity.this, CreationActivity.class));
                } else {
                    String str = et_link.getText().toString();

                    if (!str.isEmpty()) {

                        lDownload.setEnabled(false);
                        if (mInterstitialAd != null) {
                            mInterstitialAd.show(FBDownloadActivity.this);

                            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    mPrDialog.show();
                                    GetFacebookData();
                                }

                                @Override
                                public void onAdFailedToShowFullScreenContent(AdError adError) {
                                    mPrDialog.show();
                                    GetFacebookData();
                                }

                                @Override
                                public void onAdShowedFullScreenContent() {
                                    mInterstitialAd = null;
                                }
                            });

                        } else {
                            mPrDialog.show();
                            GetFacebookData();
                        }
                    } else {
                        Toast.makeText(FBDownloadActivity.this, "Please Insert Url", Toast.LENGTH_SHORT).show();
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

        btnPaste.setOnClickListener(view -> {
            if (!(clipboardManager.hasPrimaryClip())) {
            } else if (!(clipboardManager.getPrimaryClipDescription().hasMimeType(MIMETYPE_TEXT_PLAIN))) {
            } else {
                ClipData.Item item = clipboardManager.getPrimaryClip().getItemAt(0);
                et_link.setText(item.getText().toString());
            }
        });

    }


    private void loadAds() {

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

    public void init() {
        ivBack = findViewById(R.id.ivBack);
        ivCreation = findViewById(R.id.ivCreation);
        ivSetting = findViewById(R.id.ivSetting);
        btnPaste = findViewById(R.id.btnPaste);
        et_link = findViewById(R.id.et_link);
        iv_image = findViewById(R.id.iv_image);
        ib_play = findViewById(R.id.ib_play);
        cardPreview = findViewById(R.id.cardPreview);
        lDownloadText = findViewById(R.id.lDownloadText);
        lDownload = findViewById(R.id.lDownload);
        lLoginFB = findViewById(R.id.lLoginFB);
        progressBar = findViewById(R.id.progressBar);
        txtProgress = findViewById(R.id.txtProgress);
        txtDownload = findViewById(R.id.txtDownload);

        mPrDialog = new ProgressDialog(FBDownloadActivity.this);
        mPrDialog.setMessage("Please Wait..");
        clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
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

    private void GetFacebookData() {
        try {
            URL url = new URL(et_link.getText().toString());
            String host = url.getHost();
            Log.e("initViews: ", host);

            if (host.contains("facebook.com") || host.contains("fb.watch")) {
                new callGetFacebookDataDownloded().execute(et_link.getText().toString());
            } else {
                Toast.makeText(FBDownloadActivity.this, "Enter the Valid Url", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TEMP", "PARTH" + e.getMessage());
        }
    }


    class callGetFacebookDataDownloded extends AsyncTask<String, Void, Document> {
        int progressUpdate = 0;
        public Document facebookDoc;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Document doInBackground(String... strArr) {
            try {
                this.facebookDoc = Jsoup.connect(strArr[0]).get();
                Log.e("TAG PD", "Do in Back CAll get fb data");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return facebookDoc;
        }
        protected void onPostExecute(Document document) {
            Log.e("TAG PD", "onpost execute call get fb data");
            try {
                Log.e("TAG PD", "document data "+document);
                VideoUrl = document.select("meta[property=\"og:video\"]").last().attr("content");
                Log.e("TAG PD", "Done--)" + VideoUrl);
                if (VideoUrl != null && !VideoUrl.equals("")) {
                    if (mPrDialog != null && mPrDialog.isShowing()) {
                        mPrDialog.dismiss();
                    }

                    cardPreview.setVisibility(View.VISIBLE);

                    Glide.with(FBDownloadActivity.this).load(VideoUrl).centerCrop().into(iv_image);
                    ib_play.setVisibility(View.VISIBLE);
                    lDownloadText.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    txtProgress.setVisibility(View.VISIBLE);
                    Log.e("onPostExecuteee: ", VideoUrl);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new DownloadTask(getApplicationContext()).execute(VideoUrl);
                            Log.e("TAG PD", "run ui thread CAll get fb data");
                        }
                    });


                } else {
                    if (mPrDialog != null && mPrDialog.isShowing()) {
                        mPrDialog.dismiss();
                    }
                    Toast.makeText(FBDownloadActivity.this, "Url Not Valid", Toast.LENGTH_SHORT).show();
                }


            } catch (Exception e) {

                try {
                    VideoUrl = document.select("meta[property=\"og:image\"]").last().attr("content");
                    if (VideoUrl != null && !VideoUrl.equals("")) {
                        if (mPrDialog != null && mPrDialog.isShowing()) {
                            mPrDialog.dismiss();
                        }

                        cardPreview.setVisibility(View.VISIBLE);

                        Glide.with(FBDownloadActivity.this).load(VideoUrl).centerCrop().into(iv_image);
                        ib_play.setVisibility(View.VISIBLE);
                        lDownloadText.setVisibility(View.GONE);
                        progressBar.setVisibility(View.VISIBLE);
                        txtProgress.setVisibility(View.VISIBLE);
                        Log.e("onPostExecuteee: ", VideoUrl);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new DownloadTask(getApplicationContext()).execute(VideoUrl);
                            }
                        });


                    } else {
                        if (mPrDialog != null && mPrDialog.isShowing()) {
                            mPrDialog.dismiss();
                        }
                        Toast.makeText(FBDownloadActivity.this, "Url Not Valid", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ee) {
                    Log.e("TAG PD", "Exeception "+ee);
                }
            }
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

                File file = new File(getExternalFilesDir(getApplicationContext().getResources().getString(R.string.app_name)).toString());

                if (!file.exists()) {
                    file.mkdirs();
                }
                File repostfile = new File(file, getFilenameFromURL(VideoUrl));


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
                File repostfile = new File(file1, getFilenameFromURL(VideoUrl));

                moveFile(dnlUrl,repostfile.getAbsolutePath());
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

    private void scanFile(String path) {
        MediaScannerConnection.scanFile(FBDownloadActivity.this,
                new String[]{path}, null,
                (path1, uri) -> Log.e("TAG--)", "Finished scanning " + path1));
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


    public String getFilenameFromURL(String url) {
        try {
            return new File(new URL(url).getPath()).getName();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return System.currentTimeMillis() + ".mp4";
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
}