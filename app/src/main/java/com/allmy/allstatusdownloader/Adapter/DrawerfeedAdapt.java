package com.allmy.allstatusdownloader.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.bumptech.glide.Glide;
import com.allmy.allstatusdownloader.Activity.ActivitywithDrawer;
import com.allmy.allstatusdownloader.Activity.ShowAllData;
import com.allmy.allstatusdownloader.Model.DrawerParseSetter;
import com.allmy.allstatusdownloader.Model.FavPrefSetter;
import com.allmy.allstatusdownloader.Others.FavSharedPreference;
import com.allmy.allstatusdownloader.R;

import java.io.PrintStream;
import java.util.ArrayList;

public class DrawerfeedAdapt extends Adapter<ViewHolder> {
    private final String USER_PREF_BASE_FLAG = "user";
    Activity activity;
    ArrayList<String> checFavlist = new ArrayList<>();
    ArrayList<DrawerParseSetter> favarray_users;
    int fountPos = -1;
    boolean isFound = false;
    Context mcontext;
    FavSharedPreference shrdprefernces = new FavSharedPreference();
    public static final int ITEM_AD = 11;
    public static final int ITEM_IMAGE = 22;

    public class VideoViewHolder extends ViewHolder {
        ImageView iv_go;
        RelativeLayout ll_go;
        RelativeLayout profileImageContainer;
        ImageView profile_pic;
        TextView tv_stick;
        TextView tv_time;
        TextView tv_username;
        TextView user_name;

        public VideoViewHolder(View view) {
            super(view);
            profile_pic = view.findViewById(R.id.profileImage);
            user_name = view.findViewById(R.id.title);
            tv_time = view.findViewById(R.id.tv_time);
            tv_username = view.findViewById(R.id.tv_username);
            iv_go = view.findViewById(R.id.iv_go);
            ll_go = view.findViewById(R.id.ll_go);
            profileImageContainer = view.findViewById(R.id.profileImageContainer);
        }
    }

    public DrawerfeedAdapt(Activity activity, ArrayList<DrawerParseSetter> arrayList, ActivitywithDrawer activitywithDrawer) {
        this.activity = activity;
        this.favarray_users = new ArrayList<>(arrayList);
        this.mcontext = activitywithDrawer;
    }

    public class AdHolder extends ViewHolder {
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

        return favarray_users.get(i).getimageurl() != "" ? ITEM_IMAGE : ITEM_AD;

    }

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        switch (viewType) {
            case ITEM_IMAGE:
                View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.viewitemnew, viewGroup, false);
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
                    VideoViewHolder videoViewHolder = (VideoViewHolder) holder;

