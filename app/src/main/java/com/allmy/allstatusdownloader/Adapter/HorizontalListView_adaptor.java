package com.allmy.allstatusdownloader.Adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.allmy.allstatusdownloader.Activity.ShowAllData;
import com.allmy.allstatusdownloader.Auth.AQuery;
import com.allmy.allstatusdownloader.Model.HeighLightSetter;
import com.allmy.allstatusdownloader.R;

import java.util.ArrayList;

public class HorizontalListView_adaptor extends BaseAdapter {
    AQuery aQuery;
    ArrayList<HeighLightSetter> array_users;
    ShowAllData mcontext;

    private static final class MyViewHolder {
        ImageView profile_pic;
        TextView user_name;

        private MyViewHolder() {
        }
    }

    public long getItemId(int i) {
        return i;
    }

    public HorizontalListView_adaptor(ArrayList<HeighLightSetter> arrayList, ShowAllData showAllData) {
        this.array_users = arrayList;
        this.mcontext = showAllData;
        this.aQuery = new AQuery(showAllData);
    }

    public int getCount() {
        return this.array_users.size();
    }

    public Object getItem(int i) {
        return this.array_users.get(i);
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        MyViewHolder myViewHolder;
        if (view == null) {
            view = this.mcontext.getLayoutInflater().inflate(R.layout.horizntalistviewitem, viewGroup, false);
            myViewHolder = new MyViewHolder();
            myViewHolder.profile_pic = view.findViewById(R.id.iv_crimage);
            myViewHolder.user_name = view.findViewById(R.id.title);
            view.setTag(myViewHolder);
        } else {
            myViewHolder = (MyViewHolder) view.getTag();
        }
        if (i > this.array_users.size() - 1) {
            return null;
        }
        String str = "";
        String heighlight_cover = this.array_users.size() > i ? this.array_users.get(i).getHeighlight_cover() : str;
        if (heighlight_cover != null) {
            ShowAllData showAllData = this.mcontext;
            if (!(showAllData == null || heighlight_cover == str)) {
                if (showAllData.isFinishing()) {
                    return null;
                }
                Glide.with(this.mcontext.getApplicationContext()).load(heighlight_cover).apply(new RequestOptions().override(200, 200).centerCrop().skipMemoryCache(true)).into(myViewHolder.profile_pic);
            }
        }
        if (this.array_users.size() > i) {
            myViewHolder.user_name.setText(this.array_users.get(i).getUserTitle());
        }
        return view;
    }
}
