/*
 *
 *  Created by Pooran Kharol on 18/2/21 10:56 AM
 *  Copyright (c) 2021 . All rights reserved.
 *  Last modified 17/2/21 8:18 PM
 *
 */

package com.aim.pmgraph.views;

import static com.mintoak.corelib.ApiClient.getClientNew;
import static com.mintoak.corelib.Util.convertToDefaultCurrencyFormatNew;
import static com.mintoak.corelib.Util.getAmounts;
import static com.mintoak.corelib.Util.popUpDialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.aim.pmgraph.MyValueFormatter;
import com.aim.pmgraph.R;
import com.aim.pmgraph.activity.ActivityLineGraphList;
import com.aim.pmgraph.intface.TwelveMonthViewCallBack;
import com.aim.pmgraph.model.LineChartModel;
import com.aim.pmgraph.model.PerformanceItem;
import com.aim.pmgraph.model.PerformanceListItem;
import com.aim.pmgraph.model.ResPerformanceViewAll;
import com.aim.pmgraph.model.SummaryListItem;
import com.aim.pmgraph.monthutil.MonthViewUI;
import com.aim.pmgraph.theme.GraphTheme;
import com.aim.pmgraph.util.PerformanceCardUtils;
import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.MPPointF;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mintoak.corelib.ApiInterface;
import com.mintoak.corelib.LoggerUtils;
import com.mintoak.corelib.RXJavaCaller;
import com.mintoak.corelib.Util;
import com.mintoak.corelib.apiutil.ApiConstants;
import com.mintoak.corelib.apiutil.ApiNetworkUtils;
import com.mintoak.corelib.apiutil.Encrypter;
import com.mintoak.corelib.apiutil.ModuleConfiguration;
import com.mintoak.corelib.builder.RequestParameters;
import com.mintoak.corelib.calender_module.calender_filterutil.CalenderDateUI;
import com.mintoak.corelib.calender_module.listeners.CalenderDataToAppCallback;
import com.mintoak.corelib.enums.BaseUrl;
import com.mintoak.corelib.enums.Type;
import com.mintoak.corelib.interfaces.SessionExpiredListener;
import com.mintoak.corelib.location_module.filter_utils.LocationFilterUI;
import com.mintoak.corelib.location_module.interfaces.LocationDataToAppCallback;
import com.mintoak.corelib.location_module.interfaces.LocationFilterApplyCallBack;
import com.mintoak.corelib.location_module.interfaces.LocationFilterCallback;
import com.mintoak.corelib.location_module.model.CidLevelModel;
import com.mintoak.corelib.location_module.model.CityLevelModel;
import com.mintoak.corelib.location_module.model.DbaLevelModel;
import com.mintoak.corelib.location_module.model.MidLevelModel;
import com.mintoak.corelib.location_module.model.TerminalLevelModel;
import com.mintoak.corelib.location_module.utils.LocationFilterUtils;
import com.mintoak.corelib.model.FilterModel;
import com.mintoak.corelib.util.AppPreferences;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import io.reactivex.Observable;

public class PerformanceGraphView extends LinearLayout {
    private static final String TAG = PerformanceGraphView.class.getSimpleName();
    private ModuleConfiguration moduleConfiguration;
    private RequestParameters requestPL;
    private String duration;

    private int cardBackgroundColor;
    private int primaryTextColor;
    private int secondaryTextColor;
    private String title;
    private String durationText;
    private float titleSize;
    private float durationTextSize;
    private Typeface primaryTextFont;
    private Typeface secondaryTextFont;

    private int lineColor1;
    private int lineColor2;
    AppPreferences mAppPreferences;
    private LocationFilterCallback locationFilterCallback_;

    private Drawable lineColor1Drawable;
    private Drawable lineColor2Drawable;

