package com.allmy.allstatusdownloader.Adapter;

import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
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
import com.allmy.allstatusdownloader.Activity.HeighLightStory_shower;
import com.allmy.allstatusdownloader.Activity.InstaHighlightActivity;
import com.allmy.allstatusdownloader.Auth.AQuery;
import com.allmy.allstatusdownloader.Model.Story_setter;
import com.allmy.allstatusdownloader.R;

import java.io.File;
import java.util.ArrayList;

public class HilightshowerRecyclerviewAdapter extends Adapter<HilightshowerRecyclerviewAdapter.MyViewHolder> {
    AQuery aQuery;
    ArrayList<Story_setter> hilightstory_data;
    Context mcontext;
    String user_FULLNAME;

    public class MyViewHolder extends ViewHolder {
        ImageView iv_grid_check;
        RelativeLayout rlgrid_layer;
        RelativeLayout rlgrid_layernew;
        TextView taken_at;
        ImageView thumbnail;
        ImageView video_icon;
        View viewBgImage, checkedBg;

        public MyViewHolder(View view) {
            super(view);
            this.thumbnail = (ImageView) view.findViewById(R.id.iv_grid_media);
            this.video_icon = (ImageView) view.findViewById(R.id.iv_grid_play);
            this.taken_at = (TextView) view.findViewById(R.id.taken_atTv);
            this.iv_grid_check = (ImageView) view.findViewById(R.id.iv_grid_check);
            this.rlgrid_layer = (RelativeLayout) view.findViewById(R.id.rlgrid_layer);
            this.rlgrid_layernew = (RelativeLayout) view.findViewById(R.id.rlgrid_layernew);
            viewBgImage = view.findViewById(R.id.viewBgImage);
            checkedBg = view.findViewById(R.id.checkedBg);
        }
    }

