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
import com.allmy.allstatusdownloader.Model.Variants;
import com.allmy.allstatusdownloader.Others.Utils;
import com.allmy.allstatusdownloader.R;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.internal.TweetMediaUtils;

import org.json.JSONObject;
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
import java.net.URL;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class OtherDownloadActivity extends AppCompatActivity {

    ImageView ivBack, ivCreation, ivSetting, ivDashIcon;
    TextView txtTitle, txtProgress, txtDownload;
    EditText et_link;
    LinearLayout btnPaste, lDownloadText;
    CardView cardPreview, lDownload;
    ImageView iv_image, ib_play;
    ProgressBar progressBar;
    String which;
    ClipboardManager clipboardManager;
    ProgressDialog mPrDialog;
    private String tempIMURL = "";
    ArrayList<Variants> variantsList = new ArrayList<>();
    boolean isVideo = true;
    String extention;
    public String dnlUrl, savePath;
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
        setContentView(R.layout.activity_other_download);

        which = getIntent().getStringExtra("which");
        init();
        loadAds();

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        ivCreation.setOnClickListener(view -> startActivity(new Intent(OtherDownloadActivity.this, CreationActivity.class)));

        ivSetting.setOnClickListener(view -> {
            startActivity(new Intent(OtherDownloadActivity.this, SettingActivity.class));
        });

        String pasteData = getPasteText();
        if (pasteData != null && !pasteData.matches("")) {
            et_link.setText(pasteData);
        } else {
            et_link.setHint("Enter or paste URL");
        }

        btnPaste.setOnClickListener(view -> {
            if (!(clipboardManager.hasPrimaryClip())) {
            } else if (!(clipboardManager.getPrimaryClipDescription().hasMimeType(MIMETYPE_TEXT_PLAIN))) {
            } else {
                ClipData.Item item = clipboardManager.getPrimaryClip().getItemAt(0);
                et_link.setText(item.getText().toString());
            }
            if (cardPreview.getVisibility() == View.VISIBLE) {
                cardPreview.setVisibility(View.GONE);
                ib_play.setVisibility(View.GONE);
                lDownloadText.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                txtProgress.setText("0%");
                txtProgress.setVisibility(View.GONE);
            }
        });

        lDownload.setOnClickListener(view -> {


            String strUrl = et_link.getText().toString();
            if (!Utils.isEmpty(strUrl)) {

                if (txtProgress.getText().toString().matches("Completed")) {
                    txtProgress.setText("0%");
                    cardPreview.setVisibility(View.GONE);
                    ib_play.setVisibility(View.GONE);
                    lDownloadText.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    txtProgress.setVisibility(View.GONE);

                    startActivity(new Intent(OtherDownloadActivity.this, CreationActivity.class));
                } else {

                    if (mInterstitialAd != null) {
                        mInterstitialAd.show(OtherDownloadActivity.this);

                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                if (which.matches("TWITTER"))
                                {
                                    fetchTweet(strUrl);
                                }
                                else if (which.matches("MOJ"))
                                {

                                    mPrDialog.show();
                                    lDownload.setEnabled(false);
                                    new MojAsync().execute(et_link.getText().toString());

                                }
                                else if (which.matches("ROPOSO"))
                                {

                                    Log.e("strUrl--)",""+strUrl);
                                    mPrDialog.show();
                                    new RoposoAsync().execute();
                                }
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                if (which.matches("TWITTER"))
                                {
                                    fetchTweet(strUrl);
                                }
                                else if (which.matches("MOJ"))
                                {

                                    mPrDialog.show();
                                    new MojAsync().execute(et_link.getText().toString());

                                }
                                else if (which.matches("ROPOSO"))
                                {

                                    Log.e("strUrl--)",""+strUrl);
                                    mPrDialog.show();
                                    new RoposoAsync().execute();
                                }                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                mInterstitialAd = null;
                            }
                        });

                    } else {
                        if (which.matches("TWITTER"))
                        {
                            fetchTweet(strUrl);
                        }
                        else if (which.matches("MOJ"))
                        {

                            mPrDialog.show();
                            new MojAsync().execute(et_link.getText().toString());

                        }
                        else if (which.matches("ROPOSO"))
                        {

                            Log.e("strUrl--)",""+strUrl);
                            mPrDialog.show();
                            new RoposoAsync().execute();
                        }                    }

                }

            } else {
                Toast.makeText(this, getResources().getString(R.string.blank_url), Toast.LENGTH_SHORT).show();
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

    public class RoposoAsync extends AsyncTask<String, Void, Document> {
        Document roposoDoc;
        String videoUrl = "";

        @Override
        protected Document doInBackground(String... strings) {
            try {
                this.roposoDoc = Jsoup.connect(et_link.getText().toString()).get();
            } catch (Exception e) {

                Log.e("Ex--)",""+e.getMessage());
                e.printStackTrace();
            }
            return this.roposoDoc;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(Document document) {
            super.onPostExecute(document);

            try {
                if (mPrDialog != null && mPrDialog.isShowing()) {
                    mPrDialog.dismiss();
                }

                this.videoUrl = document.select("meta[property=\"og:video\"]").last().attr("content");

                Log.e("videoUrl--)",""+videoUrl);
                if (this.videoUrl.equals("")) {
                    //DownloadFailed();
                    return;
                }

                Glide.with(OtherDownloadActivity.this).load(tempIMURL).override(720, 720).into(iv_image);

                ib_play.setVisibility(View.VISIBLE);
                cardPreview.setVisibility(View.VISIBLE);
                lDownloadText.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                txtProgress.setVisibility(View.VISIBLE);

                new DownloadTask(getApplicationContext()).execute(tempIMURL);

            } catch (Exception unused) {
                Log.e("ExceptionRoposo--)",""+unused);
            }
        }
    }
    public class MojAsync extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strArr) {
            String str = strArr[0].split("/")[strArr[0].split("/").length - 1];
            OkHttpClient okHttpClient = new OkHttpClient();
            RequestBody create = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "{\"appVersion\":83,\"bn\":\"broker1\",\"client\":\"android\",\"deviceId\":\"" +
                    "ebb088d29e7287b1\",\"message\":{\"adData\":{\"adsShown\":0,\"firstFeed\":false},\"deviceInfoKey\":" +
                    "\"OSyQoHJLJ4NsXPLyQePkAICh3Q0ih0bveFwm1KEV+vReMuldqo+mSyMjdhb4EeryKxk1ctAbYaDH\\nTI+PMRPZVYH5pBccAm7OT2uz69vmD/" +
                    "wPqGuSgWV2aVNMdM75DMb8NZn1JU2b1bo/oKs80baklsvx\\n1X7jrFPL6M5EDTdPDhs=\\n\",\"deviceInfoPayload\":" +
                    "\"M6g+6j6irhFT/H6MsQ/n/tEhCl7Z5QgtVfNKU8M90zTJHxqljm2263UkjRR9bRXAjmQFXXOTXJ25\\" +
                    "nOHRjV7L5Lw+tUCONoYfyUEzADihWfAiUgXJEcKePfZONbdXXuwGgOPeD0k4iSvI7JdzroRCScKXd\\" +
                    "n41CkmXFayPaRL9aqgAgs6kSoIncCWBU2gEXiX1lgPVvdmUzCZ+yi2hFA+uFOmv1MJ6dcFKKcpBM6\\" +
                    "nHSPIrGV+YtTyfd8nElx0kyUbE4xmjOuMrctkjnJkd2tMdxB8qOFKeYrcLzy4LZJNXyUmzs29XSE+\\" +
                    "nhsrMZib8fFPJhJZIyGCWqfWiURut4Bg5HxYhYhg3ejPxFjNyXxS3Ja+/pA+A0olt5Uia7ync/Gui\\" +
                    "n58tlDQ4SKPthCzGa1tCVN+2y/PW30+LM79t0ltJ/YrNZivQx4eEnszlM9nwmIuj5z5LPniQghA6x\\" +
                    "nrfQ8IqVUZfiitXj/Fr7UjKg1cs/Ajj8g4u/KooRvVkg9tMwWePtJFqrkk1+DU4cylnSEG3XHgfer\\" +
                    "nslrzj5NNZessMEi+4Nz0O2D+b8Y+RjqN6HqpwZPDHhZwjz0Iuj2nhZLgu1bgNJev5BwxAr8akDWv\\" +
                    "nvKsibrJS9auQOYVzbYZFdKMiBnh+WHq0qO2aW1akYWCha3ZsSOtsnyPnFC+1PnMbBv+FiuJmPMXg\\" +
                    "nSODFoRIXfxgA/qaiKBipS+kIyfaPxn6O1i6MOwejVuQiWdAPTO132Spx0cFtdyj2hX6wAMe21cSy\\" +
                    "n8rs3KQxiz+cq7Rfwzsx4wiaMryFunfwUwnauGwTFOW98D5j6oO8=\\n\",\"lang\":\"Hindi\",\"playEvents\":[{\"authorId\":\"18326559001\"," +
                    "\"networkBitrate\":1900000,\"initialBufferPercentage\":100.0,\"isRepost\":false,\"sg\":false,\"meta\":\"NotifPostId\",\"md\":\"Stream\"," +
                    "\"percentage\":24.68405,\"p\":\"91484006\",\"radio\":\"wifi\",\"r\":\"deeplink_VideoPlayer\",\"repeatCount\":0,\"timeSpent\":9633," +
                    "\"duration\":15,\"videoStartTime\":3916,\"t\":1602255552820,\"clientType\":\"Android\",\"i\":79,\"appV\":83,\"sessionId\":" +
                    "\"72137847101_8863b3f5-ad2d-4d59-aa7c-cf1fb9ef32ea\"},{\"authorId\":\"73625124001\",\"networkBitrate\":1900000,\"initialBufferPercentage\":100.0,\"isRepost\":false,\"sg\":false,\"meta\":\"list2\",\"md\":\"Stream\",\"percentage\":17.766666,\"p\":\"21594412\",\"radio\":\"wifi\",\"r\":\"First Launch_VideoPlayer\",\"repeatCount\":0,\"tagId\":\"0\",\"tagName\":\"\",\"timeSpent\":31870,\"duration\":17,\"videoStartTime\":23509,\"t\":1602218215942,\"clientType\":\"Android\",\"i\":79,\"appV\":83,\"sessionId\":\"72137847101_db67c0c9-a267-4cec-a3c3-4c0fa4ea16e1\"}],\"r\":\"VideoFeed\"},\"passCode\":\"9e32d6145bfe53d14a0c\",\"resTopic\":\"response/user_72137847101_9e32d6145bfe53d14a0c\",\"userId\":\"72137847101\"}");
            Request.Builder builder = new Request.Builder();
            try {
                https://video-wm.tiki.video//in_live//2nN//08MyZz.mp4?crc=3705721429&type=5
                return okHttpClient.newCall(builder.url("https://moj-apis.sharechat.com/videoFeed?postId=" + str + "&firstFetch=true").post(create).addHeader("X-SHARECHAT-USERID", "72137847101").addHeader("X-SHARECHAT-SECRET", "9e32d6145bfe53d14a0c").addHeader("APP-VERSION", "83").addHeader("PACKAGE-NAME", "in.mohalla.video").addHeader("DEVICE-ID", "ebb088d29e7287b1").addHeader("CLIENT-TYPE", "Android").addHeader("Content-Type", "application/json; charset=UTF-8").addHeader("Host", "moj-apis.sharechat.com").addHeader("Connection", "Keep-Alive").addHeader("User-Agent", "okhttp/3.12.12").build()).execute().body().string();
//                return okHttpClient.newCall(builder.url("https://video-wm.tiki.video//in_live//videoFeed?postId=" + str + "&firstFetch=true").post(create).addHeader("X-SHARECHAT-USERID", "72137847101").addHeader("X-SHARECHAT-SECRET", "9e32d6145bfe53d14a0c").addHeader("APP-VERSION", "83").addHeader("PACKAGE-NAME", "in.mohalla.video").addHeader("DEVICE-ID", "ebb088d29e7287b1").addHeader("CLIENT-TYPE", "Android").addHeader("Content-Type", "application/json; charset=UTF-8").addHeader("Host", "moj-apis.sharechat.com").addHeader("Connection", "Keep-Alive").addHeader("User-Agent", "okhttp/3.12.12").build()).execute().body().string();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mPrDialog.show();
        }

        @Override
        protected void onPostExecute(String str) {
            super.onPostExecute(str);
//            dialog.dismiss();
            try {

                if (mPrDialog != null && mPrDialog.isShowing()) {
                    mPrDialog.dismiss();
                }

                tempIMURL = new JSONObject(new JSONObject(str).getJSONObject("payload").getJSONArray("d").get(0).toString()).get("compressedVideoUrl").toString();

                Glide.with(OtherDownloadActivity.this).load(tempIMURL).override(720, 720).into(iv_image);

                ib_play.setVisibility(View.VISIBLE);
                cardPreview.setVisibility(View.VISIBLE);
                lDownloadText.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                txtProgress.setVisibility(View.VISIBLE);

                new DownloadTask(getApplicationContext()).execute(tempIMURL);


            } catch (Exception unused) {
                unused.printStackTrace();
            }
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

    public void init() {
        txtDownload = findViewById(R.id.txtDownload);
        ivBack = findViewById(R.id.ivBack);
        ivCreation = findViewById(R.id.ivCreation);
        ivSetting = findViewById(R.id.ivSetting);
        ivDashIcon = findViewById(R.id.ivDashIcon);
        txtTitle = findViewById(R.id.txtTitle);
        txtProgress = findViewById(R.id.txtProgress);
        et_link = findViewById(R.id.et_link);
        btnPaste = findViewById(R.id.btnPaste);
        lDownloadText = findViewById(R.id.lDownloadText);
        cardPreview = findViewById(R.id.cardPreview);
        lDownload = findViewById(R.id.lDownload);
        iv_image = findViewById(R.id.iv_image);
        ib_play = findViewById(R.id.ib_play);
        progressBar = findViewById(R.id.progressBar);

        mPrDialog = new ProgressDialog(OtherDownloadActivity.this);
        mPrDialog.setMessage("Please Wait..");
        clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        if (which.matches("TWITTER")) {

            txtTitle.setText("Twitter");
            ivDashIcon.setImageDrawable(getDrawable(R.drawable.dash_twitter));
            TwitterConfig config = new TwitterConfig.Builder(this)
                    .logger(new DefaultLogger(Log.DEBUG))
                    .twitterAuthConfig(new TwitterAuthConfig(Utils.TWITTER_KEY, Utils.TWITTER_SECRET))
                    .debug(true)
                    .build();

            Twitter.initialize(config);
        }
        else if (which.matches("MOJ"))
        {
            txtTitle.setText("MOJ");
            ivDashIcon.setImageDrawable(getDrawable(R.drawable.dash_moj));
        }
        else if (which.matches("ROPOSO"))
        {
            txtTitle.setText("ROPOSO");
            ivDashIcon.setImageDrawable(getDrawable(R.drawable.dash_roposo));
        }

    }

    public void fetchTweet(String copiedURL) {
        if (copiedURL.length() > 0 && copiedURL.contains("twitter.com/")) {
            Long id = getTweetId(copiedURL);
            Log.e("fetchTweet: ", "" + id);
            if (id != null) {
                mPrDialog.show();
                getTweet(id);
            }
        } else {
            Toast.makeText(this, "Invalid URL", Toast.LENGTH_SHORT).show();
        }
    }

    private static Long getTweetId(String s) {
        try {
            return Long.parseLong(s.split("\\/")[5].split("\\?")[0]);
        } catch (Exception e) {
            Log.e("TAG", "getTweetId: " + e.getLocalizedMessage());
            // alertNoUrl();
            return null;
        }
    }

    public void getTweet(Long id) {
        variantsList = new ArrayList<>();
        runOnUiThread(() -> TwitterCore.getInstance().getApiClient().getStatusesService().show(id, null, null, null).enqueue(new Callback<Tweet>() {
            public void success(Result<Tweet> result) {
                if ((result.data).extendedEntities == null && (result.data).entities.media == null || (result.data).entities.media.size() == 0) {
                    Toast.makeText(OtherDownloadActivity.this, "No Media found", Toast.LENGTH_SHORT).show();
                } else if (((result.data).extendedEntities.media.get(0)).type.equals(TweetMediaUtils.VIDEO_TYPE) || ((result.data).extendedEntities.media.get(0)).type.equals("animated_gif")) {
                    if (((result.data).extendedEntities.media.get(0)).type.equals(TweetMediaUtils.VIDEO_TYPE)) {
                        extention = Utils.VID_EXT;
                    } else {
                        extention = Utils.GIF_EXT;
                    }
                    int size = ((result.data).extendedEntities.media.get(0)).videoInfo.variants.size();
                    String durationMillis = String.valueOf(((result.data).extendedEntities.media.get(0)).videoInfo.durationMillis);

                    for (int i = 0; i < size; i++) {
                        if ((((result.data).extendedEntities.media.get(0)).videoInfo.variants.get(i)).contentType.equals("video/mp4")) {
                            String bitrate = String.valueOf((((result.data).extendedEntities.media.get(0)).videoInfo.variants.get(i)).bitrate);
                            String url = String.valueOf((((result.data).extendedEntities.media.get(0)).videoInfo.variants.get(i)).url);
                            new getFileSize(bitrate, durationMillis, url, size, i).execute(url);
                        }
                    }
                    isVideo = true;


                } else if (((result.data).extendedEntities.media.get(0)).type.equals(TweetMediaUtils.PHOTO_TYPE)) {

                    tempIMURL = (((result.data).extendedEntities.media.get(0)).mediaUrl);

                    isVideo = false;
                    Glide.with(OtherDownloadActivity.this).load(tempIMURL).into(iv_image);

                    ib_play.setVisibility(View.GONE);
                    if (mPrDialog != null && mPrDialog.isShowing()) {
                        mPrDialog.dismiss();
                    }

                    cardPreview.setVisibility(View.VISIBLE);
                    ib_play.setVisibility(View.GONE);
                    lDownloadText.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    txtProgress.setVisibility(View.VISIBLE);

                    new DownloadTask(getApplicationContext()).execute(tempIMURL);

                } else {
                    Toast.makeText(OtherDownloadActivity.this, "No Media found", Toast.LENGTH_SHORT).show();
                }
            }

            public void failure(TwitterException exception) {
                Toast.makeText(OtherDownloadActivity.this, exception.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }));
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

                if (which.matches("TWITTER"))
                {
                    if (isVideo) {
                        strFileName = "TWI_" + System.currentTimeMillis() + Utils.VIDEO_EXTENTION;
                    } else {
                        strFileName = "TWI_" + System.currentTimeMillis() + Utils.IMAGE_EXTENTION;
                    }
                }
                else if (which.matches("MOJ"))
                {
                    strFileName = "MOJ_" + System.currentTimeMillis() + ".mp4";
                }
                else if (which.matches("ROPOSO"))
                {
                    strFileName = "ROPOSO_" + System.currentTimeMillis() + ".mp4";
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

    class getFileSize extends AsyncTask<String, String, String> {
        String bitrte;
        String duration;
        String url;
        int size;
        int position;

        public getFileSize(String bitrte, String duration, String url, int size, int position) {
            this.bitrte = bitrte;
            this.duration = duration;
            this.url = url;
            this.size = size;
            this.position = position;
        }


        @Override
        protected String doInBackground(String... strings) {
            int fileLength = 0;
            try {
                URL url = new URL(strings[0]);
                fileLength = url.openConnection().getContentLength();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return String.valueOf(fileLength);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Variants variants = new Variants();

            Log.e("s--)", "" + s);
            variants.setBitrate(s);
            variants.setMillisDuration(duration);
            variants.setUrl(url);
            variantsList.add(variants);

            boolean condition;

            if (extention.equals(Utils.VID_EXT)) {
                condition = variantsList.size() == (size - 1);
            } else {
                condition = variantsList.size() == (size);
            }

            if (condition) {
                if (Utils.isConnected(OtherDownloadActivity.this)) {

                    if (variantsList.size() > 0 && variantsList != null) {
                        tempIMURL = (variantsList.get(0).getUrl());
                    } else {
                        tempIMURL = tempIMURL;
                    }

                    Glide.with(OtherDownloadActivity.this).load(tempIMURL).into(iv_image);
                    ib_play.setVisibility(View.VISIBLE);
                    if (mPrDialog != null && mPrDialog.isShowing()) {
                        mPrDialog.dismiss();
                    }

                    cardPreview.setVisibility(View.VISIBLE);
                    ib_play.setVisibility(View.VISIBLE);
                    lDownloadText.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    txtProgress.setVisibility(View.VISIBLE);

                    new DownloadTask(getApplicationContext()).execute(tempIMURL);

                } else {
                    Toast.makeText(OtherDownloadActivity.this, "Check Connection", Toast.LENGTH_SHORT).show();
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

    private void scanFile(String path) {
        MediaScannerConnection.scanFile(OtherDownloadActivity.this,
                new String[]{path}, null,
                (path1, uri) -> Log.e("TAG--)", "Finished scanning " + path1));
    }

}