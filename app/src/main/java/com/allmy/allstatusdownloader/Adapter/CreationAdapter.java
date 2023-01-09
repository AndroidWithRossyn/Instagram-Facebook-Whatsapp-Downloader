package com.allmy.allstatusdownloader.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.allmy.allstatusdownloader.Model.VideoModel;
import com.allmy.allstatusdownloader.R;

import java.io.File;
import java.util.ArrayList;

public class CreationAdapter extends RecyclerView.Adapter<CreationAdapter.MyViewHolder> {

    Activity activity;
    ArrayList<VideoModel> strVideoList;

    public CreationAdapter(Activity activity, ArrayList<VideoModel> strVideoList) {

        this.activity = activity;
        this.strVideoList = strVideoList;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_creation, null));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {


        holder.txtTitle.setText(strVideoList.get(position).getVideoName());
        holder.txtSize.setText(strVideoList.get(position).getSize());

        if (new File(strVideoList.get(position).getVideoPath()).getName().endsWith(".mp4")) {
            Glide.with(activity)
                    .asBitmap()
                    .load(strVideoList.get(position).getThumbNail())
                    .into(holder.ivData);
        } else {
            Glide.with(activity)
                    .asBitmap()
                    .load(strVideoList.get(position).getVideoPath())
                    .into(holder.ivData);
        }
        holder.btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (new File(strVideoList.get(position).getVideoPath()).getName().endsWith(".mp4")) {
                        shareVideo(strVideoList.get(position));
                    } else {
                        shareImage(strVideoList.get(position));
                    }

                } catch (Exception e) {

                }
            }
        });

        holder.relPlay.setOnClickListener(view -> {
            try {

                if (new File(strVideoList.get(position).getVideoPath()).getName().endsWith(".mp4")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(strVideoList.get(position).getVideoPath()));
                    intent.setDataAndType(Uri.parse(strVideoList.get(position).getVideoPath()), "video/mp4");
                    activity.startActivity(intent);
                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(strVideoList.get(position).getVideoPath()));
                    intent.setDataAndType(Uri.parse(strVideoList.get(position).getVideoPath()), "image/*");
                    activity.startActivity(intent);
                }

            } catch (Exception e) {

            }
        });
    }

    public void shareVideo(VideoModel videoModel) {
        Uri uri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".fileprovider", new File(videoModel.getVideoPath()));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            final Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            final Uri contentUri = uri;
            scanIntent.setData(contentUri);
            activity.sendBroadcast(scanIntent);
        } else {
            final Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED, uri);
            activity.sendBroadcast(intent);
        }
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("video/*");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
        activity.startActivity(Intent.createChooser(sharingIntent, "Share Video"));
    }

    public void shareImage(VideoModel videoModel) {
        Uri uri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".fileprovider", new File(videoModel.getVideoPath()));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            final Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            final Uri contentUri = uri;
            scanIntent.setData(contentUri);
            activity.sendBroadcast(scanIntent);
        } else {
            final Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED, uri);
            activity.sendBroadcast(intent);
        }
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("image/*");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
        activity.startActivity(Intent.createChooser(sharingIntent, "Share Image"));
    }

    @Override
    public int getItemCount() {
        return strVideoList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView ivData, btnShare;
        TextView txtTitle, txtTime, txtSize;
        RelativeLayout relPlay;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ivData = itemView.findViewById(R.id.ivData);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtSize = itemView.findViewById(R.id.txtSize);
            btnShare = itemView.findViewById(R.id.btnShare);
            relPlay = itemView.findViewById(R.id.relPlay);


        }
    }
}
