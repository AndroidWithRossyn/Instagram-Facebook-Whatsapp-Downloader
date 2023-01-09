package com.allmy.allstatusdownloader.Others;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;

public class HorizontalListView extends HorizontalScrollView {
    private String TAG = "HorizontalListView";
    private ListAdapter mAdapter = null;
    private ViewGroup mContainer = null;
    private Context mContext = null;
    public OnListItemClickListener mListItemClickListener = null;

    public class CustomOnClickListener implements OnClickListener {
        private int mPosition;

        public CustomOnClickListener(int i) {
            this.mPosition = i;
        }

        public void onClick(View view) {
            if (HorizontalListView.this.mListItemClickListener != null) {
                HorizontalListView.this.mListItemClickListener.onClick(view, this.mPosition);
            }
        }
    }

    public interface OnListItemClickListener {
        void onClick(View view, int i);
    }

    public void registerListItemClickListener(OnListItemClickListener onListItemClickListener) {
        this.mListItemClickListener = onListItemClickListener;
    }

    @SuppressLint("WrongConstant")
    public HorizontalListView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mContext = context;
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(0);
        linearLayout.setLayoutParams(new LayoutParams(-1, -2));
        this.mContainer = linearLayout;
        addView(this.mContainer);
        setHorizontalScrollBarEnabled(false);
    }

    public void setAdapter(ListAdapter listAdapter) {
        this.mAdapter = listAdapter;
        if (getChildCount() != 0 && listAdapter != null) {
            this.mContainer.removeAllViews();
            for (int i = 0; i < listAdapter.getCount(); i++) {
                View view = listAdapter.getView(i, null, this.mContainer);
                if (view != null) {
                    view.setOnClickListener(new CustomOnClickListener(i));
                    this.mContainer.addView(view);
                }
            }
        }
    }
}
