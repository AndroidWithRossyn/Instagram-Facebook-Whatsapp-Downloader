package com.allmy.allstatusdownloader.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.allmy.allstatusdownloader.Activity.SavedFeed;
import com.allmy.allstatusdownloader.Auth.AQuery;
import com.allmy.allstatusdownloader.Model.SavedFeed_setter;
import com.allmy.allstatusdownloader.Others.Utils;
import com.allmy.allstatusdownloader.R;

import java.io.File;
import java.util.ArrayList;

public class SavedFeedAdapter extends RecyclerView.Adapter<ViewHolder> {
    AQuery aQuery;
    Context mcontext;
    ArrayList<SavedFeed_setter> savedfeed_data;
    String user_FULLNAME;
    public static final int ITEM_AD = 11;
    public static final int ITEM_IMAGE = 22;

    public static class VideoViewHolder extends ViewHolder {
        ImageView iv_grid_check;
        RelativeLayout rlgrid_layer;
        RelativeLayout rlgrid_layernew;
        TextView taken_at;
        ImageView thumbnail;
        ImageView video_icon, lottieView;
        View viewBgImage, viewDateBg, checkedBg;

        public VideoViewHolder(View view) {
            super(view);
            thumbnail = view.findViewById(R.id.iv_grid_media);
            video_icon = view.findViewById(R.id.iv_grid_play);
            taken_at = view.findViewById(R.id.taken_atTv);
            iv_grid_check = view.findViewById(R.id.iv_grid_check);
            rlgrid_layer = view.findViewById(R.id.rlgrid_layer);
            rlgrid_layernew = view.findViewById(R.id.rlgrid_layernew);
            viewBgImage = view.findViewById(R.id.viewBgImage);
            viewDateBg = view.findViewById(R.id.viewDateBg);
            checkedBg = view.findViewById(R.id.checkedBg);
            lottieView = view.findViewById(R.id.lottieView);
        }
    }

    public class AdHolder extends ViewHolder {
        public AdHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public int getItemViewType(int i) {
        return savedfeed_data.get(i).getvideourl() != "" ? ITEM_IMAGE : ITEM_AD;
    }

    public SavedFeedAdapter(ArrayList<SavedFeed_setter> arrayList, SavedFeed savedFeed, AQuery aQuery2, String str) {
        this.savedfeed_data = arrayList;
        this.aQuery = aQuery2;
        this.user_FULLNAME = str;
        this.mcontext = savedFeed;
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

        try {
            int viewType = getItemViewType(i);
            switch (viewType) {
                case ITEM_IMAGE:
                    VideoViewHolder myViewHolder = (VideoViewHolder) holder;

                    if (i <= savedfeed_data.size() - 1) {
                        savedfeed_data.get(i).getMedia_id();
                        savedfeed_data.get(i).getUserName();
                        String thumbnail_url = savedfeed_data.get(i).getThumbnail_url();
                        String thumbnail_url_video = savedfeed_data.get(i).getvideourl();
                        String taken_at = savedfeed_data.get(i).getTaken_at();
                        boolean isCheckmultiple = savedfeed_data.get(i).isCheckmultiple();
                        String type = savedfeed_data.get(i).getType();

                        Log.e("thumbnail_url--)", "" + thumbnail_url);

                        if (type.equalsIgnoreCase("video")) {


                            if (thumbnail_url_video != null && !thumbnail_url_video.equalsIgnoreCase("")) {
                                Glide.with(mcontext)
                                        .load(thumbnail_url_video)
                                        .centerCrop()
                                        .listener(new RequestListener<Drawable>() {
                                            @Override
                                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                Log.e("Resouse--)", "ReadyNot");
                                                myViewHolder.lottieView.setVisibility(View.VISIBLE);
                                                return false;
                                            }

                                            @Override
                                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                Log.e("Resouse--)", "Ready");
//                                                myViewHolder.lottieView.setVisibility(View.GONE);
                                                return false;
                                            }
                                        })
                                        .into(myViewHolder.thumbnail);
                                if (!Utils.isEmpty(taken_at)) {
                                    myViewHolder.taken_at.setText(taken_at);
                                    myViewHolder.taken_at.setVisibility(View.VISIBLE);
                                    myViewHolder.viewDateBg.setVisibility(View.VISIBLE);
                                } else {
                                    myViewHolder.taken_at.setVisibility(View.GONE);
                                    myViewHolder.viewDateBg.setVisibility(View.GONE);
                                }
                            }

                        } else {

                            if (thumbnail_url != null && !thumbnail_url.equalsIgnoreCase("")) {

                                Glide.with(mcontext)
                                        .load(thumbnail_url)
                                        .centerCrop()
                                        .listener(new RequestListener<Drawable>() {
                                            @Override
                                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                Log.e("Resouse1--)", "ReadyNot");
                                                myViewHolder.lottieView.setVisibility(View.VISIBLE);
                                                return false;
                                            }

                                            @Override
                                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                Log.e("Resouse1--)", "Ready");
                                                return false;
                                            }
                                        })
                                        .into(myViewHolder.thumbnail);
                                if (!Utils.isEmpty(taken_at)) {
                                    myViewHolder.taken_at.setText(taken_at);
                                    myViewHolder.taken_at.setVisibility(View.VISIBLE);
                                    myViewHolder.viewDateBg.setVisibility(View.VISIBLE);
                                } else {
                                    myViewHolder.taken_at.setVisibility(View.GONE);
                                    myViewHolder.viewDateBg.setVisibility(View.GONE);
                                }
                            }
                        }

