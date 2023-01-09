package com.allmy.allstatusdownloader.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.bumptech.glide.Glide;
import com.allmy.allstatusdownloader.Activity.ShowAllData;
import com.allmy.allstatusdownloader.Auth.AQuery;
import com.allmy.allstatusdownloader.Fragment.StoryFragment;
import com.allmy.allstatusdownloader.Model.Story_setter;
import com.allmy.allstatusdownloader.R;


import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;

public class StoryshowerRecyclerviewAdapter extends RecyclerView.Adapter<ViewHolder> {
    AQuery aQuery;
    Context mcontext;
    ArrayList<Story_setter> story_data;
    String user_FULLNAME;
    public static final int ITEM_AD = 11;
    public static final int ITEM_IMAGE = 22;
    ArrayList<Story_setter> dummy_story_data;
    Activity activity;

    public class VideoViewHolder extends ViewHolder {
        ImageView iv_grid_check;
        RelativeLayout rlgrid_layer;
        RelativeLayout rlgrid_layernew;
        TextView taken_at;
        ImageView thumbnail;
        ImageView video_icon;
        View viewBgImage, checkedBg;

        public VideoViewHolder(View view) {
            super(view);
            thumbnail = view.findViewById(R.id.iv_grid_media);
            video_icon = view.findViewById(R.id.iv_grid_play);
            iv_grid_check = view.findViewById(R.id.iv_grid_check);
            taken_at = view.findViewById(R.id.taken_atTv);
            rlgrid_layer = view.findViewById(R.id.rlgrid_layer);
            rlgrid_layernew = view.findViewById(R.id.rlgrid_layernew);
            viewBgImage = view.findViewById(R.id.viewBgImage);
            checkedBg = view.findViewById(R.id.checkedBg);
        }
    }

    public class AdHolder extends ViewHolder {
        FrameLayout frameLayout;
        TextView tvAdLoading;

        public AdHolder(@NonNull View itemView) {
            super(itemView);
            frameLayout = itemView.findViewById(R.id.fl_adplaceholder);
            tvAdLoading = itemView.findViewById(R.id.tvAdLoading);
        }
    }

    public int getItemViewType(int i) {

        return story_data.get(i).getCheckAd() != 0 ? ITEM_IMAGE : ITEM_AD;

    }

