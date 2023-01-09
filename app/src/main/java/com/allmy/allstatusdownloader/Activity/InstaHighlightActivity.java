package com.allmy.allstatusdownloader.Activity;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
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
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.allmy.allstatusdownloader.Model.StoriesData;
import com.allmy.allstatusdownloader.Model.Story_setter;
import com.allmy.allstatusdownloader.Others.Constant;
import com.allmy.allstatusdownloader.Others.Utils;
import com.allmy.allstatusdownloader.R;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

import jp.shts.android.storiesprogressview.StoriesProgressView;
@Keep

public class InstaHighlightActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener, View.OnClickListener {

    String checkType;
    private int counter = 2;
    long limit = 500;
    public ProgressBar mProgressBar;
    private final ArrayList<StoriesData> mStoriesList = new ArrayList<>();
    private LinearLayout mVideoViewLayout;
    ArrayList<Story_setter> media;
    private final ArrayList<View> mediaPlayerArrayList = new ArrayList<>();
    int myCounter;
    private Utils utils;
    public ProgressDialog pDialog;
    long pressTime = 0;
    ImageView repost;
    MediaScannerConnection scanner;
    ImageView share;
    public StoriesProgressView storiesProgressView;
    private RelativeLayout coordinatorLayout;
    private ProgressDialog dialog;
    InterstitialAd mInterstitialAd;
    public String dnlUrl, savePath;
    String strFileType;
    String strMimeType, strFileName;
    ProgressBar progressBarDailog;
    RelativeLayout relDownload;
    CardView ib_im_download;
    TextView txtProgress;
    LinearLayout linearDownload;
    View viewDateBg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insta_highlight);

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
        mProgressBar = findViewById(R.id.progressBar);
        mVideoViewLayout = findViewById(R.id.videoView);
        storiesProgressView = findViewById(R.id.stories);

        linearDownload = findViewById(R.id.linearDownload);
        progressBarDailog = findViewById(R.id.progressBarDialog);
        ib_im_download = findViewById(R.id.ib_im_download);
        viewDateBg = findViewById(R.id.viewDateBg);
        txtProgress = findViewById(R.id.txtProgress);
        relDownload = findViewById(R.id.relDownload);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);

        dialog = new ProgressDialog(this);

        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        initilizedview();
        Intent intent = getIntent();
        int intExtra = intent.getIntExtra("position", 0);
        media = intent.getParcelableArrayListExtra("story_data");

        Collections.reverse(media);
        int size = intExtra;
        PrintStream printStream = System.out;
        String sb = "position : " + intExtra + "::" + media.size();
        printStream.println(sb);
        storiesProgressView.setStoriesCount(media.size());
        prepareStoriesList(media);
        storiesProgressView.setStoriesListener(this);
        for (int i = 0; i < mStoriesList.size(); i++) {
            if (mStoriesList.get(i).mimeType.contains("video")) {
                mediaPlayerArrayList.add(getVideoView(i));
            } else if (mStoriesList.get(i).mimeType.contains("image")) {
                mediaPlayerArrayList.add(getImageView(i));
            }
        }
        if (media.size() > 0) {
            counter = size;
            setStoryView(counter);
        }
        viewDateBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Click--)", "Date");
            }
        });

        relDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkType = "download";

                if (txtProgress.getText().toString().matches("Completed")) {
                    setPr(0);
                    txtProgress.setVisibility(View.GONE);
                    relDownload.setVisibility(View.VISIBLE);
                    linearDownload.setVisibility(View.VISIBLE);
                    progressBarDailog.setVisibility(View.GONE);
                    txtProgress.setVisibility(View.GONE);

                    startActivity(new Intent(InstaHighlightActivity.this, CreationActivity.class));
                } else {

                    if (mInterstitialAd != null) {
                        mInterstitialAd.show(InstaHighlightActivity.this);

                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                if (Build.VERSION.SDK_INT > 22) {
                                    String str = "android.permission.WRITE_EXTERNAL_STORAGE";
                                    if (checkSelfPermission(str) != 0) {
                                        shouldShowRequestPermissionRationale(str);
                                        requestPermissions(new String[]{str}, 1);
                                        return;
                                    }
                                    callAction(checkType);
                                    return;
                                }
                                callAction(checkType);
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                if (Build.VERSION.SDK_INT > 22) {
                                    String str = "android.permission.WRITE_EXTERNAL_STORAGE";
                                    if (checkSelfPermission(str) != 0) {
                                        shouldShowRequestPermissionRationale(str);
                                        requestPermissions(new String[]{str}, 1);
                                        return;
                                    }
                                    callAction(checkType);
                                    return;
                                }
                                callAction(checkType);
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                mInterstitialAd = null;
                            }
                        });

                    } else {
                        if (Build.VERSION.SDK_INT > 22) {
                            String str = "android.permission.WRITE_EXTERNAL_STORAGE";
                            if (checkSelfPermission(str) != 0) {
                                shouldShowRequestPermissionRationale(str);
                                requestPermissions(new String[]{str}, 1);
                                return;
                            }
                            callAction(checkType);
                            return;
                        }
                        callAction(checkType);
                    }
                }
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkType = "share";

                Log.e("Share--)", "Click");
                callAction(checkType);
            }
        });


        repost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkType = "repost";
                Log.e("Share--)", "repost");
                callAction(checkType);
            }
        });

        View reverse = findViewById(R.id.reverse);
        reverse.setOnClickListener(view -> storiesProgressView.reverse());
        reverse.setOnTouchListener(onTouchListener);

        View skip = findViewById(R.id.skip);
        skip.setOnClickListener(view -> storiesProgressView.skip());
        skip.setOnTouchListener(onTouchListener);
    }

    private final View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            int action = motionEvent.getAction();
            boolean z = false;
            if (action == 0) {
                pressTime = System.currentTimeMillis();
                storiesProgressView.pause();
                return false;
            } else if (action != 1) {
                return false;
            } else {
                long currentTimeMillis = System.currentTimeMillis();
                storiesProgressView.resume();
                if (limit < currentTimeMillis - pressTime) {
                    z = true;
                }
                return z;
            }
        }
    };

    private void initilizedview() {
        ImageView imgBack = findViewById(R.id.imgBack);

        share = findViewById(R.id.share);
        repost = findViewById(R.id.repost);


        imgBack.setOnClickListener(view -> finish());
    }

    @SuppressLint("WrongConstant")
    private void setStoryView(final int i) {
        myCounter = i;

        Log.e("mediaPlayerArrayList--)", "" + mediaPlayerArrayList.size() + "--)" + myCounter);

        View view = mediaPlayerArrayList.get(myCounter);
        mVideoViewLayout.addView(view);
        if (view instanceof VideoView) {
            final VideoView videoView = (VideoView) view;
            videoView.setOnPreparedListener(mediaPlayer -> {
                mediaPlayer.setOnInfoListener((mediaPlayer1, i1, i2) -> {
                    String sb = "onInfo: =============>>>>>>>>>>>>>>>>>>>" + i1;
                    Log.d("mediaStatus", sb);
                    if (i1 == -110) {
                        mProgressBar.setVisibility(0);
                        storiesProgressView.pause();
                        return true;
                    } else if (i1 == 3) {
                        mProgressBar.setVisibility(8);
                        storiesProgressView.resume();
                        return true;
                    } else if (i1 == 804) {
                        mProgressBar.setVisibility(0);
                        storiesProgressView.pause();
                        return true;
                    } else if (i1 == 701) {
                        mProgressBar.setVisibility(0);
                        storiesProgressView.pause();
                        return true;
                    } else if (i1 != 702) {
                        return false;
                    } else {
                        mProgressBar.setVisibility(0);
                        storiesProgressView.pause();
                        return true;
                    }
                });
                videoView.start();
                mProgressBar.setVisibility(8);
                storiesProgressView.setStoryDuration(mediaPlayer.getDuration());
                storiesProgressView.startStories(i);
            });
        } else if (view instanceof ImageView) {
            ImageView imageView = (ImageView) view;
            mProgressBar.setVisibility(8);
            Glide.with(this).load(mStoriesList.get(i).mediaUrl).addListener(new RequestListener<Drawable>() {
                @SuppressLint("WrongConstant")
                public boolean onLoadFailed(GlideException glideException, Object obj, Target<Drawable> target, boolean z) {
                    Toast.makeText(InstaHighlightActivity.this, "Failed to load media...", 0).show();
                    mProgressBar.setVisibility(8);
                    return false;
                }

                public boolean onResourceReady(Drawable drawable, Object obj, Target<Drawable> target, DataSource dataSource, boolean z) {
                    mProgressBar.setVisibility(8);
                    storiesProgressView.setStoryDuration(5000);
                    storiesProgressView.startStories(i);
                    return false;
                }
            }).into(imageView);
        }
    }

    private void prepareStoriesList(ArrayList<Story_setter> arrayList) {
        for (int i = 0; i < arrayList.size(); i++) {
            String str = "";
            String str2 = "_";
            if (arrayList.get((arrayList.size() - 1) - i).getvideourl().equals(" ")) {
                mStoriesList.add(new StoriesData(arrayList.get((arrayList.size() - 1) - i).getimageurl(), "image/png", arrayList.get((arrayList.size() - 1) - i).getMedia_id().replace(str2, str)));
            } else {
                mStoriesList.add(new StoriesData(arrayList.get((arrayList.size() - 1) - i).getvideourl(), "video/mp4", arrayList.get((arrayList.size() - 1) - i).getMedia_id().replace(str2, str)));
            }
        }
    }

    private VideoView getVideoView(int i) {
        VideoView videoView = new VideoView(this);
        videoView.setVideoPath(mStoriesList.get(i).mediaUrl);
        videoView.setLayoutParams(new FrameLayout.LayoutParams(-1, -2));
        return videoView;
    }

    private ImageView getImageView(int i) {
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new FrameLayout.LayoutParams(-1, -2));
        return imageView;
    }

    @SuppressLint("WrongConstant")
    public void onNext() {
        storiesProgressView.destroy();
        mVideoViewLayout.removeAllViews();
        mProgressBar.setVisibility(0);
        int i = counter + 1;
        counter = i;
        setStoryView(i);
    }

    @SuppressLint("WrongConstant")
    public void onPrev() {
        if (counter - 1 >= 0) {
            storiesProgressView.destroy();
            mVideoViewLayout.removeAllViews();
            mProgressBar.setVisibility(0);
            int i = counter - 1;
            counter = i;
            setStoryView(i);
        }
    }

    public void onComplete() {

    }

    public void onDestroy() {
        storiesProgressView.destroy();
//        progressDailog.dismisDailog();
        super.onDestroy();
    }

    @SuppressLint("WrongConstant")
    public void onClick(View view) {
        int id = view.getId();
    }


    private void callAction(String checkType) {
        String strUrl = mStoriesList.get(myCounter).mediaUrl;
        strMimeType = mStoriesList.get(myCounter).mimeType;
        strFileName = mStoriesList.get(myCounter).mediaTitle;


        if (strMimeType.equals("video/mp4")) {
            strFileName = strFileName + Constant.VIDEO_EXTENTION;
            strFileType = Constant.VIDEO_EXTENTION;
        } else {
            strFileName = strFileName + Constant.IMAGE_EXTENTION;
            strFileType = Constant.IMAGE_EXTENTION;
        }


        setPr(0);
        progressBarDailog.setVisibility(View.GONE);
        txtProgress.setVisibility(View.GONE);
        relDownload.setVisibility(View.VISIBLE);
        linearDownload.setVisibility(View.GONE);
        progressBarDailog.setVisibility(View.VISIBLE);
        txtProgress.setVisibility(View.VISIBLE);
        new DownloadTask(getApplicationContext()).execute(strUrl);


    }


    private class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context) {
            this.context = context;
            relDownload.setEnabled(false);
//            progressDailog = new ProgressDailog(InstaHighlightActivity.this);

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

                // download the file
                input = connection.getInputStream();
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
//                    Snackbar.make(coordinatorLayout, "Download Completed!", Snackbar.LENGTH_SHORT).setAction("Action", null).show();

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


    }

    private void scanFile(String path) {
        MediaScannerConnection.scanFile(InstaHighlightActivity.this,
                new String[]{path}, null,
                (path1, uri) -> Log.e("TAG--)", "Finished scanning " + path1));
    }

    public void setPr(int progress) {
        if (progress == 100) {
            progressBarDailog.setProgress(progress);
            txtProgress.setText("Completed");
//            Snackbar.make(coordinatorLayout, "Download Completed!", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
        } else {
            progressBarDailog.setProgress(progress);
            txtProgress.setText(progress + "%");
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
            callAction(checkType);
            //new DownloadFileFromURL().execute(new String[]{((StoriesData) mStoriesList.get(myCounter)).mediaUrl, ((StoriesData) mStoriesList.get(myCounter)).mimeType});
        }
    }

}