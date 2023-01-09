package com.allmy.allstatusdownloader.Others;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;

import java.io.PrintStream;

public abstract class DetectScrollToEnd extends OnScrollListener {
    private boolean isScrollDown;
    private final GridLayoutManager layoutManager;
    private final int mThreshold;

    public abstract void onLoadMore();

    public DetectScrollToEnd(GridLayoutManager gridLayoutManager, int i) {
        this.layoutManager = gridLayoutManager;
        this.mThreshold = i;
    }

    public void onScrolled(RecyclerView recyclerView, int i, int i2) {
        super.onScrolled(recyclerView, i, i2);
        this.isScrollDown = i2 >= 0;
    }

    public void onScrollStateChanged(RecyclerView recyclerView, int i) {
        super.onScrollStateChanged(recyclerView, i);
        int childCount = this.layoutManager.getChildCount();
        int itemCount = this.layoutManager.getItemCount();
        int findLastVisibleItemPosition = this.layoutManager.findLastVisibleItemPosition();
        PrintStream printStream = System.out;
        StringBuilder sb = new StringBuilder();
        sb.append("Staes of recycleview : ");
        sb.append(i);
        printStream.println(sb.toString());
        if (i == 2 && childCount <= itemCount && this.isScrollDown && findLastVisibleItemPosition + childCount + this.mThreshold >= itemCount) {
            onLoadMore();
        }
    }
}
