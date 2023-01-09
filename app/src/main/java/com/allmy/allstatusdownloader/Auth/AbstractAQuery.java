package com.allmy.allstatusdownloader.Auth;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Keep;
import androidx.core.view.ViewCompat;

import com.allmy.allstatusdownloader.Others.AQUtility;
import com.allmy.allstatusdownloader.Others.Common;
import com.allmy.allstatusdownloader.Others.Constant;
import com.allmy.allstatusdownloader.Others.Constants;
import com.allmy.allstatusdownloader.Others.WebImage;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Constructor;
import java.nio.channels.FileChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;


@Keep
public abstract class AbstractAQuery<T extends AbstractAQuery<T>> implements Constant {
    private static Class<?>[] LAYER_TYPE_SIG = {Integer.TYPE, Paint.class};
    private static final Class<?>[] ON_CLICK_SIG = {View.class};
    private static Class<?>[] ON_ITEM_SIG = {AdapterView.class, View.class, Integer.TYPE, Long.TYPE};
    private static Class<?>[] ON_SCROLLED_STATE_SIG = {AbsListView.class, Integer.TYPE};
    private static final Class<?>[] OVER_SCROLL_SIG = {Integer.TYPE};
    private static Class<?>[] PENDING_TRANSITION_SIG = {Integer.TYPE, Integer.TYPE};
    private static final Class<?>[] TEXT_CHANGE_SIG = {CharSequence.class, Integer.TYPE, Integer.TYPE, Integer.TYPE};
    private static WeakHashMap<Dialog, Void> dialogs = new WeakHashMap<>();
    private Activity act;
    protected AccountHandle ah;
    private Constructor<T> constructor;
    private Context context;
    private int policy = 0;
    protected Object progress;
    private HttpHost proxy;
    private View root;
    private Transformer trans;
    protected View view;

    private T self() {
        return (T) this;
    }

