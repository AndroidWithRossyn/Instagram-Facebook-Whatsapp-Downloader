package com.allmy.allstatusdownloader.Activity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.allmy.allstatusdownloader.Adapter.AlubumAdapter;
import com.allmy.allstatusdownloader.Auth.AQuery;
import com.allmy.allstatusdownloader.Model.Album_setter;
import com.allmy.allstatusdownloader.Model.AppInterface;
import com.allmy.allstatusdownloader.Others.AllMultiDownloadManager;
import com.allmy.allstatusdownloader.Others.GridSpacingItemDecoration;
import com.allmy.allstatusdownloader.Others.RecyclerItemClickListener;
import com.allmy.allstatusdownloader.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

public class AlbumActivity extends AppCompatActivity {

    static final /* synthetic */ boolean $assertionsDisabled = false;
    public static boolean albumregister = false;
    public static boolean highshowtickalbum = false;
    private String USER_ID;
    SharedPreferences accountInfoPref;
    AlubumAdapter albumAdapter;
    ArrayList<Album_setter> album_data;
    private String alldownloadPath;
    AQuery aq;
    private String caption_text;
    private String code_HdUrl;
    SharedPreferences cookiePref;
    List<Cookie> cookies;
    int counter = 0;
    String csrfTocken;
    SharedPreferences currentUser;
    String download_url;
    int file_ptef;
    private String full_name;
    boolean isMultiSelect = false;
    private ImageView imgBack;
    ArrayList<Long> list = new ArrayList<>();
    SharedPreferences loginPref;
    private String media_Title;
    private String media_id;
    ArrayList<Album_setter> multiselect_list = new ArrayList<>();
    private String name;
    private CharSequence namenote;
    RelativeLayout no_storyfound;
    boolean permissionmenu = false;
    public boolean popupCheck = false;
    private String profile_url;
    RecyclerView recyclerView;
    private long refid = 10;
    RelativeLayout rl_download;
    private String taken_at;
    Toolbar tool_bar;
    private TextView txt_toolbar;
    private RelativeLayout upload_progress;
    CardView ib_im_download;
    ProgressBar progressBar;
    TextView txtProgress, txtDownload;
    LinearLayout linearDownload;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        requestQueue = Volley.newRequestQueue(AlbumActivity.this);
        requestQueue.getCache().clear();
        getIntentData();
        getViewIds();

        file_ptef = getSharedPreferences("DOWNLOAD_FILE_NAME_PREF", 0).getInt("file_value", 0);