    public HilightshowerRecyclerviewAdapter(ArrayList<Story_setter> arrayList, HeighLightStory_shower heighLightStory_shower, AQuery aQuery2, String str) {
        this.hilightstory_data = new ArrayList<>(arrayList);
        this.aQuery = aQuery2;
        this.user_FULLNAME = str;
        this.mcontext = heighLightStory_shower;
    }

    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.populargrid_row, viewGroup, false);
        inflate.setMinimumHeight(viewGroup.getMeasuredHeight() / 4);
        return new MyViewHolder(inflate);
    }

    @SuppressLint("WrongConstant")
    public void onBindViewHolder(MyViewHolder myViewHolder, final int i) {
//        if (i <= this.hilightstory_data.size() - 1)
//        {
        ArrayList<Story_setter> arrayList = this.hilightstory_data;
        ((Story_setter) arrayList.get(i)).getMedia_id();
        ArrayList<Story_setter> arrayList2 = this.hilightstory_data;
        ((Story_setter) arrayList2.get(i)).getUserName();
        ArrayList<Story_setter> arrayList3 = this.hilightstory_data;
        String thimbnail_url = ((Story_setter) arrayList3.get(i)).getThimbnail_url();
        ArrayList<Story_setter> arrayList4 = this.hilightstory_data;
        String taken_at = ((Story_setter) arrayList4.get(i)).getTaken_at();
        ArrayList<Story_setter> arrayList5 = this.hilightstory_data;
        boolean isCheckmultiple = ((Story_setter) arrayList5.get(i)).isCheckmultiple();
        if (thimbnail_url != null && !thimbnail_url.equalsIgnoreCase("")) {
//                Picasso.get().load(thimbnail_url).fit().centerCrop().into(myViewHolder.thumbnail);
            Glide.with(mcontext)
                    .load(thimbnail_url)
                    .centerCrop()
                    .into(myViewHolder.thumbnail);
            myViewHolder.taken_at.setText(taken_at);
        }
        ArrayList<Story_setter> arrayList6 = this.hilightstory_data;
        if (!((Story_setter) arrayList6.get(i)).getvideourl().equalsIgnoreCase(" ")) {
            myViewHolder.video_icon.setVisibility(0);
        } else {
            myViewHolder.video_icon.setVisibility(8);
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
        if (HeighLightStory_shower.highshowtickstory) {
//                myViewHolder.rlgrid_layernew.setVisibility(0);
        } else {
//                myViewHolder.rlgrid_layernew.setVisibility(4);
            myViewHolder.rlgrid_layer.setVisibility(4);
            myViewHolder.iv_grid_check.setVisibility(4);
        }
        myViewHolder.iv_grid_check.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
            }
        });
        myViewHolder.rlgrid_layer.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
            }
        });

        myViewHolder.rlgrid_layernew.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (HilightshowerRecyclerviewAdapter.this.isNetworkAvailable()) {
                    String str = "position";
                    String str2 = "story_data";
                    String str3 = "profileUrl";
                    String str4 = "imgUrl";
                    String str5 = "id";
                    String str6 = "title";
                    String str7 = "full_name";
//                        Collections.reverse(hilightstory_data);
                    if (((Story_setter) HilightshowerRecyclerviewAdapter.this.hilightstory_data.get(i)).getvideourl().equalsIgnoreCase(" ")) {
                        Intent intent = new Intent(HilightshowerRecyclerviewAdapter.this.mcontext, InstaHighlightActivity.class);
                        intent.putExtra(str7, HilightshowerRecyclerviewAdapter.this.user_FULLNAME);
                        intent.putExtra(str6, ((Story_setter) HilightshowerRecyclerviewAdapter.this.hilightstory_data.get(i)).getMedia_title());
                        intent.putExtra(str5, ((Story_setter) HilightshowerRecyclerviewAdapter.this.hilightstory_data.get(i)).getMedia_id());
                        intent.putExtra(str4, ((Story_setter) HilightshowerRecyclerviewAdapter.this.hilightstory_data.get(i)).getimageurl());
                        intent.putExtra(str3, ((Story_setter) HilightshowerRecyclerviewAdapter.this.hilightstory_data.get(i)).getProfile_picture());
                        intent.putParcelableArrayListExtra(str2, HilightshowerRecyclerviewAdapter.this.hilightstory_data);
                        intent.putExtra(str, i);
                        intent.setFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                        HilightshowerRecyclerviewAdapter.this.mcontext.startActivity(intent);
                        return;
                    }
                    Intent intent2 = new Intent(HilightshowerRecyclerviewAdapter.this.mcontext, InstaHighlightActivity.class);
                    intent2.putExtra(str7, HilightshowerRecyclerviewAdapter.this.user_FULLNAME);
                    intent2.putExtra(str6, ((Story_setter) HilightshowerRecyclerviewAdapter.this.hilightstory_data.get(i)).getMedia_title());
                    intent2.putExtra(str5, ((Story_setter) HilightshowerRecyclerviewAdapter.this.hilightstory_data.get(i)).getMedia_id());
                    intent2.putExtra(str4, ((Story_setter) HilightshowerRecyclerviewAdapter.this.hilightstory_data.get(i)).getimageurl());
                    intent2.putExtra(str3, ((Story_setter) HilightshowerRecyclerviewAdapter.this.hilightstory_data.get(i)).getProfile_picture());
                    intent2.putExtra("videoUrl", ((Story_setter) HilightshowerRecyclerviewAdapter.this.hilightstory_data.get(i)).getvideourl());
                    intent2.putParcelableArrayListExtra(str2, HilightshowerRecyclerviewAdapter.this.hilightstory_data);
                    intent2.putExtra(str, i);
                    intent2.setFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                    HilightshowerRecyclerviewAdapter.this.mcontext.startActivity(intent2);
                    return;
                }
                Toast.makeText(HilightshowerRecyclerviewAdapter.this.mcontext, "Please check internet connection...", 0).show();
            }
        });
//        }
    }

    public int getItemCount() {
        return this.hilightstory_data.size();
    }

    public boolean isNetworkAvailable() {
        @SuppressLint("WrongConstant") NetworkInfo activeNetworkInfo = ((ConnectivityManager) this.mcontext.getSystemService("connectivity")).getActiveNetworkInfo();
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
