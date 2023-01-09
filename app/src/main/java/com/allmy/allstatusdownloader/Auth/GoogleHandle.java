package com.allmy.allstatusdownloader.Auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.allmy.allstatusdownloader.Others.AQUtility;
import com.allmy.allstatusdownloader.Others.Constant;

import org.apache.http.HttpRequest;

import java.io.IOException;

public class GoogleHandle extends AccountHandle implements OnClickListener, OnCancelListener {
    /* access modifiers changed from: private */
    public Account acc;
    private Account[] accs;
    /* access modifiers changed from: private */
    public Activity act;
    /* access modifiers changed from: private */
    public AccountManager am;
    private String email;
    /* access modifiers changed from: private */
    public String token;
    /* access modifiers changed from: private */
    public String type;

    private class Task extends AsyncTask<String, String, Bundle> {
        private Task() {
        }

        /* access modifiers changed from: protected */
        public Bundle doInBackground(String... strArr) {
            try {
                return (Bundle) GoogleHandle.this.am.getAuthToken(GoogleHandle.this.acc, GoogleHandle.this.type, null, GoogleHandle.this.act, null, null).getResult();
            } catch (OperationCanceledException unused) {
            } catch (AuthenticatorException e) {
                AQUtility.debug((Throwable) e);
            } catch (IOException e2) {
                AQUtility.debug((Throwable) e2);
            }
            return null;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Bundle bundle) {
            if (bundle != null) {
                String str = "authtoken";
                if (bundle.containsKey(str)) {
                    GoogleHandle.this.token = bundle.getString(str);
                    GoogleHandle googleHandle = GoogleHandle.this;
                    googleHandle.success(googleHandle.act);
                    return;
                }
            }
            GoogleHandle googleHandle2 = GoogleHandle.this;
            googleHandle2.failure(googleHandle2.act, AjaxStatus.AUTH_ERROR, "rejected");
        }
    }

    public GoogleHandle(Activity activity, String str, String str2) {
        if (Constant.ACTIVE_ACCOUNT.equals(str2)) {
            str2 = getActiveAccount(activity);
        }
        this.act = activity;
        this.type = str.substring(2);
        this.email = str2;
        this.am = AccountManager.get(activity);
    }

    /* access modifiers changed from: protected */
    public void auth() {
        if (this.email == null) {
            accountDialog();
        } else {
            Account[] accountsByType = this.am.getAccountsByType("google");
            for (Account account : accountsByType) {
                if (this.email.equals(account.name)) {
                    auth(account);
                    return;
                }
            }
        }
    }

    public boolean reauth(AbstractAjaxCallback<?, ?> abstractAjaxCallback) {
        this.am.invalidateAuthToken(this.acc.type, this.token);
        try {
            this.token = this.am.blockingGetAuthToken(this.acc, this.type, true);
            AQUtility.debug("re token", this.token);
        } catch (Exception e) {
            AQUtility.debug((Throwable) e);
            this.token = null;
        }
        if (this.token != null) {
            return true;
        }
        return false;
    }

    public String getType() {
        return this.type;
    }

    private void accountDialog() {
        Builder builder = new Builder(this.act);
        this.accs = this.am.getAccountsByType("google");
        Account[] accountArr = this.accs;
        int length = accountArr.length;
        if (length == 1) {
            auth(accountArr[0]);
            return;
        }
        String[] strArr = new String[length];
        for (int i = 0; i < length; i++) {
            strArr[i] = this.accs[i].name;
        }
        builder.setItems(strArr, this);
        builder.setOnCancelListener(this);
        new AQuery(this.act).show(builder.create());
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        Account account = this.accs[i];
        AQUtility.debug("acc", account.name);
        setActiveAccount(this.act, account.name);
        auth(account);
    }

    public static void setActiveAccount(Context context, String str) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(Constant.ACTIVE_ACCOUNT, str).commit();
    }

    public static String getActiveAccount(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(Constant.ACTIVE_ACCOUNT, null);
    }

    private void auth(Account account) {
        this.acc = account;
        new Task().execute(new String[0]);
    }

    public void onCancel(DialogInterface dialogInterface) {
        failure(this.act, AjaxStatus.AUTH_ERROR, "cancel");
    }

    public boolean expired(AbstractAjaxCallback<?, ?> abstractAjaxCallback, AjaxStatus ajaxStatus) {
        int code = ajaxStatus.getCode();
        return code == 401 || code == 403;
    }

    public void applyToken(AbstractAjaxCallback<?, ?> abstractAjaxCallback, HttpRequest httpRequest) {
        StringBuilder sb = new StringBuilder();
        sb.append("GoogleLogin auth=");
        sb.append(this.token);
        httpRequest.addHeader("Authorization", sb.toString());
    }

    public String getCacheUrl(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append("#");
        sb.append(this.token);
        return sb.toString();
    }

    public boolean authenticated() {
        return this.token != null;
    }
}
