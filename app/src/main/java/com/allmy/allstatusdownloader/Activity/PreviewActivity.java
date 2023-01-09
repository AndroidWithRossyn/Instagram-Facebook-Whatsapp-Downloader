package com.allmy.allstatusdownloader.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.allmy.allstatusdownloader.R;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

public class PreviewActivity extends AppCompatActivity {


    private ImageView ivBack, ivPreviewImage, ivPlay;
    private LinearLayout lWhatsapp, lDownload, lShare;
    String previewUrl, outPutSave;
    String fileName = null;
    ProgressDialog mPrDialog;
    String actionType = "Download", contentType = null;
    InterstitialAd mInterstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        Intent intent = getIntent();
        previewUrl = intent.getStringExtra("preview");
        init();
        loadAds();

        ivBack.setOnClickListener(view -> finish());
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


    public File createDirectory() {
        File rootPath = new File(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), getResources().getString(R.string.app_name)).toString());
        if (!rootPath.exists()) {
            rootPath.mkdirs();
        }

        return rootPath;
    }

    public void init() {
        ivBack = findViewById(R.id.ivBack);
        ivPreviewImage = findViewById(R.id.ivPreviewImage);
        ivPlay = findViewById(R.id.ivPlay);
        lWhatsapp = findViewById(R.id.lWhatsapp);
        lDownload = findViewById(R.id.lDownload);
        lShare = findViewById(R.id.lShare);
        mPrDialog = new ProgressDialog(PreviewActivity.this);
        mPrDialog.setMessage("Please Wait..");
        Uri uriParse = Uri.parse(previewUrl);


        Log.e("previewUrl--)", "" + previewUrl + "--)" + uriParse);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            Glide.with(PreviewActivity.this)
                    .load(uriParse)
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                    .into(ivPreviewImage);
        } else {
            Glide.with(PreviewActivity.this)
                    .load(uriParse)
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                    .into(ivPreviewImage);
        }

        if (previewUrl.endsWith(".mp4")) {
            contentType = "VIDEO";
            ivPlay.setVisibility(View.VISIBLE);
        } else {
            contentType = "IMAGE";
            ivPlay.setVisibility(View.GONE);
        }

        ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, uriParse);
                    intent.setDataAndType(uriParse, "video/mp4");
                    startActivity(intent);

                } catch (Exception e) {

                }
            }
        });
        lDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                actionType = "Download";

                if (mInterstitialAd != null) {
                    mInterstitialAd.show(PreviewActivity.this);
                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            downloadData();
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            downloadData();
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            mInterstitialAd = null;
                        }
                    });

                } else {
                    downloadData();
                }

            }
        });

        lShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionType = "Share";
                downloadData();
            }
        });

        lWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionType = "WhatShare";
                downloadData();
            }
        });

    }

    public void downloadData() {
        createDirectory();

        String saveFilePath = createDirectory() + File.separator;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            try {
                if (previewUrl.toString().endsWith(".mp4")) {
                    if (actionType.matches("Download")) {
                        fileName = "STATUS_" + System.currentTimeMillis() + ".mp4";
                    } else {
                        fileName = ".STATUS_" + System.currentTimeMillis() + ".mp4";
                    }
                    new DownloadFileTask().execute(previewUrl);
                } else {
                    if (actionType.matches("Download")) {
                        fileName = "STATUS_" + System.currentTimeMillis() + ".png";
                    } else {
                        fileName = ".STATUS_" + System.currentTimeMillis() + ".png";
                    }
                    new DownloadFileTask().execute(previewUrl);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            final String path = Uri.parse(previewUrl).getPath();
            String filename = path.substring(path.lastIndexOf("/") + 1);
            final File file = new File(path);
            File destFile = new File(saveFilePath);
            try {
                FileUtils.copyFileToDirectory(file, destFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String fileNameChange = filename.substring(12);
            File newFile = new File(saveFilePath + fileNameChange);
            String contentType = "image/*";
            if (previewUrl.endsWith(".mp4")) {
                contentType = "video/*";
            } else {
                contentType = "image/*";
            }
            MediaScannerConnection.scanFile(PreviewActivity.this, new String[]{newFile.getAbsolutePath()}, new String[]{contentType},
                    new MediaScannerConnection.MediaScannerConnectionClient() {
                        public void onMediaScannerConnected() {
                            //NA
                        }

                        public void onScanCompleted(String path, Uri uri) {
                            //NA
                        }
                    });

            File from = new File(saveFilePath, filename);
            File to = new File(saveFilePath, fileNameChange);
            from.renameTo(to);

            outPutSave = newFile.getAbsolutePath();

            if (actionType.matches("Download")) {
                Toast.makeText(PreviewActivity.this, "Saved SuccessFully", Toast.LENGTH_LONG).show();
            } else if (actionType.matches("Share")) {
                Uri uri;
                if (Build.VERSION.SDK_INT >= 24) {
                    uri = FileProvider.getUriForFile(PreviewActivity.this, getPackageName() + ".fileprovider", new File(outPutSave));
                } else {
                    uri = Uri.fromFile(new File(outPutSave));
                }

                Intent intent = new Intent(Intent.ACTION_SEND);
                if (contentType.equals("image/*")) {
                    intent.setType("image/*");
                } else if (contentType.equals("video/*")) {
                    intent.setType("video/*");
                }
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                StringBuilder sb4 = new StringBuilder();
                sb4.append(getResources().getString(R.string.app_name) + " " + getResources().getString(R.string.Created_By_));
                sb4.append("https://play.google.com/store/apps/details?id=" + getPackageName());
                intent.putExtra(Intent.EXTRA_TEXT, sb4.toString());
                startActivity(Intent.createChooser(intent, getResources().getString(R.string.Share_With)));

            } else if (actionType.matches("WhatShare")) {
                Uri uri;
                if (Build.VERSION.SDK_INT >= 24) {
                    uri = FileProvider.getUriForFile(PreviewActivity.this, getPackageName() + ".fileprovider", new File(outPutSave));

                } else {
                    uri = Uri.fromFile(new File(outPutSave));
                }

                Intent intent = new Intent(Intent.ACTION_SEND);
                if (contentType.matches("image/*")) {
                    intent.setType("image/*");
                } else if (contentType.matches("video/*")) {
                    intent.setType("video/*");
                }
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setPackage("com.whatsapp");
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException unused) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.Msg_WhatsApp_Not_Installed), Toast.LENGTH_LONG).show();
                }
            }

        }
    }

    class DownloadFileTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {

            mPrDialog.show();
        }

        @Override
        protected String doInBackground(String... furl) {
            try {

                InputStream in = getContentResolver().openInputStream(Uri.parse(furl[0]));
                File f = null;
                File rootPath = new File(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), getResources().getString(R.string.app_name)).toString());

                f = new File(rootPath + File.separator + fileName);
                outPutSave = f.getAbsolutePath();
                f.setWritable(true, false);
                OutputStream outputStream = new FileOutputStream(f);
                byte buffer[] = new byte[1024];
                int length = 0;

                while ((length = in.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
                outputStream.close();
                in.close();
            } catch (IOException e) {

                Log.e("Exception--)", "" + e.getMessage());
                System.out.println("error in creating a file");
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... progress) {

        }

        @Override
        protected void onPostExecute(String fileUrl) {

            try {
                if (mPrDialog != null && mPrDialog.isShowing()) {
                    mPrDialog.dismiss();
                }


                if (Build.VERSION.SDK_INT >= 19) {
                    MediaScannerConnection.scanFile(PreviewActivity.this, new String[]
                                    {new File(outPutSave).getAbsolutePath()},
                            null, (path, uri) -> {
                                //no action
                            });
                } else {
                    sendBroadcast(new Intent("android.intent.action.MEDIA_MOUNTED",
                            Uri.fromFile(new File(outPutSave))));
                }


                Log.e("outPutSave--)", "" + outPutSave);
                if (actionType.matches("Download")) {
                    Toast.makeText(getApplicationContext(), "Download Completed", Toast.LENGTH_SHORT).show();
                } else if (actionType.matches("Share")) {
                    Uri uri;
                    if (Build.VERSION.SDK_INT >= 24) {
                        uri = FileProvider.getUriForFile(PreviewActivity.this, getPackageName() + ".fileprovider", new File(outPutSave));
                    } else {
                        uri = Uri.fromFile(new File(outPutSave));
                    }

                    Intent intent = new Intent(Intent.ACTION_SEND);
                    if (contentType.equals("image/*")) {
                        intent.setType("image/*");
                    } else if (contentType.equals("video/*")) {
                        intent.setType("video/*");
                    }
                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    StringBuilder sb4 = new StringBuilder();
                    sb4.append(getResources().getString(R.string.app_name) + " " + getResources().getString(R.string.Created_By_));
                    sb4.append("https://play.google.com/store/apps/details?id=" + getPackageName());
                    intent.putExtra(Intent.EXTRA_TEXT, sb4.toString());
                    startActivity(Intent.createChooser(intent, getResources().getString(R.string.Share_With)));

                } else if (actionType.matches("WhatShare")) {
                    Uri uri;
                    if (Build.VERSION.SDK_INT >= 24) {
                        uri = FileProvider.getUriForFile(PreviewActivity.this, getPackageName() + ".fileprovider", new File(outPutSave));
                    } else {
                        uri = Uri.fromFile(new File(outPutSave));
                    }

                    Intent intent = new Intent(Intent.ACTION_SEND);
                    if (contentType.matches("image/*")) {
                        intent.setType("image/*");
                    } else if (contentType.matches("video/*")) {
                        intent.setType("video/*");
                    }
                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setPackage("com.whatsapp");
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException unused) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.Msg_WhatsApp_Not_Installed), Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {

                if (mPrDialog != null && mPrDialog.isShowing()) {
                    mPrDialog.dismiss();
                }

                Log.e("Exception1--)", "" + e.getMessage());
                e.printStackTrace();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
}