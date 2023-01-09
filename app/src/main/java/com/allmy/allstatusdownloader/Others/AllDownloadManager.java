package com.allmy.allstatusdownloader.Others;


import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;


import com.allmy.allstatusdownloader.Model.AppInterface;
import com.allmy.allstatusdownloader.R;

import java.io.File;

public class AllDownloadManager {
    private DownloadManager downloadManager;
    private Activity activity;
    private String strUrl;
    private String subFolderName;
    private String strFileName;
    private AppInterface.OnDownloadStarted onDownloadStarted;
    private Utils utils;
    public static String dnlUrl;


    public AllDownloadManager(Activity activity, String strRawUrl, String subFolderName, String strFileName, AppInterface.OnDownloadStarted onDownloadStarted) {
        this.activity = activity;
        this.strUrl = strRawUrl.replaceAll(" ", "%20");
        this.subFolderName = subFolderName;
        this.strFileName = strFileName;
        this.onDownloadStarted = onDownloadStarted;

        try {

            downloadManager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);

            DownloadManager.Request downloadRequest = new DownloadManager.Request(Uri.parse(strUrl));
            downloadRequest.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
            downloadRequest.setAllowedOverRoaming(false);
            downloadRequest.setTitle(strFileName);
            downloadRequest.setVisibleInDownloadsUi(true);
            downloadRequest.allowScanningByMediaScanner();
            downloadRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            String ext = strFileName.substring(strFileName.indexOf(".") + 1);
            Log.e("ext--)", "" + ext);
            File repostfile;
            File file = new File(activity.getExternalFilesDir(activity.getResources().getString(R.string.app_name)).toString());

            if (!file.exists()) {
                file.mkdirs();
            }
            repostfile = new File(file, strFileName);

            utils.deleteFileIfExists(activity, repostfile);


            if (ext.matches("jpg") || ext.matches("png")) {
                downloadRequest.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, activity.getResources().getString(R.string.app_name) + "/" + strFileName);
            } else {
                downloadRequest.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, activity.getResources().getString(R.string.app_name) + "/" + strFileName);
            }

            onDownloadStarted.onDownloadStarted(downloadManager.enqueue(downloadRequest));
            dnlUrl = repostfile.getAbsolutePath();
            Log.e("dnlUrl--)", "" + dnlUrl);
        } catch (Exception e) {
            Log.e("Excpetion--)", "" + e.getMessage());
            e.printStackTrace();
        }
    }
}