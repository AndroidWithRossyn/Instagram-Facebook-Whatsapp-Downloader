package com.allmy.allstatusdownloader.Model;

import java.io.File;

public class AppInterface {

    public interface OnDownloadStarted {
        public void onDownloadStarted(long requestId);
    }

    public interface OnFileDownloading {
        public void onPreDownloading();

        public void onProgressUpdate(int progress);

        public void onPostDownloading(File downloadedFile);
    }

    public interface OnDeleteWhatsAppImage {
        public void onDeleteWhatsAppImage();
    }

    public interface OnProgressUpdate {
        public void onProgressUpdate(int updateProgress);
    }
}
