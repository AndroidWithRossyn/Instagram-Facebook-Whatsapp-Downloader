package com.allmy.allstatusdownloader.Adapter;

import android.app.Activity;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.allmy.allstatusdownloader.Others.FileListClickInterface;
import com.allmy.allstatusdownloader.R;


import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder> {

    Activity mActivity;
    ArrayList<Uri> fileArrayList;
    FileListClickInterface itemClick;
    public ImageAdapter(Activity mContext, ArrayList<Uri> fileArrayList, FileListClickInterface itemClick) {
        this.mActivity=mContext;
        this.fileArrayList=fileArrayList;
        this.itemClick=itemClick;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_images,parent,false);
       return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Uri uri=fileArrayList.get(position);

        if (Build.VERSION.SDK_INT>Build.VERSION_CODES.Q){
            Glide.with(mActivity)
                    .load(uri)
                    .into(holder.ivImage);
        }else {
            Glide.with(mActivity)
                    .load(uri)
                    .into(holder.ivImage);
        }
        holder.cardMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClick.getImagePosition(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return fileArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        CardView cardMain;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            cardMain=itemView.findViewById(R.id.cardMain);
            ivImage=itemView.findViewById(R.id.ivImage);
        }
    }
}
