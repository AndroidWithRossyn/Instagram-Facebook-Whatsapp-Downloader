package com.allmy.allstatusdownloader.Fragment;

import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;

import static com.allmy.allstatusdownloader.Others.Constant.VIDEO_EXTENTION;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat.Builder;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.allmy.allstatusdownloader.Activity.CreationActivity;
import com.allmy.allstatusdownloader.Activity.ShowAllData;
import com.allmy.allstatusdownloader.Activity.VideoShower;
import com.allmy.allstatusdownloader.Adapter.IgtvshowerRecyclerviewAdapter;
import com.allmy.allstatusdownloader.Auth.AQuery;
import com.allmy.allstatusdownloader.Model.IgtvOnBackPressed;
import com.allmy.allstatusdownloader.Model.Story_setter;
import com.allmy.allstatusdownloader.Others.Constant;
import com.allmy.allstatusdownloader.Others.Constants;
import com.allmy.allstatusdownloader.Others.DetectScrollToEnd;
import com.allmy.allstatusdownloader.Others.GridSpacingItemDecoration;
import com.allmy.allstatusdownloader.Others.RecyclerItemClickListener;
import com.allmy.allstatusdownloader.R;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class IgtvFragment extends Fragment implements IgtvOnBackPressed {
    public static boolean showtickigtv = false;
    private final String ACCOUNT_PREF_BASE_FLAG = "ACCOUNT_PREF";
    private final String COOKIE_PREF_BASE_FLAG = "COOKIE_PREF";
    private Uri Download_Uri;
    private final String LOGIN_PREF_BASE_FLAG = "LOGIN";
    String USER_ID;
    String USER_NAME;
    SharedPreferences accountInfoPref;
    private String alldownloadPath;
    AQuery aq;
    SharedPreferences cookiePref;
    List<Cookie> cookies;
    int counter = 0;
    String csrfTocken;
    SharedPreferences currentUser;
    private String description;
    String download_url;
    int file_ptef;
    GridLayoutManager glm;
    private int id;
    ArrayList<Story_setter> igtv_data;
    IgtvshowerRecyclerviewAdapter igtvshowerRecyclerviewAdapter;
    private int importance;
    boolean isMultiSelect = false;
    ArrayList<Long> list = new ArrayList<>();
    int listsize;
    private final boolean loading = false;
    SharedPreferences loginPref;
    private NotificationChannel mChannel;
    private Builder mNotification;
    private NotificationManager mNotificationManager;
    private PendingIntent mPendingIntent;
    ArrayList<Story_setter> multiselect_list = new ArrayList<>();
    private CharSequence name;
    public String next_url = null;
    RelativeLayout no_storyfound;
    private int notifyId;

    CardView ib_im_download;
    ProgressBar progressBar;
    TextView txtProgress, txtDownload, txt_toolbar;
    LinearLayout linearDownload;
    long durationFl;
    String dnlUrl, savePath;
    int posOfDownload = 0;

    InterstitialAd mInterstitialAd;
    private int pastVisiblesItems;
    boolean permissionmenu = false;
    int position;
    RecyclerView recyclerView;
    private long refid;
    String responseString;
    RelativeLayout rl_download;

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.position = getArguments().getInt("position");
        if (getActivity() != null) {
            this.file_ptef = getActivity().getSharedPreferences("DOWNLOAD_FILE_NAME_PREF", 0).getInt("file_value", 0);
            PrintStream printStream = System.out;
            StringBuilder sb = new StringBuilder();
            sb.append("file_ptef : ");
            sb.append(this.file_ptef);
            printStream.println(sb.toString());
        } else {
            this.file_ptef = 0;
        }
        setHasOptionsMenu(true);
        return layoutInflater.inflate(R.layout.fragment_igtv, viewGroup, false);
    }

    @SuppressLint("WrongConstant")
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        this.no_storyfound = view.findViewById(R.id.no_storyfound);
        this.recyclerView = view.findViewById(R.id.recycler_view);
        this.rl_download = view.findViewById(R.id.rl_download);

        ib_im_download = view.findViewById(R.id.ib_im_download);
        progressBar = view.findViewById(R.id.progressBar);
        txtProgress = view.findViewById(R.id.txtProgress);
        linearDownload = view.findViewById(R.id.linearDownload);
        txtDownload = view.findViewById(R.id.txtDownload);

        InterstitialAd.load(getContext(), getString(R.string.interstitial_ID), new AdRequest.Builder().build(), new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                mInterstitialAd = interstitialAd;
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                mInterstitialAd = null;

            }
        });

        glm = new GridLayoutManager(getActivity(), 3);
        glm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            public int getSpanSize(int i) {
                switch (igtvshowerRecyclerviewAdapter.getItemViewType(i)) {
                    case 22:
                        return 1;
                    case 11:
                        return 3;
                    default:
                        return 3;
                }
            }
        });
        this.recyclerView.setLayoutManager(this.glm);
        this.recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, 3, false));
        ViewCompat.setNestedScrollingEnabled(this.recyclerView, false);
        this.aq = new AQuery(getActivity());
        //this.downloadManager = (DownloadManager) getActivity().getSystemService("download");
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == 4 && keyEvent.getAction() == 1) {
                    if (IgtvFragment.showtickigtv) {
                        rl_download.setVisibility(8);
                        IgtvFragment.showtickigtv = false;
                        if (multiselect_list != null) {
                            multiselect_list.clear();
                        }
                        if (list != null) {
                            list.clear();
                            counter = 0;
                        }
                        for (int i2 = 0; i2 < igtv_data.size(); i2++) {
                            igtv_data.get(i2).setCheckmultiple(false);
                        }
                        if (igtvshowerRecyclerviewAdapter != null) {
                            igtvshowerRecyclerviewAdapter.notifyDataSetChanged();
                        }
                        return true;
                    }
                }
                return false;
            }
        });

        ib_im_download.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (multiselect_list != null && multiselect_list.size() > 0) {

                    if (progressBar.getProgress() == 100) {
                        progressBar.setProgress(0);
                        linearDownload.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        txtProgress.setVisibility(View.GONE);

                        Intent intent = new Intent(getContext(), CreationActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    } else {

                        if (mInterstitialAd != null) {
                            mInterstitialAd.show(getActivity());

                            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    linearDownload.setVisibility(View.GONE);
                                    progressBar.setVisibility(View.VISIBLE);
                                    txtProgress.setVisibility(View.VISIBLE);
                                    downloadMultipleStory();
                                }

                                @Override
                                public void onAdFailedToShowFullScreenContent(AdError adError) {
                                    linearDownload.setVisibility(View.GONE);
                                    progressBar.setVisibility(View.VISIBLE);
                                    txtProgress.setVisibility(View.VISIBLE);
                                    downloadMultipleStory();
                                }

                                @Override
                                public void onAdShowedFullScreenContent() {
                                    mInterstitialAd = null;
                                }
                            });

                        } else {
                            linearDownload.setVisibility(View.GONE);
                            progressBar.setVisibility(View.VISIBLE);
                            txtProgress.setVisibility(View.VISIBLE);
                            downloadMultipleStory();
                        }


                    }


                    Log.e("arr_downloadlists--)", "" + multiselect_list.size() + "--)" + progressBar.getProgress());

                } else {
                    if (progressBar.getProgress() == 100) {
                        Intent intent = new Intent(getContext(), CreationActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                }
            }
        });

    }

    @SuppressLint("WrongConstant")
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        ((ShowAllData) getActivity()).setIgtvOnBackPressedListener(this);
        if (isNetworkAvailable()) {
            getLoginCookies();
        } else if (getActivity() != null) {
            Toast.makeText(getActivity(), getResources().getString(R.string.internet_connection), 0).show();
        }
    }

    private void getLoginCookies() {
        String str = "CURRENT_USER";
        this.currentUser = getActivity().getSharedPreferences(str, 0);
        String string = this.currentUser.getString(str, null);
        FragmentActivity activity = getActivity();
        StringBuilder sb = new StringBuilder();
        sb.append("LOGIN_");
        sb.append(string.toLowerCase(Locale.getDefault()).trim());
        this.loginPref = activity.getSharedPreferences(sb.toString(), 0);
        FragmentActivity activity2 = getActivity();
        StringBuilder sb2 = new StringBuilder();
        sb2.append("COOKIE_PREF_");
        sb2.append(string.toLowerCase(Locale.getDefault()).trim());
        this.cookiePref = activity2.getSharedPreferences(sb2.toString(), 0);
        FragmentActivity activity3 = getActivity();
        StringBuilder sb3 = new StringBuilder();
        sb3.append("ACCOUNT_PREF_");
        sb3.append(string.toLowerCase(Locale.getDefault()).trim());
        this.accountInfoPref = activity3.getSharedPreferences(sb3.toString(), 0);
        this.cookies = new ArrayList();
        int i = this.cookiePref.getInt("cookie_count", -1);
        for (int i2 = 0; i2 < i; i2++) {
            PrintStream printStream = System.out;
            StringBuilder sb4 = new StringBuilder();
            sb4.append("Cookies from pref : ");
            String str2 = "";
            sb4.append(this.cookiePref.getString(String.valueOf(i2), str2));
            printStream.println(sb4.toString());
            try {
                Cookie parse = Cookie.parse(HttpUrl.get(new URL("https://i.instagram.com/")), this.cookiePref.getString(String.valueOf(i2), str2));
                this.cookies.add(parse);
                if (parse.toString().contains("csrftoken")) {
                    this.csrfTocken = parse.toString().split(";")[0].split("=")[1];
                    PrintStream printStream2 = System.out;
                    StringBuilder sb5 = new StringBuilder();
                    sb5.append("csrfTocken  :");
                    sb5.append(this.csrfTocken);
                    printStream2.println(sb5.toString());
                }
                PrintStream printStream3 = System.out;
                StringBuilder sb6 = new StringBuilder();
                sb6.append("MYTestcookie  :");
                sb6.append(parse);
                printStream3.println(sb6.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        fetchIgtv();
    }

    private void fetchIgtv() {
//        this.progressBarfeed.setVisibility(0);
        String uuid = UUID.randomUUID().toString();
        StringBuilder sb = new StringBuilder();
        sb.append("user_");
        sb.append(ShowAllData.USER_ID);
        String sb2 = sb.toString();
        OkHttpClient build = new OkHttpClient.Builder().cookieJar(new CookieJar() {
            public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
            }

            public List<Cookie> loadForRequest(HttpUrl httpUrl) {
                return cookies;
            }
        }).build();
        String str = "_csrftoken";
        FormBody build2 = new FormBody.Builder().add("id", sb2).add("_uuid", uuid).add("_uid", ShowAllData.USER_ID).add(str, this.csrfTocken).build();
        PrintStream printStream = System.out;
        StringBuilder sb3 = new StringBuilder();
        sb3.append("url_Feed :");
        String str2 = "https://i.instagram.com/api/v1/igtv/channel/";
        sb3.append(str2);
        printStream.println(sb3.toString());
        build.newCall(new Request.Builder().url(str2).post(build2).addHeader("User-Agent", Constants.USER_AGENT).addHeader("Connection", "close").addHeader("Accept-Language", "en-US").addHeader("language", "en").addHeader("Accept", "*/*").addHeader("Content-type", "application/x-www-form-urlencoded; charset=UTF-8").build()).enqueue(new Callback() {
            public void onFailure(Call call, IOException iOException) {
            }

            public void onResponse(Call call, Response response) throws IOException {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
//                            progressBarfeed.setVisibility(4);
                        }
                    });
                }
                responseString = response.body().string();
                PrintStream printStream = System.out;
                StringBuilder sb = new StringBuilder();
                sb.append("Igtv response = :");
                sb.append(responseString);
                printStream.println(sb.toString());
                if (getActivity() == null) {
                    return;
                }
                if (responseString != null && !getActivity().isFinishing()) {
                    IgtvFragment igtvFragment = IgtvFragment.this;
                    igtvFragment.parseResponce(igtvFragment.responseString);
                } else if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @SuppressLint("WrongConstant")
                        public void run() {
                            if (!isNetworkAvailable() && getActivity() != null) {
                                Toast.makeText(getActivity(), getResources().getString(R.string.internet_connection), 0).show();
                            }
                        }
                    });
                }
            }
        });
    }

    public void parseResponce(String str) {
        IgtvFragment igtvFragment;
        String str2;
        String str3;
        String str4 = null;
        IgtvFragment igtvFragment2 = this;
        String str5 = "caption";
        String str6 = "max_id";
        try {
            igtvFragment2.igtv_data = new ArrayList<>();
            JSONObject jSONObject = new JSONObject(str);
            PrintStream printStream = System.out;
            StringBuilder sb = new StringBuilder();
            sb.append("obj :");
            sb.append(jSONObject.toString());
            printStream.println(sb.toString());
            JSONArray jSONArray = jSONObject.getJSONArray("items");
            if (!jSONObject.isNull(str6)) {
                igtvFragment2.next_url = jSONObject.getString(str6);
                PrintStream printStream2 = System.out;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("next_url : ");
                sb2.append(igtvFragment2.next_url);
                printStream2.println(sb2.toString());
            } else {
                igtvFragment2.next_url = null;
            }
            int i = 0;
            Log.e("Igtv length", "" + jSONArray.length());
            while (i < jSONArray.length()) {
                Story_setter story_setter = new Story_setter();
                String string = jSONArray.getJSONObject(i).getString("taken_at");
                JSONObject jSONObject2 = jSONArray.getJSONObject(i);
                String str7 = "";
                if (!jSONObject2.isNull(str5)) {
                    str2 = jSONObject2.getJSONObject(str5).getString("text");
                    PrintStream printStream3 = System.out;
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("text : ");
                    sb3.append(str2);
                    printStream3.println(sb3.toString());
                } else {
                    str2 = str7;
                }
                String string2 = jSONArray.getJSONObject(i).getString("code");
                String string3 = jSONArray.getJSONObject(i).getString("original_width");
                String string4 = jSONArray.getJSONObject(i).getString("original_height");
                String replace = jSONArray.getJSONObject(i).getString("id").replace("_", str7);
                if (igtvFragment2.file_ptef == 0) {
                    str3 = replace;
                } else if (igtvFragment2.file_ptef == 1) {
                    StringBuilder sb4 = new StringBuilder();
                    sb4.append(igtvFragment2.USER_NAME);
                    sb4.append(replace);
                    str3 = sb4.toString();
                } else {
                    StringBuilder sb5 = new StringBuilder();
                    sb5.append(replace);
                    sb5.append(igtvFragment2.USER_NAME);
                    str3 = sb5.toString();
                }
                PrintStream printStream4 = System.out;
                StringBuilder sb6 = new StringBuilder();
                sb6.append("code_HdUrl : ");
                sb6.append(string2);
                printStream4.println(sb6.toString());
                boolean equalsIgnoreCase = jSONArray.getJSONObject(i).getString("media_type").equalsIgnoreCase("1");
                String str8 = "candidates";
                String str9 = "image_versions2";
                String str10 = "width";
                String str11 = str5;
                String str12 = "height";
                String str13 = "url";
                if (equalsIgnoreCase) {
                    try {
                        JSONArray jSONArray2 = jSONArray.getJSONObject(i).getJSONObject(str9).getJSONArray(str8);
                        String string5 = jSONArray2.getJSONObject(0).getString(str13);
                        if (jSONArray2.length() > 1) {
                            story_setter.setThimbnail_url(jSONArray2.getJSONObject(1).getString(str13));
                        } else {
                            story_setter.setThimbnail_url(jSONArray2.getJSONObject(0).getString(str13));
                        }
                        story_setter.setimageurl(string5);
                        story_setter.setvideourl(" ");
                        String string6 = jSONArray2.getJSONObject(0).getString(str12);
                        String string7 = jSONArray2.getJSONObject(0).getString(str10);
                        PrintStream printStream5 = System.out;
                        str4 = string;
                        StringBuilder sb7 = new StringBuilder();
                        int i2 = i;
                        sb7.append("Height : width :: ");
                        sb7.append(" ::: ");
                        sb7.append(string6);
                        sb7.append(string7);
                        printStream5.println(sb7.toString());
                        story_setter.setimageheight(jSONArray2.getJSONObject(0).getString(str12));
                        story_setter.setimagewidth(jSONArray2.getJSONObject(0).getString(str10));
                        story_setter.setCode_HdUrl(string2);
                        story_setter.setOriginal_height(string4);
                        story_setter.setOriginal_width(string3);
                        story_setter.setCheckmultiple(false);
                        story_setter.setMedia_title(str3);
                        story_setter.setMedia_id(replace);
                        story_setter.setCheckAd(1);
                        story_setter.setCaption_text(str2);
                        i = i2;
                    } catch (JSONException e) {
                        e = e;
                        igtvFragment = this;
                        e.printStackTrace();
                        if (getActivity() != null) {
                        }
                    }
                } else {
                    str4 = string;
                    JSONArray jSONArray3 = jSONArray.getJSONObject(i).getJSONObject(str9).getJSONArray(str8);
                    String string8 = jSONArray3.getJSONObject(0).getString(str13);
                    if (jSONArray3.length() > 1) {
                        story_setter.setThimbnail_url(jSONArray3.getJSONObject(1).getString(str13));
                    } else {
                        story_setter.setThimbnail_url(jSONArray3.getJSONObject(0).getString(str13));
                    }
                    story_setter.setimageurl(string8);
                    story_setter.setimageheight(jSONArray3.getJSONObject(0).getString(str12));
                    story_setter.setimagewidth(jSONArray3.getJSONObject(0).getString(str10));
                    String string9 = jSONArray.getJSONObject(i).getJSONObject("user").getString("profile_pic_url");
                    story_setter.setCode_HdUrl(string2);
                    story_setter.setOriginal_height(string4);
                    story_setter.setOriginal_width(string3);
                    story_setter.setCheckmultiple(false);
                    story_setter.setMedia_title(str3);
                    story_setter.setMedia_id(replace);
                    story_setter.setCheckAd(1);
                    story_setter.setCaption_text(str2);
                    story_setter.setProfile_picture(string9);
                    story_setter.setvideourl(jSONArray.getJSONObject(i).getJSONArray("video_versions").getJSONObject(0).getString(str13));
                }
                long parseLong = Long.parseLong(str4);
                PrintStream printStream6 = System.out;
                StringBuilder sb8 = new StringBuilder();
                sb8.append("dateString : ");
                igtvFragment = this;
                sb8.append(igtvFragment.getDate(parseLong));
                printStream6.println(sb8.toString());
                story_setter.setGetDate(igtvFragment.getDate(parseLong));
                if (igtvFragment.igtv_data != null) {
                    igtvFragment.igtv_data.add(story_setter);
                    i++;
                    igtvFragment2 = igtvFragment;
                    str5 = str11;
                } else {
                    return;
                }
            }
            igtvFragment = igtvFragment2;
        } catch (JSONException e) {
            igtvFragment = igtvFragment2;
            e.printStackTrace();
            if (getActivity() != null) {
            }
        }
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    if (responseString != null && igtv_data != null) {
                        showGridstory();
                        try {
//                            progressBarfeed.setVisibility(4);
                        } catch (Exception unused) {
                        } catch (Throwable th) {
//                            progressBarfeed.setVisibility(4);
                            throw th;
                        }
//                        progressBarfeed.setVisibility(4);
                    }
                }
            });
        }
    }

    /* access modifiers changed from: private */
    @SuppressLint("WrongConstant")
    public void showGridstory() {
        if (!getActivity().isFinishing()) {
            ArrayList<Story_setter> arrayList = this.igtv_data;
            if (arrayList != null && arrayList.size() > 0) {
//                this.progressBarfeed.setVisibility(8);


                Log.e("SizeBeforeIGTV--)", "" + igtv_data.size());
                Log.e("SizeAfterIGTV--)", "" + igtv_data.size());


                this.igtvshowerRecyclerviewAdapter = new IgtvshowerRecyclerviewAdapter(this.igtv_data, (ShowAllData) getActivity(), this.aq, ShowAllData.user_FULLNAME);
                this.recyclerView.setAdapter(this.igtvshowerRecyclerviewAdapter);
                this.recyclerView.addOnScrollListener(new DetectScrollToEnd(this.glm, 5) {
                    public void onLoadMore() {
                        if (next_url != null) {
//                            progressBarbottom.setVisibility(0);
                            IgtvFragment igtvFragment = IgtvFragment.this;
                            igtvFragment.reloadIgtv(igtvFragment.next_url);
                        }
                    }
                });
                if (getActivity() != null) {
                    this.recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), this.recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                        public void onItemClick(View view, int i) {
                            if (isMultiSelect) {
                                multi_select(i);
                            } else {
                                if (isNetworkAvailable()) {
                                    String str = "full_name";

                                    Intent intent2 = new Intent(getContext(), VideoShower.class);
                                    intent2.putExtra(str, ShowAllData.user_FULLNAME);
                                    intent2.putExtra("title", (igtv_data.get(i)).getMedia_title());
                                    intent2.putExtra("id", (igtv_data.get(i)).getMedia_id());
                                    intent2.putExtra("imgUrl", (igtv_data.get(i)).getimageurl());
                                    intent2.putExtra("profileUrl", (igtv_data.get(i)).getProfile_picture());
                                    PrintStream printStream = System.out;
                                    StringBuilder sb = new StringBuilder();
                                    sb.append("profileUrl :");
                                    sb.append((igtv_data.get(i)).getProfile_picture());
                                    printStream.println(sb.toString());
                                    intent2.putExtra("videoUrl", (igtv_data.get(i)).getvideourl());
                                    intent2.setFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                                    startActivity(intent2);
                                    return;
                                }
                                Toast.makeText(getContext(), "Please check internet connection...", 0).show();
                            }
                        }

                        @SuppressLint("WrongConstant")
                        public void onItemLongClick(View view, int i) {
                            if (!isMultiSelect) {
                                multiselect_list = new ArrayList<>();
                                isMultiSelect = true;

                                linearDownload.setVisibility(View.VISIBLE);
                                txtDownload.setVisibility(View.VISIBLE);
                                txtProgress.setVisibility(View.GONE);
                                progressBar.setVisibility(View.GONE);
                                rl_download.setVisibility(View.VISIBLE);
                            }
                            multi_select(i);
//                    }
                            if (!isMultiSelect) {
                                multiselect_list = new ArrayList<>();
                                isMultiSelect = true;
                            }
                            IgtvFragment.showtickigtv = true;
                            igtvshowerRecyclerviewAdapter.notifyDataSetChanged();


                        }
                    }));
                    return;
                }
                return;
            }
        }
        ArrayList<Story_setter> arrayList2 = this.igtv_data;
        if (arrayList2 == null || arrayList2.size() >= 1 || getActivity().isFinishing()) {
//            this.progressBarfeed.setVisibility(8);
            this.no_storyfound.setVisibility(0);
            return;
        }
