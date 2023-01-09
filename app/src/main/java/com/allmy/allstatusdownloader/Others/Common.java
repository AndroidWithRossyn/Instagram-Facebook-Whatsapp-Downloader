package com.allmy.allstatusdownloader.Others;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Gallery;
import android.widget.ListAdapter;
import android.widget.ProgressBar;


import com.allmy.allstatusdownloader.Auth.AQuery;
import com.allmy.allstatusdownloader.Auth.BitmapAjaxCallback;

import java.io.File;
import java.util.Comparator;

public class Common implements Comparator<File>, Runnable, OnClickListener, OnLongClickListener, OnItemClickListener, OnScrollListener, OnItemSelectedListener, TextWatcher {
    protected static final int CLEAN_CACHE = 2;
    protected static final int STORE_FILE = 1;
    private boolean fallback;
    private boolean galleryListen = false;
    private OnItemSelectedListener galleryListener;
    private Object handler;
    private int lastBottom;
    private String method;
    private int methodId;
    private OnScrollListener osl;
    private Object[] params;
    private int scrollState = 0;
    private Class<?>[] sig;

    public void afterTextChanged(Editable editable) {
    }

    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    public Common forward(Object obj, String str, boolean z, Class<?>[] clsArr) {
        this.handler = obj;
        this.method = str;
        this.fallback = z;
        this.sig = clsArr;
        return this;
    }

    public Common method(int i, Object... objArr) {
        this.methodId = i;
        this.params = objArr;
        return this;
    }

    private Object invoke(Object... r8) {
        throw new UnsupportedOperationException("Method not decompiled: com.androidquery.util.Common.invoke(java.lang.Object[]):java.lang.Object");
    }

    public int compare(File file, File file2) {
        int i = (file2.lastModified() > file.lastModified() ? 1 : (file2.lastModified() == file.lastModified() ? 0 : -1));
        if (i > 0) {
            return 1;
        }
        return i == 0 ? 0 : -1;
    }

    public void run() {
        invoke(new Object[0]);
    }

    public void onClick(View view) {
        invoke(view);
    }

    public boolean onLongClick(View view) {
        Object invoke = invoke(view);
        if (invoke instanceof Boolean) {
            return ((Boolean) invoke).booleanValue();
        }
        return false;
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        invoke(adapterView, view, Integer.valueOf(i), Long.valueOf(j));
    }

    public void onScroll(AbsListView absListView, int i, int i2, int i3) {
        checkScrolledBottom(absListView, this.scrollState);
        OnScrollListener onScrollListener = this.osl;
        if (onScrollListener != null) {
            onScrollListener.onScroll(absListView, i, i2, i3);
        }
    }

    public int getScrollState() {
        return this.scrollState;
    }

    public void forward(OnScrollListener onScrollListener) {
        this.osl = onScrollListener;
    }

    private void checkScrolledBottom(AbsListView absListView, int i) {
        int count = absListView.getCount();
        int lastVisiblePosition = absListView.getLastVisiblePosition();
        if (i != 0 || count != lastVisiblePosition + 1) {
            this.lastBottom = -1;
        } else if (lastVisiblePosition != this.lastBottom) {
            this.lastBottom = lastVisiblePosition;
            invoke(absListView, Integer.valueOf(i));
        }
    }

    public void onScrollStateChanged(AbsListView absListView, int i) {
        this.scrollState = i;
        checkScrolledBottom(absListView, i);
        if (absListView instanceof ExpandableListView) {
            onScrollStateChanged((ExpandableListView) absListView, i);
        } else {
            onScrollStateChanged2(absListView, i);
        }
        OnScrollListener onScrollListener = this.osl;
        if (onScrollListener != null) {
            onScrollListener.onScrollStateChanged(absListView, i);
        }
    }

    private void onScrollStateChanged(ExpandableListView expandableListView, int i) {
        expandableListView.setTag(1090453508, Integer.valueOf(i));
        if (i == 0) {
            int firstVisiblePosition = expandableListView.getFirstVisiblePosition();
            int lastVisiblePosition = expandableListView.getLastVisiblePosition() - firstVisiblePosition;
            ExpandableListAdapter expandableListAdapter = expandableListView.getExpandableListAdapter();
            for (int i2 = 0; i2 <= lastVisiblePosition; i2++) {
                long expandableListPosition = expandableListView.getExpandableListPosition(i2 + firstVisiblePosition);
                int packedPositionGroup = ExpandableListView.getPackedPositionGroup(expandableListPosition);
                int packedPositionChild = ExpandableListView.getPackedPositionChild(expandableListPosition);
                if (packedPositionGroup >= 0) {
                    View childAt = expandableListView.getChildAt(i2);
                    Long l = (Long) childAt.getTag(1090453508);
                    if (l != null && l.longValue() == expandableListPosition) {
                        if (packedPositionChild == -1) {
                            expandableListAdapter.getGroupView(packedPositionGroup, expandableListView.isGroupExpanded(packedPositionGroup), childAt, expandableListView);
                        } else {
                            expandableListAdapter.getChildView(packedPositionGroup, packedPositionChild, packedPositionChild == expandableListAdapter.getChildrenCount(packedPositionGroup) - 1, childAt, expandableListView);
                        }
                        childAt.setTag(1090453508, null);
                    }
                }
            }
        }
    }

    private void onScrollStateChanged2(AbsListView absListView, int i) {
        absListView.setTag(1090453508, Integer.valueOf(i));
        if (i == 0) {
            int firstVisiblePosition = absListView.getFirstVisiblePosition();
            int lastVisiblePosition = absListView.getLastVisiblePosition() - firstVisiblePosition;
            ListAdapter listAdapter = (ListAdapter) absListView.getAdapter();
            for (int i2 = 0; i2 <= lastVisiblePosition; i2++) {
                long j = (long) (i2 + firstVisiblePosition);
                View childAt = absListView.getChildAt(i2);
                if (((Number) childAt.getTag(1090453508)) != null) {
                    listAdapter.getView((int) j, childAt, absListView);
                    childAt.setTag(1090453508, null);
                }
            }
        }
    }

