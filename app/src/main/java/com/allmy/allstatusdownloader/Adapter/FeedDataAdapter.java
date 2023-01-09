package com.allmy.allstatusdownloader.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.RecyclerView;

import com.allmy.allstatusdownloader.Activity.ShowAllData;
import com.allmy.allstatusdownloader.Fragment.FeedFragment;
import com.allmy.allstatusdownloader.Model.Bean;
import com.allmy.allstatusdownloader.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FeedDataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<Bean> imagePath;
    BroadcastReceiver onComplete;
    ShowAllData showAllData;
    String user_FULLNAME;
    Activity activity;

    public static final int ITEM_AD = 11;
    public static final int ITEM_IMAGE = 22;

    public class VideoViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        ImageView iv_grid_check;
        ImageView play;
        RelativeLayout rlgrid_layernew;
        ImageView select;
        TextView taken_atTv;
        View viewBgImage, checkedBg;

        public VideoViewHolder(View view) {
            super(view);
            play = view.findViewById(R.id.iv_grid_play);
            select = view.findViewById(R.id.iv_grid_select);
            icon = view.findViewById(R.id.iv_grid_media);
            iv_grid_check = view.findViewById(R.id.iv_grid_check);
            taken_atTv = view.findViewById(R.id.taken_atTv);
            rlgrid_layernew = view.findViewById(R.id.rlgrid_layernew);
            viewBgImage = view.findViewById(R.id.viewBgImage);
            checkedBg = view.findViewById(R.id.checkedBg);
        }
    }

    public class AdHolder extends RecyclerView.ViewHolder {
        //    Native Ads
        FrameLayout frameLayout;
        TextView tvAdLoading;

        public AdHolder(@NonNull View itemView) {
            super(itemView);
            frameLayout = itemView.findViewById(R.id.fl_adplaceholder);
            tvAdLoading = itemView.findViewById(R.id.tvAdLoading);
        }
    }

    public int getItemViewType(int i) {
        return imagePath.get(i).getImageUrl() != "" ? ITEM_IMAGE : ITEM_AD;
    }

    public FeedDataAdapter(Activity activity, ShowAllData showAllData2, ArrayList<Bean> arrayList, String str) {
        this.activity = activity;
        this.showAllData = showAllData2;
        this.imagePath = arrayList;
        this.user_FULLNAME = str;
    }

    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        switch (viewType) {
            case ITEM_IMAGE:
                View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.populargrid_row_feed, viewGroup, false);
                inflate.setMinimumHeight(viewGroup.getMeasuredHeight() / 4);
                return new VideoViewHolder(inflate);
            case ITEM_AD:
                View inflate1 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_ad_layout, viewGroup, false);
                return new AdHolder(inflate1);
        }
        return null;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
        try {
            int viewType = getItemViewType(i);
            switch (viewType) {
                case ITEM_IMAGE:
                    VideoViewHolder viewHolder = (VideoViewHolder) holder;

                    (imagePath.get(i)).getMedia_id();
                    (imagePath.get(i)).getUserName();
                    viewHolder.taken_atTv.setVisibility(View.VISIBLE);
                    (imagePath.get(i)).getType();
                    String media_type = (imagePath.get(i)).getMedia_type();
                    if (media_type.equalsIgnoreCase(ExifInterface.GPS_MEASUREMENT_2D)) {
                        viewHolder.play.setVisibility(View.VISIBLE);
                        viewHolder.viewBgImage.setVisibility(View.VISIBLE);
                        viewHolder.play.setImageResource(R.drawable.ic_video);
                    } else if (media_type.equalsIgnoreCase("8")) {
                        viewHolder.play.setVisibility(View.VISIBLE);
                        viewHolder.viewBgImage.setVisibility(View.VISIBLE);
                        viewHolder.play.setImageResource(R.drawable.album);
                    } else {
                        viewHolder.play.setVisibility(View.GONE);
                        viewHolder.viewBgImage.setVisibility(View.GONE);
                    }
                    if ((imagePath.get(i)).isSelect()) {
                        viewHolder.select.setVisibility(View.VISIBLE);
                    } else {
                        viewHolder.select.setVisibility(View.GONE);
                    }
                    if ((imagePath.get(i)).isCheckmultiple()) {
                        viewHolder.iv_grid_check.setVisibility(View.VISIBLE);
                        viewHolder.checkedBg.setVisibility(View.VISIBLE);

                    } else {
                        viewHolder.iv_grid_check.setVisibility(View.GONE);
                        viewHolder.checkedBg.setVisibility(View.INVISIBLE);
                    }

                    viewHolder.iv_grid_check.setOnClickListener(view -> {
                    });
                    viewHolder.rlgrid_layernew.setOnClickListener(view -> {
                    });
                    if (FeedFragment.showtick) {
                        viewHolder.rlgrid_layernew.setVisibility(View.INVISIBLE);
                    } else {
                        viewHolder.rlgrid_layernew.setVisibility(View.INVISIBLE);
                        viewHolder.iv_grid_check.setVisibility(View.INVISIBLE);
                    }
                    viewHolder.taken_atTv.setText((imagePath.get(i)).getDate_of_post());
                    Picasso.get().load((imagePath.get(i)).getImageUrl()).fit().centerCrop().into(viewHolder.icon);
                    break;
                case ITEM_AD:
                    AdHolder adHolder2 = (AdHolder) holder;
                    Log.e("Layout--)", "Ad");

                    break;
            }
        } catch (Exception e) {
            Log.e("Exce--)", "" + e.getMessage());
        }
    }


    public int getItemCount() {
        return imagePath.size();
    }

    public boolean isNetworkAvailable() {
        @SuppressLint("WrongConstant") NetworkInfo activeNetworkInfo = ((ConnectivityManager) showAllData.getSystemService("connectivity")).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
