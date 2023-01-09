package com.allmy.allstatusdownloader.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.bumptech.glide.Glide;
import com.allmy.allstatusdownloader.Activity.FavUserActivity;
import com.allmy.allstatusdownloader.Activity.ShowAllData;
import com.allmy.allstatusdownloader.Model.FavStorySetter;
import com.allmy.allstatusdownloader.R;

import java.io.PrintStream;
import java.util.ArrayList;

public class FragFavRecyclerAdapt extends Adapter<FragFavRecyclerAdapt.MyViewHolder> {
    ArrayList<FavStorySetter> array_users;
    ArrayList<String> checFavlist = new ArrayList<>();
    private final boolean isRefresh;
    Context mcontext;
    private boolean networkAvailable;

    public class MyViewHolder extends ViewHolder {
        //LikeButton favouriteBtn;
        ImageView iv_go;
        RelativeLayout ll_go;
        RelativeLayout profileImageContainer;
        ImageView profile_pic;
        View tv_stick;
        TextView tv_time;
        TextView tv_username;
        TextView user_name;

        public MyViewHolder(View view) {
            super(view);
            this.profile_pic = view.findViewById(R.id.profileImage);
            this.user_name = view.findViewById(R.id.title);
            this.tv_time = view.findViewById(R.id.tv_time);
            this.tv_username = view.findViewById(R.id.tv_username);
            this.iv_go = view.findViewById(R.id.iv_go);
            this.ll_go = view.findViewById(R.id.ll_go);
            this.profileImageContainer = view.findViewById(R.id.profileImageContainer);
        }
    }

    public FragFavRecyclerAdapt(ArrayList<FavStorySetter> arrayList, FavUserActivity favUserActivity, boolean z) {
        this.array_users = arrayList;
        this.mcontext = favUserActivity;
        this.isRefresh = z;
    }

    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.viewitemnew, viewGroup, false));
    }

    @SuppressLint("WrongConstant")
    public void onBindViewHolder(MyViewHolder myViewHolder, int i) {
        if (i <= this.array_users.size() - 1) {
            FavStorySetter favStorySetter = this.array_users.get(i);
            String profile_picture = favStorySetter.getProfile_picture();
            final String id = favStorySetter.getID();
            final String userName = favStorySetter.getUserName();
            final String userFullName = favStorySetter.getUserFullName();
            String timeAgo = favStorySetter.getTimeAgo();
            final String profile_picture2 = favStorySetter.getProfile_picture();
            PrintStream printStream = System.out;
            StringBuilder sb = new StringBuilder();
            sb.append("FragFav_FULLNAME :");
            sb.append(userFullName);
            printStream.println(sb.toString());
            Glide.with(this.mcontext).load(profile_picture).into(myViewHolder.profile_pic);
            if (this.isRefresh) {
                myViewHolder.tv_time.setVisibility(8);
            } else {
                myViewHolder.tv_time.setText(timeAgo);
            }
            myViewHolder.user_name.setText(userFullName);
            TextView textView = myViewHolder.tv_username;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("@");
            sb2.append(userName);
            textView.setText(sb2.toString());
            myViewHolder.iv_go.setImageResource(R.drawable.ic_star);
            View view = myViewHolder.itemView;
            final int i2 = i;
            OnClickListener r2 = new OnClickListener() {
                public void onClick(View view) {
                    if (FragFavRecyclerAdapt.this.isNetworkAvailable()) {

                        //e("profile_picture2--)",""+profile_picture2);
                        Intent intent = new Intent(FragFavRecyclerAdapt.this.mcontext, ShowAllData.class);
                        intent.putExtra("position", i2);
                        intent.putExtra("USER_ID", id);
                        intent.putExtra("USER_NAME", userName);
                        intent.putExtra("USER_FULLNAME", userFullName);
                        intent.putExtra("PROFILE_PIC", profile_picture2);
                        FragFavRecyclerAdapt.this.mcontext.startActivity(intent);
                        return;
                    }
                    Toast.makeText(FragFavRecyclerAdapt.this.mcontext, "Please check internet connection...", Toast.LENGTH_SHORT).show();
                }
            };
            view.setOnClickListener(r2);
        }
    }

    public int getItemCount() {
        return this.array_users.size();
    }

    public boolean isNetworkAvailable() {
        @SuppressLint("WrongConstant") NetworkInfo activeNetworkInfo = ((ConnectivityManager) this.mcontext.getSystemService("connectivity")).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