//        this.progressBarfeed.setVisibility(8);
        this.no_storyfound.setVisibility(0);
    }

    @SuppressLint("WrongConstant")
    public void downloadAllMedia() {
        this.multiselect_list = new ArrayList<>();
        for (int i = 0; i < this.igtv_data.size(); i++) {
            if (!isFilepath(this.igtv_data.get(i).getMedia_id())) {
                this.multiselect_list.add(this.igtv_data.get(i));
            }
        }
        ArrayList<Story_setter> arrayList = this.multiselect_list;
        if (arrayList != null && arrayList.size() > 0) {
            downloadMultipleStory();
        } else if (getActivity() != null) {
            Toast.makeText(getActivity(), getResources().getString(R.string.select_media), 0).show();
        }
    }

    public void multi_select(int i) {
        if (multiselect_list.contains(igtv_data.get(i))) {

            igtv_data.get(i).setCheckmultiple(false);
            multiselect_list.remove(igtv_data.get(i));
        } else {

            igtv_data.get(i).setCheckmultiple(true);
            multiselect_list.add(igtv_data.get(i));
        }
        if (multiselect_list.size() == 0) {
            isMultiSelect = false;
            multiselect_list.clear();
        }

        refreshAdapter(i);

        if (multiselect_list.size() == 0) {
            rl_download.setVisibility(View.GONE);

            isMultiSelect = false;
        } else {
            rl_download.setVisibility(View.VISIBLE);
            txtDownload.setText("Download (" + multiselect_list.size() + ")");
        }

    }

    private void refreshAdapter(int i) {
        try {
            IgtvshowerRecyclerviewAdapter igtvshowerRecyclerviewAdapter2 = this.igtvshowerRecyclerviewAdapter;
            if (igtvshowerRecyclerviewAdapter2 != null) {
                igtvshowerRecyclerviewAdapter2.notifyItemChanged(i);
            }
        } catch (Exception e) {

        }

    }

    /* access modifiers changed from: private */
    public void reloadIgtv(String str) {
        String uuid = UUID.randomUUID().toString();
        StringBuilder sb = new StringBuilder();
        sb.append("user_");
        sb.append(ShowAllData.USER_ID);
        String sb2 = sb.toString();
        OkHttpClient build = new OkHttpClient.Builder().cookieJar(new CookieJar() {
            public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
            }

            public List<Cookie> loadForRequest(HttpUrl httpUrl) {
                return cookies;
            }
        }).build();
        String str2 = "_csrftoken";
        FormBody build2 = new FormBody.Builder().add("id", sb2).add("_uuid", uuid).add("_uid", ShowAllData.USER_ID).add(str2, this.csrfTocken).add("max_id", str).build();
        PrintStream printStream = System.out;
        StringBuilder sb3 = new StringBuilder();
        sb3.append("url_Feed :");
        String str3 = "https://i.instagram.com/api/v1/igtv/channel/";
        sb3.append(str3);
        printStream.println(sb3.toString());
        build.newCall(new Request.Builder().url(str3).post(build2).addHeader("User-Agent", Constants.USER_AGENT).addHeader("Connection", "close").addHeader("Accept-Language", "en-US").addHeader("language", "en").addHeader("Accept", "*/*").addHeader("Content-type", "application/x-www-form-urlencoded; charset=UTF-8").build()).enqueue(new Callback() {
            public void onFailure(Call call, IOException iOException) {
            }

            public void onResponse(Call call, Response response) throws IOException {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
//                            progressBarbottom.setVisibility(4);
                        }
                    });
                }
                String str = response.body().string();
                PrintStream printStream = System.out;
                StringBuilder sb = new StringBuilder();
                sb.append("parsing class response = :");
                sb.append(responseString);
                printStream.println(sb.toString());
                if (getActivity() != null) {
                    if (!getActivity().isFinishing()) {
                        getreloadedIgtvData(str);
                    } else if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @SuppressLint("WrongConstant")
                            public void run() {
                                if (!isNetworkAvailable() && getActivity() != null) {
                                    Toast.makeText(getActivity(), getResources().getString(R.string.internet_connection), 0).show();
                                }
                            }
                        });
                    }
                }

            }
        });
    }

    public void getreloadedIgtvData(String str) {
        String str2;
        String str3;
        String str4 = null;
        int iUpdate = 0;
        IgtvFragment igtvFragment = this;
        String str5 = "caption";
        String str6 = "max_id";
        int size = igtv_data.size();
        try {
            JSONObject jSONObject = new JSONObject(str);
            JSONArray jSONArray = jSONObject.getJSONArray("items");
            if (!jSONObject.isNull(str6)) {
                igtvFragment.next_url = jSONObject.getString(str6);
            } else {
                igtvFragment.next_url = null;
            }
            int i = 0;
            while (i < jSONArray.length()) {
                Story_setter story_setter = new Story_setter();
                String string = jSONArray.getJSONObject(i).getString("taken_at");
                JSONObject jSONObject2 = jSONArray.getJSONObject(i);
                String str7 = "";

                int i4 = size;
                if (!jSONObject2.isNull(str5)) {
                    str2 = jSONObject2.getJSONObject(str5).getString("text");
                    PrintStream printStream = System.out;
                    StringBuilder sb = new StringBuilder();
                    sb.append("text : ");
                    sb.append(str2);
                    printStream.println(sb.toString());
                } else {
                    str2 = str7;
                }
                String string2 = jSONArray.getJSONObject(i).getString("code");
                String string3 = jSONArray.getJSONObject(i).getString("original_width");
                String string4 = jSONArray.getJSONObject(i).getString("original_height");
                String replace = jSONArray.getJSONObject(i).getString("id").replace("_", str7);
                if (igtvFragment.file_ptef == 0) {
                    str3 = replace;
                } else if (igtvFragment.file_ptef == 1) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append(igtvFragment.USER_NAME);
                    sb2.append(replace);
                    str3 = sb2.toString();
                } else {
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append(replace);
                    sb3.append(igtvFragment.USER_NAME);
                    str3 = sb3.toString();
                }
                PrintStream printStream2 = System.out;
                StringBuilder sb4 = new StringBuilder();
                sb4.append("code_HdUrl : ");
                sb4.append(string2);
                printStream2.println(sb4.toString());
                boolean equalsIgnoreCase = jSONArray.getJSONObject(i).getString("media_type").equalsIgnoreCase("1");
                String str8 = "candidates";
                String str9 = "image_versions2";
                String str10 = "width";
                String str11 = str5;
                String str12 = "height";
                String str13 = "url";
                if (equalsIgnoreCase) {
                    try {
                        JSONArray jSONArray2 = jSONArray.getJSONObject(i).getJSONObject(str9).getJSONArray(str8);
                        String string5 = jSONArray2.getJSONObject(0).getString(str13);
                        str4 = string;
                        if (jSONArray2.length() > 1) {
                            story_setter.setThimbnail_url(jSONArray2.getJSONObject(1).getString(str13));
                        } else {
                            story_setter.setThimbnail_url(jSONArray2.getJSONObject(0).getString(str13));
                        }
                        story_setter.setimageurl(string5);
                        story_setter.setvideourl(" ");
                        String string6 = jSONArray2.getJSONObject(0).getString(str12);
                        String string7 = jSONArray2.getJSONObject(0).getString(str10);
                        PrintStream printStream3 = System.out;
                        StringBuilder sb5 = new StringBuilder();
                        int i2 = i;
                        sb5.append("Height : width :: ");
                        sb5.append(string6);
                        sb5.append(" ::: ");
                        sb5.append(string7);
                        printStream3.println(sb5.toString());
                        story_setter.setimageheight(jSONArray2.getJSONObject(0).getString(str12));
                        story_setter.setimagewidth(jSONArray2.getJSONObject(0).getString(str10));
                        story_setter.setCode_HdUrl(string2);
                        story_setter.setOriginal_height(string4);
                        story_setter.setOriginal_width(string3);
                        story_setter.setMedia_title(str3);
                        story_setter.setMedia_id(replace);
                        story_setter.setCaption_text(str2);
                        story_setter.setCheckAd(1);


                        i = i2;
                    } catch (JSONException e) {
                        e = e;
                        igtvFragment = this;
                        e.printStackTrace();
                        if (getActivity() != null) {
                        }
                    }
                } else {
                    str4 = string;
                    JSONArray jSONArray3 = jSONArray.getJSONObject(i).getJSONObject(str9).getJSONArray(str8);
                    String string8 = jSONArray3.getJSONObject(0).getString(str13);
                    if (jSONArray3.length() > 1) {
                        story_setter.setThimbnail_url(jSONArray3.getJSONObject(1).getString(str13));
                    } else {
                        story_setter.setThimbnail_url(jSONArray3.getJSONObject(0).getString(str13));
                    }
                    story_setter.setimageurl(string8);
                    story_setter.setimageheight(jSONArray3.getJSONObject(0).getString(str12));
                    story_setter.setimagewidth(jSONArray3.getJSONObject(0).getString(str10));
                    story_setter.setCode_HdUrl(string2);
                    story_setter.setOriginal_height(string4);
                    story_setter.setOriginal_width(string3);
                    story_setter.setMedia_title(str3);
                    story_setter.setMedia_id(replace);
                    story_setter.setCaption_text(str2);
                    story_setter.setCheckAd(1);
                    story_setter.setvideourl(jSONArray.getJSONObject(i).getJSONArray("video_versions").getJSONObject(0).getString(str13));

                }

                iUpdate = i4;
                double longValue = ((double) (Long.valueOf(System.currentTimeMillis() / 1000).longValue() - Long.valueOf(Long.parseLong(str4)).longValue())) / 3600.0d;
                String.format("%.2f", Double.valueOf(longValue));
                String.format("%.0f", Double.valueOf(longValue));
                igtvFragment = this;
                if (igtvFragment.igtv_data != null) {
                    igtvFragment.igtv_data.add(story_setter);
                    i++;
                    str5 = str11;
                } else {
                    return;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            if (getActivity() != null) {
            }
        }


        if (getActivity() != null) {
            int finalIUpdate = iUpdate;

            Log.e("finalIUpdate--)", "" + finalIUpdate);
            getActivity().runOnUiThread(new Runnable() {
                @SuppressLint("WrongConstant")
                public void run() {
                    if (responseString != null && igtv_data != null) {
                        if (igtvshowerRecyclerviewAdapter != null && recyclerView.getVisibility() == 0) {
//
                            igtvshowerRecyclerviewAdapter.notifyDataSetChanged();
                        }

                        try {
                        } catch (Exception unused) {
                        } catch (Throwable th) {
                            throw th;
                        }
                    }
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public boolean isNetworkAvailable() {
        if (getActivity() == null) {
            return false;
        }
        @SuppressLint("WrongConstant") NetworkInfo activeNetworkInfo = ((ConnectivityManager) getActivity().getSystemService("connectivity")).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        if (i != 1 || iArr.length <= 0) {
            if (i != 2 || iArr[0] != 0) {
                if (i != 3 || iArr[0] != 0) {
                    getActivity().finish();
                }
            }
        } else if (iArr[0] == 0) {
            getActivity();
            if (this.permissionmenu) {
                downloadAllMedia();
            } else {
                downloadMultipleStory();
            }
        }
    }

    public void writeToFile(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.DIRECTORY_DCIM);
        sb.append("/testingFile/");
        File externalStoragePublicDirectory = Environment.getExternalStoragePublicDirectory(sb.toString());
        if (!externalStoragePublicDirectory.exists()) {
            externalStoragePublicDirectory.mkdirs();
        }
        File file = new File(externalStoragePublicDirectory, "newigTv.txt");
        try {
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            outputStreamWriter.append(str);
            outputStreamWriter.close();
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("File write failed: ");
            sb2.append(e.toString());
            Log.e("Exception", sb2.toString());
        }
    }

    /* access modifiers changed from: private */
    @SuppressLint("WrongConstant")
    public void downloadMultipleStory() {
        if (showtickigtv) {
//            this.rl_download.setVisibility(8);
            showtickigtv = false;
            this.igtvshowerRecyclerviewAdapter.notifyDataSetChanged();
            for (int i = 0; i < this.igtv_data.size(); i++) {
                this.igtv_data.get(i).setCheckmultiple(false);
            }
        }
        this.list.clear();
        startDownloadMulti(0);

    }

    private void startDownloadMulti(int pos) {
        posOfDownload = pos;


        Log.e("Status--)", "" + multiselect_list.get(pos).getvideourl() + "--)" + multiselect_list.get(pos).getimageurl());

        String url = multiselect_list.get(pos).getvideourl();

        if (url.matches(" ") || url == null || url.isEmpty()) {
            new DownloadMultiImage(getContext()).execute(multiselect_list.get(pos).getimageurl());
        } else {
            new DownloadMultiVideo(getContext()).execute(multiselect_list.get(pos).getvideourl());
        }


    }

    private class DownloadMultiImage extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadMultiImage(Context context) {
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

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();

                String strFileName = System.currentTimeMillis() + Constant.IMAGE_EXTENTION;


                String ext = strFileName.substring(strFileName.indexOf(".") + 1);
                File repostfile;
                File file = new File(getContext().getExternalFilesDir(getContext().getResources().getString(R.string.app_name)).toString());

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
                Log.e("Done--)", "Compl--)" + dnlUrl);
                File file1 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), getResources().getString(R.string.app_name));
                if (!file1.exists()) {
                    file1.mkdirs();
                }
                File repostfile = new File(file1, new File(dnlUrl).getName());

                moveFile(dnlUrl, repostfile.getAbsolutePath());
                savePath = repostfile.getAbsolutePath();
                try {
                    if (VERSION.SDK_INT >= 19) {
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

                if (multiselect_list.size() > 0) {


                    posOfDownload++;

                    if (posOfDownload < multiselect_list.size()) {

                        startDownloadMulti(posOfDownload);
                    }

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

    private class DownloadMultiVideo extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadMultiVideo(Context context) {
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

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }


                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();

                String strFileName = System.currentTimeMillis() + VIDEO_EXTENTION;


                String ext = strFileName.substring(strFileName.indexOf(".") + 1);
                File repostfile;
                File file = new File(getContext().getExternalFilesDir(getContext().getResources().getString(R.string.app_name)).toString());

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
                    if (VERSION.SDK_INT >= 19) {
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
                Log.e("Done--)", "Compl--)" + dnlUrl);

                if (multiselect_list.size() > 0) {
                    posOfDownload++;
                    if (posOfDownload < multiselect_list.size()) {

                        startDownloadMulti(posOfDownload);
                    }
                    ib_im_download.setEnabled(true);

                }
            }


        }
    }

    public void setPr(int progress) {
        if (progress == 100) {
            progressBar.setProgress(progress);
            txtProgress.setText("Completed");
            ib_im_download.setEnabled(true);
//            Snackbar.make(coordinatorLayout, "Download Completed!", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
        } else {
            progressBar.setProgress(progress);
            ib_im_download.setEnabled(false);
            txtProgress.setText("Downloaded.." + progress + "%");
        }
    }

    public long getDurationDialog(File file) {
        long timeInMillisec = 0;
        try {

            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(file.getPath());

            String duration = retriever
                    .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

            timeInMillisec = Long.parseLong(duration);

            return timeInMillisec;
        } catch (Exception e) {

        }
        return timeInMillisec;

    }

    private void scanFile(String path) {
        MediaScannerConnection.scanFile(getContext(),
                new String[]{path}, null,
                (path1, uri) -> Log.e("TAG--)", "Finished scanning " + path1));
    }

    private String getDate(long j) {
        Calendar instance = Calendar.getInstance(Locale.ENGLISH);
        instance.setTimeInMillis(j * 1000);
        int i = Calendar.getInstance().get(1);
        int parseInt = Integer.parseInt(DateFormat.format("yyyy", instance).toString());
        PrintStream printStream = System.out;
        StringBuilder sb = new StringBuilder();
        sb.append("postYear : currentYear : ");
        sb.append(parseInt);
        sb.append(":");
        sb.append(i);
        printStream.println(sb.toString());
        double longValue = (double) (Long.valueOf(System.currentTimeMillis() / 1000).longValue() - Long.valueOf(j).longValue());
        double d = longValue / 3600.0d;
        String str = "%.0f";
        String format = String.format(str, Double.valueOf(d));
        if (d <= 24.0d) {
            String str2 = " ";
            if (d > 2.0d) {
                if (getActivity() != null) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append(format);
                    sb2.append(str2);
                    sb2.append(getResources().getString(R.string.hours_ago));
                    return sb2.toString();
                }
            } else if (d <= 1.0d) {
                double d2 = longValue / 60.0d;
                String format2 = String.format(str, Double.valueOf(d2));
                if (d2 > 2.0d) {
                    if (getActivity() != null) {
                        StringBuilder sb3 = new StringBuilder();
                        sb3.append(format2);
                        sb3.append(str2);
                        sb3.append(getResources().getString(R.string.minutes_ago));
                        return sb3.toString();
                    }
                } else if (getActivity() != null) {
                    return getResources().getString(R.string.few_moments_ago);
                }
            } else if (getActivity() != null) {
                StringBuilder sb4 = new StringBuilder();
                sb4.append(format);
                sb4.append(str2);
                sb4.append(getResources().getString(R.string.hour_ago));
                return sb4.toString();
            }
            return null;
        } else if (i == parseInt) {
            return DateFormat.format("MMM dd", instance).toString();
        } else {
            return DateFormat.format("MMM dd yyyy", instance).toString();
        }
    }

    @SuppressLint("WrongConstant")
    public boolean onBackPressedtgtv() {
        if (showtickigtv) {
            this.rl_download.setVisibility(8);
            showtickigtv = false;
            this.igtvshowerRecyclerviewAdapter.notifyDataSetChanged();
            ArrayList<Story_setter> arrayList = this.multiselect_list;
            if (arrayList != null) {
                arrayList.clear();
            }
            ArrayList<Long> arrayList2 = this.list;
            if (arrayList2 != null) {
                arrayList2.clear();
                this.counter = 0;
            }
            for (int i = 0; i < this.igtv_data.size(); i++) {
                this.igtv_data.get(i).setCheckmultiple(false);
            }
            return true;
        }
        getActivity().finish();
        return false;
    }

    public void setMenuVisibility(boolean z) {
        super.setMenuVisibility(z);

    }

    public boolean isFilepath(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStorageDirectory().getPath());
        sb.append("/All Status Downloader");
        this.alldownloadPath = sb.toString();
        StringBuilder sb2 = new StringBuilder();
        sb2.append(str);
        String str2 = ".mp4";
        sb2.append(str2);
        String sb3 = sb2.toString();
        StringBuilder sb4 = new StringBuilder();
        sb4.append(str);
        String str3 = ".jpg";
        sb4.append(str3);
        String sb5 = sb4.toString();
        StringBuilder sb6 = new StringBuilder();
        sb6.append(this.alldownloadPath);
        String str4 = "/";
        sb6.append(str4);
        sb6.append(sb3);
        File file = new File(sb6.toString());
        StringBuilder sb7 = new StringBuilder();
        sb7.append(this.alldownloadPath);
        sb7.append(str4);
        sb7.append(sb5);
        File file2 = new File(sb7.toString());
        StringBuilder sb8 = new StringBuilder();
        sb8.append(str);
        sb8.append(ShowAllData.USER_NAME);
        String sb9 = sb8.toString();
        StringBuilder sb10 = new StringBuilder();
        sb10.append(sb9);
        sb10.append(str2);
        String sb11 = sb10.toString();
        StringBuilder sb12 = new StringBuilder();
        sb12.append(sb9);
        sb12.append(str3);
        String sb13 = sb12.toString();
        StringBuilder sb14 = new StringBuilder();
        sb14.append(this.alldownloadPath);
        sb14.append(str4);
        sb14.append(sb11);
        File file3 = new File(sb14.toString());
        StringBuilder sb15 = new StringBuilder();
        sb15.append(this.alldownloadPath);
        sb15.append(str4);
        sb15.append(sb13);
        File file4 = new File(sb15.toString());
        StringBuilder sb16 = new StringBuilder();
        sb16.append(ShowAllData.USER_NAME);
        sb16.append(str);
        String sb17 = sb16.toString();
        StringBuilder sb18 = new StringBuilder();
        sb18.append(sb17);
        sb18.append(str2);
        String sb19 = sb18.toString();
        StringBuilder sb20 = new StringBuilder();
        sb20.append(sb17);
        sb20.append(str3);
        String sb21 = sb20.toString();
        StringBuilder sb22 = new StringBuilder();
        sb22.append(this.alldownloadPath);
        sb22.append(str4);
        sb22.append(sb19);
        File file5 = new File(sb22.toString());
        StringBuilder sb23 = new StringBuilder();
        sb23.append(this.alldownloadPath);
        sb23.append(str4);
        sb23.append(sb21);
        return file.exists() || file2.exists() || file3.exists() || file4.exists() || file5.exists() || new File(sb23.toString()).exists();
    }
}