    private boolean canShowDate;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public PerformanceGraphView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        setOrientation(VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.performance_graph_view, this, true);

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomPerformanceGraph, 0, 0);

        try {
            handleAttributes(typedArray);
        } finally {
            typedArray.recycle();
        }


    }

    public Drawable getLineColor1Drawable() {
        return lineColor1Drawable;
    }

    public void setLineColor1Drawable(Drawable lineColor1Drawable) {
        this.lineColor1Drawable = lineColor1Drawable;
    }

    public Drawable getLineColor2Drawable() {
        return lineColor2Drawable;
    }

    public void setLineColor2Drawable(Drawable lineColor2Drawable) {
        this.lineColor2Drawable = lineColor2Drawable;
    }

    public int getCardBackgroundColor() {
        return cardBackgroundColor;
    }

    public void setCardBackgroundColor(int cardBackgroundColor) {
        this.cardBackgroundColor = cardBackgroundColor;
        /*if (cardBackgroundColor != 0) {
            lineChartCard.setCardBackgroundColor(cardBackgroundColor);
        }*/
    }

    public int getPrimaryTextColor() {
        if (primaryTextColor == 0) {
            primaryTextColor = Color.parseColor("#263238");
        }
        return primaryTextColor;
    }

    public void setPrimaryTextColor(int primaryTextColor) {
        this.primaryTextColor = primaryTextColor;
        /*if (primaryTextColor != 0) {
            tv_performance.setTextColor(primaryTextColor);
        }*/
    }

    public int getSecondaryTextColor() {
        return secondaryTextColor;
    }

    public void setSecondaryTextColor(int secondaryTextColor) {
        this.secondaryTextColor = secondaryTextColor;
        /*if (secondaryTextColor != 0) {
            tv_Range_Performance.setTextColor(secondaryTextColor);
            // tv_twelve_month.setTextColor(secondaryTextColor);
            tv_viewmore.setTextColor(secondaryTextColor);
            tv_txn.setTextColor(secondaryTextColor);
            tv_sale.setTextColor(secondaryTextColor);
            tv_empty_state.setTextColor(secondaryTextColor);

            iv_viewmore.getDrawable().setTint(secondaryTextColor);
            iv_twelve_month.getDrawable().setTint(secondaryTextColor);
        }*/
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        if (!TextUtils.isEmpty(title)) {
            tv_performance.setText(title);
        }
    }

    public String getDurationText() {
        return durationText;
    }

    public void setDurationText(String durationText) {
        this.durationText = durationText;
        if (!TextUtils.isEmpty(durationText)) {
            tv_Range_Performance.setText(durationText);
        }
    }

    public float getTitleSize() {
        return titleSize;
    }

    public void setTitleSize(float titleSize) {
        this.titleSize = titleSize;
        /*if (titleSize != 0) {
            tv_performance.setTextSize(titleSize);
        }*/
    }

    public float getDurationTextSize() {
        return durationTextSize;
    }

    public void setDurationTextSize(float durationTextSize) {
        this.durationTextSize = durationTextSize;
       /* if (durationTextSize != 0) {
            tv_Range_Performance.setTextSize(durationTextSize);
        }*/
    }

    public Typeface getPrimaryTextFont() {
        return primaryTextFont;
    }

    public void setPrimaryTextFont(Typeface primaryTextFont) {
        this.primaryTextFont = primaryTextFont;
       /* if (primaryTextFont != null) {
            tv_performance.setTypeface(primaryTextFont);
        }*/
    }

    public Typeface getSecondaryTextFont() {
        return secondaryTextFont;
    }

    public void setSecondaryTextFont(Typeface secondaryTextFont) {
        this.secondaryTextFont = secondaryTextFont;
        /*if (secondaryTextFont != null) {
            tv_Range_Performance.setTypeface(secondaryTextFont);
            tv_viewmore.setTypeface(secondaryTextFont);
            tv_twelve_month.setTypeface(secondaryTextFont);
            tv_sale.setTypeface(secondaryTextFont);
            tv_txn.setTypeface(secondaryTextFont);
        }*/
    }

    private LineChart linechart;
    private ImageView saleIv;
    private ImageView txnIv;
    private boolean isTwelveMonthClicked = false;
    private String emptyStateText;
    private Drawable emptyStateImage;

    public void setEmptyStateText(String text) {
        this.emptyStateText = text;
        if (!TextUtils.isEmpty(text))
            tv_empty_state.setText(text);
    }

    public void setEmptyStateImage(Drawable emptyStateImage) {
        this.emptyStateImage = emptyStateImage;
        if (emptyStateImage != null)
            //iv_empty_state.setImageDrawable(emptyStateImage);
        Glide.with(getContext()).load(emptyStateImage).into(iv_empty_state);
    }

    private TextView tv_performance;
    private TextView tv_Range_Performance;
    private TextView tv_viewmore;
    private TextView tv_twelve_month;
    private TextView tv_txn;
    private TextView tv_sale;
    private LinearLayout LLViewAllLineGraphs;
    private LinearLayout layout_no_performancedata;
    private LinearLayout layout_performancedata;
    private ImageView iv_viewmore;
    private ImageView iv_twelve_month;
    private CardView cv_yearly_performance;
    private CardView lineChartCard;
    private ShimmerFrameLayout shimmerPerformanceLl;
    private TextView tv_empty_state;
    private ImageView iv_empty_state;

    public int getLineColor1() {
        return lineColor1;
    }

    public void setLineColor1(int lineColor1) {
        this.lineColor1 = lineColor1;
        if (lineColor1 != 0) {
            /*saleIv.setColorFilter(ContextCompat.getColor(getContext(),
                    lineColor1), PorterDuff.Mode.SRC);*/
            saleIv.getDrawable().setTint(lineColor1);
        }

    }

    private float getRawSizeValue(TypedArray a, int index, float defValue) {
        TypedValue outValue = new TypedValue();
        boolean result = a.getValue(index, outValue);
        if (!result) {
            return defValue;
        }
        return TypedValue.complexToFloat(outValue.data);
    }

    public int getLineColor2() {
        return lineColor2;
    }

    public void setLineColor2(int lineColor2) {
        this.lineColor2 = lineColor2;
        if (lineColor2 != 0) {
            /*txnIv.setColorFilter(ContextCompat.getColor(getContext(),
                    lineColor2), PorterDuff.Mode.SRC);*/
            txnIv.getDrawable().setTint(lineColor2);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void handleAttributes(TypedArray typedArray) {
        cardBackgroundColor = typedArray.getColor(R.styleable.CustomPerformanceGraph_cardBackgroundColor, ContextCompat.getColor(getContext(),R.color.card_background));
        title = typedArray.getString(R.styleable.CustomPerformanceGraph_title);
        titleSize = getRawSizeValue(typedArray, R.styleable.CustomPerformanceGraph_titleSize, 18);

        if (Build.VERSION.SDK_INT >= 26) {
            primaryTextFont = typedArray.getFont(R.styleable.CustomPerformanceGraph_primaryTextFont);
        } else {
            if (typedArray.hasValue(R.styleable.CustomPerformanceGraph_primaryTextFont)) {
                int fontId = typedArray.getResourceId(R.styleable.CustomPerformanceGraph_primaryTextFont, -1);
                primaryTextFont = ResourcesCompat.getFont(getContext(), fontId);
            }

        }
        durationText = typedArray.getString(R.styleable.CustomPerformanceGraph_durationText);
        durationTextSize = getRawSizeValue(typedArray, R.styleable.CustomPerformanceGraph_durationTextSize, 12);

        if (Build.VERSION.SDK_INT >= 26) {
            secondaryTextFont = typedArray.getFont(R.styleable.CustomPerformanceGraph_secondaryTextFont);
        } else {
            if (typedArray.hasValue(R.styleable.CustomPerformanceGraph_secondaryTextFont)) {
                int fontId = typedArray.getResourceId(R.styleable.CustomPerformanceGraph_secondaryTextFont, -1);
                secondaryTextFont = ResourcesCompat.getFont(getContext(), fontId);
            }

        }
        primaryTextColor = typedArray.getColor(R.styleable.CustomPerformanceGraph_primaryTextColor, ContextCompat.getColor(getContext(), R.color.title_label));
        secondaryTextColor = typedArray.getColor(R.styleable.CustomPerformanceGraph_secondaryTextColor, ContextCompat.getColor(getContext(), R.color.grayText2));

        lineColor1 = typedArray.getColor(R.styleable.CustomPerformanceGraph_lineColor1, ContextCompat.getColor(getContext(), R.color.graphline_color1));
        lineColor2 = typedArray.getColor(R.styleable.CustomPerformanceGraph_lineColor2, ContextCompat.getColor(getContext(), R.color.graphline_color2));
        lineColor1Drawable = typedArray.getDrawable(R.styleable.CustomPerformanceGraph_lineColor1Drawable);
        lineColor2Drawable = typedArray.getDrawable(R.styleable.CustomPerformanceGraph_lineColor2Drawable);

        canShowDate = typedArray.getBoolean(R.styleable.CustomPerformanceGraph_canShowDate, false);

        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public PerformanceGraphView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.performance_graph_view, this, true);

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomPerformanceGraph, 0, 0);

        try {
            handleAttributes(typedArray);
        } finally {
            typedArray.recycle();
        }
    }

    public SessionExpiredListener mSessionExpiredListener;

    private void setTheArrowTint(ImageView iv, boolean b) {
        Drawable drawable = iv.getDrawable();
        drawable = DrawableCompat.wrap(drawable);
        if (b) {
            DrawableCompat.setTint(drawable, ContextCompat.getColor(getContext(),R.color.performance_duration_text_color));
            //iv.setImageDrawable(drawable);
            Glide.with(getContext()).load(drawable).into(iv);
        } else {
            DrawableCompat.setTint(drawable, ContextCompat.getColor(getContext(), R.color.subtitle_label));
            //iv.setImageDrawable(drawable);
            Glide.with(getContext()).load(drawable).into(iv);
        }

    }


    public void startShimmer() {
        shimmerPerformanceLl.startShimmer();
        shimmerPerformanceLl.setVisibility(VISIBLE);
        lineChartCard.setVisibility(GONE);
    }

    public void stopShimmer() {
        shimmerPerformanceLl.stopShimmer();
        shimmerPerformanceLl.setVisibility(GONE);
        lineChartCard.setVisibility(VISIBLE);
    }

    String startDate, endDate;

    public ArrayList<LineChartModel> lineChartModels = new ArrayList<>();

    public static boolean isValidDate(String inDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyy-MM-dd", Locale.ENGLISH);
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(inDate.trim());
        } catch (ParseException pe) {
            return false;
        }
        return true;
    }

    public static List<Date> getDaysBetweenDates(String str_date, String end_date) {
        List<Date> dates = new ArrayList<Date>();


        DateFormat formatter;

        formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date startDate = null;
        Date endDate = null;
        try {
            startDate = formatter.parse(str_date);
            endDate = formatter.parse(end_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long interval = 24 * 1000 * 60 * 60; // 1 hour in millis
        long endTime = endDate.getTime(); // create your endtime here, possibly using Calendar or Date
        long curTime = startDate.getTime();
        while (curTime <= endTime) {
            dates.add(new Date(curTime));
            curTime += interval;
        }
        for (int i = 0; i < dates.size(); i++) {
            Date lDate = dates.get(i);
            String ds = formatter.format(lDate);
            System.out.println(" Date is ..." + ds);
        }
        return dates;
    }

    private void setOnAllLocationCard(@NotNull List<PerformanceListItem> performanceListNew, String TAG, String selectedDayType, final String startDate, final String endDate) {
        try {
            double transactionAmount = 0D;
            int transactionCount = 0;
            List<SummaryListItem> summaryListItems = new ArrayList<>();
            List<PerformanceItem> performanceItems = new ArrayList<>();
            ArrayList<String> listDurations = new ArrayList<>();
            for (int i = 0; i < performanceListNew.size(); i++) {
                PerformanceListItem item = performanceListNew.get(i);
                if (item.getSummaryList() != null && item.getSummaryList().size() > 0) {
                    for (int j = 0; j < item.getSummaryList().size(); j++) {
                        SummaryListItem item1 = item.getSummaryList().get(j);
                        summaryListItems.add(item1);
                        listDurations.add(item1.getDuration());
                    }
                }
            }

            LinkedHashSet<String> hashSet = new LinkedHashSet<>(listDurations);

            ArrayList<String> listDurationsFinal = new ArrayList<>(hashSet);
            Collections.sort(listDurationsFinal, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    if (startDate.equals(endDate)) {
                        String[] separated1 = o1.split("-");
                        String[] separated2 = o2.split("-");

                        String compare1 = separated1[0].trim().length() == 2 ? separated1[0].trim() : "0" + separated1[0].trim();
                        String compare2 = separated2[0].trim().length() == 2 ? separated2[0].trim() : "0" + separated2[0].trim();
                        return compare1.compareTo(compare2);
                    } else {
                        return o1.compareTo(o2);
                    }
                }
            });

            for (int i = 0; i < listDurationsFinal.size(); i++) {
                LoggerUtils.E("PerformanceGraphDebug", " durations:" + listDurationsFinal.get(i));
                double totalamount = 0D;
                int totaltransaction = 0;
                String duration = listDurationsFinal.get(i);
                for (int j = 0; j < summaryListItems.size(); j++) {
                    SummaryListItem item = summaryListItems.get(j);
                    if (item.getDuration().equals(duration)) {
                        totalamount = totalamount + Double.parseDouble(item.getTotalamount());
                        totaltransaction = totaltransaction + Integer.parseInt(item.getTotaltransaction());
                    }
                }
                if (totaltransaction > 0) {
                    PerformanceItem performanceItem = new PerformanceItem();
                    performanceItem.setDuration(duration);
                    performanceItem.setTotalamount(totalamount + "");
                    performanceItem.setTotaltransaction(totaltransaction + "");
                    performanceItems.add(performanceItem);
                }
            }


            if (performanceItems.size() > 0) {
                for (int i = 0; i < performanceItems.size(); i++) {
                    PerformanceItem item = performanceItems.get(i);
                    transactionAmount = transactionAmount + Double.parseDouble(item.getTotalamount());
                    transactionCount = transactionCount + Integer.parseInt(item.getTotaltransaction());
                }

                setPerformanceGraph(performanceItems, "PerformanceGraphDebug", selectedDayType, startDate, endDate);
            }
        } catch (Exception e) {
            LoggerUtils.E("PerformanceGraphDebug", e.toString());
        }
    }

    private void setPerformanceGraph(@NotNull List<PerformanceItem> performance, String TAG, String selectedDayType, String startDate, String endDate) {
        TAG = "PERFORMANCE";
        LoggerUtils.E("refreshContent", "setPerformanceGraph:selectedDayType:" + selectedDayType);
        //2019-11-01
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd", Locale.ENGLISH);
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        SimpleDateFormat sdf3 = new SimpleDateFormat("HH", Locale.ENGLISH);
        SimpleDateFormat sdf4 = new SimpleDateFormat("yyyy-MM", Locale.ENGLISH);
        SimpleDateFormat sdf5 = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);
        LoggerUtils.E(TAG, "setPerformanceGraph:size():" + performance.size());

        List<PerformanceItem> performanceNew = new ArrayList<>();
        List<PerformanceItem> performanceAll = new ArrayList<>();


        performanceAll.clear();

        if (selectedDayType.equalsIgnoreCase("yearly")) {

            try {
                String cMonthStr = sdf4.format(new Date());
                LoggerUtils.E(TAG, "cMonthStr:" + cMonthStr);

                Calendar cal = Calendar.getInstance();

                String cendStr = "";
                for (int i = 0; i < 13; i++) {
                    cal.add(Calendar.MONTH, (-1));
                    cendStr = sdf4.format(cal.getTimeInMillis());
                }
                LoggerUtils.E(TAG, "EndMonthStr:" + cendStr);

                Date cEndMonth = sdf4.parse(cendStr);
                Calendar cend = Calendar.getInstance();
                cend.setTime(cEndMonth);
                LoggerUtils.E(TAG, "EndMonthStr:" + sdf4.format(cend.getTimeInMillis()));

                ArrayList<Long> cals = new ArrayList<>();
                for (int i = 0; i < 12; i++) {
                    cend.add(Calendar.MONTH, (+1));
                    cals.add(cend.getTimeInMillis());
                    LoggerUtils.E(TAG, "Months:" + sdf4.format(cend.getTimeInMillis()));
                }


                for (int i = 0; i < cals.size(); i++) {
                    PerformanceItem item = new PerformanceItem();

                    item.setDuration(sdf4.format(cals.get(i)));
                    item.setTotalamount("0.0");
                    item.setTotaltransaction("0");
                    performanceAll.add(item);
                }

                for (int i = 0; i < performanceAll.size(); i++) {
                    for (int j = 0; j < performance.size(); j++) {
                        if (performanceAll.get(i).getDuration().equalsIgnoreCase(performance.get(j).getDuration())) {
                            LoggerUtils.E(TAG, "MATCHING:" + performanceAll.get(i).getDuration() + " | " + performance.get(j).getDuration());
                            performanceAll.get(i).setDuration(performance.get(j).getDuration());
                            performanceAll.get(i).setTotalamount(performance.get(j).getTotalamount());
                            performanceAll.get(i).setTotaltransaction(performance.get(j).getTotaltransaction());
                        }
                    }
                }

                Collections.sort(performanceAll, new Comparator<PerformanceItem>() {
                    @Override
                    public int compare(PerformanceItem o1, PerformanceItem o2) {
                        return o1.getDuration().compareTo(o2.getDuration());
                    }
                });

            } catch (Exception e) {
                LoggerUtils.E(TAG, "Exception 12:" + e.toString());
            }
        } else {
            if (startDate.equals(endDate)) {
                selectedDayType = "day";

                for (int i = 0; i < performance.size(); i++) {
                    int c = 0, d = 0;
                    int ipos = performance.get(i).getDuration().indexOf("-");
                    if (ipos != -1) {
                        c = Integer.parseInt(performance.get(i).getDuration().substring(0, ipos));
                    }

                    d = c + 1;
                    if (d == 24) {
                        d = 0;
                    }

                    String s1 = "0", s2 = "0";
                    if (c < 10) {
                        s1 = "0";
                    } else {
                        s1 = "";
                    }
                    if (d < 10) {
                        s2 = "0";
                    } else {
                        s2 = "";
                    }

                    performance.get(i).setDuration(s1 + c + "-" + s2 + d);
                }


                PerformanceItem item1 = new PerformanceItem();
                item1.setDuration("00-01");
                item1.setTotalamount("0.0");
                item1.setTotaltransaction("0");
                performanceNew.add(item1);
                performanceNew.addAll(performance);


                int c = 0, d = 0;
                int ipos = performanceNew.get(0).getDuration().indexOf("-");
                if (ipos != -1) {
                    c = Integer.parseInt(performanceNew.get(0).getDuration().substring(0, ipos));
                }
                int ipos1 = performanceNew.get(performanceNew.size() - 1).getDuration().indexOf("-");
                if (ipos1 != -1) {
                    d = Integer.parseInt(performanceNew.get(performanceNew.size() - 1).getDuration().substring(0, ipos));
                }

                LoggerUtils.E(TAG, "performance.size():" + performance.size());
                LoggerUtils.E(TAG, "performanceNew.size():" + performanceAll.size());
                LoggerUtils.E(TAG, "c:" + c);
                LoggerUtils.E(TAG, "d:" + d);
                for (int i = c; i <= d; i++) {
                    PerformanceItem item = new PerformanceItem();
                    int a = 0;
                    int b = 0;
                    a = i;
                    b = i + 1;
                    String s1 = "0", s2 = "0";
                    if (a < 10) {
                        s1 = "0";
                    } else {
                        s1 = "";
                    }
                    if (b < 10) {
                        s2 = "0";
                    } else {
                        s2 = "";
                    }

                    item.setDuration(s1 + a + "-" + s2 + b);
                    //item.setDuration(a + "-" + b);
                    item.setTotalamount("0.0");
                    item.setTotaltransaction("0");
                    performanceAll.add(item);
                }

                for (int i = 0; i < performanceAll.size(); i++) {
                    for (int j = 0; j < performance.size(); j++) {
                        if (performanceAll.get(i).getDuration().equalsIgnoreCase(performance.get(j).getDuration())) {
                            LoggerUtils.E(TAG, "MATCHING:" + performanceAll.get(i).getDuration() + " | " + performance.get(j).getDuration());
                            performanceAll.get(i).setDuration(performance.get(j).getDuration());
                            performanceAll.get(i).setTotalamount(performance.get(j).getTotalamount());
                            performanceAll.get(i).setTotaltransaction(performance.get(j).getTotaltransaction());
                        }
                    }
                }
                Collections.sort(performanceAll, new Comparator<PerformanceItem>() {
                    @Override
                    public int compare(PerformanceItem o1, PerformanceItem o2) {
                        return o1.getDuration().compareTo(o2.getDuration());
                    }
                });
            } else {
                selectedDayType = "month";
                DateFormat formatter;
                formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                List<Date> dateList = getDaysBetweenDates(startDate, endDate);
                ArrayList<String> dateToBeAdded = new ArrayList<>();
                for (int i = 0; i < dateList.size(); i++) {
                    Date lDate = dateList.get(i);
                    String ds = formatter.format(lDate);
                    boolean present = false;
                    for (int j = 0; j < performance.size(); j++) {
                        present = ds.equals(performance.get(j).getDuration());
                        if (present) {
                            break;
                        }
                    }
                    if (!present) {
                        dateToBeAdded.add(ds);
                    }
                }
                Set<String> stringsD = new LinkedHashSet<>(dateToBeAdded);
                dateToBeAdded.clear();
                dateToBeAdded.addAll(stringsD);
                for (int i = 0; i < dateToBeAdded.size(); i++) {
                    PerformanceItem item = new PerformanceItem();
                    item.setDuration(dateToBeAdded.get(i));
                    item.setTotalamount("0.0");
                    item.setTotaltransaction("0");
                    performanceAll.add(item);
                }
                performanceAll.addAll(performance);
                Set<PerformanceItem> perf = new LinkedHashSet<>(performanceAll);
                performanceAll.clear();
                performanceAll.addAll(perf);
                Collections.sort(performanceAll, new Comparator<PerformanceItem>() {
                    @Override
                    public int compare(PerformanceItem o1, PerformanceItem o2) {
                        return o1.getDuration().compareTo(o2.getDuration());
                    }
                });
            }
        }

        LoggerUtils.E(TAG, "performanceAll.size():" + performanceAll.size());

        lineChartModels.clear();
        LoggerUtils.E(TAG, "item.getDuration():" + selectedDayType);

        for (int i = 0; i < performanceAll.size(); i++) {
            PerformanceItem item = performanceAll.get(i);
            LineChartModel model = new LineChartModel();
            LoggerUtils.E(TAG, "item.getDuration():" + item.getDuration());
            LoggerUtils.E(TAG, "item.getTotalamount():" + item.getTotalamount());
            LoggerUtils.E(TAG, "item.getTotaltransaction():" + item.getTotaltransaction());
            if (item.getDuration() != null) {
                try {
                    //selectedDayType = "week";
                    if (selectedDayType.equalsIgnoreCase("yearly")) {
                        SimpleDateFormat format = new SimpleDateFormat("MMM yy", Locale.ENGLISH);
                        String date = format.format(sdf4.parse(item.getDuration()));
                        model.setxAxisValue(date);

                    } else {

                        if (startDate.equals(endDate)) {
                            int ipos = item.getDuration().indexOf("-");
                            String date1 = "NA";
                            if (ipos != -1) {
                                date1 = item.getDuration().substring(0, ipos);
                            }
                            /*SimpleDateFormat sdfTime1 = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
                            SimpleDateFormat sdfTime2 = new SimpleDateFormat("hh aa", Locale.ENGLISH);
                            String time1 = sdfTime2.format(sdfTime1.parse(date1 + ":00"));
                            LoggerUtils.E("refreshContent", "refreshContent:" + time1);*/

                            model.setxAxisValue(date1);
                        } else {
                            String date = getPerformanceDurationText(item.getDuration());
                            model.setxAxisValue(date);
                        }
                    }
                } catch (Exception e) {
                    LoggerUtils.E(TAG, "Exception:" + e.toString());
                    model.setxAxisValue(item.getDuration());
                }
                model.setDuration(item.getDuration());
            } else {
                model.setxAxisValue("NA");
                model.setDuration("NA");
            }

            model.setTotalTransaction(item.getTotaltransaction());
            model.setTotalAmount(item.getTotalamount());

            LoggerUtils.E(TAG, "model.getTotalTransaction():" + model.getTotalTransaction());
            LoggerUtils.E(TAG, "model.getTotalAmount():" + model.getTotalAmount());
            LoggerUtils.E(TAG, "model.getDuration():" + model.getDuration());
            LoggerUtils.E(TAG, "model.getxAxisValue():" + model.getxAxisValue());

            lineChartModels.add(model);

        }

       /* boolean isAllDate = false;
        for (int i = 0; i < lineChartModels.size(); i++) {
            isAllDate = isValidDate(lineChartModels.get(i).getDuration());
        }
        if (isAllDate) {
            LineGraphMethod("", selectedDayType);
        } else {
            LineGraphMethod("", "day");
        }*/

        LineGraphMethod("", selectedDayType);

    }
    public void setSaleCardLocationApplyCallback(LocationDataToAppCallback locationDataToAppCallback) {
        LocationFilterUI.mLocationDataToAppCallback = locationDataToAppCallback;
    }

    public void setSaleCardCalenderApplyCallback(CalenderDataToAppCallback calDataToAppCallback) {
        CalenderDateUI.calenderDataToAppCallback = calDataToAppCallback;
    }
    public void setMonthViewApplyCallback(TwelveMonthViewCallBack mviewAppCallback) {
        MonthViewUI.monthViewToAppCallback = mviewAppCallback;
    }

    public void setApiSessionExpiredListener(SessionExpiredListener sessionExpiredListener) {
        PerformanceCardUtils.mPerCardApiSessionExpired = sessionExpiredListener;
    }
    private String getPerformanceDurationText(String duration) {
        String durationtext = "";
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd", Locale.ENGLISH);
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        SimpleDateFormat sdf3 = new SimpleDateFormat("HH", Locale.ENGLISH);
        SimpleDateFormat sdf4 = new SimpleDateFormat("yyyy-MM", Locale.ENGLISH);
        SimpleDateFormat sdf5 = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);

        try {
            durationtext = sdf5.format(Objects.requireNonNull(sdf2.parse(duration)));
        } catch (Exception e) {
            durationtext = duration;
        }

        return durationtext;
    }

    ArrayList<Entry> valuesAmt = new ArrayList<Entry>();
    ArrayList<Entry> valuesCount = new ArrayList<Entry>();

    private void init() {

        mAppPreferences = new AppPreferences();
        moduleConfiguration = ModuleConfiguration.getInstance();
        linechart = findViewById(R.id.linechart);
        iv_empty_state = findViewById(R.id.iv_empty_state);
        tv_empty_state = findViewById(R.id.tv_empty_state);
        shimmerPerformanceLl = findViewById(R.id.shimmerPerformanceLl);
        lineChartCard = findViewById(R.id.lineChartCard);
        tv_txn = findViewById(R.id.tv_txn);
        tv_sale = findViewById(R.id.tv_sale);
        layout_performancedata = findViewById(R.id.layout_performancedata);
        layout_no_performancedata = findViewById(R.id.layout_no_performancedata);
        iv_twelve_month = findViewById(R.id.iv_twelve_month);
        tv_twelve_month = findViewById(R.id.tv_twelve_month);
        cv_yearly_performance = findViewById(R.id.cv_yearly_performance);
        iv_viewmore = findViewById(R.id.iv_viewmore);
        tv_viewmore = findViewById(R.id.tv_viewmore);
        LLViewAllLineGraphs = findViewById(R.id.LLViewAllLineGraphs);
        tv_Range_Performance = findViewById(R.id.tv_Range_Performance);
        tv_performance = findViewById(R.id.tv_performance);
        saleIv = findViewById(R.id.saleIv);
        txnIv = findViewById(R.id.txnIv);
        //iv_empty_state.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.no_performancegraph1));
        //Glide.with(getContext()).load(R.drawable.no_performancegraph1).into(iv_empty_state);


        setCardBackgroundColor(cardBackgroundColor);
        setTitle(title);
        setTitleSize(titleSize);
        setPrimaryTextFont(primaryTextFont);
        setDurationText(durationText);
        setDurationTextSize(durationTextSize);
        setSecondaryTextFont(secondaryTextFont);
        setPrimaryTextColor(primaryTextColor);
        setSecondaryTextColor(secondaryTextColor);
        setLineColor1(lineColor1);
        setLineColor2(lineColor2);

        canShowDate(canShowDate);

        LLViewAllLineGraphs.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.avoidDoubleClicks(view);

//                Bundle bundle1 = new Bundle();
//                bundle1.putString("section", "Performance");
//                ApiNetworkUtils.captureEvent("bizview-viewmore-link-clicked", "Bizview", "bizview", "viewmore", "link", "clicked", bundle1, getContext());


                if (Util.isNetworkConnected(getContext())) {

                    JSONObject jsonObject = new JSONObject();
                    FilterModel filterModel = ModuleConfiguration.getInstance().getFilterModel();
                    if (filterModel != null) {
                        if (filterModel.getTYPE() == Type.TYPE_CORP) {
                            try {
                                //jsonObject.put("corpId", filterModel.getCID());
                                // jsonObject.put("duration", mAppPreferences.getPreferences(getContext(), StringConstants.DURATION));
                                jsonObject.put("startDate", startDate);
                                jsonObject.put("endDate", endDate);
                                //jsonObject.put("type", "corp");
                                jsonObject.put("id", filterModel.getCID());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            JSONArray ja;
                            ja = new JSONArray(filterModel.getCORP_MID());
                            try {
                                //jsonObject.put("midList", ja.toString());
                                jsonObject.put("startDate", startDate);
                                jsonObject.put("endDate", endDate);
                                //jsonObject.put("type", "merc");
                                jsonObject.put("id", filterModel.getCORP_MID());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        try {
                            //jsonObject.put("corpId", requestPL.getCid());
                            jsonObject.put("startDate", startDate);
                            jsonObject.put("endDate", endDate);
                            //jsonObject.put("type", "corp");
//                            jsonObject.put("id", requestPL.getCid());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
/*                        if (requestPL.getType() == Type.TYPE_CORP) {
                            try {
                                //jsonObject.put("corpId", requestPL.getCid());
                                jsonObject.put("startDate", startDate);
                                jsonObject.put("endDate", endDate);
                                //jsonObject.put("type", "corp");
                                jsonObject.put("id", requestPL.getCid());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                //jsonObject.put("midList", ja.toString());
                                jsonObject.put("startDate", startDate);
                                jsonObject.put("endDate", endDate);
                                //jsonObject.put("type", "merc");
                                jsonObject.put("id", requestPL.getMidList());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }*/
                    }
                    GraphTheme graphTheme = new GraphTheme(cardBackgroundColor, primaryTextColor, secondaryTextColor,
                            primaryTextFont, secondaryTextFont, lineColor1, lineColor2, emptyStateText, emptyStateImage);
                    Intent intent = new Intent(getContext(), ActivityLineGraphList.class);
                    intent.putExtra("GraphTheme", graphTheme);
                    intent.putExtra("data", jsonObject.toString());
                    intent.putExtra("startDate", startDate);
                    intent.putExtra("endDate", endDate);
                    intent.putExtra("isTwelveMonthClicked", isTwelveMonthClicked);
                    getContext().startActivity(intent);
                    //getContext().startActivity(new Intent(getContext(), ActivityLineGraphList.class).putExtra("GraphTheme", graphTheme));
                } else {
                    popUpDialog((Activity) getContext(), "", getResources().getString(R.string.no_internet_connection_n_please_try_again_later));
                }

            }
        });
        cv_yearly_performance.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ApiNetworkUtils.isConnected((Activity) getContext())) {
                    startShimmer();
                    if (isTwelveMonthClicked) {
//                        Bundle bundle = new Bundle();
//                        bundle.putString("Section", "Performance");
//                        ApiNetworkUtils.captureEvent("bizview-12month-button-deactivated", "Bizview", "bizview", "Bizview", "button", "deactivated", bundle, getContext());
                        setPerformanceData(requestPL, startDate, endDate);
                    } else {
                        getYearlyPerformance(startDate, endDate);
                    }
                } else {
                    popUpDialog((Activity) getContext(), "", getResources().getString(R.string.no_internet));
                }
            }
        });
        startShimmer();
    }

    public void getYearlyPerformance(String startDate, String endDate) {
//        Bundle bundle = new Bundle();
//        bundle.putString("Section", "Performance");
//        ApiNetworkUtils.captureEvent("bizview-12month-button-activated", "Bizview", "bizview", "Bizview", "button", "activated", bundle, getContext());
        isTwelveMonthClicked = true;

        this.startDate =startDate;
        this.endDate =endDate;
        LocationFilterUtils.getSelectedLocationFilter(new LocationFilterApplyCallBack() {
            @Override
            public void allSelectedData(CidLevelModel cidLevelModel, List<DbaLevelModel> dbaLevelModelList, List<CityLevelModel> cityLevelModelList, List<MidLevelModel> midLevelModelList, List<TerminalLevelModel> terminalLevelModelList) {
                ArrayList<String> terminals = new ArrayList<>();
                try {
                    terminals.clear();
                    for (int i = 0; i < midLevelModelList.size(); i++) {
                        MidLevelModel model = midLevelModelList.get(i);
                        for (int j = 0; j < model.getTerminalLevelModels().size(); j++) {
                            TerminalLevelModel model1 = model.getTerminalLevelModels().get(j);
                            terminals.add(model1.getTid());
                        }

                    }

//                    if (requestPL.getProjectType() == ProjectType.TYPE_ONE_APP_SMARTHUB) {
//                        terminals.clear();
//                        terminals.add(requestPL.getTid());
//                    }

                    JSONArray ja = new JSONArray(terminals);
                    JSONObject params = new JSONObject();
                    params.put("tidList", ja.toString());
                    params.put("serviceType", "year");

                    LoggerUtils.E("PerformanceGraphDebug", "jsonObject:" + params.toString());

                    if (ApiConstants.publicKey == null) {
                        LoggerUtils.E("PerformanceGraphDebug", "publicKey == null:");
                        ApiInterface apiInterface = getClientNew(getContext(), requestPL).create(ApiInterface.class);
                        Observable<String> call = apiInterface.callAPIURL();
                        RXJavaCaller.GetKey(call, new RXJavaCaller.OnKeyReceived() {
                            @Override
                            public void onKeyReceivedSuccess() {
                                LoggerUtils.E("PerformanceGraphDebug", "onKeyReceivedSuccess:");
                                getYearlyPerformance(startDate, endDate);
                            }

                            @Override
                            public void onKeyReceivedError(String error) {
                                LoggerUtils.E("PerformanceGraphDebug", "onKeyReceivedError:" + error);
                                stopShimmer();
                                showEmptyState();
                            }
                        });
                    } else {
                        LoggerUtils.E("PerformanceGraphDebug", "publicKey != null:");
                        JSONObject enc = Encrypter.getEncryptedJSON(params);
                        String skey = enc.optString("SKEY", "");
                        String iv = enc.optString("IV", "");
                        JSONObject jsonObject1 = Encrypter.getFinalJSON(enc);
                        LoggerUtils.E("RXJavaCaller", jsonObject1.toString());
                        com.aim.pmgraph.apiinterface.ApiInterface apiClient = getClientNew(getContext(), requestPL).create(com.aim.pmgraph.apiinterface.ApiInterface.class);
                        Observable<String> call = apiClient.getPerformanceGraphViewAllNewApi(jsonObject1.toString());
                        RXJavaCaller.executeService(call, skey, iv, false, new RXJavaCaller.OnServiceExecuted() {
                            @Override
                            public void onSuccess(String result) {
                                LoggerUtils.E("PerformanceGraphDebug", "onSuccess:" + result);
                                stopShimmer();
                                JSONObject payLaterResponse;
                                try {
                                    payLaterResponse = new JSONObject(result);
                                } catch (JSONException e) {
                                    payLaterResponse = new JSONObject();
                                }
                                GsonBuilder gsonBuilder = new GsonBuilder();
                                Gson gson = gsonBuilder.create();
                                ResPerformanceViewAll summary = gson.fromJson(payLaterResponse.toString(), ResPerformanceViewAll.class);
                                List<PerformanceListItem> performanceList = summary.getPerformanceList();
                                if (performanceList != null && performanceList.size() > 0) {
                                    Util.setVisibility(layout_no_performancedata, false);
                                    Util.setVisibility(layout_performancedata, true);
                                    LoggerUtils.E(TAG, "performance.size():>0");
                                    isTwelveMonthClicked = true;
                                    setOnAllLocationCard(performanceList, TAG, "yearly", PerformanceGraphView.this.startDate, PerformanceGraphView.this.endDate);
                                    //setPerformanceGraph(performanceList, TAG, "yearly", startDate, endDate);
                                    tv_twelve_month.setTextColor(ContextCompat.getColor(getContext(),R.color.text_color_month));
                                    iv_twelve_month.getDrawable().setTint(ContextCompat.getColor(getContext(),R.color.text_color_month));
                                    Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.montserrat_semibold);
                                    tv_twelve_month.setTypeface(typeface);
                                } else {
                                    tv_twelve_month.setTextColor(ContextCompat.getColor(getContext(),R.color.subtitle_label));
                                    iv_twelve_month.getDrawable().setTint(ContextCompat.getColor(getContext(),R.color.subtitle_label));
                                    Typeface typeface2 = ResourcesCompat.getFont(getContext(), R.font.montserrat_regular);
                                    tv_twelve_month.setTypeface(typeface2);
                                    Util.setVisibility(layout_no_performancedata, true);
                                    Util.setVisibility(layout_performancedata, false);
                                }

                                cv_yearly_performance.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.performance_duration_bg_color));
                                setTheArrowTint(iv_twelve_month, true);
                            }

                            @Override
                            public void onError(String error) {
                                stopShimmer();
                                LoggerUtils.E("PerformanceGraphDebug", "onError:" + error);
                                Util.setVisibility(layout_no_performancedata, true);
                                Util.setVisibility(layout_performancedata, false);
                            }

                            @Override
                            public void onKeyExpired() {
                                LoggerUtils.E("PerformanceGraphDebug", "onKeyExpired:");
                                getYearlyPerformance(startDate, endDate);

                            }

                            @Override
                            public void onSessionExpired() {
                                LoggerUtils.E("PerformanceGraphDebug", "onSessionExpired:");
//                                mSessionExpiredListener.onSessionExpired();
                                PerformanceCardUtils.mPerCardApiSessionExpired.onSessionExpired();
                            }
                        });
                    }


                } catch (JSONException e) {
                    Util.setVisibility(layout_no_performancedata, true);
                    Util.setVisibility(layout_performancedata, false);
                    stopShimmer();
                    e.printStackTrace();
                }
            }

            @Override
            public void onCidSelected(CidLevelModel cidLevelModel) {

            }

            @Override
            public void onDBASelected(List<DbaLevelModel> dbaLevelModelList) {

            }

            @Override
            public void onCitySelected(List<CityLevelModel> cityLevelModelList) {

            }

            @Override
            public void onMidSelected(List<MidLevelModel> midLevelModelList) {

            }

            @Override
            public void onTidSelected(List<TerminalLevelModel> terminalLevelModelList) {

            }
        });

    }

    private void showEmptyState() {
        layout_no_performancedata.setVisibility(VISIBLE);
        layout_performancedata.setVisibility(GONE);
    }

    private void hideEmptyState() {
        layout_no_performancedata.setVisibility(GONE);
        layout_performancedata.setVisibility(VISIBLE);
    }

    public void LineGraphMethod(String selected, String selectedDayType) {
        if (!lineChartModels.isEmpty()) {
            linechart.clear();
            Util.setVisibility(layout_no_performancedata, false);
            Util.setVisibility(layout_performancedata, true);
            //Util.setVisibility(cv_yearly_performance, true);
            final ArrayList<String> xAxisLabel = new ArrayList<>();
            ArrayList<String> xVals = new ArrayList<>();
            xAxisLabel.clear();
            valuesAmt.clear();
            valuesCount.clear();

            double totalCount = 0.0;
            double totalAmt = 0.0;
            for (int i = 0; i < lineChartModels.size(); i++) {
                totalCount += (Double.valueOf(lineChartModels.get(i).getTotalTransaction()));
                totalAmt += (Double.valueOf(lineChartModels.get(i).getTotalAmount()));
            }

            LoggerUtils.E("PERFORMANCE", "totalCount:" + totalCount);
            LoggerUtils.E("PERFORMANCE", "totalAmt:" + totalAmt);

            for (int i = 0; i < lineChartModels.size(); i++) {
                float amt = Float.parseFloat(lineChartModels.get(i).getTotalAmount());
                float count = Float.parseFloat(lineChartModels.get(i).getTotalTransaction());

                LoggerUtils.E("PERFORMANCE", "count:" + count);
                LoggerUtils.E("PERFORMANCE", "amt:" + amt);

                valuesAmt.add(new Entry(i, amt, ContextCompat.getDrawable(getContext(), R.drawable.round_icon1)));
                valuesCount.add(new Entry(i, count, ContextCompat.getDrawable(getContext(), R.drawable.round_icon2)));

                xAxisLabel.add("" + lineChartModels.get(i).getxAxisValue() + "");

                LoggerUtils.E("PERFORMANCE", "xAxisLabel:" + lineChartModels.get(i).getxAxisValue());
                LoggerUtils.E("PERFORMANCE", "values: X:" + valuesAmt.get(i).getX() + " Y:" + valuesAmt.get(i).getY() + "");
                LoggerUtils.E("PERFORMANCE", "values1: X:" + valuesCount.get(i).getX() + " Y:" + valuesCount.get(i).getY() + "");
            }

            linechart.setBorderWidth(5f);
            linechart.setTouchEnabled(true);
            linechart.setDrawGridBackground(false);
            linechart.getDescription().setEnabled(false);
            linechart.setDoubleTapToZoomEnabled(false);
            linechart.setExtraOffsets(10, 10, 10, 10);

            XAxis xAxis1 = linechart.getXAxis();
            xAxis1.setDrawGridLines(false);
            xAxis1.setPosition(XAxis.XAxisPosition.BOTTOM);
            if (getSecondaryTextColor() != 0) {
                xAxis1.setTextColor(getSecondaryTextColor());
            } else {
                xAxis1.setTextColor(getResources().getColor(R.color.text_color));
            }
            xAxis1.setLabelCount(lineChartModels.size());

            xAxis1.setLabelRotationAngle(-90);

            if (lineChartModels.size() > 0) {
                xAxis1.setValueFormatter(new IndexAxisValueFormatter(xAxisLabel));
            }

            xAxis1.setTypeface(Typeface.DEFAULT_BOLD);

            xAxis1.disableGridDashedLine();
            xAxis1.disableAxisLineDashedLine();

            YAxis rightAxis1 = linechart.getAxisRight();
            rightAxis1.setDrawGridLines(false);
            rightAxis1.setAxisMinimum(0f); // this replaces setStartAtZero(true)
            rightAxis1.setTextColor(Color.TRANSPARENT);
            rightAxis1.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
            rightAxis1.setZeroLineColor(Color.TRANSPARENT);
            rightAxis1.setEnabled(false);


            YAxis leftAxis1 = linechart.getAxisLeft();
            leftAxis1.setDrawGridLines(true);
            leftAxis1.setAxisMinimum(0f); // this replaces setStartAtZero(true)
            leftAxis1.setSpaceTop(50f);
            leftAxis1.setEnabled(true);
            leftAxis1.setDrawAxisLine(false);
            leftAxis1.setAxisLineColor(Color.TRANSPARENT);
            leftAxis1.setGridLineWidth(0.5f);
            leftAxis1.setGridColor(Color.parseColor("#ECECEC"));
            LoggerUtils.E("LineGraphMethod: ", " : " + leftAxis1.getGridLineWidth());
            leftAxis1.setDrawLabels(false);
            leftAxis1.setValueFormatter(new MyValueFormatter(1200000f, leftAxis1));
            leftAxis1.setAxisMinimum(0f); // this replaces setStartAtZero(true)
            leftAxis1.setTextColor(getResources().getColor(R.color.textwhite));
            Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.montserrat_semibold);
            leftAxis1.setTypeface(typeface);

            LineDataSet lineDataSetAmt = new LineDataSet(valuesAmt, "");
            LineDataSet lineDataSetCount = new LineDataSet(valuesCount, "");


//            Gson gson = new GsonBuilder().create();
//            String myCustomArray = gson.toJson(lineDataSetAmt);
//            LoggerUtils.E("strValuesAmt",""+myCustomArray.toString());
//
//            String myCustomArrayvaluesCount = gson.toJson(lineDataSetCount);
//            LoggerUtils.E("strmyCustomArrayvaluesCount",""+myCustomArrayvaluesCount.toString());

            lineDataSetAmt.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
            lineDataSetCount.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
            lineDataSetAmt.setCircleColor(Color.parseColor("#F6F6F6"));
            lineDataSetAmt.setCircleHoleColor(ContextCompat.getColor(getContext(), R.color.graphline_color1));
            lineDataSetAmt.setCircleHoleRadius(4f);

            lineDataSetAmt.disableDashedLine();
            lineDataSetCount.disableDashedLine();

            lineDataSetAmt.setCircleRadius(7f);
            lineDataSetAmt.setColor(getLineColor1());
            lineDataSetAmt.setLineWidth(2f);
            lineDataSetAmt.setDrawValues(false);
            lineDataSetAmt.setDrawHighlightIndicators(true);
            lineDataSetAmt.setDrawFilled(true);
            lineDataSetAmt.setHighLightColor(getResources().getColor(R.color.graphline1_2));
            lineDataSetAmt.setDrawCircles(true);
            lineDataSetAmt.setDrawHorizontalHighlightIndicator(false);
            lineDataSetAmt.setDrawIcons(false);
            lineDataSetAmt.setAxisDependency(YAxis.AxisDependency.LEFT);

            lineDataSetCount.setCircleRadius(7f);
            lineDataSetCount.setColor(getLineColor2());
            lineDataSetCount.setLineWidth(2f);
            lineDataSetCount.setDrawValues(false);
            lineDataSetCount.setDrawHighlightIndicators(true);
            lineDataSetCount.setDrawFilled(true);
            lineDataSetCount.setHighLightColor(getContext().getResources().getColor(R.color.graphline2_2));
            lineDataSetCount.setDrawCircles(true);
            lineDataSetCount.setDrawHorizontalHighlightIndicator(false);
            lineDataSetCount.setDrawIcons(false);
            lineDataSetCount.setAxisDependency(YAxis.AxisDependency.RIGHT);

            lineDataSetCount.setCircleColor(Color.parseColor("#F6F6F6"));
            lineDataSetCount.setCircleHoleColor(ContextCompat.getColor(getContext(), R.color.graphline_color2));
            lineDataSetCount.setCircleHoleRadius(4f);

            if (com.github.mikephil.charting.utils.Utils.getSDKInt() >= 18) {
                // fill drawable only supported on api level 18 and above
                //Drawable drawable1 = /*ContextCompat.getDrawable(getContext(), R.drawable.fade_drawable1);*/ getLineColor1Drawable();
                //Drawable drawable2 = /*ContextCompat.getDrawable(getContext(), R.drawable.fade_drawable2);*/ getLineColor2Drawable();
                Drawable drawable1 = ContextCompat.getDrawable(getContext(), R.drawable.fade_drawable1);
                Drawable drawable2 = ContextCompat.getDrawable(getContext(), R.drawable.fade_drawable2);

                lineDataSetAmt.setFillDrawable(drawable1);
                lineDataSetCount.setFillDrawable(drawable2);
            } else {
                lineDataSetAmt.setFillColor(Color.BLACK);
                lineDataSetCount.setFillColor(Color.BLACK);
            }
            linechart.setPinchZoom(true);
            List<ILineDataSet> dataSetList = new ArrayList<>();
            dataSetList.clear();
            dataSetList.add(lineDataSetCount);
            dataSetList.add(lineDataSetAmt);
            LineData lineData = new LineData(dataSetList);
            lineData.setValueTextColor(getResources().getColor(R.color.textwhite));
            lineData.setValueTextSize(10f);
            lineData.setValueTextColor(getResources().getColor(R.color.green));
            linechart.setDrawMarkers(true);

            CustomMarkerView mv = new CustomMarkerView(getContext(), R.layout.item_line_graph_marker, selected, selectedDayType);
            mv.setChartView(linechart);
            linechart.getLegend().setTextColor(getResources().getColor(R.color.text_color));
            linechart.getLegend().setEnabled(false);
            linechart.setMarker(mv);
            linechart.getXAxis().setDrawAxisLine(true);
            linechart.getAxisLeft().setDrawAxisLine(true);
            linechart.getAxisRight().setDrawAxisLine(false);
            linechart.getXAxis().setDrawGridLines(false);
            linechart.setData(lineData);
            linechart.fitScreen();

            linechart.invalidate(); // refresh

        } else {
            Util.setVisibility(layout_performancedata, false);
            Util.setVisibility(cv_yearly_performance, false);
        }

        //NewMethodHere
        linechart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                /*List<Entry> data1 = linechart.getData().getDataSets().get(0).getEntriesForXValue(e.getX());
                ILineDataSet data2 = linechart.getData().getDataSets().get(1);*/
                int line1 = linechart.getData().getDataSets().get(0).getEntryCount();
                for (int i = 0; i < line1; i++) {
                    if ((int) e.getX() == i) {
                        linechart.getData().getDataSets().get(0).getEntriesForXValue(i).get(0).setIcon(ContextCompat.getDrawable(getContext(), R.drawable.green_round_icon_fix));
                        linechart.getData().getDataSets().get(1).getEntriesForXValue(i).get(0).setIcon(ContextCompat.getDrawable(getContext(), R.drawable.blue_round_icon_fix));
                    } else {
                        linechart.getData().getDataSets().get(0).getEntriesForXValue(i).get(0).setIcon(ContextCompat.getDrawable(getContext(), R.drawable.round_icon1));
                        linechart.getData().getDataSets().get(1).getEntriesForXValue(i).get(0).setIcon(ContextCompat.getDrawable(getContext(), R.drawable.round_icon2));
                    }
                }
                linechart.notifyDataSetChanged();
                linechart.invalidate();
                //int line2 = linechart.getData().getDataSets().get(1).getEntryCount();
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }

    public class CustomMarkerView extends MarkerView {

        String selected;
        String selectedDayType;
        LinearLayout LL_marker;
        ImageView rupeeIcon, iv_markerAmt, iv_rupeeAmt, iv_markerCount;
        private final TextView tvContent;
        private final TextView tvContent11;
        private final TextView txt_total_transaction;
        private final TextView txt_date_time;

        public CustomMarkerView(Context context, int layoutResource, String selected, String selectedDayType) {
            super(context, layoutResource);
            // this markerview only displays a textview
            this.selected = selected;
            this.selectedDayType = selectedDayType;
            tvContent = findViewById(R.id.tvContent);
            tvContent11 = findViewById(R.id.tvContent1);
            LL_marker = findViewById(R.id.LL_marker);
            iv_markerCount = findViewById(R.id.iv_markerCount);
            rupeeIcon = findViewById(R.id.imageView);
            iv_markerAmt = findViewById(R.id.iv_markerAmt);
            iv_rupeeAmt = findViewById(R.id.iv_rupeeAmt);
            txt_total_transaction = findViewById(R.id.txt_total_transaction);
            txt_date_time = findViewById(R.id.txt_date_time);
            if (getSecondaryTextColor() != 0) {
                txt_date_time.setTextColor(getSecondaryTextColor());
                txt_total_transaction.setTextColor(getSecondaryTextColor());
            }
            if (getPrimaryTextColor() != 0) {
                tvContent.setTextColor(getPrimaryTextColor());
                tvContent11.setTextColor(getPrimaryTextColor());
                iv_rupeeAmt.getDrawable().setTint(getPrimaryTextColor());
            }

            if (getPrimaryTextFont() != null) {
                tvContent.setTypeface(getPrimaryTextFont());
                tvContent11.setTypeface(getPrimaryTextFont());
            }

            if (getSecondaryTextFont() != null) {
                txt_date_time.setTypeface(getSecondaryTextFont());
                txt_total_transaction.setTypeface(getSecondaryTextFont());
            }

            if (getLineColor1() != -0) {
                iv_markerAmt.getDrawable().setTint(getLineColor1());
            }
            if (getLineColor2() != -0) {
                iv_markerCount.getDrawable().setTint(getLineColor2());
            }

            if (getCardBackgroundColor() != 0) {
                LL_marker.getBackground().setTint(getCardBackgroundColor());
            }
        }

        // callbacks everytime the MarkerView is redrawn, can be used to update the
        // content (user-interface)
        @Override
        public void refreshContent(Entry e, Highlight highlight) {
//            LoggerUtils.E("Refresh", e.getY() + "");  formatK(e.getY())

            String stotal = String.valueOf(e.getY());
            stotal = lineChartModels.get((int) e.getX()).getTotalAmount();
            LoggerUtils.E("refreshContent", "stotal:" + stotal);
            LoggerUtils.E("refreshContent", "selectedDayType:" + selectedDayType);
            String[] amt = getAmounts(stotal);
            String void1 = convertToDefaultCurrencyFormatNew(amt[0]);
            String void2 = amt[1];


            try {
                if (!TextUtils.isEmpty(selectedDayType) && selectedDayType.equalsIgnoreCase("day")) {
                    int ipos = lineChartModels.get((int) e.getX()).getDuration().indexOf("-");
                    String date = "NA";
                    if (ipos != -1) {
                        date = lineChartModels.get((int) e.getX()).getDuration().substring(0, ipos);
                    }
                    SimpleDateFormat sdf = new SimpleDateFormat("HH", Locale.ENGLISH);
                    SimpleDateFormat sdf2 = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);
                    String time1 = sdf2.format(sdf.parse(date + ":00"));
                    txt_date_time.setText(time1);
                    LoggerUtils.E("refreshContent", "refreshContent:" + time1);
                } else if (!TextUtils.isEmpty(selectedDayType) && (selectedDayType.equalsIgnoreCase("week") || selectedDayType.equalsIgnoreCase("month"))) {
                    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                    SimpleDateFormat sdf2 = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
                    String time1 = sdf2.format(sdf1.parse(lineChartModels.get((int) e.getX()).getDuration()));
                    txt_date_time.setText(time1);
                } else if (!TextUtils.isEmpty(selectedDayType) && selectedDayType.equalsIgnoreCase("yearly")) {
                    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM", Locale.ENGLISH);
                    SimpleDateFormat sdf2 = new SimpleDateFormat("MMM yyyy", Locale.ENGLISH);
                    String time1 = sdf2.format(sdf1.parse(lineChartModels.get((int) e.getX()).getDuration()));
                    txt_date_time.setText(time1);
                } else {
                    txt_date_time.setText(lineChartModels.get((int) e.getX()).getDuration());
                    int ipos = lineChartModels.get((int) e.getX()).getDuration().indexOf("-");
                    String date = "NA";
                    if (ipos != -1) {
                        date = lineChartModels.get((int) e.getX()).getDuration().substring(0, ipos);
                    }
                    SimpleDateFormat sdf = new SimpleDateFormat("HH", Locale.ENGLISH);
                    SimpleDateFormat sdf2 = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);
                    String time1 = sdf2.format(sdf.parse(date + ":00"));
                    txt_date_time.setText(time1);
                    LoggerUtils.E("refreshContent", "refreshContent:" + time1);
                }
            } catch (Exception e1) {
                LoggerUtils.E("refreshContent", "refreshContent:" + e1.toString());
                txt_date_time.setText(lineChartModels.get((int) e.getX()).getxAxisValue());
                e1.printStackTrace();
            }
            txt_total_transaction.setText(lineChartModels.get((int) e.getX()).getTotalTransaction() + " " + getContext().getResources().getString(R.string.transactions));
            tvContent.setText(void1);
            tvContent11.setText(void2);

            super.refreshContent(e, highlight);
        }

        @Override
        public MPPointF getOffset() {
            return new MPPointF(-(getWidth() / 2), -getHeight());
        }

    }

