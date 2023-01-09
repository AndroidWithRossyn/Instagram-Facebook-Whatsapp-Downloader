package com.allmy.allstatusdownloader.Others;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import java.lang.ref.WeakReference;

public class RatioDrawable extends BitmapDrawable {
    private boolean adjusted;
    private float anchor;
    private Matrix m;
    private float ratio;
    private WeakReference<ImageView> ref;
    private int w;

    public RatioDrawable(Resources resources, Bitmap bitmap, ImageView imageView, float f, float f2) {
        super(resources, bitmap);
        this.ref = new WeakReference<>(imageView);
        this.ratio = f;
        this.anchor = f2;
        imageView.setScaleType(ScaleType.MATRIX);
        imageView.setImageMatrix(new Matrix());
        adjust(imageView, bitmap, false);
    }

    private int getWidth(ImageView imageView) {
        LayoutParams layoutParams = imageView.getLayoutParams();
        int i = layoutParams != null ? layoutParams.width : 0;
        if (i <= 0) {
            i = imageView.getWidth();
        }
        return i > 0 ? (i - imageView.getPaddingLeft()) - imageView.getPaddingRight() : i;
    }

    public void draw(Canvas canvas) {
        WeakReference<ImageView> weakReference = this.ref;
        ImageView imageView = weakReference != null ? (ImageView) weakReference.get() : null;
        if (this.ratio == 0.0f || imageView == null) {
            super.draw(canvas);
        } else {
            draw(canvas, imageView, getBitmap());
        }
    }

    private void draw(Canvas canvas, ImageView imageView, Bitmap bitmap) {
        Matrix matrix = getMatrix(imageView, bitmap);
        if (matrix != null) {
            int paddingTop = imageView.getPaddingTop() + imageView.getPaddingBottom();
            int paddingLeft = imageView.getPaddingLeft() + imageView.getPaddingRight();
            if (paddingTop > 0 || paddingLeft > 0) {
                canvas.clipRect(0, 0, imageView.getWidth() - paddingLeft, imageView.getHeight() - paddingTop);
            }
            canvas.drawBitmap(bitmap, matrix, getPaint());
        }
        if (!this.adjusted) {
            adjust(imageView, bitmap, true);
        }
    }

    private void adjust(ImageView imageView, Bitmap bitmap, boolean z) {
        int width = getWidth(imageView);
        if (width > 0) {
            int targetHeight = targetHeight(bitmap.getWidth(), bitmap.getHeight(), width) + imageView.getPaddingTop() + imageView.getPaddingBottom();
            LayoutParams layoutParams = imageView.getLayoutParams();
            if (layoutParams != null) {
                if (targetHeight != layoutParams.height) {
                    layoutParams.height = targetHeight;
                    imageView.setLayoutParams(layoutParams);
                }
                if (z) {
                    this.adjusted = true;
                }
            }
        }
    }

    private int targetHeight(int i, int i2, int i3) {
        float f = this.ratio;
        if (f == Float.MAX_VALUE) {
            f = ((float) i2) / ((float) i);
        }
        return (int) (((float) i3) * f);
    }

    private Matrix getMatrix(ImageView imageView, Bitmap bitmap) {
        float f;
        float f2;
        int width = bitmap.getWidth();
        Matrix matrix = this.m;
        if (matrix != null && width == this.w) {
            return matrix;
        }
        int height = bitmap.getHeight();
        int width2 = getWidth(imageView);
        int targetHeight = targetHeight(width, height, width2);
        if (width <= 0 || height <= 0 || width2 <= 0 || targetHeight <= 0) {
            return null;
        }
        if (this.m == null || width != this.w) {
            this.m = new Matrix();
            float f3 = 0.0f;
            if (width * targetHeight >= width2 * height) {
                float f4 = ((float) targetHeight) / ((float) height);
                f3 = (((float) width2) - (((float) width) * f4)) * 0.5f;
                f2 = f4;
                f = 0.0f;
            } else {
                f2 = ((float) width2) / ((float) width);
                f = (((float) targetHeight) - (((float) height) * f2)) * getYOffset(width, height);
            }
            this.m.setScale(f2, f2);
            this.m.postTranslate(f3, f);
            this.w = width;
        }
        return this.m;
    }

    private float getYOffset(int i, int i2) {
        float f = this.anchor;
        if (f != Float.MAX_VALUE) {
            return (1.0f - f) / 2.0f;
        }
        return ((1.5f - Math.max(1.0f, Math.min(1.5f, ((float) i2) / ((float) i)))) / 2.0f) + 0.25f;
    }
}
