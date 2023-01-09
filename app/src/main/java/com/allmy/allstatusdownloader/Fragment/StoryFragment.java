package com.allmy.allstatusdownloader.Fragment;

import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;

import android.annotation.SuppressLint;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.allmy.allstatusdownloader.Activity.CreationActivity;
import com.allmy.allstatusdownloader.Activity.HeighLightStory_shower;
import com.allmy.allstatusdownloader.Activity.InstaHighlightActivity;
import com.allmy.allstatusdownloader.Activity.ShowAllData;
import com.allmy.allstatusdownloader.Adapter.HorizontalListView_adaptor;
import com.allmy.allstatusdownloader.Adapter.StoryshowerRecyclerviewAdapter;
import com.allmy.allstatusdownloader.Auth.AQuery;
import com.allmy.allstatusdownloader.Model.AppInterface;
import com.allmy.allstatusdownloader.Model.HeighLightSetter;
import com.allmy.allstatusdownloader.Model.IOnBackPressed;
import com.allmy.allstatusdownloader.Model.Story_setter;
import com.allmy.allstatusdownloader.Others.AllMultiDownloadManager;
import com.allmy.allstatusdownloader.Others.Constant;
import com.allmy.allstatusdownloader.Others.Constants;
import com.allmy.allstatusdownloader.Others.GridSpacingItemDecoration;
import com.allmy.allstatusdownloader.Others.HorizontalListView;
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
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class StoryFragment extends Fragment implements HorizontalListView.OnListItemClickListener, IOnBackPressed {
    public static boolean showtickstory = false;
    String USER_NAME;
    SharedPreferences accountInfoPref;
    private String alldownloadPath;
    AQuery aq;
    SharedPreferences cookiePref;
    List<Cookie> cookies;
    int counter = 0;
    String csrfTocken;
    SharedPreferences currentUser;
    String download_url;
    int file_ptef;
    ArrayList<HeighLightSetter> heighLightArrlist;
    private HorizontalListView horilistview;
    HorizontalListView_adaptor horizontalList_adaptor;
    private boolean isFragmentLoadedfeed = false;
    boolean isMultiSelect = false;
    ArrayList<Long> list = new ArrayList<>();
    int listsize;
    SharedPreferences loginPref;
    Menu menu;
    ArrayList<Story_setter> multiselect_list = new ArrayList<>();
    RelativeLayout no_storyfound;
    boolean permissionmenu = false;
    private final boolean popupCheck = false;
    int position;
    RecyclerView recyclerView;
    private long refid;
    String responseString;
    RelativeLayout rl_download;
    ArrayList<Story_setter> story_data;
    ArrayList<Story_setter> dummy_story_data;
    StoryshowerRecyclerviewAdapter storyshowerRecyclerviewAdapter;
    public RelativeLayout upload_progress;
    InterstitialAd mInterstitialAd;
    CardView ib_im_download;
    ProgressBar progressBar;
    TextView txtProgress, txtDownload, txt_toolbar;
    LinearLayout linearDownload;
    long durationFl;
    String dnlUrl,savePath;
    int posOfDownload = 0;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        position = getArguments().getInt("position");
        if (getActivity() != null) {
            file_ptef = getActivity().getSharedPreferences("DOWNLOAD_FILE_NAME_PREF", 0).getInt("file_value", 0);
            PrintStream printStream = System.out;
            StringBuilder sb = new StringBuilder();
            sb.append("file_ptef : ");
            sb.append(file_ptef);
            printStream.println(sb.toString());
        } else {
            file_ptef = 0;
        }
        return layoutInflater.inflate(R.layout.fragment_story, viewGroup, false);
    }

    @SuppressLint("WrongConstant")
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        upload_progress = view.findViewById(R.id.upload_progress);
        no_storyfound = view.findViewById(R.id.no_storyfound);
        horilistview = view.findViewById(R.id.horilistview);
        recyclerView = view.findViewById(R.id.recycler_view);


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

        ib_im_download = view.findViewById(R.id.ib_im_download);
        progressBar = view.findViewById(R.id.progressBar);
        txtProgress = view.findViewById(R.id.txtProgress);
        linearDownload = view.findViewById(R.id.linearDownload);
        txtDownload = view.findViewById(R.id.txtDownload);
        GridLayoutManager glm = new GridLayoutManager(getActivity(), 3);
        glm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            public int getSpanSize(int i) {
                switch (storyshowerRecyclerviewAdapter.getItemViewType(i)) {
                    case 22:
                        return 1;
                    case 11:
                        return 3;
                    default:
                        return 3;
                }
            }
        });

        recyclerView.setLayoutManager(glm);

        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, 3, false));
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);
        ViewCompat.setNestedScrollingEnabled(horilistview, false);
        aq = new AQuery(getActivity());
        rl_download = view.findViewById(R.id.rl_download);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener((view1, i, keyEvent) -> {
            PrintStream printStream = System.out;
            StringBuilder sb = new StringBuilder();
            String str = "onBackPressed showtickstory : ";
            sb.append(str);
            sb.append(StoryFragment.showtickstory);
            printStream.println(sb.toString());
            if (i == 4 && keyEvent.getAction() == 1) {
                PrintStream printStream2 = System.out;
                StringBuilder sb2 = new StringBuilder();
                sb2.append(str);
                sb2.append(StoryFragment.showtickstory);
                printStream2.println(sb2.toString());
                if (getActivity() == null || !StoryFragment.showtickstory) {
                    System.out.println("onBackPressed  else");
                } else {
                    rl_download.animate().alpha(0.0f).setDuration(200);
                    rl_download.setVisibility(8);
                    StoryFragment.showtickstory = false;
                    if (multiselect_list != null) {
                        multiselect_list.clear();
                    }
                    if (list != null) {
                        list.clear();
                        counter = 0;
                    }
                    if (story_data != null) {
                        for (int i2 = 0; i2 < story_data.size(); i2++) {
                            story_data.get(i2).setCheckmultiple(false);
                        }
                    }

                    if (storyshowerRecyclerviewAdapter != null) {
                        storyshowerRecyclerviewAdapter.notifyDataSetChanged();
                    }
                    return true;
                }
            }
            return false;
        });

        ib_im_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

                } else {

                    Log.e("Click--)", "" + progressBar.getProgress());
                    if (progressBar.getProgress() == 100) {
                        Intent intent = new Intent(getContext(), CreationActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                }


                Log.e("arr_downloadlists--)", "" + multiselect_list.size() + "--)" + progressBar.getProgress());


            }
        });
    }

    @SuppressLint("WrongConstant")
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        ((ShowAllData) getActivity()).setStoryOnBackPressedListener(this);
//        if (!(getActivity() == null || onComplete == null)) {
//            getActivity().registerReceiver(onComplete, new IntentFilter("android.intent.action.DOWNLOAD_COMPLETE"));
//            isFragmentLoadedfeed = true;
//        }
        if (isNetworkAvailable()) {
            getLoginCookies();
        } else if (getActivity() != null) {
            Toast.makeText(getActivity(), getResources().getString(R.string.internet_connection), 0).show();
        }
    }

    private void getLoginCookies() {
        if (getActivity() != null) {
            String str = "CURRENT_USER";
            currentUser = getActivity().getSharedPreferences(str, 0);
            String string = currentUser.getString(str, null);
            FragmentActivity activity = getActivity();
            StringBuilder sb = new StringBuilder();
            sb.append("LOGIN_");
            sb.append(string.toLowerCase(Locale.getDefault()).trim());
            loginPref = activity.getSharedPreferences(sb.toString(), 0);
            FragmentActivity activity2 = getActivity();
            StringBuilder sb2 = new StringBuilder();
            sb2.append("COOKIE_PREF_");
            sb2.append(string.toLowerCase(Locale.getDefault()).trim());
            cookiePref = activity2.getSharedPreferences(sb2.toString(), 0);
            FragmentActivity activity3 = getActivity();
            StringBuilder sb3 = new StringBuilder();
            sb3.append("ACCOUNT_PREF_");
            sb3.append(string.toLowerCase(Locale.getDefault()).trim());
            accountInfoPref = activity3.getSharedPreferences(sb3.toString(), 0);
            cookies = new ArrayList();
            int i = cookiePref.getInt("cookie_count", -1);
            for (int i2 = 0; i2 < i; i2++) {
                PrintStream printStream = System.out;
                StringBuilder sb4 = new StringBuilder();
                sb4.append("Cookies from pref : ");
                String str2 = "";
                sb4.append(cookiePref.getString(String.valueOf(i2), str2));
                printStream.println(sb4.toString());
                try {
                    Cookie parse = Cookie.parse(HttpUrl.get(new URL("https://i.instagram.com/")), cookiePref.getString(String.valueOf(i2), str2));
                    cookies.add(parse);
                    if (parse.toString().contains("csrftoken")) {
                        csrfTocken = parse.toString().split(";")[0].split("=")[1];
                        PrintStream printStream2 = System.out;
                        StringBuilder sb5 = new StringBuilder();
                        sb5.append("csrfTocken  :");
                        sb5.append(csrfTocken);
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
            getVIewInfo();
            fetchHeighLightStory();
        }
    }

    @SuppressLint("WrongConstant")
    private void getVIewInfo() {
        upload_progress.setVisibility(0);
        StringBuilder sb = new StringBuilder();
        sb.append("https://i.instagram.com/api/v1/feed/user/");
        sb.append(ShowAllData.USER_ID);
        sb.append("/reel_media/");
        Log.e("Story View Service >>", sb.toString());
        new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            public Response intercept(Chain chain) throws IOException {
                return chain.proceed(chain.request().newBuilder().header("User-Agent", Constants.USER_AGENT).header("Connection", "close").header("language", "en").header("Accept", "*/*").header("X-IG-Capabilities", "3QI=").header("Content-type", "application/x-www-form-urlencoded; charset=UTF-8").build());
            }
        }).cookieJar(new CookieJar() {
            public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
            }

            public List<Cookie> loadForRequest(HttpUrl httpUrl) {
                return cookies;
            }
        }).build().newCall(new Request.Builder().url(sb.toString()).build()).enqueue(new Callback() {
            public void onFailure(Call call, IOException iOException) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> upload_progress.setVisibility(4));
                }
            }

            public void onResponse(Call call, Response response) throws IOException {
                Callback r1;
                String r12;
                String str;
                String str2;
                String str3 = null;
                Callback r13 = null;
                JSONArray jSONArray = null;
                Callback r14 = this;
                String str4 = "user";
                String str5 = "%.0f";
                String str6 = "caption";
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            upload_progress.setVisibility(4);
                        }
                    });
                }
                responseString = response.body().string();
                PrintStream printStream = System.out;
                StringBuilder sb = new StringBuilder();
                sb.append("parsing class response = :");
                sb.append(responseString);
                printStream.println(sb.toString());
                try {
                    if (getActivity() != null) {
                        if (responseString != null && !getActivity().isFinishing()) {
                            story_data = new ArrayList<>();
                            JSONObject jSONObject = new JSONObject(responseString);
                            JSONArray jSONArray2 = jSONObject.getJSONArray("items");
                            int i = 0;
                            while (i < jSONArray2.length()) {
                                Story_setter story_setter = new Story_setter();
                                String string = jSONArray2.getJSONObject(i).getString("taken_at");
                                JSONObject jSONObject2 = jSONArray2.getJSONObject(i);
                                String str7 = "";
                                if (!jSONObject2.isNull(str6)) {
                                    str = jSONObject2.getJSONObject(str6).getString("text");
                                    PrintStream printStream2 = System.out;
                                    StringBuilder sb2 = new StringBuilder();
                                    sb2.append("text : ");
                                    sb2.append(str);
                                    printStream2.println(sb2.toString());
                                } else {
                                    str = str7;
                                }
                                String string2 = jSONArray2.getJSONObject(i).getString("code");
                                String string3 = jSONArray2.getJSONObject(i).getString("original_width");
                                String string4 = jSONArray2.getJSONObject(i).getString("original_height");
                                String replace = jSONArray2.getJSONObject(i).getString("id").replace("_", str7);
                                if (file_ptef == 0) {
                                    str2 = replace;
                                } else if (file_ptef == 1) {
                                    StringBuilder sb3 = new StringBuilder();
                                    sb3.append(USER_NAME);
                                    sb3.append(replace);
                                    str2 = sb3.toString();
                                } else {
                                    StringBuilder sb4 = new StringBuilder();
                                    sb4.append(replace);
                                    sb4.append(USER_NAME);
                                    str2 = sb4.toString();
                                }
                                PrintStream printStream3 = System.out;
                                String str8 = str6;
                                StringBuilder sb5 = new StringBuilder();
                                String str9 = str4;
                                sb5.append("code_HdUrl Story : ");
                                sb5.append(string2);
                                printStream3.println(sb5.toString());
                                boolean equalsIgnoreCase = jSONArray2.getJSONObject(i).getString("media_type").equalsIgnoreCase("1");
                                String str10 = "candidates";
                                String str11 = "image_versions2";
                                JSONObject jSONObject3 = jSONObject;
                                String str12 = "width";
                                String str13 = str5;
                                String str14 = "height";
                                String str15 = string;
                                String str16 = " ";
                                String r15 = "url";
                                //Log.e("JSONArray2 Length",jSONArray2.length() + "    Media Type >>> "+jSONArray2.getJSONObject(i).getString("media_type") +" Disk Lru >>> "+DiskLruCache.VERSION_1);
                                if (equalsIgnoreCase) {
                                    try {
                                        r12 = r15;
                                        JSONArray jSONArray3 = jSONArray2.getJSONObject(i).getJSONObject(str11).getJSONArray(str10);
                                        String string5 = jSONArray3.getJSONObject(0).getString(r15);
                                        JSONArray jSONArray4 = jSONArray2;
                                        if (jSONArray3.length() > 1) {
                                            story_setter.setThimbnail_url(jSONArray3.getJSONObject(1).getString(r15));
                                        } else {
                                            story_setter.setThimbnail_url(jSONArray3.getJSONObject(0).getString(r15));
                                        }
                                        story_setter.setimageurl(string5);


                                        story_setter.setvideourl(str16);


                                        String string6 = jSONArray3.getJSONObject(0).getString(str14);
                                        String string7 = jSONArray3.getJSONObject(0).getString(str12);
                                        PrintStream printStream4 = System.out;
                                        StringBuilder sb6 = new StringBuilder();
                                        str3 = str16;
                                        sb6.append("Height : width :: ");
                                        sb6.append(string6);
                                        sb6.append(" ::: ");
                                        sb6.append(string7);
                                        printStream4.println(sb6.toString());
                                        story_setter.setimageheight(jSONArray3.getJSONObject(0).getString(str14));
                                        story_setter.setimagewidth(jSONArray3.getJSONObject(0).getString(str12));
                                        story_setter.setCode_HdUrl(string2);
                                        story_setter.setOriginal_height(string4);
                                        story_setter.setOriginal_width(string3);
                                        story_setter.setCheckmultiple(false);
                                        story_setter.setMedia_title(str2);
                                        story_setter.setMedia_id(replace);
                                        story_setter.setCaption_text(str);
                                        story_setter.setCheckdownloaded(false);
                                        story_setter.setCheckAd(1);
                                        r13 = this;
                                        jSONArray = jSONArray4;
                                    } catch (JSONException e) {
                                        e = e;
                                        r1 = this;
                                        e.printStackTrace();
                                        r14 = r1;
                                        if (getActivity() != null) {
                                        }
                                    }
                                } else {
                                    jSONArray = jSONArray2;
                                    str3 = str16;
                                    JSONArray jSONArray5 = jSONArray.getJSONObject(i).getJSONObject(str11).getJSONArray(str10);
                                    String string8 = jSONArray5.getJSONObject(0).getString(r15);
                                    if (jSONArray5.length() > 1) {
                                        story_setter.setThimbnail_url(jSONArray5.getJSONObject(1).getString(r15));
                                    } else {
                                        story_setter.setThimbnail_url(jSONArray5.getJSONObject(0).getString(r15));
                                    }
                                    story_setter.setimageurl(string8);
                                    story_setter.setimageheight(jSONArray5.getJSONObject(0).getString(str14));
                                    story_setter.setimagewidth(jSONArray5.getJSONObject(0).getString(str12));
                                    story_setter.setCode_HdUrl(string2);
                                    story_setter.setOriginal_height(string4);
                                    story_setter.setOriginal_width(string3);
                                    PrintStream printStream5 = System.out;
                                    StringBuilder sb7 = new StringBuilder();
                                    sb7.append("story_response :");
                                    String r4 = r15;
//                                    String r16;
//                                    r12 = r16;
                                    sb7.append(responseString);
                                    printStream5.println(sb7.toString());

                                    Log.e("str166--)", "" + jSONArray.getJSONObject(i).getJSONArray("video_versions").getJSONObject(0).getString(r4));
                                    story_setter.setvideourl(jSONArray.getJSONObject(i).getJSONArray("video_versions").getJSONObject(0).getString(r4));
                                    story_setter.setCheckmultiple(false);
                                    story_setter.setMedia_title(str2);
                                    story_setter.setMedia_id(replace);
                                    story_setter.setCheckAd(1);
                                    story_setter.setCaption_text(str);
                                    story_setter.setCheckdownloaded(false);
//                                    r13 = r16;
                                }
                                double longValue = (double) (Long.valueOf(System.currentTimeMillis() / 1000).longValue() - Long.valueOf(Long.parseLong(str15)).longValue());
                                double d = longValue / 3600.0d;
                                String.format("%.2f", Double.valueOf(d));
                                String str17 = str13;
                                String format = String.format(str17, Double.valueOf(d));
                                String str18 = str9;
                                JSONObject jSONObject4 = jSONObject3;
                                story_setter.setUserName(jSONObject4.getJSONObject(str18).getString("username"));
                                story_setter.setProfile_picture(jSONObject4.getJSONObject(str18).getString("profile_pic_url"));
                                story_setter.setGetDate(str15);
                                //story_data.add(story_setter);

                                if (d <= 2.0d) {
                                    String str19 = str3;
                                    if (d <= 1.0d) {
                                        double d2 = longValue / 60.0d;
                                        String format2 = String.format(str17, Double.valueOf(d2));
                                        if (d2 > 2.0d) {
                                            if (getActivity() != null) {
                                                StringBuilder sb8 = new StringBuilder();
                                                sb8.append(format2);
                                                sb8.append(str19);
                                                sb8.append(getResources().getString(R.string.minutes_ago));
                                                story_setter.setTaken_at(sb8.toString());
                                            }
                                        } else if (getActivity() != null) {
                                            story_setter.setTaken_at(getResources().getString(R.string.few_moments_ago));
                                        }
                                        // story_data.add(story_setter);
                                        if (story_data == null) {
                                            story_data.add(story_setter);
                                            i++;
                                            jSONArray2 = jSONArray;
                                            str5 = str17;
                                            jSONObject = jSONObject4;
                                            str4 = str18;
                                            str6 = str8;
                                            r14 = r13;
                                        } else {
                                            // return;
                                        }
                                    } else if (getActivity() != null) {
                                        StringBuilder sb9 = new StringBuilder();
                                        sb9.append(format);
                                        sb9.append(str19);
                                        sb9.append(getResources().getString(R.string.hour_ago));
                                        story_setter.setTaken_at(sb9.toString());

                                    }
                                    story_data.add(story_setter);
                                } else if (getActivity() != null) {
                                    StringBuilder sb10 = new StringBuilder();
                                    sb10.append(format);
                                    sb10.append(str3);
                                    sb10.append(getResources().getString(R.string.hours_ago));
                                    story_setter.setTaken_at(sb10.toString());
                                    story_data.add(story_setter);
                                }
                                i++;
                                jSONArray2 = jSONArray;
                                str5 = str17;
                                jSONObject = jSONObject4;
                                str4 = str18;
                                str6 = str8;
                                r14 = r13;

                            }
                            r14 = r14;

                        } else if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    if (!isNetworkAvailable() && getActivity() != null) {
                                        Toast.makeText(getActivity(), getResources().getString(R.string.internet_connection), 0).show();
                                    }
                                }
                            });
                        }
                    }
                } catch (JSONException e2) {
//                    e = e2;
//                    r1 = r12;

                    Log.e("Exception--)", "" + e2.getMessage());
                    e2.printStackTrace();
//                    r14 = r1;
                    if (getActivity() != null) {
                    }
                }
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            if (responseString == null || story_data == null) {
                                upload_progress.setVisibility(4);
                                no_storyfound.setVisibility(0);
                                return;
                            }
                            showGridstory();
                            try {
                                upload_progress.setVisibility(4);
                            } catch (Exception unused) {
                            } catch (Throwable th) {
                                upload_progress.setVisibility(4);
                                throw th;
                            }
                            upload_progress.setVisibility(4);
                        }
                    });
                }
            }
        });
    }

    /* access modifiers changed from: private */
    @SuppressLint("WrongConstant")
    public void showGridstory() {
        if (!getActivity().isFinishing()) {
            ArrayList<Story_setter> arrayList = story_data;
            if (arrayList != null && arrayList.size() > 0) {


//                for (int i = 0; i < story_data.size(); i += ITEM_PER_AD) {
//                    story_data.add(i, new Story_setter(0));
//                }

                Collections.reverse(story_data);

                dummy_story_data = new ArrayList<>();
                dummy_story_data.clear();
                dummy_story_data.addAll(story_data);

                storyshowerRecyclerviewAdapter = new StoryshowerRecyclerviewAdapter(getActivity(), story_data, (ShowAllData) getActivity(), aq, ShowAllData.user_FULLNAME, dummy_story_data);
                recyclerView.setAdapter(storyshowerRecyclerviewAdapter);
                recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    public void onItemClick(View view, int i) {

                        Log.e("ismUltiSelect--)", "" + isMultiSelect);
                        if (isMultiSelect) {
                            multi_select(i);
                        } else {
                            if (isNetworkAvailable()) {
                                String str = "position";
                                String str2 = "story_data";
                                String str3 = "profileUrl";
                                String str4 = "imgUrl";
                                String str5 = "id";
                                String str6 = "title";
                                String str7 = "full_name";

                                Log.e("Str--)", "" + i + "--)" + dummy_story_data.size());
                                if (dummy_story_data.get((i)).getvideourl().equalsIgnoreCase(" ")) {
                                    Intent intent = new Intent(getContext(), InstaHighlightActivity.class);
                                    intent.putExtra(str7, ShowAllData.user_FULLNAME);
                                    intent.putExtra(str6, dummy_story_data.get((i)).getMedia_title());
                                    intent.putExtra(str5, dummy_story_data.get((i)).getMedia_id());
                                    intent.putExtra(str4, dummy_story_data.get((i)).getimageurl());
                                    intent.putExtra(str3, dummy_story_data.get((i)).getProfile_picture());
                                    intent.putParcelableArrayListExtra(str2, dummy_story_data);
                                    intent.putExtra(str, i);
                                    intent.setFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                                    startActivity(intent);
                                    return;
                                }
                                Intent intent2 = new Intent(getContext(), InstaHighlightActivity.class);
                                intent2.putExtra(str7, ShowAllData.user_FULLNAME);
                                intent2.putExtra(str6, dummy_story_data.get((i)).getMedia_title());
                                intent2.putExtra(str5, dummy_story_data.get((i)).getMedia_id());
                                intent2.putExtra(str4, dummy_story_data.get((i)).getimageurl());
                                intent2.putExtra(str3, dummy_story_data.get((i)).getProfile_picture());
                                intent2.putExtra("videoUrl", dummy_story_data.get((i)).getvideourl());
                                intent2.putParcelableArrayListExtra(str2, dummy_story_data);
                                intent2.putExtra(str, i);
                                intent2.setFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                                startActivity(intent2);
                                return;
                            }
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
                        StoryFragment.showtickstory = true;
                        storyshowerRecyclerviewAdapter.notifyDataSetChanged();
                    }
                }));
                return;
            }
        }
        ArrayList<Story_setter> arrayList2 = story_data;
        if (arrayList2 == null || arrayList2.size() >= 1 || getActivity().isFinishing()) {
            upload_progress.setVisibility(4);
            no_storyfound.setVisibility(0);
            return;
        }
        upload_progress.setVisibility(4);
        no_storyfound.setVisibility(0);
    }

    public void all_select(int i) {
        isMultiSelect = true;
        for (int i2 = 0; i2 < story_data.size(); i2++) {
            ArrayList<Story_setter> arrayList = multiselect_list;
            ArrayList<Story_setter> arrayList2 = story_data;
            arrayList.add(arrayList2.get((arrayList2.size() - 1) - i2));
            ArrayList<Story_setter> arrayList3 = story_data;
            arrayList3.get((arrayList3.size() - 1) - i2).setCheckmultiple(true);
        }
        storyshowerRecyclerviewAdapter.notifyDataSetChanged();
    }

    @SuppressLint("WrongConstant")
    public void downloadAllMedia() {

        ArrayList<Story_setter> arrayList5 = multiselect_list;
        if (arrayList5 != null && multiselect_list.size() > 0) {
            downloadMultipleStory();
        } else if (getActivity() != null) {
            Toast.makeText(getActivity(), getResources().getString(R.string.select_media), 0).show();
        }
    }

    public void multi_select(int i) {

        if (multiselect_list.contains(story_data.get(i))) {

            story_data.get(i).setCheckmultiple(false);
            multiselect_list.remove(story_data.get(i));
        } else {

            story_data.get(i).setCheckmultiple(true);
            multiselect_list.add(story_data.get(i));
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
        StoryshowerRecyclerviewAdapter storyshowerRecyclerviewAdapter2 = storyshowerRecyclerviewAdapter;
        if (storyshowerRecyclerviewAdapter2 != null) {
            storyshowerRecyclerviewAdapter2.notifyItemChanged(i);
        }
    }

    private void fetchHeighLightStory() {
        OkHttpClient build = new OkHttpClient.Builder().cookieJar(new CookieJar() {
            public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
            }

            public List<Cookie> loadForRequest(HttpUrl httpUrl) {
                return cookies;
            }
        }).build();
        Request.Builder builder = new Request.Builder();
        StringBuilder sb = new StringBuilder();
        sb.append("https://i.instagram.com/api/v1/highlights/");
        sb.append(ShowAllData.USER_ID);
        sb.append("/highlights_tray");
        build.newCall(builder.url(sb.toString()).addHeader("User-Agent", Constants.USER_AGENT).addHeader("Connection", "close").addHeader("Accept-Language", "en-US").addHeader("language", "en").addHeader("Accept", "*/*").addHeader("Content-type", "application/x-www-form-urlencoded; charset=UTF-8").build()).enqueue(new Callback() {
            public void onFailure(Call call, IOException iOException) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @SuppressLint("WrongConstant")
                        public void run() {
                            upload_progress.setVisibility(4);
                        }
                    });
                }
            }

            public void onResponse(Call call, Response response) throws IOException {
                if (!(getActivity() == null || response == null || getActivity().isFinishing())) {
                    heighLightArrlist = new ArrayList<>();
                    try {
                        String string = response.body().string();
                        PrintStream printStream = System.out;
                        StringBuilder sb = new StringBuilder();
                        sb.append("Height story_response-->");
                        sb.append(string);
                        printStream.println(sb.toString());
                        JSONArray jSONArray = new JSONObject(string).getJSONArray("tray");
                        for (int i = 0; i < jSONArray.length(); i++) {
                            JSONObject jSONObject = jSONArray.getJSONObject(i);
                            JSONObject jSONObject2 = jSONObject.getJSONObject("user");
                            String string2 = jSONObject.getJSONObject("cover_media").getJSONObject("cropped_image_version").getString("url");
                            String string3 = jSONArray.getJSONObject(i).getString("title");
                            jSONObject2.getString("username");
                            String string4 = jSONArray.getJSONObject(i).getString("latest_reel_media");
                            String string5 = jSONArray.getJSONObject(i).getString("id");
                            PrintStream printStream2 = System.out;
                            StringBuilder sb2 = new StringBuilder();
                            sb2.append("latest_reel_media :");
                            sb2.append(string4);
                            printStream2.println(sb2.toString());
                            HeighLightSetter heighLightSetter = new HeighLightSetter();
                            heighLightSetter.setHeighlight_cover(string2);
                            heighLightSetter.setUserTitle(string3);
                            heighLightSetter.setHeighlight_id(string5);
                            if (heighLightArrlist != null) {
                                heighLightArrlist.add(heighLightSetter);
                            }
                        }
                    } catch (JSONException e) {
                        PrintStream printStream3 = System.out;
                        StringBuilder sb3 = new StringBuilder();
                        sb3.append("JSONException :");
                        sb3.append(e.toString());
                        printStream3.println(sb3.toString());
                        e.printStackTrace();
                    }
                }
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            heighLightSetAdapter();
                        }
                    });
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public void heighLightSetAdapter() {
        if (getActivity() != null && !getActivity().isFinishing()) {
            ArrayList<HeighLightSetter> arrayList = heighLightArrlist;
            if (arrayList != null && arrayList.size() > 0) {

                Log.e("HeighlightSize--)", "" + heighLightArrlist.size());
                horizontalList_adaptor = new HorizontalListView_adaptor(heighLightArrlist, (ShowAllData) getActivity());
                horilistview.setAdapter(horizontalList_adaptor);
                horilistview.registerListItemClickListener(this);
            }
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
            if (permissionmenu) {
                downloadAllMedia();
            } else {
                downloadMultipleStory();
            }
        }
    }

    /* access modifiers changed from: private */
    @SuppressLint("WrongConstant")
    public void downloadMultipleStory() {
        if (showtickstory) {
//            rl_download.setVisibility(8);
            showtickstory = false;
            storyshowerRecyclerviewAdapter.notifyDataSetChanged();
            for (int i = 0; i < story_data.size(); i++) {
                story_data.get(i).setCheckmultiple(false);
            }
        }
        list.clear();

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
                File file1 = new File(getContext().getExternalFilesDir(getContext().getResources().getString(R.string.app_name)).toString());
                if (!file1.exists()) {
                    file1.mkdirs();
                }
                repostfile = new File(file1, strFileName);
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
//            progressDailog.showDailog();

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

                }
            }


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

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();

                String strFileName = System.currentTimeMillis() + Constant.VIDEO_EXTENTION;


                String ext = strFileName.substring(strFileName.indexOf(".") + 1);
                File repostfile;
                File file1 = new File(getContext().getExternalFilesDir(getContext().getResources().getString(R.string.app_name)).toString());
                if (!file1.exists()) {
                    file1.mkdirs();
                }
                repostfile = new File(file1, strFileName);
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
                    ib_im_download.setEnabled(true);

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

    public void setPr(int progress) {
        if (progress == 100) {
            ib_im_download.setEnabled(true);
            progressBar.setProgress(progress);
            txtProgress.setText("Completed");

//            Snackbar.make(coordinatorLayout, "Download Completed!", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
        } else {
            ib_im_download.setEnabled(false);
            progressBar.setProgress(progress);
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
    /* access modifiers changed from: private */
    public void downloadVideoAndImageAll(ArrayList<Story_setter> arrayList, int i) {
        if (i < arrayList.size()) {
            if (arrayList.get(i).getvideourl().equalsIgnoreCase(" ")) {
                fullVersionUrlNew(arrayList, i);
            } else {
                String str = arrayList.get(i).getvideourl();
                if (str != null) {
                    String media_title = arrayList.get(i).getMedia_title();
                    new AllMultiDownloadManager(getActivity(), str, "", media_title + Constant.VIDEO_EXTENTION, new AppInterface.OnDownloadStarted() {
                        @Override
                        public void onDownloadStarted(long requestId) {
                            refid = requestId;
                            list.add(Long.valueOf(refid));
                            downloadVideoAndImageAll(arrayList, i + 1);
                        }
                    });
                }
            }
        }
    }

    private void fullVersionUrlNew(ArrayList<Story_setter> arrayList, int i) {
        final String str = arrayList.get(i).getimageurl();
        String code_HdUrl = arrayList.get(i).getCode_HdUrl();
        if (str != null) {
            final String media_title = arrayList.get(i).getMedia_title();
            StringBuilder sb = new StringBuilder();
            sb.append("https://www.instagram.com/p/");
            sb.append(code_HdUrl);
            sb.append("/?__a=1");
            Call newCall = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
                public Response intercept(Chain chain) throws IOException {
                    return chain.proceed(chain.request().newBuilder().header("User-Agent", Constants.USER_AGENT).header("Connection", "close").header("language", "en").header("Accept", "*/*").header("X-IG-Capabilities", "3QI=").header("Content-type", "application/x-www-form-urlencoded; charset=UTF-8").build());
                }
            }).cookieJar(new CookieJar() {
                public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
                }

                public List<Cookie> loadForRequest(HttpUrl httpUrl) {
                    return cookies;
                }
            }).build().newCall(new Request.Builder().url(sb.toString()).build());
            final ArrayList<Story_setter> arrayList2 = arrayList;
            final int i2 = i;
            Callback r1 = new Callback() {
                public void onFailure(Call call, IOException iOException) {
                    final String message = iOException.getMessage();
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            PrintStream printStream = System.out;
                            StringBuilder sb1 = new StringBuilder();
                            sb1.append("onFailure : ");
                            sb1.append(message);
                            printStream.println(sb1.toString());
                            download_url = str;
                            if (download_url != null) {
                                downloadMediaAllnew(download_url, media_title);
                            }
                            downloadVideoAndImageAll(arrayList2, i2 + 1);
                        });
                    }
                }

                public void onResponse(Call call, Response response) throws IOException {

                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                        });
                    }
                    if (response.isSuccessful()) {
                        final String str = "display_resources";
                        String str2 = "shortcode_media";
                        String str3 = "graphql";
                        String str4 = response.body().string();
                        PrintStream printStream = System.out;
                        StringBuilder sb = new StringBuilder();
                        sb.append("response fullversion = :");
                        sb.append(str4);
                        printStream.println(sb.toString());
                        try {
                            if (!getActivity().isFinishing()) {
                                JSONObject jSONObject = new JSONObject(str4);
                                if (jSONObject.has(str3)) {
                                    JSONObject jSONObject2 = jSONObject.getJSONObject(str3);
                                    if (jSONObject2.has(str2)) {
                                        JSONObject jSONObject3 = jSONObject2.getJSONObject(str2);
                                        if (jSONObject3.has(str)) {
                                            JSONArray jSONArray = jSONObject3.getJSONArray(str);
                                            if (jSONArray.length() > 0) {
                                                JSONObject jSONObject4 = jSONArray.getJSONObject(jSONArray.length() - 1);
                                                download_url = jSONObject4.getString("src");
                                                PrintStream printStream2 = System.out;
                                                StringBuilder sb2 = new StringBuilder();
                                                sb2.append("download_url : ");
                                                sb2.append(download_url);
                                                printStream2.println(sb2.toString());
                                            } else {
                                                download_url = str;
                                            }
                                        } else {
                                            download_url = str;
                                        }
                                    } else {
                                        download_url = str;
                                    }
                                } else {
                                    download_url = str;
                                }
                                if (getActivity() != null) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        public void run() {
                                            downloadMediaAllnew(download_url, media_title);
                                            PrintStream printStream = System.out;
                                            StringBuilder sb = new StringBuilder();
                                            sb.append("download_url 1281 : ");
                                            sb.append(download_url);
                                            printStream.println(sb.toString());
                                            downloadVideoAndImageAll(arrayList2, i2 + 1);
                                        }
                                    });
                                }
                                PrintStream printStream3 = System.out;
                                StringBuilder sb3 = new StringBuilder();
                                sb3.append("display_url : ");
                                sb3.append(download_url);
                                printStream3.println(sb3.toString());
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
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                download_url = str;
                                downloadMediaAllnew(download_url, media_title);
                                PrintStream printStream = System.out;
                                StringBuilder sb = new StringBuilder();
                                sb.append("download_url 1307 : ");
                                sb.append(download_url);
                                printStream.println(sb.toString());
                                downloadVideoAndImageAll(arrayList2, i2 + 1);
                            }
                        });
                    }
                }
            };
            newCall.enqueue(r1);
        }
    }

    public void downloadMediaAllnew(String str, String str2) {
        Log.e("Url", str + " File Name >>> " + str2);
        new AllMultiDownloadManager(getActivity(), str, "", str2 + Constant.IMAGE_EXTENTION, new AppInterface.OnDownloadStarted() {
            @Override
            public void onDownloadStarted(long requestId) {
                refid = requestId;
                list.add(Long.valueOf(refid));
            }
        });
    }

    @SuppressLint("WrongConstant")
    public void onClick(View view, int i) {
        if (isNetworkAvailable()) {
            HeighLightSetter heighLightSetter = heighLightArrlist.get(i);
            String heighlight_id = heighLightSetter.getHeighlight_id();
            String heighlight_cover = heighLightSetter.getHeighlight_cover();
            String userTitle = heighLightSetter.getUserTitle();

            Log.e("Highlight_Id--)", "" + heighlight_id);
            Log.e("Highlight_profile--)", "" + heighlight_cover);
            Log.e("Highlight_userTitle--)", "" + userTitle);
            Log.e("Highlight_USER_ID--)", "" + ShowAllData.USER_ID);
            Log.e("Highlight_USER_Full--)", "" + ShowAllData.user_FULLNAME);
            Log.e("Highlight_position--)", "" + position);
            Log.e("Highlight_name--)", "" + ShowAllData.USER_NAME);


            Intent intent = new Intent(getActivity(), HeighLightStory_shower.class);
            intent.putExtra("highlight_Id", heighlight_id);
            intent.putExtra("hilight_profilecover", heighlight_cover);
            intent.putExtra("hilight_title", userTitle);
            intent.putExtra("USER_ID", ShowAllData.USER_ID);
            intent.putExtra("user_FULLNAME", ShowAllData.user_FULLNAME);
            intent.putExtra("position", i);
            intent.putExtra("USER_NAME", ShowAllData.USER_NAME);
            intent.setFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        } else if (getActivity() != null) {
            Toast.makeText(getActivity(), getResources().getString(R.string.internet_connection), 0).show();
        }
    }

    @SuppressLint("WrongConstant")
    public boolean onBackPressed() {
        if (showtickstory) {
            rl_download.animate().alpha(0.0f).setDuration(200);
            rl_download.setVisibility(8);
            showtickstory = false;
            ArrayList<Story_setter> arrayList = multiselect_list;
            if (arrayList != null) {
                arrayList.clear();
            }
            ArrayList<Long> arrayList2 = list;
            if (arrayList2 != null) {
                arrayList2.clear();
                counter = 0;
            }
            for (int i = 0; i < story_data.size(); i++) {
                story_data.get(i).setCheckmultiple(false);
            }
            StoryshowerRecyclerviewAdapter storyshowerRecyclerviewAdapter2 = storyshowerRecyclerviewAdapter;
            if (storyshowerRecyclerviewAdapter2 != null) {
                storyshowerRecyclerviewAdapter2.notifyDataSetChanged();
            }
            return true;
        }
        getActivity().finish();
        return false;
    }

    public void registterBReciver() {
//        if (getActivity() != null && onComplete != null) {
//            getActivity().registerReceiver(onComplete, new IntentFilter("android.intent.action.DOWNLOAD_COMPLETE"));
//            isFragmentLoadedfeed = true;
//        }
    }

    public void setUserVisibleHint(boolean z) {
        super.setUserVisibleHint(z);
    }


    public void onResume() {
        super.onResume();
    }

    public void writeToFile(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.DIRECTORY_DCIM);
        sb.append("/testingFile/");
        File externalStoragePublicDirectory = Environment.getExternalStoragePublicDirectory(sb.toString());
        if (!externalStoragePublicDirectory.exists()) {
            externalStoragePublicDirectory.mkdirs();
        }
        File file = new File(externalStoragePublicDirectory, "hilightstory.txt");
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

    public boolean isFilepath(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStorageDirectory().getPath());
        sb.append("/All Status Downloader");
        alldownloadPath = sb.toString();
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
        sb6.append(alldownloadPath);
        String str4 = "/";
        sb6.append(str4);
        sb6.append(sb3);
        File file = new File(sb6.toString());
        StringBuilder sb7 = new StringBuilder();
        sb7.append(alldownloadPath);
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
        sb14.append(alldownloadPath);
        sb14.append(str4);
        sb14.append(sb11);
        File file3 = new File(sb14.toString());
        StringBuilder sb15 = new StringBuilder();
        sb15.append(alldownloadPath);
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
        sb22.append(alldownloadPath);
        sb22.append(str4);
        sb22.append(sb19);
        File file5 = new File(sb22.toString());
        StringBuilder sb23 = new StringBuilder();
        sb23.append(alldownloadPath);
        sb23.append(str4);
        sb23.append(sb21);
        return file.exists() || file2.exists() || file3.exists() || file4.exists() || file5.exists() || new File(sb23.toString()).exists();
    }
}