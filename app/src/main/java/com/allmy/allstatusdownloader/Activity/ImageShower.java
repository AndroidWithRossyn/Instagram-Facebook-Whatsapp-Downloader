package com.allmy.allstatusdownloader.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.allmy.allstatusdownloader.Others.Utils;
import com.allmy.allstatusdownloader.R;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

public class ImageShower extends AppCompatActivity {

    public static final int progress_bar_type = 0;
    ImageView back_arrow;
    String checkType;
    String full_name;
    String id;
    String imgUrl;
    String mediaTitle;
    public ProgressDialog pDialog;
    CircleImageView profilePic;
    String profileUrl, savePath;
    ImageView repost, share;
    MediaScannerConnection scanner;
    String title;
    TextView titletv;
    private RelativeLayout coordinatorLayout;
    private ProgressDialog dialog;
    Utils utils;
    public String dnlUrl;
    long durationFl;
    ProgressBar progressBar;
    RelativeLayout relDownload;
    CardView ib_im_download;
    TextView txtProgress;
    LinearLayout linearDownload;
    InterstitialAd mInterstitialAd;
    FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_shower);

        getIntentValue();

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

        initilizeView();
        ImageView bigImageView = findViewById(R.id.mBigImage);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);

        dialog = new ProgressDialog(this);

        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        linearDownload = findViewById(R.id.linearDownload);
        progressBar = findViewById(R.id.progressBar);
        ib_im_download = findViewById(R.id.ib_im_download);
        txtProgress = findViewById(R.id.txtProgress);

        relDownload = findViewById(R.id.relDownload);
        Glide.with(this).load(imgUrl).into(bigImageView);

        back_arrow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onBackPressed();
            }
        });
        Glide.with((FragmentActivity) this).load(profileUrl).into((ImageView) profilePic);

        repost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkType = "repost";
                callAction();
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkType = "share";
                callAction();
            }
        });

        relDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mInterstitialAd != null) {
                    mInterstitialAd.show(ImageShower.this);

                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            if (progressBar.getProgress() == 100) {

                                progressBar.setProgress(0);
                                linearDownload.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                                txtProgress.setVisibility(View.GONE);

                                startActivity(new Intent(ImageShower.this, CreationActivity.class));
                            } else {
                                checkType = "download";
                                callAction();
                            }
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            if (progressBar.getProgress() == 100) {
                                progressBar.setProgress(0);
                                linearDownload.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                                txtProgress.setVisibility(View.GONE);
                                startActivity(new Intent(ImageShower.this, CreationActivity.class));
                            } else {
                                checkType = "download";
                                callAction();
                            }
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            mInterstitialAd = null;
                        }
                    });

                } else {
                    if (progressBar.getProgress() == 100) {
                        progressBar.setProgress(0);
                        linearDownload.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        txtProgress.setVisibility(View.GONE);
                        startActivity(new Intent(ImageShower.this, CreationActivity.class));
                    } else {
                        checkType = "download";
                        callAction();
                    }
                }
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void getIntentValue() {
        Intent intent = getIntent();
        full_name = intent.getStringExtra("full_name");
        imgUrl = intent.getStringExtra("imgUrl");
        profileUrl = intent.getStringExtra("profileUrl");
        title = intent.getStringExtra("title");
        id = intent.getStringExtra("id");
        mediaTitle = id.replace("_", "");
    }

    private void initilizeView() {

        share = findViewById(R.id.share);
        repost = findViewById(R.id.repost);
        profilePic = (CircleImageView) findViewById(R.id.profilePic);
        back_arrow = findViewById(R.id.backInsta);
        titletv = (TextView) findViewById(R.id.tv_title);
        titletv.setText(full_name);
        frameLayout = findViewById(R.id.frameBanner);
        utils=new Utils(ImageShower.this);
        utils.loadBanner(ImageShower.this, frameLayout);

    }

    private void callAction() {

        setPr(0);
        progressBar.setVisibility(View.GONE);
        txtProgress.setVisibility(View.GONE);
        relDownload.setVisibility(View.VISIBLE);

        linearDownload.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        txtProgress.setVisibility(View.VISIBLE);
        new DownloadTask(getApplicationContext()).execute(imgUrl);

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

                String strFileName = mediaTitle + ".jpg";

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
                reset(checkType, dnlUrl);

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

    public void reset(String which, String url) {
        relDownload.setEnabled(true);
        if (which.contains("download")) {

        } else if (which.matches("repost")) {

            File files = new File(url);
            Uri uris;
            if (Build.VERSION.SDK_INT >= 24) {
                uris = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".fileprovider", files);
            } else {
                uris = Uri.fromFile(files);
            }

            try {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setPackage("com.instagram.android");
                intent.putExtra(Intent.EXTRA_STREAM, uris);
                intent.setType("image/*");
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
                Intent intent2 = new Intent("android.intent.action.VIEW");
                intent2.addFlags(268435456);
                intent2.setData(Uri.parse("market://details?id=com.instagram.android"));
                startActivity(intent2);
            }

        } else if (which.matches("share")) {

            File files = new File(url);

            Uri uri = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".fileprovider", files);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                final Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                final Uri contentUri = uri;
                scanIntent.setData(contentUri);
                sendBroadcast(scanIntent);
            } else {
                final Intent intent1 = new Intent(Intent.ACTION_MEDIA_MOUNTED, uri);
                sendBroadcast(intent1);
            }
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("image/*");
            sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(sharingIntent, "Share Image"));
        }

        relDownload.setVisibility(View.VISIBLE);
    }

    private void scanFile(String path) {
        MediaScannerConnection.scanFile(ImageShower.this,
                new String[]{path}, null,
                (path1, uri) -> Log.e("TAG--)", "Finished scanning " + path1));
    }

    public void setPr(int progress) {
        if (progress == 100) {
            relDownload.setEnabled(true);
            progressBar.setProgress(progress);
            txtProgress.setText("Completed");
//            Snackbar.make(coordinatorLayout, "Download Completed!", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
        } else {
            relDownload.setEnabled(false);
            progressBar.setProgress(progress);
            txtProgress.setText("Downloaded.." + progress + "%");
        }
    }

    public Dialog onCreateDialog(int i) {
        if (i != 0) {
            return null;
        }
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Downloading file. Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setMax(100);
        pDialog.setProgressStyle(1);
        pDialog.setCancelable(true);
        pDialog.show();
        return pDialog;
    }

    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (i != 1 || iArr.length <= 0) {
            if (i != 2 || iArr[0] != 0) {
                if (i != 3 || iArr[0] != 0) {
                    finish();
                }
            }
        } else if (iArr[0] == 0) {
            //new DownloadFileFromURL().execute(new String[]{imgUrl});
            callAction();
        }
    }

    /* access modifiers changed from: private */
    public void download(File file) {
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStorageDirectory().getPath());
        sb.append("/All Status Downloader/");
        File file2 = new File(sb.toString());
        if (!file2.exists()) {
            file2.mkdir();
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append(mediaTitle);
        sb2.append(".jpg");
        final File file3 = new File(file2, sb2.toString());
        if (!file2.exists()) {
            file2.mkdirs();
        }
        try {
            FileUtils.copyFile(file, file3);
        } catch (IOException e) {
            e.printStackTrace();
        }
        scanner = new MediaScannerConnection(this, new MediaScannerConnection.MediaScannerConnectionClient() {
            public void onScanCompleted(String str, Uri uri) {
                scanner.disconnect();
            }

            public void onMediaScannerConnected() {
                scanner.scanFile(file3.getAbsolutePath().toString(), null);
            }
        });
        scanner.connect();
    }
}