        getLoginCookies();
    }

    public void onPause() {
        super.onPause();
    }

    public void onResume() {
        super.onResume();
    }

    public void onDestroy() {
        super.onDestroy();
    }

    @SuppressLint("WrongConstant")
    private void getViewIds() {
        requestBackButtonFocus();

        txt_toolbar = findViewById(R.id.txt_toolbar);
        imgBack = findViewById(R.id.imgBack);

        if (full_name != null && !full_name.isEmpty()) {
            txt_toolbar.setText(full_name);
        } else {
            txt_toolbar.setText("My Saved Posts");
        }

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        upload_progress = findViewById(R.id.upload_progress);
        no_storyfound = (RelativeLayout) findViewById(R.id.no_storyfound);
        ib_im_download = findViewById(R.id.ib_im_download);
        progressBar = findViewById(R.id.progressBar);
        txtProgress = findViewById(R.id.txtProgress);
        linearDownload = findViewById(R.id.linearDownload);
        txtDownload = findViewById(R.id.txtDownload);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        rl_download = (RelativeLayout) findViewById(R.id.rl_download);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, 3, false));
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);
        aq = new AQuery(this);

        rl_download.setOnClickListener(view -> {
            if (multiselect_list == null || multiselect_list.size() <= 0) {
                AlbumActivity albumActivity = AlbumActivity.this;
                if (albumActivity != null) {
                    Toast.makeText(albumActivity, albumActivity.getResources().getString(R.string.select_media), Toast.LENGTH_SHORT).show();
                    return;
                }
                return;
            }
            isMultiSelect = false;
            if (Build.VERSION.SDK_INT > 22) {
                String str = "android.permission.WRITE_EXTERNAL_STORAGE";
                if (checkSelfPermission(str) != 0) {
                    shouldShowRequestPermissionRationale(str);
                    requestPermissions(new String[]{str}, 1);
                    return;
                }
                downloadMultipleStory();
                return;
            }
            downloadMultipleStory();
        });
    }

    private void requestBackButtonFocus() {
        Toolbar toolbar = tool_bar;
        if (toolbar != null) {
            int childCount = toolbar.getChildCount();
            for (int i = 0; i < childCount; i++) {
                if (tool_bar.getChildAt(i) instanceof ImageButton) {
                    tool_bar.getChildAt(i).requestFocus();
                    return;
                }
            }
        }
    }

    private void getIntentData() {
        media_Title = getIntent().getStringExtra("media_Title");
        media_id = getIntent().getStringExtra("media_id");
        name = getIntent().getStringExtra("user_name");
        full_name = getIntent().getStringExtra("full_name");
        USER_ID = getIntent().getStringExtra("USER_ID");
        code_HdUrl = getIntent().getStringExtra("code_HdUrl");
        taken_at = getIntent().getStringExtra("taken_at");
        profile_url = getIntent().getStringExtra("user_image_url");
        caption_text = getIntent().getStringExtra("caption");
        PrintStream printStream = System.out;
        String str = ":::::::::";
        String sb = "parameters : " + media_id + str + name + str + USER_ID;
        printStream.println(sb);
    }

    private void getLoginCookies() {
        String str = "CURRENT_USER";
        currentUser = getSharedPreferences(str, 0);
        String string = currentUser.getString(str, null);
        StringBuilder sb = new StringBuilder();
        sb.append("LOGIN_");
        sb.append(string.toLowerCase(Locale.getDefault()).trim());
        loginPref = getSharedPreferences(sb.toString(), 0);
        StringBuilder sb2 = new StringBuilder();
        sb2.append("COOKIE_PREF_");
        sb2.append(string.toLowerCase(Locale.getDefault()).trim());
        cookiePref = getSharedPreferences(sb2.toString(), 0);
        StringBuilder sb3 = new StringBuilder();
        sb3.append("ACCOUNT_PREF_");
        sb3.append(string.toLowerCase(Locale.getDefault()).trim());
        accountInfoPref = getSharedPreferences(sb3.toString(), 0);
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
        fetchAlbum();
    }

    public void fetchAlbum() {
        String uuid = UUID.randomUUID().toString();
        StringBuilder sb = new StringBuilder();
        sb.append("user_");
        sb.append(USER_ID);
        sb.toString();
        OkHttpClient build = new OkHttpClient.Builder().cookieJar(new CookieJar() {
            public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
            }

            public List<Cookie> loadForRequest(HttpUrl httpUrl) {
                return cookies;
            }
        }).build();
        String str = "_csrftoken";
        String str2 = "media_id";
        StringBuilder sb2 = new StringBuilder();
        sb2.append("https://www.instagram.com/p/");
        sb2.append(code_HdUrl);
        sb2.append("/?__a=1");
        String sb3 = sb2.toString();
        PrintStream printStream = System.out;
        StringBuilder sb4 = new StringBuilder();
        sb4.append("url_Feed :");
        sb4.append(sb3);

        Log.e("sb3--)", "" + sb3 + "--)" + sb4);
        printStream.println(sb4.toString());
        JsonObjectRequest request1 = new JsonObjectRequest(Request.Method.POST, sb3, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    parseResponce("null", response);
                    Log.e("----LOGIN---)", "" + response);
                } catch (Exception e) {
                    Log.e("LoginEx1--)", "" + e.getMessage());
                    e.printStackTrace();
                }
            }
        }, Throwable::printStackTrace);
        request1.setShouldCache(false);
        requestQueue.add(request1);
    }

    public void parseResponce(String str, JSONObject jsonObject) {
        String str2;
        String str4 = "node";
        String str5 = "edges";
        String str6 = "edge_sidecar_to_children";
        String str7 = "shortcode_media";
        String str8 = "graphql";
        album_data = new ArrayList<>();
        PrintStream printStream = System.out;
        String sb = "response fullversion = :" + str;
        printStream.println(sb);
        if (str != null) {
            try {
                if (!isFinishing()) {
                    JSONObject jSONObject = new JSONObject(str);
                    if (jSONObject.has(str8)) {
                        JSONObject jSONObject2 = jSONObject.getJSONObject(str8);
                        if (jSONObject2.has(str7)) {
                            JSONObject jSONObject3 = jSONObject2.getJSONObject(str7);
                            jSONObject3.getString("edge_media_to_caption");
                            if (jSONObject3.has(str6)) {
                                JSONObject jSONObject4 = jSONObject3.getJSONObject(str6);
                                if (jSONObject4.has(str5)) {
                                    JSONArray jSONArray = jSONObject4.getJSONArray(str5);
                                    PrintStream printStream2 = System.out;
                                    StringBuilder sb2 = new StringBuilder();
                                    sb2.append("edges : ");
                                    sb2.append(jSONArray.length());
                                    sb2.append(":::::");
                                    sb2.append(jSONArray);
                                    printStream2.println(sb2.toString());
                                    for (int i = 0; i < jSONArray.length(); i++) {
                                        JSONObject jSONObject5 = jSONArray.getJSONObject(i);
                                        Album_setter album_setter = new Album_setter();
                                        if (jSONObject5.has(str4)) {
                                            long parseLong = Long.parseLong(taken_at);
                                            PrintStream printStream3 = System.out;
                                            StringBuilder sb3 = new StringBuilder();
                                            sb3.append("dateString : ");
                                            sb3.append(getDate(parseLong));
                                            printStream3.println(sb3.toString());
                                            String date = getDate(parseLong);
                                            JSONObject jSONObject6 = jSONObject5.getJSONObject(str4);
                                            String string = jSONObject6.getString("id");
                                            if (file_ptef == 0) {
                                                str2 = string;
                                            } else if (file_ptef == 1) {
                                                StringBuilder sb4 = new StringBuilder();
                                                sb4.append(name);
                                                sb4.append(string);
                                                str2 = sb4.toString();
                                            } else {
                                                StringBuilder sb5 = new StringBuilder();
                                                sb5.append(string);
                                                sb5.append(name);
                                                str2 = sb5.toString();
                                            }
                                            String str9 = " ";
                                            String str10 = "config_height";
                                            String str11 = "config_width";
                                            String str12 = "src";
                                            String str13 = "display_resources";
                                            if (jSONObject6.getString("__typename").equalsIgnoreCase("GraphVideo")) {
                                                if (jSONObject6.has(str13)) {
                                                    JSONObject jSONObject7 = jSONObject6.getJSONArray(str13).getJSONObject(0);
                                                    String string2 = jSONObject7.getString(str12);
                                                    String string3 = jSONObject6.getString("video_url");
                                                    String string4 = jSONObject7.getString(str11);
                                                    String string5 = jSONObject7.getString(str10);
                                                    album_setter.setimageurl(str9);
                                                    album_setter.setvideourl(string3);
                                                    album_setter.setThumbnail_url(string2);
                                                    album_setter.setCode_HdUrl(code_HdUrl);
                                                    album_setter.setOriginal_height(string5);
                                                    album_setter.setOriginal_width(string4);
                                                    album_setter.setMedia_title(str2);
                                                    album_setter.setMedia_id(string);
                                                    album_setter.setUserName(name);
                                                    album_setter.setProfile_picture(profile_url);
                                                    album_setter.setTaken_at(date);
                                                    album_setter.setCaption_text(caption_text);
                                                }
                                            } else if (jSONObject6.has(str13)) {
                                                JSONArray jSONArray2 = jSONObject6.getJSONArray(str13);
                                                JSONObject jSONObject8 = jSONArray2.getJSONObject(jSONArray2.length() - 1);
                                                String string6 = jSONArray2.getJSONObject(0).getString(str12);
                                                String string7 = jSONObject8.getString(str11);
                                                String string8 = jSONObject8.getString(str10);
                                                String str14 = null;
                                                if (jSONObject8.has(str12)) {
                                                    str14 = jSONObject8.getString(str12);
                                                }
                                                album_setter.setimageurl(str14);
                                                album_setter.setvideourl(str9);
                                                album_setter.setThumbnail_url(string6);
                                                album_setter.setCode_HdUrl(code_HdUrl);
                                                album_setter.setOriginal_height(string8);
                                                album_setter.setOriginal_width(string7);
                                                album_setter.setMedia_title(str2);
                                                album_setter.setMedia_id(string);
                                                album_setter.setUserName(name);
                                                album_setter.setProfile_picture(profile_url);
                                                album_setter.setTaken_at(date);
                                                album_setter.setCaption_text(caption_text);
                                            }
                                        }
                                        album_data.add(album_setter);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        runOnUiThread(this::showGridstory);
    }

    @SuppressLint("WrongConstant")
    public void showGridstory() {
        if (album_data.size() < 1) {
            upload_progress.setVisibility(4);
            no_storyfound.setVisibility(View.VISIBLE);
        }
        upload_progress.setVisibility(4);
        popupCheck = true;
        albumAdapter = new AlubumAdapter(album_data, this, aq, full_name);
        recyclerView.setAdapter(albumAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            public void onItemClick(View view, int i) {
                if (isMultiSelect) {
                    multi_select(i);
                }
            }

            public void onItemLongClick(View view, int i) {
                Log.e("isMultiSelect--)", "" + isMultiSelect);

            }
        }));
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
        double longValue = (double) (System.currentTimeMillis() / 1000 - j);
        double d = longValue / 3600.0d;
        String str = "%.0f";
        String format = String.format(str, new Object[]{Double.valueOf(d)});
        if (d <= 23.0d) {
            String str2 = " ";
            if (d > 2.0d) {
                return format + str2 + getResources().getString(R.string.hours_ago);
            } else if (d > 1.0d) {
                return format + str2 + getResources().getString(R.string.hour_ago);
            } else {
                double d2 = longValue / 60.0d;
                String format2 = String.format(str, new Object[]{Double.valueOf(d2)});
                if (d2 <= 2.0d) {
                    return getResources().getString(R.string.few_moments_ago);
                }
                return format2 + str2 + getResources().getString(R.string.minutes_ago);
            }
        } else if (i == parseInt) {
            return DateFormat.format("MMM dd", instance).toString();
        } else {
            return DateFormat.format("MMM dd yyyy", instance).toString();
        }
    }

    private void setForceShowIcon(PopupMenu popupMenu) {
        Field[] declaredFields;
        try {
            for (Field field : popupMenu.getClass().getDeclaredFields()) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object obj = field.get(popupMenu);
                    Class.forName(obj.getClass().getName()).getMethod("setForceShowIcon", new Class[]{Boolean.TYPE}).invoke(obj, new Object[]{Boolean.valueOf(true)});
                    return;
                }
            }
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    @SuppressLint("WrongConstant")
    public void downloadAllMedia() {
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStorageDirectory().getPath());
        sb.append("/All Status Downloader");
        alldownloadPath = sb.toString();
        multiselect_list = new ArrayList<>();
        for (int i = 0; i < album_data.size(); i++) {
            String media_id2 = ((Album_setter) album_data.get(i)).getMedia_id();
            PrintStream printStream = System.out;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("media_id  : ");
            sb2.append(media_id2);
            printStream.println(sb2.toString());
            if (!isFilepath(media_id2)) {
                multiselect_list.add(album_data.get(i));
            }
        }
        ArrayList<Album_setter> arrayList = multiselect_list;
        if (arrayList == null || arrayList.size() <= 0) {
            Toast.makeText(this, getResources().getString(R.string.select_media), 0).show();
        } else {
            downloadMultipleStory();
        }
    }

    public void multi_select(int i) {
        ((Album_setter) album_data.get(i)).getMedia_id();
        if (multiselect_list.contains(album_data.get(i))) {
            multiselect_list.remove(album_data.get(i));
            ((Album_setter) album_data.get(i)).setCheckmultiple(false);
        } else {
            multiselect_list.add(album_data.get(i));
            ((Album_setter) album_data.get(i)).setCheckmultiple(true);
        }
        refreshAdapter(i);
    }

    private void refreshAdapter(int i) {
        AlubumAdapter alubumAdapter = albumAdapter;
        if (alubumAdapter != null) {
            alubumAdapter.notifyItemChanged(i);
        }
    }

    @SuppressLint("WrongConstant")
    public void downloadMultipleStory() {
        if (highshowtickalbum) {
            rl_download.setVisibility(8);
            highshowtickalbum = false;
            albumAdapter.notifyDataSetChanged();
            for (int i = 0; i < album_data.size(); i++) {
                ((Album_setter) album_data.get(i)).setCheckmultiple(false);
            }
        }
        list.clear();
        downloadVideoAndImageAll(multiselect_list, 0);
    }

    private void downloadVideoAndImageAll(ArrayList<Album_setter> arrayList, int i) {
        if (i < arrayList.size()) {
            if (((Album_setter) arrayList.get(i)).getvideourl().equalsIgnoreCase(" ")) {
                fullVersionUrlNew(arrayList, i);
            } else {
                String str = ((Album_setter) arrayList.get(i)).getvideourl();
                if (str != null) {
                    String media_title = ((Album_setter) arrayList.get(i)).getMedia_title();
                    new AllMultiDownloadManager(AlbumActivity.this, str, "", media_title + ".mp4", new AppInterface.OnDownloadStarted() {
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

    private void fullVersionUrlNew(ArrayList<Album_setter> arrayList, int i) {
        String str = ((Album_setter) arrayList.get(i)).getimageurl();
        String media_title = ((Album_setter) arrayList.get(i)).getMedia_title();
        download_url = str;
        downloadMediaAllnew(download_url, media_title);
        downloadVideoAndImageAll(arrayList, i + 1);
        PrintStream printStream = System.out;
        StringBuilder sb = new StringBuilder();
        sb.append("img_Url : ");
        sb.append(str);
        printStream.println(sb.toString());
    }

    private void downloadMediaAllnew(String str, String str2) {
        new AllMultiDownloadManager(AlbumActivity.this, str, "", str2 + ".jpg", new AppInterface.OnDownloadStarted() {
            @Override
            public void onDownloadStarted(long requestId) {
                refid = requestId;
                list.add(Long.valueOf(refid));
            }
        });
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
        sb8.append(name);
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
        sb16.append(name);
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

    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (i != 1 || iArr.length <= 0) {
            if (i != 2 || iArr[0] != 0) {
                if (i != 3 || iArr[0] != 0) {
                    finish();
                }
            }
        } else if (iArr[0] != 0) {
        } else {
            if (permissionmenu) {
                downloadAllMedia();
            } else {
                downloadMultipleStory();
            }
        }
    }

    @SuppressLint("WrongConstant")
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            if (highshowtickalbum) {
                rl_download.setVisibility(8);
                highshowtickalbum = false;
                AlubumAdapter alubumAdapter = albumAdapter;
                if (alubumAdapter != null) {
                    alubumAdapter.notifyDataSetChanged();
                }
                ArrayList<Album_setter> arrayList = multiselect_list;
                if (arrayList != null) {
                    arrayList.clear();
                }
                ArrayList<Long> arrayList2 = list;
                if (arrayList2 != null) {
                    arrayList2.clear();
                    counter = 0;
                }
                for (int i = 0; i < album_data.size(); i++) {
                    ((Album_setter) album_data.get(i)).setCheckmultiple(false);
                }
            } else {
                albumregister = true;
                finish();
            }
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @SuppressLint("WrongConstant")
    public void onBackPressed() {
        if (highshowtickalbum) {
            rl_download.setVisibility(8);
            highshowtickalbum = false;
            AlubumAdapter alubumAdapter = albumAdapter;
            if (alubumAdapter != null) {
                alubumAdapter.notifyDataSetChanged();
            }
            ArrayList<Album_setter> arrayList = multiselect_list;
            if (arrayList != null) {
                arrayList.clear();
            }
            ArrayList<Long> arrayList2 = list;
            if (arrayList2 != null) {
                arrayList2.clear();
                counter = 0;
            }
            for (int i = 0; i < album_data.size(); i++) {
                ((Album_setter) album_data.get(i)).setCheckmultiple(false);
            }
            return;
        }
        albumregister = true;
        finish();
    }
}