                        if (type.equalsIgnoreCase("video")) {
                            myViewHolder.video_icon.setVisibility(View.VISIBLE);
                            myViewHolder.viewBgImage.setVisibility(View.VISIBLE);
                            myViewHolder.video_icon.setImageResource(R.drawable.ic_video);
                        } else if (type.equalsIgnoreCase("album")) {
                            myViewHolder.video_icon.setVisibility(View.VISIBLE);
                            myViewHolder.viewBgImage.setVisibility(View.VISIBLE);
                            myViewHolder.video_icon.setImageResource(R.drawable.ic_image);
                        } else {
                            myViewHolder.video_icon.setVisibility(View.GONE);
                            myViewHolder.viewBgImage.setVisibility(View.GONE);
                        }
                        if (isCheckmultiple) {
                            myViewHolder.rlgrid_layer.setVisibility(View.VISIBLE);
                            myViewHolder.iv_grid_check.setVisibility(View.VISIBLE);
                            myViewHolder.checkedBg.setVisibility(View.VISIBLE);
//                            myViewHolder.iv_grid_check.setImageResource(R.drawable.correct);
                        } else {
                            myViewHolder.rlgrid_layer.setVisibility(View.INVISIBLE);
                            myViewHolder.iv_grid_check.setVisibility(View.INVISIBLE);
                            myViewHolder.checkedBg.setVisibility(View.INVISIBLE);
                        }
                        if (SavedFeed.savedfeedhowtick) {
                            myViewHolder.rlgrid_layernew.setVisibility(View.VISIBLE);
                        } else {
                            myViewHolder.rlgrid_layernew.setVisibility(View.INVISIBLE);
                            myViewHolder.rlgrid_layer.setVisibility(View.INVISIBLE);
                            myViewHolder.iv_grid_check.setVisibility(View.INVISIBLE);
                        }
                    }

                    break;
                case ITEM_AD:
                    break;
            }
        } catch (Exception e) {

        }
    }


    public int getItemCount() {
        return savedfeed_data.size();
    }

    public boolean isNetworkAvailable() {
        @SuppressLint("WrongConstant") NetworkInfo activeNetworkInfo = ((ConnectivityManager) mcontext.getSystemService("connectivity")).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public boolean isFilepath(String str, String str2) {
        String sb2 = Environment.getExternalStorageDirectory().getPath() +
                "/All Status Downloader";
        String str3 = ".mp4";
        String sb4 = str +
                str3;
        String str4 = ".jpg";
        String sb6 = str +
                str4;
        String str5 = "/";
        String sb7 = sb2 +
                str5 +
                sb4;
        File file = new File(sb7);
        String sb8 = sb2 +
                str5 +
                sb6;
        File file2 = new File(sb8);
        String sb10 = str +
                str2;
        String sb12 = sb10 +
                str3;
        String sb14 = sb10 +
                str4;
        String sb15 = sb2 +
                str5 +
                sb12;
        File file3 = new File(sb15);
        String sb16 = sb2 +
                str5 +
                sb14;
        File file4 = new File(sb16);
        String sb18 = str2 +
                str;
        String sb20 = sb18 +
                str3;
        String sb22 = sb18 +
                str4;
        String sb23 = sb2 +
                str5 +
                sb20;
        File file5 = new File(sb23);
        String sb24 = sb2 +
                str5 +
                sb22;
        return file.exists() || file2.exists() || file3.exists() || file4.exists() || file5.exists() || new File(sb24).exists();
    }
}
