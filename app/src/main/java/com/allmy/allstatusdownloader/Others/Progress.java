package com.allmy.allstatusdownloader.Others;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.view.View;
import android.widget.ProgressBar;

import com.allmy.allstatusdownloader.Auth.AQuery;
import com.loopj.android.http.AsyncHttpClient;
import com.miguelcatalan.materialsearchview.MaterialSearchView;


public class Progress implements Runnable {
    private Activity act;
    private int bytes;
    private int current;
    private ProgressBar pb;
    private ProgressDialog pd;
    private boolean unknown;
    private String url;
    private View view;

    public Progress(Object obj) {
        if (obj instanceof ProgressBar) {
            this.pb = (ProgressBar) obj;
        } else if (obj instanceof ProgressDialog) {
            this.pd = (ProgressDialog) obj;
        } else if (obj instanceof Activity) {
            this.act = (Activity) obj;
        } else if (obj instanceof View) {
            this.view = (View) obj;
        }
    }

    public void reset() {
        ProgressBar progressBar = this.pb;
        if (progressBar != null) {
            progressBar.setProgress(0);
            this.pb.setMax(AsyncHttpClient.DEFAULT_SOCKET_TIMEOUT);
        }
        ProgressDialog progressDialog = this.pd;
        if (progressDialog != null) {
            progressDialog.setProgress(0);
            this.pd.setMax(AsyncHttpClient.DEFAULT_SOCKET_TIMEOUT);
        }
        Activity activity = this.act;
        if (activity != null) {
            activity.setProgress(0);
        }
        this.unknown = false;
        this.current = 0;
        this.bytes = AsyncHttpClient.DEFAULT_SOCKET_TIMEOUT;
    }

    public void setBytes(int i) {
        if (i <= 0) {
            this.unknown = true;
            i = AsyncHttpClient.DEFAULT_SOCKET_TIMEOUT;
        }
        this.bytes = i;
        ProgressBar progressBar = this.pb;
        if (progressBar != null) {
            progressBar.setProgress(0);
            this.pb.setMax(i);
        }
        ProgressDialog progressDialog = this.pd;
        if (progressDialog != null) {
            progressDialog.setProgress(0);
            this.pd.setMax(i);
        }
    }

    public void increment(int i) {
        int i2;
        ProgressBar progressBar = this.pb;
        int i3 = 1;
        if (progressBar != null) {
            progressBar.incrementProgressBy(this.unknown ? 1 : i);
        }
        ProgressDialog progressDialog = this.pd;
        if (progressDialog != null) {
            if (!this.unknown) {
                i3 = i;
            }
            progressDialog.incrementProgressBy(i3);
        }
        if (this.act != null) {
            if (this.unknown) {
                i2 = this.current;
                this.current = i2 + 1;
            } else {
                this.current += i;
                i2 = (this.current * AsyncHttpClient.DEFAULT_SOCKET_TIMEOUT) / this.bytes;
            }
            if (i2 > 9999) {
                i2 = MaterialSearchView.REQUEST_VOICE;
            }
            this.act.setProgress(i2);
        }
    }

    public void done() {
        ProgressBar progressBar = this.pb;
        if (progressBar != null) {
            progressBar.setProgress(progressBar.getMax());
        }
        ProgressDialog progressDialog = this.pd;
        if (progressDialog != null) {
            progressDialog.setProgress(progressDialog.getMax());
        }
        Activity activity = this.act;
        if (activity != null) {
            activity.setProgress(MaterialSearchView.REQUEST_VOICE);
        }
    }

    public void run() {
        dismiss(this.url);
    }

    @SuppressLint("WrongConstant")
    public void show(String str) {
        reset();
        ProgressDialog progressDialog = this.pd;
        if (progressDialog != null) {
            new AQuery(progressDialog.getContext()).show(this.pd);
        }
        Activity activity = this.act;
        if (activity != null) {
            activity.setProgressBarIndeterminateVisibility(true);
            this.act.setProgressBarVisibility(true);
        }
        ProgressBar progressBar = this.pb;
        if (progressBar != null) {
            progressBar.setTag(1090453505, str);
            this.pb.setVisibility(0);
        }
        View view2 = this.view;
        if (view2 != null) {
            view2.setTag(1090453505, str);
            this.view.setVisibility(0);
        }
    }

    public void hide(String str) {
        if (AQUtility.isUIThread()) {
            dismiss(str);
            return;
        }
        this.url = str;
        AQUtility.post(this);
    }

    @SuppressLint("WrongConstant")
    private void dismiss(String str) {
        ProgressDialog progressDialog = this.pd;
        if (progressDialog != null) {
            new AQuery(progressDialog.getContext()).dismiss(this.pd);
        }
        Activity activity = this.act;
        if (activity != null) {
            activity.setProgressBarIndeterminateVisibility(false);
            this.act.setProgressBarVisibility(false);
        }
        ProgressBar progressBar = this.pb;
        if (progressBar != null) {
            progressBar.setTag(1090453505, str);
            this.pb.setVisibility(0);
        }
        View view2 = this.pb;
        if (view2 == null) {
            view2 = this.view;
        }
        if (view2 != null) {
            Object tag = view2.getTag(1090453505);
            if (tag == null || tag.equals(str)) {
                view2.setTag(1090453505, null);
                ProgressBar progressBar2 = this.pb;
                if (progressBar2 != null && progressBar2.isIndeterminate()) {
                    view2.setVisibility(8);
                }
            }
        }
    }

    @SuppressLint("WrongConstant")
    private void showProgress(Object obj, String str, boolean z) {
        if (obj == null) {
            return;
        }
        if (obj instanceof View) {
            View view2 = (View) obj;
            ProgressBar progressBar = obj instanceof ProgressBar ? (ProgressBar) obj : null;
            if (z) {
                view2.setTag(1090453505, str);
                view2.setVisibility(0);
                if (progressBar != null) {
                    progressBar.setProgress(0);
                    progressBar.setMax(100);
                    return;
                }
                return;
            }
            Object tag = view2.getTag(1090453505);
            if (tag == null || tag.equals(str)) {
                view2.setTag(1090453505, null);
                if (progressBar != null && progressBar.isIndeterminate()) {
                    view2.setVisibility(8);
                }
            }
        } else if (obj instanceof Dialog) {
            Dialog dialog = (Dialog) obj;
            AQuery aQuery = new AQuery(dialog.getContext());
            if (z) {
                aQuery.show(dialog);
            } else {
                aQuery.dismiss(dialog);
            }
        } else if (obj instanceof Activity) {
            Activity activity = (Activity) obj;
            activity.setProgressBarIndeterminateVisibility(z);
            activity.setProgressBarVisibility(z);
            if (z) {
                activity.setProgress(0);
            }
        }
    }
}