    /* access modifiers changed from: protected */
    public T create(View view2) {
        T t = null;
        try {
            T t2 = (T) getConstructor().newInstance(new Object[]{view2});
            try {
//                ((T) t2).act = this.act;
                return t2;
            } catch (Exception e) {
                Exception exc = e;
                t = t2;
                e = exc;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return t;
        }
        return t;
    }

    private Constructor<T> getConstructor() {
        if (this.constructor == null) {
            try {
                this.constructor = (Constructor<T>) getClass().getConstructor(new Class[]{View.class});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return this.constructor;
    }

    public AbstractAQuery(Activity activity) {
        this.act = activity;
    }

    public AbstractAQuery(View view2) {
        this.root = view2;
        this.view = view2;
    }

    public AbstractAQuery(Activity activity, View view2) {
        this.root = view2;
        this.view = view2;
        this.act = activity;
    }

    public AbstractAQuery(Context context2) {
        this.context = context2;
    }

    private View findView(int i) {
        View view2 = this.root;
        if (view2 != null) {
            return view2.findViewById(i);
        }
        Activity activity = this.act;
        if (activity != null) {
            return activity.findViewById(i);
        }
        return null;
    }

    private View findView(String str) {
        View view2 = this.root;
        if (view2 != null) {
            return view2.findViewWithTag(str);
        }
        Activity activity = this.act;
        if (activity != null) {
            @SuppressLint("ResourceType") View childAt = ((ViewGroup) activity.findViewById(16908290)).getChildAt(0);
            if (childAt != null) {
                return childAt.findViewWithTag(str);
            }
        }
        return null;
    }

    private View findView(int... iArr) {
        View findView = findView(iArr[0]);
        for (int i = 1; i < iArr.length && findView != null; i++) {
            findView = findView.findViewById(iArr[i]);
        }
        return findView;
    }

    public T find(int i) {
        return create(findView(i));
    }

    public T parent(int r3) {
        throw new UnsupportedOperationException("Method not decompiled: com.androidquery.AbstractAQuery.parent(int):com.androidquery.AbstractAQuery");
    }

    public T recycle(View view2) {
        this.root = view2;
        this.view = view2;
        reset();
        this.context = null;
        return self();
    }

    public View getView() {
        return this.view;
    }

    public T id(int i) {
        return id(findView(i));
    }

    public T id(View view2) {
        this.view = view2;
        reset();
        return self();
    }

    public T id(String str) {
        return id(findView(str));
    }

    public T id(int... iArr) {
        return id(findView(iArr));
    }

    public T progress(int i) {
        this.progress = findView(i);
        return self();
    }

    public T progress(Object obj) {
        this.progress = obj;
        return self();
    }

    public T progress(Dialog dialog) {
        this.progress = dialog;
        return self();
    }

    public T auth(AccountHandle accountHandle) {
        this.ah = accountHandle;
        return self();
    }

    public T transformer(Transformer transformer) {
        this.trans = transformer;
        return self();
    }

    public T policy(int i) {
        this.policy = i;
        return self();
    }

    public T proxy(String str, int i) {
        this.proxy = new HttpHost(str, i);
        return self();
    }

    public T rating(float f) {
        View view2 = this.view;
        if (view2 instanceof RatingBar) {
            ((RatingBar) view2).setRating(f);
        }
        return self();
    }

    public T text(int i) {
        View view2 = this.view;
        if (view2 instanceof TextView) {
            ((TextView) view2).setText(i);
        }
        return self();
    }

    public T text(int i, Object... objArr) {
        Context context2 = getContext();
        if (context2 != null) {
            text((CharSequence) context2.getString(i, objArr));
        }
        return self();
    }

    public T text(CharSequence charSequence) {
        View view2 = this.view;
        if (view2 instanceof TextView) {
            ((TextView) view2).setText(charSequence);
        }
        return self();
    }

    public T text(CharSequence charSequence, boolean z) {
        if (!z || (charSequence != null && charSequence.length() != 0)) {
            return text(charSequence);
        }
        return gone();
    }

    public T text(Spanned spanned) {
        View view2 = this.view;
        if (view2 instanceof TextView) {
            ((TextView) view2).setText(spanned);
        }
        return self();
    }

    public T textColor(int i) {
        View view2 = this.view;
        if (view2 instanceof TextView) {
            ((TextView) view2).setTextColor(i);
        }
        return self();
    }

    public T typeface(Typeface typeface) {
        View view2 = this.view;
        if (view2 instanceof TextView) {
            ((TextView) view2).setTypeface(typeface);
        }
        return self();
    }

    public T textSize(float f) {
        View view2 = this.view;
        if (view2 instanceof TextView) {
            ((TextView) view2).setTextSize(f);
        }
        return self();
    }

    public T adapter(Adapter adapter) {
        View view2 = this.view;
        if (view2 instanceof AdapterView) {
            ((AdapterView) view2).setAdapter(adapter);
        }
        return self();
    }

    public T adapter(ExpandableListAdapter expandableListAdapter) {
        View view2 = this.view;
        if (view2 instanceof ExpandableListView) {
            ((ExpandableListView) view2).setAdapter(expandableListAdapter);
        }
        return self();
    }

    public T image(int i) {
        View view2 = this.view;
        if (view2 instanceof ImageView) {
            ImageView imageView = (ImageView) view2;
            imageView.setTag(Constants.TAG_URL, null);
            if (i == 0) {
                imageView.setImageBitmap(null);
            } else {
                imageView.setImageResource(i);
            }
        }
        return self();
    }

    public T image(Drawable drawable) {
        View view2 = this.view;
        if (view2 instanceof ImageView) {
            ImageView imageView = (ImageView) view2;
            imageView.setTag(Constants.TAG_URL, null);
            imageView.setImageDrawable(drawable);
        }
        return self();
    }

    public T image(Bitmap bitmap) {
        View view2 = this.view;
        if (view2 instanceof ImageView) {
            ImageView imageView = (ImageView) view2;
            imageView.setTag(Constants.TAG_URL, null);
            imageView.setImageBitmap(bitmap);
        }
        return self();
    }

    public T image(String str) {
        return image(str, true, true, 0, 0);
    }

    public T image(String str, boolean z, boolean z2) {
        return image(str, z, z2, 0, 0);
    }

    public T image(String str, boolean z, boolean z2, int i, int i2) {
        return image(str, z, z2, i, i2, null, 0);
    }

    public T image(String str, boolean z, boolean z2, int i, int i2, Bitmap bitmap, int i3) {
        return image(str, z, z2, i, i2, bitmap, i3, 0.0f);
    }

    public T image(String str, boolean z, boolean z2, int i, int i2, Bitmap bitmap, int i3, float f) {
        return image(str, z, z2, i, i2, bitmap, i3, f, 0, null);
    }

    /* access modifiers changed from: protected */
    public T image(String str, boolean z, boolean z2, int i, int i2, Bitmap bitmap, int i3, float f, int i4, String str2) {
        if (this.view instanceof ImageView) {
            BitmapAjaxCallback.async(this.act, getContext(), (ImageView) this.view, str, z, z2, i, i2, bitmap, i3, f, Float.MAX_VALUE, this.progress, this.ah, this.policy, i4, this.proxy, str2);
            reset();
        }
        return self();
    }

    public T image(String str, ImageOptions imageOptions) {
        return image(str, imageOptions, (String) null);
    }

    /* access modifiers changed from: protected */
    public T image(String str, ImageOptions imageOptions, String str2) {
        if (this.view instanceof ImageView) {
            BitmapAjaxCallback.async(this.act, getContext(), (ImageView) this.view, str, this.progress, this.ah, imageOptions, this.proxy, str2);
            reset();
        }
        return self();
    }

    public T image(BitmapAjaxCallback bitmapAjaxCallback) {
        View view2 = this.view;
        if (view2 instanceof ImageView) {
            bitmapAjaxCallback.imageView((ImageView) view2);
//            invoke(String.valueOf(bitmapAjaxCallback));
        }
        return self();
    }

    public T image(String str, boolean z, boolean z2, int i, int i2, BitmapAjaxCallback bitmapAjaxCallback) {
        ((BitmapAjaxCallback) ((BitmapAjaxCallback) bitmapAjaxCallback.targetWidth(i).fallback(i2).url(str)).memCache(z)).fileCache(z2);
        return image(bitmapAjaxCallback);
    }

    public T image(File file, int i) {
        return image(file, true, i, null);
    }

    public T image(File file, boolean z, int i, BitmapAjaxCallback bitmapAjaxCallback) {
        if (bitmapAjaxCallback == null) {
            bitmapAjaxCallback = new BitmapAjaxCallback();
        }
        BitmapAjaxCallback bitmapAjaxCallback2 = bitmapAjaxCallback;
        bitmapAjaxCallback2.file(file);
        return image(file != null ? file.getAbsolutePath() : null, z, true, i, 0, bitmapAjaxCallback2);
    }

    public T image(Bitmap bitmap, float f) {
        BitmapAjaxCallback bitmapAjaxCallback = new BitmapAjaxCallback();
        bitmapAjaxCallback.ratio(f).bitmap(bitmap);
        return image(bitmapAjaxCallback);
    }

    public T tag(Object obj) {
        View view2 = this.view;
        if (view2 != null) {
            view2.setTag(obj);
        }
        return self();
    }

    public T tag(int i, Object obj) {
        View view2 = this.view;
        if (view2 != null) {
            view2.setTag(i, obj);
        }
        return self();
    }

    public T transparent(boolean z) {
        View view2 = this.view;
        if (view2 != null) {
            AQUtility.transparent(view2, z);
        }
        return self();
    }

    public T enabled(boolean z) {
        View view2 = this.view;
        if (view2 != null) {
            view2.setEnabled(z);
        }
        return self();
    }

    public T checked(boolean z) {
        View view2 = this.view;
        if (view2 instanceof CompoundButton) {
            ((CompoundButton) view2).setChecked(z);
        }
        return self();
    }

    public boolean isChecked() {
        View view2 = this.view;
        if (view2 instanceof CompoundButton) {
            return ((CompoundButton) view2).isChecked();
        }
        return false;
    }

    public T clickable(boolean z) {
        View view2 = this.view;
        if (view2 != null) {
            view2.setClickable(z);
        }
        return self();
    }

    @SuppressLint("WrongConstant")
    public T gone() {
        View view2 = this.view;
        if (!(view2 == null || view2.getVisibility() == 8)) {
            this.view.setVisibility(8);
        }
        return self();
    }

    @SuppressLint("WrongConstant")
    public T invisible() {
        View view2 = this.view;
        if (!(view2 == null || view2.getVisibility() == 4)) {
            this.view.setVisibility(4);
        }
        return self();
    }

    @SuppressLint("WrongConstant")
    public T visible() {
        View view2 = this.view;
        if (!(view2 == null || view2.getVisibility() == 0)) {
            this.view.setVisibility(0);
        }
        return self();
    }

    public T background(int i) {
        View view2 = this.view;
        if (view2 != null) {
            if (i != 0) {
                view2.setBackgroundResource(i);
            } else {
                view2.setBackgroundDrawable(null);
            }
        }
        return self();
    }

    public T backgroundColor(int i) {
        View view2 = this.view;
        if (view2 != null) {
            view2.setBackgroundColor(i);
        }
        return self();
    }

    public T dataChanged() {
        View view2 = this.view;
        if (view2 instanceof AdapterView) {
            Adapter adapter = ((AdapterView) view2).getAdapter();
            if (adapter instanceof BaseAdapter) {
                ((BaseAdapter) adapter).notifyDataSetChanged();
            }
        }
        return self();
    }

    public boolean isExist() {
        return this.view != null;
    }

    public Object getTag() {
        View view2 = this.view;
        if (view2 != null) {
            return view2.getTag();
        }
        return null;
    }

    public Object getTag(int i) {
        View view2 = this.view;
        if (view2 != null) {
            return view2.getTag(i);
        }
        return null;
    }

    public ImageView getImageView() {
        return (ImageView) this.view;
    }

    public Gallery getGallery() {
        return (Gallery) this.view;
    }

    public TextView getTextView() {
        return (TextView) this.view;
    }

    public EditText getEditText() {
        return (EditText) this.view;
    }

    public ProgressBar getProgressBar() {
        return (ProgressBar) this.view;
    }

    public SeekBar getSeekBar() {
        return (SeekBar) this.view;
    }

    public Button getButton() {
        return (Button) this.view;
    }

    public CheckBox getCheckBox() {
        return (CheckBox) this.view;
    }

    public ListView getListView() {
        return (ListView) this.view;
    }

    public ExpandableListView getExpandableListView() {
        return (ExpandableListView) this.view;
    }

    public GridView getGridView() {
        return (GridView) this.view;
    }

    public RatingBar getRatingBar() {
        return (RatingBar) this.view;
    }

    public WebView getWebView() {
        return (WebView) this.view;
    }

    public Spinner getSpinner() {
        return (Spinner) this.view;
    }

    public Editable getEditable() {
        View view2 = this.view;
        if (view2 instanceof EditText) {
            return ((EditText) view2).getEditableText();
        }
        return null;
    }

    public CharSequence getText() {
        View view2 = this.view;
        if (view2 instanceof TextView) {
            return ((TextView) view2).getText();
        }
        return null;
    }

    public Object getSelectedItem() {
        View view2 = this.view;
        if (view2 instanceof AdapterView) {
            return ((AdapterView) view2).getSelectedItem();
        }
        return null;
    }

    public int getSelectedItemPosition() {
        View view2 = this.view;
        if (view2 instanceof AdapterView) {
            return ((AdapterView) view2).getSelectedItemPosition();
        }
        return -1;
    }

    public T clicked(Object obj, String str) {
        return clicked(new Common().forward(obj, str, true, ON_CLICK_SIG));
    }

    public T clicked(OnClickListener onClickListener) {
        View view2 = this.view;
        if (view2 != null) {
            view2.setOnClickListener(onClickListener);
        }
        return self();
    }

    public T longClicked(Object obj, String str) {
        return longClicked(new Common().forward(obj, str, true, ON_CLICK_SIG));
    }

    public T longClicked(OnLongClickListener onLongClickListener) {
        View view2 = this.view;
        if (view2 != null) {
            view2.setOnLongClickListener(onLongClickListener);
        }
        return self();
    }

    public T itemClicked(Object obj, String str) {
        return itemClicked(new Common().forward(obj, str, true, ON_ITEM_SIG));
    }

    public T itemClicked(OnItemClickListener onItemClickListener) {
        View view2 = this.view;
        if (view2 instanceof AdapterView) {
            ((AdapterView) view2).setOnItemClickListener(onItemClickListener);
        }
        return self();
    }

    public T itemSelected(Object obj, String str) {
        return itemSelected(new Common().forward(obj, str, true, ON_ITEM_SIG));
    }

    public T itemSelected(OnItemSelectedListener onItemSelectedListener) {
        View view2 = this.view;
        if (view2 instanceof AdapterView) {
            ((AdapterView) view2).setOnItemSelectedListener(onItemSelectedListener);
        }
        return self();
    }

    public T setSelection(int i) {
        View view2 = this.view;
        if (view2 instanceof AdapterView) {
            ((AdapterView) view2).setSelection(i);
        }
        return self();
    }

    public T scrolledBottom(Object obj, String str) {
        if (this.view instanceof AbsListView) {
            setScrollListener().forward(obj, str, true, ON_SCROLLED_STATE_SIG);
        }
        return self();
    }

    private Common setScrollListener() {
        AbsListView absListView = (AbsListView) this.view;
        Common common = (Common) absListView.getTag(Constants.TAG_SCROLL_LISTENER);
        if (common != null) {
            return common;
        }
        Common common2 = new Common();
        absListView.setOnScrollListener(common2);
        absListView.setTag(Constants.TAG_SCROLL_LISTENER, common2);
        AQUtility.debug((Object) "set scroll listenr");
        return common2;
    }

    public T scrolled(OnScrollListener onScrollListener) {
        if (this.view instanceof AbsListView) {
            setScrollListener().forward(onScrollListener);
        }
        return self();
    }

    public T textChanged(Object obj, String str) {
        View view2 = this.view;
        if (view2 instanceof TextView) {
            ((TextView) view2).addTextChangedListener(new Common().forward(obj, str, true, TEXT_CHANGE_SIG));
        }
        return self();
    }

    public T overridePendingTransition5(int i, int i2) {
        Activity activity = this.act;
        if (activity != null) {
            AQUtility.invokeHandler(activity, "overridePendingTransition", false, false, PENDING_TRANSITION_SIG, Integer.valueOf(i), Integer.valueOf(i2));
        }
        return self();
    }

    public T setOverScrollMode9(int i) {
        View view2 = this.view;
        if (view2 instanceof AbsListView) {
            AQUtility.invokeHandler(view2, "setOverScrollMode", false, false, OVER_SCROLL_SIG, Integer.valueOf(i));
        }
        return self();
    }

    public T setLayerType11(int i, Paint paint) {
        View view2 = this.view;
        if (view2 != null) {
            AQUtility.invokeHandler(view2, "setLayerType", false, false, LAYER_TYPE_SIG, Integer.valueOf(i), paint);
        }
        return self();
    }

    public Object invoke(String str, Class<?>[] clsArr, Object... objArr) {
        Object obj = this.view;
        if (obj == null) {
            obj = this.act;
        }
        return AQUtility.invokeHandler(obj, str, false, false, clsArr, objArr);
    }

//    public T hardwareAccelerated11() {
//        Activity activity = this.act;
//        if (activity != null) {
//            activity.getWindow().setFlags(16777216, 16777216);
//        }
//        return self();
//    }

    public T clear() {
        View view2 = this.view;
        if (view2 != null) {
            if (view2 instanceof ImageView) {
                ImageView imageView = (ImageView) view2;
                imageView.setImageBitmap(null);
                imageView.setTag(Constants.TAG_URL, null);
            } else if (view2 instanceof WebView) {
                WebView webView = (WebView) view2;
                webView.stopLoading();
                webView.clearView();
                webView.setTag(Constants.TAG_URL, null);
            } else if (view2 instanceof TextView) {
                ((TextView) view2).setText("");
            }
        }
        return self();
    }

    public T margin(float f, float f2, float f3, float f4) {
        View view2 = this.view;
        if (view2 != null) {
            LayoutParams layoutParams = view2.getLayoutParams();
            if (layoutParams instanceof MarginLayoutParams) {
                Context context2 = getContext();
                ((MarginLayoutParams) layoutParams).setMargins(AQUtility.dip2pixel(context2, f), AQUtility.dip2pixel(context2, f2), AQUtility.dip2pixel(context2, f3), AQUtility.dip2pixel(context2, f4));
                this.view.setLayoutParams(layoutParams);
            }
        }
        return self();
    }

    public T width(int i) {
        size(true, i, true);
        return self();
    }

    public T height(int i) {
        size(false, i, true);
        return self();
    }

    public T width(int i, boolean z) {
        size(true, i, z);
        return self();
    }

    public T height(int i, boolean z) {
        size(false, i, z);
        return self();
    }

    private void size(boolean z, int i, boolean z2) {
        View view2 = this.view;
        if (view2 != null) {
            LayoutParams layoutParams = view2.getLayoutParams();
            Context context2 = getContext();
            if (i > 0 && z2) {
                i = AQUtility.dip2pixel(context2, (float) i);
            }
            if (z) {
                layoutParams.width = i;
            } else {
                layoutParams.height = i;
            }
            this.view.setLayoutParams(layoutParams);
        }
    }

    public Context getContext() {
        Activity activity = this.act;
        if (activity != null) {
            return activity;
        }
        View view2 = this.root;
        if (view2 != null) {
            return view2.getContext();
        }
        return this.context;
    }

    public <K> T ajax(AjaxCallback ajaxCallback) {
        return invoke(ajaxCallback);
    }

    /* access modifiers changed from: protected */
    public <K> T invoke(AjaxCallback abstractAjaxCallback) {
        abstractAjaxCallback.auth(this.ah);
        abstractAjaxCallback.progress(this.progress);
        abstractAjaxCallback.transformer(this.trans);
        abstractAjaxCallback.policy(this.policy);
        HttpHost httpHost = this.proxy;
        if (httpHost != null) {
            abstractAjaxCallback.proxy(httpHost.getHostName(), this.proxy.getPort());
        }
        Activity activity = this.act;
        if (activity != null) {
            abstractAjaxCallback.async(activity);
        } else {
            abstractAjaxCallback.async(getContext());
        }
        reset();
        return self();
    }

    /* access modifiers changed from: protected */
    public void reset() {
        this.ah = null;
        this.progress = null;
        this.trans = null;
        this.policy = 0;
        this.proxy = null;
    }

    public <K> T ajax(String str, Class<K> cls, AjaxCallback<K> ajaxCallback) {
        ((AjaxCallback) ajaxCallback.type(cls)).url(str);
        return ajax(ajaxCallback);
    }

    public <K> T ajax(String str, Class<K> cls, long j, AjaxCallback<K> ajaxCallback) {
        ((AjaxCallback) ((AjaxCallback) ((AjaxCallback) ajaxCallback.type(cls)).url(str)).fileCache(true)).expire(j);
        return ajax(ajaxCallback);
    }

    public <K> T ajax(String str, Class<K> cls, Object obj, String str2) {
        AjaxCallback ajaxCallback = new AjaxCallback();
        ((AjaxCallback) ajaxCallback.type(cls)).weakHandler(obj, str2);
        return (T) ajax(str, cls, ajaxCallback);
    }

    public <K> T ajax(String str, Class<K> cls, long j, Object obj, String str2) {
        AjaxCallback ajaxCallback = new AjaxCallback();
        ((AjaxCallback) ((AjaxCallback) ((AjaxCallback) ajaxCallback.type(cls)).weakHandler(obj, str2)).fileCache(true)).expire(j);
        return (T) ajax(str, cls, ajaxCallback);
    }

    public <K> T ajax(String str, Map<String, ?> map, Class<K> cls, AjaxCallback<K> ajaxCallback) {
        ((AjaxCallback) ((AjaxCallback) ajaxCallback.type(cls)).url(str)).params(map);
        return ajax(ajaxCallback);
    }

    public <K> T ajax(String str, Map<String, ?> map, Class<K> cls, Object obj, String str2) {
        AjaxCallback ajaxCallback = new AjaxCallback();
        ((AjaxCallback) ajaxCallback.type(cls)).weakHandler(obj, str2);
        return (T) ajax(str, map, cls, ajaxCallback);
    }

    public <K> T delete(String str, Class<K> cls, AjaxCallback<K> ajaxCallback) {
        ((AjaxCallback) ((AjaxCallback) ajaxCallback.url(str)).type(cls)).method(2);
        return ajax(ajaxCallback);
    }

    public <K> T put(String str, String str2, HttpEntity httpEntity, Class<K> cls, AjaxCallback<K> ajaxCallback) {
        ((AjaxCallback) ((AjaxCallback) ((AjaxCallback) ((AjaxCallback) ajaxCallback.url(str)).type(cls)).method(3)).header("Content-Type", str2)).param(Constants.POST_ENTITY, httpEntity);
        return ajax(ajaxCallback);
    }

    public <K> T delete(String str, Class<K> cls, Object obj, String str2) {
        AjaxCallback ajaxCallback = new AjaxCallback();
        ajaxCallback.weakHandler(obj, str2);
        return (T) delete(str, cls, ajaxCallback);
    }

    public <K> T sync(AjaxCallback<K> ajaxCallback) {
        ajax(ajaxCallback);
        ajaxCallback.block();
        return self();
    }

    public T cache(String str, long j) {
        return ajax(str, byte[].class, j, (Object) null, (String) null);
    }

    public T ajaxCancel() {
        AjaxCallback.cancel();
        return self();
    }

    public File getCachedFile(String str) {
        File existedCacheByUrl = AQUtility.getExistedCacheByUrl(AQUtility.getCacheDir(getContext(), 1), str);
        return existedCacheByUrl == null ? AQUtility.getExistedCacheByUrl(AQUtility.getCacheDir(getContext(), 0), str) : existedCacheByUrl;
    }

    public T invalidate(String str) {
        File cachedFile = getCachedFile(str);
        if (cachedFile != null) {
            cachedFile.delete();
        }
        return self();
    }

    public Bitmap getCachedImage(String str) {
        return getCachedImage(str, 0);
    }

    public Bitmap getCachedImage(String str, int i) {
        Bitmap memoryCached = BitmapAjaxCallback.getMemoryCached(str, i);
        if (memoryCached != null) {
            return memoryCached;
        }
        File cachedFile = getCachedFile(str);
        return cachedFile != null ? BitmapAjaxCallback.getResizedImage(cachedFile.getAbsolutePath(), null, i, true, 0) : memoryCached;
    }

    public Bitmap getCachedImage(int i) {
        return BitmapAjaxCallback.getMemoryCached(getContext(), i);
    }

    @Deprecated
    public boolean shouldDelay(View view2, ViewGroup viewGroup, String str, float f) {
        return Common.shouldDelay(view2, viewGroup, str, f, true);
    }

    @Deprecated
    public boolean shouldDelay(View view2, ViewGroup viewGroup, String str, float f, boolean z) {
        return Common.shouldDelay(view2, viewGroup, str, f, z);
    }

    public boolean shouldDelay(int i, boolean z, View view2, ViewGroup viewGroup, String str) {
        return Common.shouldDelay(i, -1, view2, viewGroup, str);
    }

    public boolean shouldDelay(int i, int i2, boolean z, View view2, ViewGroup viewGroup, String str) {
        return Common.shouldDelay(i, i2, view2, viewGroup, str);
    }

    public boolean shouldDelay(int i, View view2, ViewGroup viewGroup, String str) {
        if (!(viewGroup instanceof ExpandableListView)) {
            return Common.shouldDelay(i, view2, viewGroup, str);
        }
        throw new IllegalArgumentException("Please use the other shouldDelay methods for expandable list.");
    }

    public File makeSharedFile(String str, String str2) {
        FileChannel channel = null;
        FileChannel channel2 = null;
        File file = null;
        try {
            File cachedFile = getCachedFile(str);
            if (cachedFile == null) {
                return null;
            }
            File tempDir = AQUtility.getTempDir();
            if (tempDir == null) {
                return null;
            }
            File file2 = new File(tempDir, str2);
            try {
                file2.createNewFile();
                channel = new FileInputStream(cachedFile).getChannel();
                channel2 = new FileOutputStream(file2).getChannel();
                channel.transferTo(0, channel.size(), channel2);
                if (channel != null) {
                    channel.close();
                }
                if (channel2 != null) {
                    channel2.close();
                }
                return file2;
            } catch (Exception e) {
                file = file2;
                AQUtility.debug((Throwable) e);
                return file;
            } catch (Throwable th) {
                if (channel != null) {
                    channel.close();
                }
                if (channel2 != null) {
                    channel2.close();
                }
                throw th;
            }
        } catch (Exception e) {
            AQUtility.debug((Throwable) e);
            return file;
        }
    }

    public T animate(int i) {
        return animate(i, null);
    }

    public T animate(int i, AnimationListener animationListener) {
        Animation loadAnimation = AnimationUtils.loadAnimation(getContext(), i);
        loadAnimation.setAnimationListener(animationListener);
        return animate(loadAnimation);
    }

    public T animate(Animation animation) {
        View view2 = this.view;
        if (!(view2 == null || animation == null)) {
            view2.startAnimation(animation);
        }
        return self();
    }

    public T click() {
        View view2 = this.view;
        if (view2 != null) {
            view2.performClick();
        }
        return self();
    }

    public T longClick() {
        View view2 = this.view;
        if (view2 != null) {
            view2.performLongClick();
        }
        return self();
    }

    public T show(Dialog dialog) {
        if (dialog != null) {
            try {
                dialog.show();
                dialogs.put(dialog, null);
            } catch (Exception unused) {
            }
        }
        return self();
    }

    public T dismiss(Dialog dialog) {
        if (dialog != null) {
            try {
                dialogs.remove(dialog);
                dialog.dismiss();
            } catch (Exception unused) {
            }
        }
        return self();
    }

    public T dismiss() {
        Iterator it = dialogs.keySet().iterator();
        while (it.hasNext()) {
            try {
                ((Dialog) it.next()).dismiss();
            } catch (Exception unused) {
            }
            it.remove();
        }
        return self();
    }

    public T webImage(String str) {
        return webImage(str, true, false, ViewCompat.MEASURED_STATE_MASK);
    }

    public T webImage(String str, boolean z, boolean z2, int i) {
        if (this.view instanceof WebView) {
            setLayerType11(1, null);
            WebImage webImage = new WebImage((WebView) this.view, str, this.progress, z, z2, i);
            webImage.load();
            this.progress = null;
        }
        return self();
    }

    @SuppressLint("WrongConstant")
    public View inflate(View view2, int i, ViewGroup viewGroup) {
        LayoutInflater layoutInflater;
        if (view2 != null) {
            Integer num = (Integer) view2.getTag(Constants.TAG_LAYOUT);
            if (num != null && num.intValue() == i) {
                return view2;
            }
        }
        Activity activity = this.act;
        if (activity != null) {
            layoutInflater = activity.getLayoutInflater();
        } else {
            layoutInflater = (LayoutInflater) getContext().getSystemService("layout_inflater");
        }
        View inflate = layoutInflater.inflate(i, viewGroup, false);
        inflate.setTag(Constants.TAG_LAYOUT, Integer.valueOf(i));
        return inflate;
    }

    public T expand(int i, boolean z) {
        View view2 = this.view;
        if (view2 instanceof ExpandableListView) {
            ExpandableListView expandableListView = (ExpandableListView) view2;
            if (z) {
                expandableListView.expandGroup(i);
            } else {
                expandableListView.collapseGroup(i);
            }
        }
        return self();
    }

    public T expand(boolean z) {
        View view2 = this.view;
        if (view2 instanceof ExpandableListView) {
            ExpandableListView expandableListView = (ExpandableListView) view2;
            ExpandableListAdapter expandableListAdapter = expandableListView.getExpandableListAdapter();
            if (expandableListAdapter != null) {
                int groupCount = expandableListAdapter.getGroupCount();
                for (int i = 0; i < groupCount; i++) {
                    if (z) {
                        expandableListView.expandGroup(i);
                    } else {
                        expandableListView.collapseGroup(i);
                    }
                }
            }
        }
        return self();
    }

    public T download(String str, File file, AjaxCallback<File> ajaxCallback) {
        ((AjaxCallback) ((AjaxCallback) ajaxCallback.url(str)).type(File.class)).targetFile(file);
        return ajax(ajaxCallback);
    }

    public T download(String str, File file, Object obj, String str2) {
        AjaxCallback ajaxCallback = new AjaxCallback();
        ajaxCallback.weakHandler(obj, str2);
        return (T) download(str, file, ajaxCallback);
    }
}
