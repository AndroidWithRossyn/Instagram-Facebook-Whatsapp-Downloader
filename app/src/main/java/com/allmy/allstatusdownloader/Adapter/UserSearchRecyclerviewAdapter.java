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
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.bumptech.glide.Glide;
import com.allmy.allstatusdownloader.Activity.ActivitySearch;
import com.allmy.allstatusdownloader.Activity.ShowAllData;
import com.allmy.allstatusdownloader.Model.FavPrefSetter;
import com.allmy.allstatusdownloader.Model.NewSearchUserSetter;
import com.allmy.allstatusdownloader.Others.FavSharedPreference;
import com.allmy.allstatusdownloader.R;

import java.io.PrintStream;
import java.util.ArrayList;

public class UserSearchRecyclerviewAdapter extends Adapter<UserSearchRecyclerviewAdapter.MyViewHolder> {
    ArrayList<NewSearchUserSetter> array_users;
    ArrayList<String> checFavlist = new ArrayList<>();
    int fountPos = -1;
    boolean isFound = false;
    private final boolean isRefresh;
    Context mcontext;
    FavSharedPreference shrdprefernces = new FavSharedPreference();

    public static String profile_picture;
    public static String userID;
    public static String userName;
    public static String userFullName;
    public static String latest_reel_media;