                    Log.e("Layout--)", "Image--)" + favarray_users.get(i).getUserName());
                    if (i <= favarray_users.size() - 1) {
                        DrawerParseSetter drawerParseSetter = favarray_users.get(i);
                        String profile_picture = drawerParseSetter.getProfile_picture();
                        final String userID = drawerParseSetter.getUserID();
                        final String userName = drawerParseSetter.getUserName();
                        final String userFullName = drawerParseSetter.getUserFullName();


                        String latest_reel_media = drawerParseSetter.getLatest_reel_media();
                        final String profile_picture2 = drawerParseSetter.getProfile_picture();
                        Glide.with(mcontext).load(profile_picture).into(videoViewHolder.profile_pic);
                        videoViewHolder.tv_time.setText(latest_reel_media);
                        TextView textView = videoViewHolder.tv_username;
                        StringBuilder sb = new StringBuilder();
                        sb.append("@");
                        sb.append(userName);
                        textView.setText(sb.toString());
                        videoViewHolder.user_name.setText(userFullName);
                        ArrayList favorites = shrdprefernces.getFavorites(mcontext);
                        if (favorites != null && favorites.size() > 0) {
                            for (int i2 = 0; i2 < favorites.size(); i2++) {
                                checFavlist.add(((FavPrefSetter) favorites.get(i2)).getUserName());
                                if (checFavlist.contains(userName)) {
                                    videoViewHolder.iv_go.setImageResource(R.drawable.ic_star);
                                } else {
                                    videoViewHolder.iv_go.setImageResource(R.drawable.ic_star_not_fill);
                                }
                            }
                        }
                        videoViewHolder.ll_go.setOnClickListener(view -> {

                            ArrayList favorites1 = shrdprefernces.getFavorites(mcontext);


                            if (favorites1 != null && favorites1.size() > 0) {
                                int i1 = 0;
                                while (true) {
                                    if (i1 >= favorites1.size()) {
                                        break;
                                    }
                                    if (((FavPrefSetter) favorites1.get(i1)).getUserName() != null) {
                                        String userName1 = ((FavPrefSetter) favorites1.get(i1)).getUserName();
                                        String str = drawerParseSetter.getUserName();
                                        if (str != null && userName1.contains(str)) {
                                            DrawerfeedAdapt drawerfeedAdapt = DrawerfeedAdapt.this;
                                            drawerfeedAdapt.isFound = true;
                                            drawerfeedAdapt.fountPos = i1;
                                            break;
                                        }
                                    }
                                    i1++;
                                }
                            }
                            String str = "position : ";
                            if (favorites1 != null && favorites1.size() == 0) {
                                PrintStream printStream2 = System.out;
                                String sb2 = str + i;
                                printStream2.println(sb2);

                                Log.e("fav--)", "Add--) " + i);
                                DrawerParseSetter drawerParseSetter1 = favarray_users.get(i);
                                String profile_picture1 = drawerParseSetter1.getProfile_picture();
                                String userID1 = drawerParseSetter1.getUserID();
                                String userName1 = drawerParseSetter1.getUserName();
                                String userFullName1 = drawerParseSetter1.getUserFullName();
                                String latest_reel_media1 = drawerParseSetter1.getLatest_reel_media();
                                FavPrefSetter favPrefSetter = new FavPrefSetter();
                                favPrefSetter.setProfile_picture(profile_picture1);
                                favPrefSetter.setUserID(userID1);
                                favPrefSetter.setUserName(userName1);
                                favPrefSetter.setUserFullName(userFullName1);
                                favPrefSetter.setLatest_reel_media(latest_reel_media1);
                                shrdprefernces.addFavorite(mcontext, favPrefSetter);
                                videoViewHolder.iv_go.setBackgroundResource(R.drawable.ic_star);

                            } else if (isFound) {
                                isFound = false;
                                Log.e("fav--)", "Remove--) " + i);
                                PrintStream printStream3 = System.out;
                                StringBuilder sb3 = new StringBuilder();
                                sb3.append("position :isFound");
                                sb3.append(fountPos);
                                printStream3.println(sb3.toString());
                                shrdprefernces.removeFavorite(mcontext, (FavPrefSetter) favorites1.get(fountPos), fountPos);
                                videoViewHolder.iv_go.setBackgroundResource(R.drawable.ic_star_not_fill);

                            } else {

                                Log.e("fav--)", "Addd--) " + i);
                                PrintStream printStream4 = System.out;
                                StringBuilder sb4 = new StringBuilder();
                                sb4.append(str);
                                sb4.append(i);
                                printStream4.println(sb4.toString());
                                DrawerParseSetter drawerParseSetter2 = favarray_users.get(i);
                                String profile_picture21 = drawerParseSetter2.getProfile_picture();
                                String userID2 = drawerParseSetter2.getUserID();
                                String userName2 = drawerParseSetter2.getUserName();
                                String userFullName2 = drawerParseSetter2.getUserFullName();
                                String latest_reel_media2 = drawerParseSetter2.getLatest_reel_media();
                                FavPrefSetter favPrefSetter2 = new FavPrefSetter();
                                favPrefSetter2.setProfile_picture(profile_picture21);
                                favPrefSetter2.setUserID(userID2);
                                favPrefSetter2.setUserName(userName2);
                                favPrefSetter2.setUserFullName(userFullName2);
                                favPrefSetter2.setLatest_reel_media(latest_reel_media2);
                                shrdprefernces.addFavorite(mcontext, favPrefSetter2);
                                videoViewHolder.iv_go.setBackgroundResource(R.drawable.ic_star);

                            }

                            notifyDataSetChanged();
                            ((ActivitywithDrawer) activity).updateData();
                        });
                        View view = videoViewHolder.itemView;
                        final String str = userName;
                        view.setOnClickListener(view1 -> {
                            if (isNetworkAvailable()) {
                                Intent intent = new Intent(mcontext, ShowAllData.class);
                                intent.putExtra("position", i);
                                intent.putExtra("USER_ID", userID);
                                intent.putExtra("USER_NAME", str);
                                intent.putExtra("USER_FULLNAME", userFullName);
                                intent.putExtra("PROFILE_PIC", profile_picture2);
                                mcontext.startActivity(intent);
                                return;
                            }
                            Toast.makeText(mcontext, "Please check internet connection...", Toast.LENGTH_SHORT).show();
                        });
                        videoViewHolder.itemView.setOnLongClickListener(new OnLongClickListener() {
                            public boolean onLongClick(View view) {
                                final ArrayList favorites = shrdprefernces.getFavorites(mcontext);
                                if (favorites != null && favorites.size() > 0) {
                                    int i = 0;
                                    while (true) {
                                        if (i >= favorites.size()) {
                                            break;
                                        } else if (((FavPrefSetter) favorites.get(i)).getUserName().contains(userName)) {
                                            DrawerfeedAdapt drawerfeedAdapt = DrawerfeedAdapt.this;
                                            drawerfeedAdapt.isFound = true;
                                            drawerfeedAdapt.fountPos = i;
                                            PrintStream printStream = System.out;
                                            StringBuilder sb = new StringBuilder();
                                            sb.append("position :");
                                            sb.append(i);
                                            printStream.println(sb.toString());
                                            break;
                                        } else {
                                            i++;
                                        }
                                    }
                                }
                                if (videoViewHolder.profileImageContainer != null) {
                                    Animation loadAnimation = AnimationUtils.loadAnimation(mcontext, R.anim.anim_expand_in);
                                    videoViewHolder.profileImageContainer.startAnimation(loadAnimation);
                                    loadAnimation.setAnimationListener(new AnimationListener() {
                                        public void onAnimationRepeat(Animation animation) {
                                        }

                                        public void onAnimationStart(Animation animation) {
                                        }

                                        public void onAnimationEnd(Animation animation) {
                                        }
                                    });
                                }
                                return true;
                            }
                        });
                    }

                    break;

                case ITEM_AD:
                    AdHolder adHolder2 = (AdHolder) holder;
                    Log.e("Layout--)", "Ad");

                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Exc--)", "" + e.getMessage());
        }
    }

    public int getItemCount() {
        return favarray_users.size();
    }

    public boolean isNetworkAvailable() {
        @SuppressLint("WrongConstant") NetworkInfo activeNetworkInfo = ((ConnectivityManager) mcontext.getSystemService("connectivity")).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}