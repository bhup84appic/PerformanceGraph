package com.aim.pmgraph.util;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.core.graphics.drawable.DrawableCompat;

public class Utils {
    public static void setTintImageView(ImageView v, int color) {
        Drawable drawable = v.getDrawable();
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, color);
        v.setImageDrawable(drawable);
    }

}
