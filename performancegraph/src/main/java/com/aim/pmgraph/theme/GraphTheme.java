package com.aim.pmgraph.theme;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import java.io.Serializable;

public class GraphTheme implements Serializable {
    private int cardBackgroundColor;
    private int primaryTextColor;
    private int secondaryTextColor;
    private Typeface primaryTextFont;
    private Typeface secondaryTextFont;
    private int lineColor1;
    private int lineColor2;
    private String emptyStateText;
    private Drawable emptyStateImage;

    public int getCardBackgroundColor() {
        return cardBackgroundColor;
    }

    public int getPrimaryTextColor() {
        if (primaryTextColor == 0) {
            primaryTextColor = Color.parseColor("#263238");
        }
        return primaryTextColor;
    }

    public int getSecondaryTextColor() {
        return secondaryTextColor;
    }

    public Typeface getPrimaryTextFont() {
        return primaryTextFont;
    }

    public Typeface getSecondaryTextFont() {
        return secondaryTextFont;
    }

    public int getLineColor1() {
        return lineColor1;
    }

    public int getLineColor2() {
        return lineColor2;
    }


    public String getEmptyStateText() {
        return emptyStateText;
    }

    public Drawable getEmptyStateImage() {
        return emptyStateImage;
    }


    public GraphTheme(int cardBackgroundColor, int primaryTextColor, int secondaryTextColor, Typeface primaryTextFont, Typeface secondaryTextFont, int lineColor1, int lineColor2, String emptyStateText, Drawable emptyStateImage) {
        this.cardBackgroundColor = cardBackgroundColor;
        this.primaryTextColor = primaryTextColor;
        this.secondaryTextColor = secondaryTextColor;
        this.primaryTextFont = primaryTextFont;
        this.secondaryTextFont = secondaryTextFont;
        this.lineColor1 = lineColor1;
        this.lineColor2 = lineColor2;
        this.emptyStateText = emptyStateText;
        this.emptyStateImage = emptyStateImage;
    }


}