    public class MyViewHolder extends ViewHolder {
        //LikeButton favouriteBtn;
        ImageView iv_go;
        RelativeLayout ll_go;
        RelativeLayout profileImageContainer;
        ImageView profile_pic;
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
            ll_go = view.findViewById(R.id.ll_go);
            this.profileImageContainer = view.findViewById(R.id.profileImageContainer);
        }
    }

    public UserSearchRecyclerviewAdapter(ArrayList<NewSearchUserSetter> arrayList, ActivitySearch activitySearch, boolean z) {
        this.array_users = new ArrayList<>(arrayList);
        this.mcontext = activitySearch;
        this.isRefresh = z;
    }

    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.viewitemnew, viewGroup, false));
    }

    @SuppressLint("WrongConstant")
    public void onBindViewHolder(final MyViewHolder myViewHolder, final int i) {
        if (i <= this.array_users.size() - 1) {
            NewSearchUserSetter newSearchUserSetter = this.array_users.get(i);

            userID = newSearchUserSetter.getUserID();
            userName = newSearchUserSetter.getUserName();
            userFullName = newSearchUserSetter.getUserFullName();
            latest_reel_media = newSearchUserSetter.getLatest_reel_media();

            if (newSearchUserSetter.getProfile_picture() != null) {
                profile_picture = newSearchUserSetter.getProfile_picture();

                Glide.with(mcontext)
                        .load(profile_picture)
                        .placeholder(R.drawable.applogo)
                        .into(myViewHolder.profile_pic);
            }


            if (this.isRefresh) {
                myViewHolder.tv_time.setVisibility(8);
            } else {
                myViewHolder.tv_time.setText(latest_reel_media);
            }
            TextView textView = myViewHolder.tv_username;
            StringBuilder sb = new StringBuilder();
            sb.append("@");
            sb.append(userName);
            textView.setText(sb.toString());
            myViewHolder.user_name.setText(userFullName);
            ArrayList favorites = this.shrdprefernces.getFavorites(this.mcontext);
            if (favorites != null && favorites.size() > 0) {
                for (int i2 = 0; i2 < favorites.size(); i2++) {
                    this.checFavlist.add(((FavPrefSetter) favorites.get(i2)).getUserName());
                    if (this.checFavlist.contains(userName)) {
                        myViewHolder.iv_go.setBackgroundResource(R.drawable.ic_star);
                    } else {
                        myViewHolder.iv_go.setBackgroundResource(R.drawable.ic_star_not_fill);
                    }
                }
            }
            myViewHolder.ll_go.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    ArrayList favorites = UserSearchRecyclerviewAdapter.this.shrdprefernces.getFavorites(UserSearchRecyclerviewAdapter.this.mcontext);
                    if (favorites != null && favorites.size() > 0) {
                        int i = 0;
                        while (true) {
                            if (i >= favorites.size()) {
                                break;
                            } else if (((FavPrefSetter) favorites.get(i)).getUserName().contains(userName)) {
                                UserSearchRecyclerviewAdapter userSearchRecyclerviewAdapter = UserSearchRecyclerviewAdapter.this;
                                userSearchRecyclerviewAdapter.isFound = true;
                                userSearchRecyclerviewAdapter.fountPos = i;
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
                    if (favorites != null && favorites.size() == 0) {
                        NewSearchUserSetter newSearchUserSetter = UserSearchRecyclerviewAdapter.this.array_users.get(i);
                        String profile_picture = newSearchUserSetter.getProfile_picture();
                        String userID = newSearchUserSetter.getUserID();
                        String userName = newSearchUserSetter.getUserName();
                        String userFullName = newSearchUserSetter.getUserFullName();
                        String latest_reel_media = newSearchUserSetter.getLatest_reel_media();
                        FavPrefSetter favPrefSetter = new FavPrefSetter();
                        favPrefSetter.setProfile_picture(profile_picture);
                        favPrefSetter.setUserID(userID);
                        favPrefSetter.setUserName(userName);
                        favPrefSetter.setUserFullName(userFullName);
                        favPrefSetter.setLatest_reel_media(latest_reel_media);
                        UserSearchRecyclerviewAdapter.this.shrdprefernces.addFavorite(UserSearchRecyclerviewAdapter.this.mcontext, favPrefSetter);
                        myViewHolder.iv_go.setBackgroundResource(R.drawable.ic_star);
                    } else if (UserSearchRecyclerviewAdapter.this.isFound) {
                        UserSearchRecyclerviewAdapter.this.isFound = false;
                        PrintStream printStream2 = System.out;
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("position :isFound");
                        sb2.append(UserSearchRecyclerviewAdapter.this.fountPos);
                        printStream2.println(sb2.toString());
                        UserSearchRecyclerviewAdapter.this.shrdprefernces.removeFavorite(UserSearchRecyclerviewAdapter.this.mcontext, (FavPrefSetter) favorites.get(UserSearchRecyclerviewAdapter.this.fountPos), UserSearchRecyclerviewAdapter.this.fountPos);
                        myViewHolder.iv_go.setBackgroundResource(R.drawable.ic_star_not_fill);
                    } else {
                        NewSearchUserSetter newSearchUserSetter2 = UserSearchRecyclerviewAdapter.this.array_users.get(i);
                        String profile_picture2 = newSearchUserSetter2.getProfile_picture();
                        String userID2 = newSearchUserSetter2.getUserID();
                        String userName2 = newSearchUserSetter2.getUserName();
                        String userFullName2 = newSearchUserSetter2.getUserFullName();
                        String latest_reel_media2 = newSearchUserSetter2.getLatest_reel_media();
                        FavPrefSetter favPrefSetter2 = new FavPrefSetter();
                        favPrefSetter2.setProfile_picture(profile_picture2);
                        favPrefSetter2.setUserID(userID2);
                        favPrefSetter2.setUserName(userName2);
                        favPrefSetter2.setUserFullName(userFullName2);
                        favPrefSetter2.setLatest_reel_media(latest_reel_media2);
                        UserSearchRecyclerviewAdapter.this.shrdprefernces.addFavorite(UserSearchRecyclerviewAdapter.this.mcontext, favPrefSetter2);
                        myViewHolder.iv_go.setBackgroundResource(R.drawable.ic_star);
                    }
                }
            });
            View view = myViewHolder.itemView;
            final int i3 = i;
            final String str = userName;
            OnClickListener r2 = view1 -> {
                if (UserSearchRecyclerviewAdapter.this.isNetworkAvailable()) {

                    NewSearchUserSetter newSearchUserSetter1 = this.array_users.get(i);
                    profile_picture = newSearchUserSetter1.getProfile_picture();
                    userID = newSearchUserSetter1.getUserID();
                    String strName = newSearchUserSetter1.getUserName();
                    userFullName = newSearchUserSetter1.getUserFullName();

                    Intent intent = new Intent(mcontext, ShowAllData.class);
                    intent.putExtra("position", i);
                    intent.putExtra("USER_ID", userID);
                    intent.putExtra("USER_NAME", strName);
                    intent.putExtra("USER_FULLNAME", userFullName);
                    intent.putExtra("PROFILE_PIC", profile_picture);
                    UserSearchRecyclerviewAdapter.this.mcontext.startActivity(intent);
                    return;
                }
                Toast.makeText(UserSearchRecyclerviewAdapter.this.mcontext, "Please check internet connection...", Toast.LENGTH_SHORT).show();
            };
            view.setOnClickListener(r2);
            myViewHolder.itemView.setOnLongClickListener(view12 -> {
                final ArrayList favorites1 = UserSearchRecyclerviewAdapter.this.shrdprefernces.getFavorites(UserSearchRecyclerviewAdapter.this.mcontext);
                if (favorites1 != null && favorites1.size() > 0) {
                    int i1 = 0;
                    while (true) {
                        if (i1 >= favorites1.size()) {
                            break;
                        } else if (((FavPrefSetter) favorites1.get(i1)).getUserName().contains(userName)) {
                            isFound = true;
                            fountPos = i1;

                            break;
                        } else {
                            i1++;
                        }
                    }
                }
                if (myViewHolder.profileImageContainer != null) {
                    Animation loadAnimation = AnimationUtils.loadAnimation(UserSearchRecyclerviewAdapter.this.mcontext, R.anim.anim_expand_in);
                    myViewHolder.profileImageContainer.startAnimation(loadAnimation);
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
            });
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