//    public void registerCallBack(LocationFilterCallback locationFilterCallback) {
//        this.locationFilterCallback_ = locationFilterCallback;
//        moduleConfiguration.setLocationFilterCallback(locationFilterCallback);
//    }

/*
    public void applyLocationFilter(int requestCode, FilterModel filterModel) {
        if (filterModel != null) {
            RequestParameters requestParameters = moduleConfiguration.getRequestParameters();
            if (requestParameters == null) {
                requestParameters = Util.getRequestValue(getContext());
            }

            if (requestPL.getType() == Type.TYPE_CORP) {
                requestParameters.setCid(filterModel.getCID());
            } else {
                requestParameters.setMidList(filterModel.getCORP_MID());
            }
        }
        moduleConfiguration.setFilterModel(filterModel);
        JSONObject jsonObject = new JSONObject();
        assert filterModel != null;
        if (filterModel.getTYPE() == Type.TYPE_CORP) {
            try {
                //jsonObject.put("corpId", filterModel.getCID());
                //jsonObject.put("duration", mAppPreferences.getPreferences(getContext(), StringConstants.DURATION));
                jsonObject.put("startDate", startDate);
                jsonObject.put("endDate", endDate);
                //jsonObject.put("type", "corp");
                jsonObject.put("id", filterModel.getCORP_MID());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            JSONArray ja;
            ja = new JSONArray(filterModel.getCORP_MID());
            try {
                //jsonObject.put("midList", ja.toString());
                if (mAppPreferences.getPreferences(getContext(), StringConstants.DURATION).equals("year")) {
                    jsonObject.put("midList", filterModel.getCORP_MID());
                } else {
                    jsonObject.put("id", filterModel.getCORP_MID());
                    //jsonObject.put("duration", mAppPreferences.getPreferences(getContext(), StringConstants.DURATION));
                    jsonObject.put("startDate", startDate);
                    jsonObject.put("endDate", endDate);
                }
                //jsonObject.put("type", "merc");


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Intent intent = new Intent(getContext(), ActivityLineGraphList.class);
        intent.putExtra("data", jsonObject.toString());
        GraphTheme graphTheme = new GraphTheme(cardBackgroundColor, primaryTextColor, secondaryTextColor,
                primaryTextFont, secondaryTextFont, lineColor1, lineColor2, emptyStateText, emptyStateImage);
        intent.putExtra("GraphTheme", graphTheme);
        intent.putExtra("isTwelveMonthClicked", isTwelveMonthClicked);
        intent.putExtra("duration", mAppPreferences.getPreferences(getContext(), StringConstants.DURATION));
        getContext().startActivity(intent);


    }
*/

    private void canShowDate(boolean showDate) {
        if (canShowDate) {
            this.tv_Range_Performance.setVisibility(VISIBLE);
        } else {
            this.tv_Range_Performance.setVisibility(GONE);
        }
    }

    public void setPerformanceData(RequestParameters requestParameters, final String startDate, final String endDate) {
        //Bundle bundle = new Bundle();
        //bundle.putString("Section", "Performance");
        //ApiNetworkUtils.captureEvent("bizview-12month-button-deactivated", "Bizview", "bizview", "Bizview", "button", "deactivated", bundle, getContext());
        highlightTwelveMonth(false);
        isTwelveMonthClicked = false;
        //mAppPreferences.setPreferences(getContext(), StringConstants.DURATION, duration);
        if (ApiNetworkUtils.isConnected((Activity) getContext())) {
            startShimmer();
            if (startDate == null || endDate == null) {
                throw new IllegalArgumentException("duration should not be null");
            }
            if (startDate.isEmpty() || endDate.isEmpty()) {
                throw new IllegalArgumentException("duration should not be empty");
            }
            this.requestPL = requestParameters;
            this.startDate = startDate;
            this.endDate = endDate;
            //JSONObject params = new JSONObject();
      /*      try {
                if (requestPL.getProjectType() == ProjectType.TYPE_ONE_APP_BIZVIEW) {
                    requestPL.setBaseurl(BaseUrl.URL_ONE_APP);
                } else if (requestPL.getProjectType() == ProjectType.TYPE_ONE_APP_SMARTHUB) {
                    requestPL.setBaseurl(BaseUrl.URL_ONE_APP);
                } else {
                    requestPL.setBaseurl(BaseUrl.URL_ONE_APP);
                }
                moduleConfiguration.setRequestParameters(requestPL);
            } catch (Exception e) {

            }*/
            requestPL.setBaseurl(BaseUrl.URLMDEV4);
            moduleConfiguration.setRequestParameters(requestPL);

            LocationFilterUtils.getSelectedLocationFilter(new LocationFilterApplyCallBack() {
                @Override
                public void allSelectedData(CidLevelModel cidLevelModel, List<DbaLevelModel> dbaLevelModelList, List<CityLevelModel> cityLevelModelList, List<MidLevelModel> midLevelModelList, List<TerminalLevelModel> terminalLevelModelList) {

                    ArrayList<String> terminals = new ArrayList<>();
                    try {
                        terminals.clear();
                        for (int i = 0; i < midLevelModelList.size(); i++) {
                            MidLevelModel model = midLevelModelList.get(i);
                            for (int j = 0; j < model.getTerminalLevelModels().size(); j++) {
                                TerminalLevelModel model1 = model.getTerminalLevelModels().get(j);
                                terminals.add(model1.getTid());
                            }
                        }

                        JSONArray ja = new JSONArray(terminals);
                        JSONObject params = new JSONObject();
                        params.put("tidList", ja.toString());
                        params.put("startDate", startDate);
                        params.put("endDate", endDate);

                        LoggerUtils.E("PerformanceGraphDebug", "params:" + params.toString());
                        if (ApiConstants.publicKey == null) {
                            LoggerUtils.E("PerformanceGraphDebug", "publicKey == null:");
                            ApiInterface apiInterface = getClientNew(getContext(), requestPL).create(ApiInterface.class);
                            Observable<String> call = apiInterface.callAPIURL();
                            RXJavaCaller.GetKey(call, new RXJavaCaller.OnKeyReceived() {
                                @Override
                                public void onKeyReceivedSuccess() {
                                    LoggerUtils.E("PerformanceGraphDebug", "onKeyReceivedSuccess:");
                                    setPerformanceData(requestPL, startDate, endDate);
                                }

                                @Override
                                public void onKeyReceivedError(String error) {
                                    LoggerUtils.E("PerformanceGraphDebug", "onKeyReceivedError:" + error);
                                    stopShimmer();
                                    showEmptyState();
                                }
                            });
                        } else {
                            LoggerUtils.E("PerformanceGraphDebug", "publicKey != null:");
                            JSONObject enc = Encrypter.getEncryptedJSON(params);
                            String skey = enc.optString("SKEY", "");
                            String iv = enc.optString("IV", "");
                            JSONObject jsonObject1 = Encrypter.getFinalJSON(enc);
                            com.aim.pmgraph.apiinterface.ApiInterface apiInterface = getClientNew(getContext(), requestPL).create(com.aim.pmgraph.apiinterface.ApiInterface.class);
                            Observable<String> call;

                            call = apiInterface.getPerformanceGraphViewAllNewApi(jsonObject1.toString());
                            RXJavaCaller.executeService(call, skey, iv, false, new RXJavaCaller.OnServiceExecuted() {
                                @Override
                                public void onSuccess(String result) {
                                    LoggerUtils.E("PerformanceGraphDebug", "onSuccess:" + result);
                                    JSONObject payLaterResponse;
                                    try {
                                        payLaterResponse = new JSONObject(result);
                                    } catch (JSONException e) {
                                        payLaterResponse = new JSONObject();
                                    }
                                    GsonBuilder gsonBuilder = new GsonBuilder();
                                    Gson gson = gsonBuilder.create();
                                    //ResDashboardCards cards = gson.fromJson(payLaterResponse.toString(), ResDashboardCards.class);
                                    ResPerformanceViewAll summary = gson.fromJson(payLaterResponse.toString(), ResPerformanceViewAll.class);
                                    if (summary != null) {
                                        if (summary.getMessage().equalsIgnoreCase("Data Available") || summary.getMessage().equalsIgnoreCase("Success")) {
                                            //Line Graph (Performance)
                                            //List<PerformanceItem> performance = cards.getPerformance();
                                            List<PerformanceListItem> performanceList = summary.getPerformanceList();
                                            if (performanceList != null && performanceList.size() > 0) {
                                                LoggerUtils.E(TAG, "performance.size():" + performanceList.size());
                                                PerformanceGraphView.this.isTwelveMonthClicked = false;
                                                //setPerformanceGraph(performanceList, TAG, "", startDate, endDate);
                                                setOnAllLocationCard(performanceList, TAG, "", startDate, endDate);
                                                stopShimmer();
                                                hideEmptyState();
                                                tv_twelve_month.setTextColor(ContextCompat.getColor(getContext(),R.color.subtitle_label));
                                                iv_twelve_month.getDrawable().setTint(ContextCompat.getColor(getContext(),R.color.subtitle_label));
                                                Typeface typeface2 = ResourcesCompat.getFont(getContext(), R.font.montserrat_regular);
                                                tv_twelve_month.setTypeface(typeface2);
                                            } else {
                                                PerformanceGraphView.this.isTwelveMonthClicked = false;
                                                tv_twelve_month.setTextColor(ContextCompat.getColor(getContext(),R.color.subtitle_label));
                                                iv_twelve_month.getDrawable().setTint(ContextCompat.getColor(getContext(),R.color.subtitle_label));
                                                Typeface typeface2 = ResourcesCompat.getFont(getContext(), R.font.montserrat_regular);
                                                tv_twelve_month.setTypeface(typeface2);
                                                Util.setVisibility(layout_no_performancedata, true);
                                                Util.setVisibility(layout_performancedata, false);
                                                //setVisibility(cv_yearly_performance, false);
                                                LoggerUtils.E(TAG, "performance.size():0");
                                                stopShimmer();
                                                showEmptyState();
                                            }
                                        } else {
                                            stopShimmer();
                                            showEmptyState();
                                        }
                                    }

                                }

                                @Override
                                public void onError(String error) {
                                    stopShimmer();
                                    showEmptyState();
                                }

                                @Override
                                public void onKeyExpired() {
                                    setPerformanceData(requestPL, startDate, endDate);
                                }

                                @Override
                                public void onSessionExpired() {
//                                  mSessionExpiredListener.onSessionExpired();
                                    PerformanceCardUtils.mPerCardApiSessionExpired.onSessionExpired();
                                }
                            });
                        }

                    } catch (Exception e) {
                        stopShimmer();
                        showEmptyState();
                    }
                }

                @Override
                public void onCidSelected(CidLevelModel cidLevelModel) {

                }

                @Override
                public void onDBASelected(List<DbaLevelModel> dbaLevelModelList) {

                }

                @Override
                public void onCitySelected(List<CityLevelModel> cityLevelModelList) {

                }

                @Override
                public void onMidSelected(List<MidLevelModel> midLevelModelList) {

                }

                @Override
                public void onTidSelected(List<TerminalLevelModel> terminalLevelModelList) {

                }
            });

        } else {
            stopShimmer();
            showEmptyState();
            //Toast.makeText(getContext(), getResources().getString(R.string.no_internet_connection_n_please_try_again_later), Toast.LENGTH_SHORT).show();
        }


    }

//    public void setSessionListener(SessionExpiredListener mSessionExpiredListener) {
//        this.mSessionExpiredListener = mSessionExpiredListener;
//    }

    private void highlightTwelveMonth(boolean b) {
        if (b) {
            tv_twelve_month.setTextColor(ContextCompat.getColor(getContext(),R.color.text_color_month));
            iv_twelve_month.getDrawable().setTint(ContextCompat.getColor(getContext(),R.color.text_color_month));
            Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.montserrat_semibold);
            tv_twelve_month.setTypeface(typeface);
        } else {
            tv_twelve_month.setTextColor(ContextCompat.getColor(getContext(),R.color.subtitle_label));
            iv_twelve_month.getDrawable().setTint(ContextCompat.getColor(getContext(),R.color.subtitle_label));
            Typeface typeface2 = ResourcesCompat.getFont(getContext(), R.font.montserrat_regular);
            tv_twelve_month.setTypeface(typeface2);
        }
    }
}

