package com.allmy.allstatusdownloader.Activity;

import static android.provider.MediaStore.Video.Thumbnails.MINI_KIND;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.allmy.allstatusdownloader.Adapter.CreationAdapter;
import com.allmy.allstatusdownloader.Model.VideoModel;
import com.allmy.allstatusdownloader.Others.Utils;
import com.allmy.allstatusdownloader.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class CreationActivity extends AppCompatActivity {

    ImageView ivBack;
    LinearLayout lPhotos,lVideos;
    TextView txtPhotos,txtVideos;
    View vBotomPhoto,vBotomVideo;
    RecyclerView rvData;
    TextView txtNoData;
    public SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
    private ArrayList<VideoModel> strlistImages = new ArrayList<>();
    private ArrayList<VideoModel> strlistVideo = new ArrayList<>();
    CreationAdapter mCreationAdapter;
    ProgressDialog mPrDialog;
    FrameLayout frameBanner;
    Utils utils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation);

        init();
        GetMediaVideo video = new GetMediaVideo("");
        video.execute();


        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        lPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtPhotos.setTextColor(getResources().getColor(R.color.white));
                txtVideos.setTextColor(getResources().getColor(R.color.unselText));

                vBotomPhoto.setVisibility(View.VISIBLE);
                vBotomVideo.setVisibility(View.GONE);

                setImageAdapter();
            }
        });

        lVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtPhotos.setTextColor(getResources().getColor(R.color.unselText));
                txtVideos.setTextColor(getResources().getColor(R.color.white));
                vBotomPhoto.setVisibility(View.GONE);
                vBotomVideo.setVisibility(View.VISIBLE);

                setVideoAdapter();
            }
        });
    }
    public void setImageAdapter()
    {
        Log.e("strlistImages--)",""+strlistImages.size());

        if (strlistImages.size() == 0) {
            txtNoData.setVisibility(View.VISIBLE);
        } else {
            txtNoData.setVisibility(View.GONE);
        }
        mCreationAdapter = new CreationAdapter(CreationActivity.this, strlistImages);
        rvData.setAdapter(mCreationAdapter);
        rvData.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
    }
    public void setVideoAdapter()
    {
        Log.e("strlistImagesV--)",""+strlistVideo.size());
        if (strlistVideo.size() == 0) {
            txtNoData.setVisibility(View.VISIBLE);
        } else {
            txtNoData.setVisibility(View.GONE);

        }
        mCreationAdapter = new CreationAdapter(CreationActivity.this, strlistVideo);
        rvData.setAdapter(mCreationAdapter);
        rvData.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
    }
    public void init()
    {
        ivBack=findViewById(R.id.ivBack);
        lPhotos=findViewById(R.id.lPhotos);
        lVideos=findViewById(R.id.lVideos);
        txtPhotos=findViewById(R.id.txtPhotos);
        txtVideos=findViewById(R.id.txtVideos);
        vBotomPhoto=findViewById(R.id.vBotomPhoto);
        vBotomVideo=findViewById(R.id.vBotomVideo);
        rvData=findViewById(R.id.rvData);
        txtNoData=findViewById(R.id.txtNoData);
        frameBanner = findViewById(R.id.frameBanner);
        utils = new Utils(CreationActivity.this);
        utils.loadBanner(CreationActivity.this, frameBanner);
        mPrDialog = new ProgressDialog(CreationActivity.this);
        mPrDialog.setMessage("Getting Videos..");
    }

    private class GetMediaVideo extends AsyncTask<String, ArrayList<VideoModel>, ArrayList<VideoModel>> {

        String from;

        public GetMediaVideo(String from) {
            this.from = from;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            strlistImages = new ArrayList<>();
            strlistVideo = new ArrayList<>();
            mPrDialog.show();
        }

        @Override
        protected ArrayList<VideoModel> doInBackground(String... voids) {

            ArrayList<VideoModel> videos = new ArrayList<>();

            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), getResources().getString(R.string.app_name));
            if (!file.exists()) {
                return videos;
            }

            File[] files = file.listFiles();

            if (files != null) {
                for (int i = 0; i < files.length; i++) {

                    if (!files[i].isHidden())
                    {
                        File ff = files[i];
                        if (ff.getName().toLowerCase().endsWith(".jpg") || ff.getName().toLowerCase().endsWith(".png") &&  !ff.getName().toLowerCase().endsWith(".temp")) {
                            VideoModel model = new VideoModel();
                            model.setVideoPath(ff.getPath());
                            model.setThumbNail(getThumblineImage(ff.getPath()));
                            model.setVideoFile(ff);
                            model.setVideoName(ff.getName());
                            model.setSize(formatFileSize(ff.length()));
                            model.setDate(simpleDateFormat.format(ff.lastModified()));
                            Log.e("DateVideo--)", "" + model.getDate());
                            strlistImages.add(model);
                        }
                        else if (ff.getName().toLowerCase().endsWith(".mp4") )
                        {
                            VideoModel model = new VideoModel();
                            model.setVideoPath(ff.getPath());
                            model.setThumbNail(getThumblineImage(ff.getPath()));
                            model.setVideoFile(ff);
                            model.setVideoName(ff.getName());
                            model.setSize(formatFileSize(ff.length()));
                            model.setDate(simpleDateFormat.format(ff.lastModified()));
                            Log.e("DateVideo--)", "" + model.getDate());
                            strlistVideo.add(model);
                        }
                    }

                }
            }
            return videos;
        }

        @Override
        protected void onPostExecute(ArrayList<VideoModel> list) {
            super.onPostExecute(list);

            if (mPrDialog != null && mPrDialog.isShowing()) {
                mPrDialog.dismiss();
            }

            setImageAdapter();
        }


    }
    public Bitmap getThumblineImage(String videoPath) {
        return ThumbnailUtils.createVideoThumbnail(videoPath, MINI_KIND);
    }
    public static String formatFileSize(long size) {
        String sFileSize = "";
        if (size > 0) {
            double dFileSize = (double) size;

            double kiloByte = dFileSize / 1024;
            if (kiloByte < 1 && kiloByte > 0) {
                return size + " Byte";
            }
            double megaByte = kiloByte / 1024;
            if (megaByte < 1) {
                sFileSize = String.format("%.2f", kiloByte);
                return sFileSize + " KB";
            }

            double gigaByte = megaByte / 1024;
            if (gigaByte < 1) {
                sFileSize = String.format("%.2f", megaByte);
                return sFileSize + " MB";
            }

            double teraByte = gigaByte / 1024;
            if (teraByte < 1) {
                sFileSize = String.format("%.2f", gigaByte);
                return sFileSize + " GB";
            }

            sFileSize = String.format("%.2f", teraByte);
            return sFileSize + " TB";
        }
        return "0K";
    }

}