package com.allmy.allstatusdownloader.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.allmy.allstatusdownloader.Adapter.ImageAdapter;
import com.allmy.allstatusdownloader.Adapter.VideoAdapter;
import com.allmy.allstatusdownloader.Others.FileListClickInterface;
import com.allmy.allstatusdownloader.Others.Utils;
import com.allmy.allstatusdownloader.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class WhatsappActivity extends AppCompatActivity implements FileListClickInterface {

    ImageView ivBack,ivCreation,ivSetting,ivDashIcon;
    LinearLayout lPhotos,lVideos,btnAllow,btnCancel;
    TextView txtPhotos,txtVideos,txtTitle;
    View vBotomPhoto,vBotomVideo;
    RecyclerView rvData;
    TextView txtNoData;
    ProgressDialog progressDialog;
    private ArrayList<Uri> fileArrayImages = new ArrayList<>();
    private ArrayList<Uri> fileArrayVideos = new ArrayList<>();
    private File[] allFilePath;
    ImageAdapter mImageAdapter;
    VideoAdapter mVideoAdapter;
    String which, strQpath, strNormalPath;
    Utils utils;
    Dialog dailogPermission;

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
        setContentView(R.layout.activity_whatsapp);
        Intent intent = getIntent();
        which = intent.getStringExtra("which");

        init();
        setDailog();

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        ivCreation.setOnClickListener(view -> startActivity(new Intent(WhatsappActivity.this, CreationActivity.class)));

        ivSetting.setOnClickListener(view -> {
            startActivity(new Intent(WhatsappActivity.this, SettingActivity.class));
        });

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {

            if (which.equals("WA")) {
                Log.e("PD equals WA", "WA");
                if (getContentResolver().getPersistedUriPermissions().size() > 0) {
                    String path = getContentResolver().getPersistedUriPermissions().get(0).getUri().getPath();

                    if (path.contains("WhatsApp")) {
                        Log.e("PD contains WA", path);
                        if (dailogPermission != null && dailogPermission.isShowing()) {
                            dailogPermission.dismiss();
                        }
                        progressDialog.show();
                        new LoadAllDataQImages().execute();
                    } else {
                        if (dailogPermission != null) {
                            dailogPermission.show();
                        }
                    }
                } else {
                    if (dailogPermission != null) {
                        dailogPermission.show();
                    }
                }
            }
            else if (which.equals("WGB")) {
                Log.e("PD equals WGB", "WGB");
                if (getContentResolver().getPersistedUriPermissions().size() > 0) {
                    String path = getContentResolver().getPersistedUriPermissions().get(0).getUri().getPath();

                    if (path.contains("GBWhatsApp")) {
                        Log.e("PD contains WGB", path);
                        if (dailogPermission != null && dailogPermission.isShowing()) {
                            dailogPermission.dismiss();
                        }
                        progressDialog.show();
                        new LoadAllDataQImages().execute();
                    } else {
                        if (dailogPermission != null) {
                            dailogPermission.show();
                        }
                    }
                } else {
                    if (dailogPermission != null) {
                        dailogPermission.show();
                    }
                }
            }
            else {
                Log.e("PD equals WB", "WB");
                if (getContentResolver().getPersistedUriPermissions().size() > 0) {
                    String path = getContentResolver().getPersistedUriPermissions().get(0).getUri().getPath();

                    if (path.contains("Business")) {
                        Log.e("PD contains WB", path);
                        if (dailogPermission != null && dailogPermission.isShowing()) {
                            dailogPermission.dismiss();
                        }
                        progressDialog.show();
                        new LoadAllDataQImages().execute();
                    } else {
                        if (dailogPermission != null) {
                            dailogPermission.show();
                        }
                    }
                } else {
                    if (dailogPermission != null) {
                        dailogPermission.show();
                    }
                }
            }

        } else {
            progressDialog.show();
            new LoadAllImageData().execute();
        }

        lPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                txtPhotos.setTextColor(getResources().getColor(R.color.white));
                txtVideos.setTextColor(getResources().getColor(R.color.unselText));
                vBotomPhoto.setVisibility(View.VISIBLE);
                vBotomVideo.setVisibility(View.GONE);

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                    if (getContentResolver().getPersistedUriPermissions().size() > 0) {
                        if (dailogPermission != null && dailogPermission.isShowing()) {
                            dailogPermission.dismiss();
                        }

                        progressDialog.show();
                        new LoadAllDataQImages().execute();

                    } else {
                        if (dailogPermission != null) {
                            dailogPermission.show();
                        }
                    }
                } else {
                    progressDialog.show();
                    new LoadAllImageData().execute();
                }


            }
        });

        lVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtPhotos.setTextColor(getResources().getColor(R.color.unselText));
                txtVideos.setTextColor(getResources().getColor(R.color.white));
                vBotomPhoto.setVisibility(View.GONE);
                vBotomVideo.setVisibility(View.VISIBLE);

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                    if (dailogPermission != null && dailogPermission.isShowing()) {
                        dailogPermission.dismiss();
                    }

                    progressDialog.show();
                    new LoadAllDataQVideos().execute();
                } else {
                    progressDialog.show();
                    new LoadAllVideoData().execute();
                }


            }
        });

    }
    private void setDailog() {

        dailogPermission = new Dialog(WhatsappActivity.this, R.style.WideDialog);
        dailogPermission.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dailogPermission.setCanceledOnTouchOutside(false);
        dailogPermission.setCancelable(false);
        dailogPermission.setContentView(R.layout.dialog_permission);
        dailogPermission.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        progressDialog = new ProgressDialog(WhatsappActivity.this, R.style.AppCompatAlertDialogStyle);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Loading Status. Please wait...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);

        btnAllow = dailogPermission.findViewById(R.id.btnAllow);
        btnCancel = dailogPermission.findViewById(R.id.btnCancel);

        btnCancel.setOnClickListener(view -> {
            if (dailogPermission != null && dailogPermission.isShowing()) {
                dailogPermission.dismiss();
            }

            finish();
        });

        btnAllow.setOnClickListener(view -> {
            try {

                if (dailogPermission != null && dailogPermission.isShowing()) {
                    dailogPermission.dismiss();
                }

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                    StorageManager sm = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
                    Intent intent = sm.getPrimaryStorageVolume().createOpenDocumentTreeIntent();
                    String startDir = strQpath;
                    Uri uri = intent.getParcelableExtra("android.provider.extra.INITIAL_URI");
                    String scheme = uri.toString();
                    scheme = scheme.replace("/root/", "/document/");
                    scheme += "%3A" + startDir;
                    uri = Uri.parse(scheme);
                    intent.putExtra("android.provider.extra.INITIAL_URI", uri);
                    startActivityForResult(intent, 2001);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void init()
    {
        utils = new Utils(WhatsappActivity.this);
        ivBack=findViewById(R.id.ivBack);
        ivCreation=findViewById(R.id.ivCreation);
        ivSetting=findViewById(R.id.ivSetting);
        ivDashIcon=findViewById(R.id.ivDashIcon);
        txtTitle=findViewById(R.id.txtTitle);
        lVideos=findViewById(R.id.lVideos);
        lPhotos=findViewById(R.id.lPhotos);
        txtPhotos=findViewById(R.id.txtPhotos);
        txtVideos=findViewById(R.id.txtVideos);
        vBotomPhoto=findViewById(R.id.vBotomPhoto);
        vBotomVideo=findViewById(R.id.vBotomVideo);
        rvData=findViewById(R.id.rvData);
        txtNoData=findViewById(R.id.txtNoData);

        if (which.matches("WA")) {
            strQpath = "Android%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2F.Statuses";
            strNormalPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/WhatsApp/Media/.Statuses";
            ivDashIcon.setImageDrawable(getResources().getDrawable(R.drawable.dash_wp));
            txtTitle.setText(getResources().getString(R.string.tWhat));
        }else if (which.matches("WGB")) {
            strQpath = "Android%2Fmedia%2Fcom.gbwhatsapp%2FGBWhatsApp%2FMedia%2F.Statuses";
            strNormalPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/GBWhatsApp/Media/.Statuses";
            ivDashIcon.setImageDrawable(getResources().getDrawable(R.drawable.dash_wgb));
            txtTitle.setText(getResources().getString(R.string.tGB));
        }
        else {
            strQpath = "Android%2Fmedia%2Fcom.whatsapp.w4b%2FWhatsApp Business%2FMedia%2F.Statuses";
            strNormalPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/WhatsApp Business/Media/.Statuses";
            ivDashIcon.setImageDrawable(getResources().getDrawable(R.drawable.dash_wb));
            txtTitle.setText(getResources().getString(R.string.tWB));
        }
    }

    public void setImageAdapter() {
        mImageAdapter = new ImageAdapter(WhatsappActivity.this, fileArrayImages, WhatsappActivity.this);
        if (fileArrayImages.size() == 0) {
            txtNoData.setVisibility(View.VISIBLE);
        } else {
            txtNoData.setVisibility(View.GONE);
            rvData.setAdapter(mImageAdapter);
            rvData.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
        }
    }

    public void setVideoAdapter() {
        mVideoAdapter = new VideoAdapter(WhatsappActivity.this, fileArrayVideos, WhatsappActivity.this);
        if (fileArrayVideos.size() == 0) {
            txtNoData.setVisibility(View.VISIBLE);
        } else {
            txtNoData.setVisibility(View.GONE);
            rvData.setAdapter(mVideoAdapter);
            rvData.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == 2001 && resultCode == RESULT_OK) {
                Uri dataUri = data.getData();

                if (which.equals("WA")) {
                    utils.setWAUri(dataUri.toString());
                    Log.e("PD set WA uri", dataUri.toString());
                }
                else if (which.equals("WGB")) {
                    Log.e("PD set WGB uri", dataUri.toString());
                    utils.setGBUri(dataUri.toString());
                }
                else {
                    Log.e("PD set WB uri", dataUri.toString());
                    utils.setWBUri(dataUri.toString());
                }
                if (dataUri.toString().contains(".Statuses")) {
                    getContentResolver().takePersistableUriPermission(dataUri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION |
                                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    progressDialog.show();
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                        new LoadAllDataQImages().execute();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.txtWrongfolder), Toast.LENGTH_LONG).show();
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void getImagePosition(int position) {

        String path = fileArrayImages.get(position).toString();

        Log.e("pathImage--)", "" + path);
        Intent intent = new Intent(WhatsappActivity.this, PreviewActivity.class);
        intent.putExtra("preview", path);
        startActivity(intent);
    }

    @Override
    public void getVideoPosition(int position) {
        String path = fileArrayVideos.get(position).toString();
        Log.e("pathVideo--)", "" + path);
        Intent intent = new Intent(WhatsappActivity.this, PreviewActivity.class);
        intent.putExtra("preview", path);
        startActivity(intent);
    }


    class LoadAllDataQImages extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {

            fileArrayImages = new ArrayList<>();
        }

        @Override
        protected String doInBackground(String... furl) {
            DocumentFile documentFile;
            if (which.matches("WA")) {
                Uri uriParse = Uri.parse(utils.getWAUri());
                documentFile = DocumentFile.fromTreeUri(WhatsappActivity.this, uriParse);
            }
            else if (which.matches("WGB")) {
                Uri uriParse = Uri.parse(utils.getGBUri());
                documentFile = DocumentFile.fromTreeUri(WhatsappActivity.this, uriParse);
            }
            else {
                Uri uriParse = Uri.parse(utils.getWBUri());
                documentFile = DocumentFile.fromTreeUri(WhatsappActivity.this, uriParse);
            }

            for (DocumentFile file : documentFile.listFiles()) {
                if (file.isDirectory()) {

                } else {
                    if (!file.getName().equals(".nomedia")) {
                        if (file.getUri().toString().endsWith(".png") || file.getUri().toString().endsWith(".jpg")) {
                            fileArrayImages.add(file.getUri());
                        }
                    }
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... progress) {

        }

        @Override
        protected void onPostExecute(String fileUrl) {
            progressDialog.dismiss();
            setImageAdapter();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            progressDialog.dismiss();
        }
    }

    class LoadAllDataQVideos extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {

            fileArrayVideos = new ArrayList<>();
        }

        @Override
        protected String doInBackground(String... furl) {
            DocumentFile documentFile;
            if (which.matches("WA")) {
                Uri uriParse = Uri.parse(utils.getWAUri());
                documentFile = DocumentFile.fromTreeUri(WhatsappActivity.this, uriParse);
            }
            else if (which.matches("WGB")) {
                Uri uriParse = Uri.parse(utils.getGBUri());
                documentFile = DocumentFile.fromTreeUri(WhatsappActivity.this, uriParse);
            }
            else {
                Uri uriParse = Uri.parse(utils.getWBUri());
                documentFile = DocumentFile.fromTreeUri(WhatsappActivity.this, uriParse);
            }
            for (DocumentFile file : documentFile.listFiles()) {
                if (file.isDirectory()) {

                } else {
                    if (!file.getName().equals(".nomedia")) {

                        if (file.getUri().toString().endsWith(".mp4")) {
                            fileArrayVideos.add(file.getUri());
                        }
                    }
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... progress) {

        }

        @Override
        protected void onPostExecute(String fileUrl) {
            progressDialog.dismiss();
            setVideoAdapter();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            progressDialog.dismiss();
        }
    }

    class LoadAllImageData extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            String targetPath = strNormalPath;

            File targetDirector = new File(targetPath);
            allFilePath = targetDirector.listFiles();
        }

        @Override
        protected String doInBackground(String... furl) {


            try {
                Arrays.sort(allFilePath, (Comparator) (o1, o2) -> {
                    if (((File) o1).lastModified() > ((File) o2).lastModified()) {
                        return -1;
                    } else if (((File) o1).lastModified() < ((File) o2).lastModified()) {
                        return +1;
                    } else {
                        return 0;
                    }
                });

                for (int i = 0; i < allFilePath.length; i++) {
                    File file = allFilePath[i];
                    if (Uri.fromFile(file).toString().endsWith(".png") || Uri.fromFile(file).toString().endsWith(".jpg")) {
                        fileArrayImages.add(Uri.fromFile(file));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... progress) {

        }

        @Override
        protected void onPostExecute(String fileUrl) {
            progressDialog.dismiss();
            setImageAdapter();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            progressDialog.dismiss();
        }
    }

    class LoadAllVideoData extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            String targetPath = strNormalPath;

            File targetDirector = new File(targetPath);
            allFilePath = targetDirector.listFiles();
        }

        @Override
        protected String doInBackground(String... furl) {


            try {
                Arrays.sort(allFilePath, (Comparator) (o1, o2) -> {
                    if (((File) o1).lastModified() > ((File) o2).lastModified()) {
                        return -1;
                    } else if (((File) o1).lastModified() < ((File) o2).lastModified()) {
                        return +1;
                    } else {
                        return 0;
                    }
                });

                for (int i = 0; i < allFilePath.length; i++) {
                    File file = allFilePath[i];
                    if (Uri.fromFile(file).toString().endsWith(".mp4")) {
                        fileArrayVideos.add(Uri.fromFile(file));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... progress) {
        }

        @Override
        protected void onPostExecute(String fileUrl) {
            progressDialog.dismiss();
            setVideoAdapter();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            progressDialog.dismiss();
        }
    }

}