package com.allmy.allstatusdownloader.Others;

import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener;

public class RecyclerItemClickListener implements OnItemTouchListener {
    private GestureDetector mGestureDetector;
    /* access modifiers changed from: private */
    public OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(View view, int i);

        void onItemLongClick(View view, int i);
    }

    public void onRequestDisallowInterceptTouchEvent(boolean z) {
    }

    public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
    }

    public RecyclerItemClickListener(Context context, final RecyclerView recyclerView, OnItemClickListener onItemClickListener) {
        this.mListener = onItemClickListener;
        this.mGestureDetector = new GestureDetector(context, new SimpleOnGestureListener() {
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                return true;
            }

            public void onLongPress(MotionEvent motionEvent) {
                View findChildViewUnder = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
                if (findChildViewUnder != null && RecyclerItemClickListener.this.mListener != null) {
                    RecyclerItemClickListener.this.mListener.onItemLongClick(findChildViewUnder, recyclerView.getChildPosition(findChildViewUnder));
                }
            }
        });
    }

    public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
        View findChildViewUnder = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
        if (!(findChildViewUnder == null || this.mListener == null || !this.mGestureDetector.onTouchEvent(motionEvent))) {
            this.mListener.onItemClick(findChildViewUnder, recyclerView.getChildPosition(findChildViewUnder));
        }
        return false;
    }
}