    public static boolean shouldDelay(int i, int i2, View view, ViewGroup viewGroup, String str) {
        if (str != null && !BitmapAjaxCallback.isMemoryCached(str)) {
            AbsListView absListView = (AbsListView) viewGroup;
            if (((OnScrollListener) viewGroup.getTag(Constants.TAG_SCROLL_LISTENER)) == null) {
                Common common = new Common();
                absListView.setOnScrollListener(common);
                viewGroup.setTag(Constants.TAG_SCROLL_LISTENER, common);
            }
            Integer num = (Integer) absListView.getTag(1090453508);
            if (!(num == null || num.intValue() == 0 || num.intValue() == 1)) {
                long j = (long) i2;
                if (viewGroup instanceof ExpandableListView) {
                    j = ExpandableListView.getPackedPositionForChild(i, i2);
                }
                view.setTag(1090453508, Long.valueOf(j));
                return true;
            }
        }
        return false;
    }

    public static boolean shouldDelay(int i, View view, ViewGroup viewGroup, String str) {
        if (viewGroup instanceof Gallery) {
            return shouldDelayGallery(i, view, viewGroup, str);
        }
        return shouldDelay(-2, i, view, viewGroup, str);
    }

    public static boolean shouldDelay(View view, ViewGroup viewGroup, String str, float f, boolean z) {
        return shouldDelay(-1, view, viewGroup, str);
    }

    private static boolean shouldDelayGallery(int i, View view, ViewGroup viewGroup, String str) {
        Integer valueOf = Integer.valueOf(0);
        if (str == null || BitmapAjaxCallback.isMemoryCached(str)) {
            return false;
        }
        Gallery gallery = (Gallery) viewGroup;
        Integer num = (Integer) gallery.getTag(1090453508);
        if (num == null) {
            gallery.setTag(1090453508, valueOf);
            gallery.setCallbackDuringFling(false);
            new Common().listen(gallery);
        } else {
            valueOf = num;
        }
        int lastVisiblePosition = ((gallery.getLastVisiblePosition() - gallery.getFirstVisiblePosition()) / 2) + 1;
        int intValue = valueOf.intValue() - lastVisiblePosition;
        int intValue2 = valueOf.intValue() + lastVisiblePosition;
        if (intValue < 0) {
            intValue2 -= intValue;
            intValue = 0;
        }
        if (i < intValue || i > intValue2) {
            view.setTag(1090453508, null);
            return true;
        }
        view.setTag(1090453508, Integer.valueOf(i));
        return false;
    }

    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        invoke(charSequence, Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3));
    }

    public void listen(Gallery gallery) {
        this.galleryListener = gallery.getOnItemSelectedListener();
        this.galleryListen = true;
        gallery.setOnItemSelectedListener(this);
    }

    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
        invoke(adapterView, view, Integer.valueOf(i), Long.valueOf(j));
        OnItemSelectedListener onItemSelectedListener = this.galleryListener;
        if (onItemSelectedListener != null) {
            onItemSelectedListener.onItemSelected(adapterView, view, i, j);
        }
        if (this.galleryListen && ((Integer) adapterView.getTag(1090453508)).intValue() != i) {
            Adapter adapter = adapterView.getAdapter();
            adapterView.setTag(1090453508, Integer.valueOf(i));
            int childCount = adapterView.getChildCount();
            int firstVisiblePosition = adapterView.getFirstVisiblePosition();
            for (int i2 = 0; i2 < childCount; i2++) {
                View childAt = adapterView.getChildAt(i2);
                int i3 = firstVisiblePosition + i2;
                Integer num = (Integer) childAt.getTag(1090453508);
                if (num == null || num.intValue() != i3) {
                    adapter.getView(i3, childAt, adapterView);
                }
            }
        }
    }

    public void onNothingSelected(AdapterView<?> adapterView) {
        OnItemSelectedListener onItemSelectedListener = this.galleryListener;
        if (onItemSelectedListener != null) {
            onItemSelectedListener.onNothingSelected(adapterView);
        }
    }

    @SuppressLint("WrongConstant")
    public static void showProgress(Object obj, String str, boolean z) {
        if (obj == null) {
            return;
        }
        if (obj instanceof View) {
            View view = (View) obj;
            ProgressBar progressBar = obj instanceof ProgressBar ? (ProgressBar) obj : null;
            if (z) {
                view.setTag(Constants.TAG_URL, str);
                view.setVisibility(0);
                if (progressBar != null) {
                    progressBar.setProgress(0);
                    progressBar.setMax(100);
                    return;
                }
                return;
            }
            Object tag = view.getTag(Constants.TAG_URL);
            if (tag == null || tag.equals(str)) {
                view.setTag(Constants.TAG_URL, null);
                if (progressBar == null || progressBar.isIndeterminate()) {
                    view.setVisibility(8);
                }
            }
        } else if (obj instanceof Dialog) {
            Dialog dialog = (Dialog) obj;
            AQuery aQuery = new AQuery(dialog.getContext());
            if (z) {
                aQuery.show(dialog);
            } else {
                aQuery.dismiss(dialog);
            }
        } else if (obj instanceof Activity) {
            Activity activity = (Activity) obj;
            activity.setProgressBarIndeterminateVisibility(z);
            activity.setProgressBarVisibility(z);
            if (z) {
                activity.setProgress(0);
            }
        }
    }
}
