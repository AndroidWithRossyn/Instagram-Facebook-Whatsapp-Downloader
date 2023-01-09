package com.allmy.allstatusdownloader.Others;

import android.graphics.Bitmap;

import java.util.Iterator;
import java.util.LinkedHashMap;

public class BitmapCache extends LinkedHashMap<String, Bitmap> {
    private static final long serialVersionUID = 1;
    private int maxCount;
    private int maxPixels;
    private int maxTotalPixels;
    private int pixels;

    public BitmapCache(int i, int i2, int i3) {
        super(8, 0.75f, true);
        this.maxCount = i;
        this.maxPixels = i2;
        this.maxTotalPixels = i3;
    }

    public Bitmap put(String str, Bitmap bitmap) {
        int pixels2 = pixels(bitmap);
        if (pixels2 > this.maxPixels) {
            return null;
        }
        this.pixels += pixels2;
        Bitmap bitmap2 = (Bitmap) super.put(str, bitmap);
        if (bitmap2 == null) {
            return bitmap2;
        }
        this.pixels -= pixels(bitmap2);
        return bitmap2;
    }

    public Bitmap remove(Object obj) {
        Bitmap bitmap = (Bitmap) super.remove(obj);
        if (bitmap != null) {
            this.pixels -= pixels(bitmap);
        }
        return bitmap;
    }

    public void clear() {
        super.clear();
        this.pixels = 0;
    }

    private int pixels(Bitmap bitmap) {
        if (bitmap == null) {
            return 0;
        }
        return bitmap.getWidth() * bitmap.getHeight();
    }

    private void shrink() {
        if (this.pixels > this.maxTotalPixels) {
            Iterator it = keySet().iterator();
            while (it.hasNext()) {
                it.next();
                it.remove();
                if (this.pixels <= this.maxTotalPixels) {
                    return;
                }
            }
        }
    }

    public boolean removeEldestEntry(Entry<String, Bitmap> entry) {
        if (this.pixels > this.maxTotalPixels || size() > this.maxCount) {
            remove(entry.getKey());
        }
        shrink();
        return false;
    }
}