    public StoryshowerRecyclerviewAdapter(Activity activity, ArrayList<Story_setter> arrayList, ShowAllData showAllData, AQuery aQuery2, String str, ArrayList<Story_setter> dummy_story_data) {
        this.activity = activity;
        this.story_data = arrayList;
        this.aQuery = aQuery2;
        this.user_FULLNAME = str;
        this.mcontext = showAllData;
        this.dummy_story_data = dummy_story_data;
    }

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        switch (viewType) {
            case ITEM_IMAGE:
                View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.populargrid_row, viewGroup, false);
                inflate.setMinimumHeight(viewGroup.getMeasuredHeight() / 4);
                return new VideoViewHolder(inflate);
            case ITEM_AD:
                View inflate1 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_ad_layout, viewGroup, false);
                return new AdHolder(inflate1);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        int viewType = getItemViewType(i);
        switch (viewType) {
            case ITEM_IMAGE:

                Log.e("LayStory--)", "Data");
                VideoViewHolder myViewHolder = (VideoViewHolder) holder;


                if (i <= story_data.size()) {
                    ArrayList<Story_setter> arrayList2 = story_data;
                    String thimbnail_url = arrayList2.get(i).getThimbnail_url();
                    PrintStream printStream = System.out;
                    StringBuilder sb9 = new StringBuilder();
                    sb9.append("url : ");
                    sb9.append(thimbnail_url);
                    printStream.println(sb9.toString());
                    String taken_at = arrayList2.get(i).getTaken_at();

                    boolean isCheckmultiple = arrayList2.get(i).isCheckmultiple();
                    if (thimbnail_url != null && !thimbnail_url.equalsIgnoreCase("")) {
//                Picasso.get().load(thimbnail_url).fit().centerCrop().into(myViewHolder.thumbnail);
                        Glide.with(mcontext)
                                .load(thimbnail_url)
                                .centerCrop()
                                .into(myViewHolder.thumbnail);
                        myViewHolder.taken_at.setText(taken_at);
                    }


                    if (!(arrayList2.get(i).getvideourl().equalsIgnoreCase(" "))) {
                        myViewHolder.video_icon.setVisibility(View.VISIBLE);
                        myViewHolder.viewBgImage.setVisibility(View.VISIBLE);
                    } else {
                        myViewHolder.video_icon.setVisibility(View.GONE);
                        myViewHolder.viewBgImage.setVisibility(View.GONE);
                    }


                    if (isCheckmultiple) {
                        myViewHolder.iv_grid_check.setVisibility(View.VISIBLE);
                        myViewHolder.rlgrid_layer.setVisibility(View.VISIBLE);
                        myViewHolder.checkedBg.setVisibility(View.VISIBLE);
                        myViewHolder.iv_grid_check.setImageResource(R.drawable.correct);
                    } else {
                        myViewHolder.iv_grid_check.setVisibility(View.INVISIBLE);
                        myViewHolder.rlgrid_layer.setVisibility(View.INVISIBLE);
                        myViewHolder.checkedBg.setVisibility(View.INVISIBLE);
                    }
                    if (StoryFragment.showtickstory) {
                    } else {
                        myViewHolder.rlgrid_layer.setVisibility(View.INVISIBLE);
                        myViewHolder.iv_grid_check.setVisibility(View.INVISIBLE);
                    }
                    myViewHolder.iv_grid_check.setOnClickListener(view -> {
                    });
                    myViewHolder.rlgrid_layer.setOnClickListener(view -> {
                    });

                }
                break;
            case ITEM_AD:
                AdHolder adHolder2 = (AdHolder) holder;
                Log.e("LayStory--)", "AD");

                break;
        }
    }


    public int getItemCount() {
        return story_data.size();
    }

    public boolean isNetworkAvailable() {
        @SuppressLint("WrongConstant") NetworkInfo activeNetworkInfo = ((ConnectivityManager) mcontext.getSystemService("connectivity")).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public boolean isFilepath(String str, String str2) {
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStorageDirectory().getPath());
        sb.append("/All Status Downloader");
        String sb2 = sb.toString();
        StringBuilder sb3 = new StringBuilder();
        sb3.append(str);
        String str3 = ".mp4";
        sb3.append(str3);
        String sb4 = sb3.toString();
        StringBuilder sb5 = new StringBuilder();
        sb5.append(str);
        String str4 = ".jpg";
        sb5.append(str4);
        String sb6 = sb5.toString();
        StringBuilder sb7 = new StringBuilder();
        sb7.append(sb2);
        String str5 = "/";
        sb7.append(str5);
        sb7.append(sb4);
        File file = new File(sb7.toString());
        StringBuilder sb8 = new StringBuilder();
        sb8.append(sb2);
        sb8.append(str5);
        sb8.append(sb6);
        File file2 = new File(sb8.toString());
        StringBuilder sb9 = new StringBuilder();
        sb9.append(str);
        sb9.append(str2);
        String sb10 = sb9.toString();
        StringBuilder sb11 = new StringBuilder();
        sb11.append(sb10);
        sb11.append(str3);
        String sb12 = sb11.toString();
        StringBuilder sb13 = new StringBuilder();
        sb13.append(sb10);
        sb13.append(str4);
        String sb14 = sb13.toString();
        StringBuilder sb15 = new StringBuilder();
        sb15.append(sb2);
        sb15.append(str5);
        sb15.append(sb12);
        File file3 = new File(sb15.toString());
        StringBuilder sb16 = new StringBuilder();
        sb16.append(sb2);
        sb16.append(str5);
        sb16.append(sb14);
        File file4 = new File(sb16.toString());
        StringBuilder sb17 = new StringBuilder();
        sb17.append(str2);
        sb17.append(str);
        String sb18 = sb17.toString();
        StringBuilder sb19 = new StringBuilder();
        sb19.append(sb18);
        sb19.append(str3);
        String sb20 = sb19.toString();
        StringBuilder sb21 = new StringBuilder();
        sb21.append(sb18);
        sb21.append(str4);
        String sb22 = sb21.toString();
        StringBuilder sb23 = new StringBuilder();
        sb23.append(sb2);
        sb23.append(str5);
        sb23.append(sb20);
        File file5 = new File(sb23.toString());
        StringBuilder sb24 = new StringBuilder();
        sb24.append(sb2);
        sb24.append(str5);
        sb24.append(sb22);
        return file.exists() || file2.exists() || file3.exists() || file4.exists() || file5.exists() || new File(sb24.toString()).exists();
    }

}
