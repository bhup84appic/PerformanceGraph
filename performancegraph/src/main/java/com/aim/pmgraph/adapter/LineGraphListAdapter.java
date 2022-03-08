/*
 *
 *  Created by Pooran Kharol on 18/2/21 6:53 PM
 *  Copyright (c) 2021 . All rights reserved.
 *  Last modified 18/2/21 6:53 PM
 *
 */

package com.aim.pmgraph.adapter;


import static com.mintoak.corelib.Util.convertToDefaultCurrencyFormatNew;
import static com.mintoak.corelib.Util.getAmounts;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.aim.pmgraph.MyValueFormatter;
import com.aim.pmgraph.R;
import com.aim.pmgraph.model.LineChartModel;
import com.aim.pmgraph.model.PerformanceListItem;
import com.aim.pmgraph.model.SummaryListItem;
import com.aim.pmgraph.theme.GraphTheme;
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
import com.github.mikephil.charting.utils.Utils;
import com.mintoak.corelib.LoggerUtils;
import com.mintoak.corelib.Util;
import com.mintoak.corelib.apiutil.ApiNetworkUtils;

import net.cachapa.expandablelayout.ExpandableLayout;

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


public class LineGraphListAdapter extends RecyclerView.Adapter<LineGraphListAdapter.ViewHolder> {
    private final Context mContext;
    private static final String TAG = "LineGraphListAdapter";
    //private ArrayList<DummyData> listdata = new ArrayList<>();
    private final List<PerformanceListItem> listdata;
    public ArrayList<LineChartModel> lineChartModels;
    private String selectedDayType;
    private RecyclerView mRecycleview;
    private static GraphTheme graphTheme;
    private final String startDate;
    private final String endDate;

    public LineGraphListAdapter(Context context, List<PerformanceListItem> listdata, String selectedDayType, GraphTheme graphTheme, String startDate, String endDate) {
        this.mContext = context;
        this.listdata = listdata;
        this.selectedDayType = selectedDayType;
        LineGraphListAdapter.graphTheme = graphTheme;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.activity_line_graph_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecycleview = recyclerView;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        //DummyData d = listdata.get(position);
        final PerformanceListItem item = listdata.get(position);
        holder.tv_location.setText(item.getPincode());
        holder.tvAddress.setText(item.getAddress());
        setPerformanceGraph(holder, item, "Values");
        if (item.isVisible()) {
            setArrowRotation(holder.arrowExpander, 180f);
            //setVisibility(holder.linechart, true);
            //setVisibility(holder.iv_graph, false);
            holder.LLlinechart.expand();
            holder.iv_graph.collapse();
        } else {
            setArrowRotation(holder.arrowExpander, 0f);
            //setVisibility(holder.linechart, false);
            //setVisibility(holder.iv_graph, true);
            holder.iv_graph.expand();
            holder.LLlinechart.collapse();
        }

        holder.RLArrowExpander.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*if (isViewVisible(holder.linechart)) {
                    d.setVisible(false);
                    setArrowRotation(holder.arrowExpander, 0f);
                    setVisibility(holder.linechart, false);
                    //collapse(holder.linechart);
                    setVisibility(holder.iv_graph, true);
                    //expand(holder.iv_graph);
                } else {
                    d.setVisible(true);
                    setArrowRotation(holder.arrowExpander, 180f);
                    setVisibility(holder.linechart, true);
                    //expand(holder.linechart);
                    setVisibility(holder.iv_graph, false);
                    //collapse(holder.iv_graph);

                }*/

                if (holder.LLlinechart.isExpanded()) {
                    //item.setVisible(false);
                    setArrowRotation(holder.arrowExpander, 0f);
                    holder.iv_graph.expand();
                    holder.LLlinechart.collapse();

//                    Bundle bundle = new Bundle();
//                    bundle.putString("location", "Location " + item.getArea());
//                    ApiNetworkUtils.captureEvent("performance-collapse-icon-clicked", "Performance", "performance",
//                            "", "",
//                            "", bundle, mContext);
                } else {
                    //item.setVisible(true);
                    setArrowRotation(holder.arrowExpander, 180f);
                    holder.LLlinechart.expand();
                    holder.iv_graph.collapse();

//                    Bundle bundle = new Bundle();
//                    bundle.putString("location", "Location " + item.getArea());
//                    ApiNetworkUtils.captureEvent("performance-expand-icon-clicked", "Performance", "performance",
//                            "", "",
//                            "", bundle, mContext);
                }
            }
        });
        /*holder.iv_graph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isViewVisible(holder.linechart)) {
                    d.setVisible(false);
                    setArrowRotation(holder.arrowExpander, 0f);
                    setVisibility(holder.linechart, false);
                    setVisibility(holder.iv_graph, true);
                } else {
                    d.setVisible(true);
                    setArrowRotation(holder.arrowExpander, 180f);
                    setVisibility(holder.linechart, true);
                    setVisibility(holder.iv_graph, false);
                }
            }
        });*/

        /*holder.txt_valueTxtLineGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.viewValue.setVisibility(View.VISIBLE);
                holder.viewTranscation.setVisibility(View.INVISIBLE);
                Typeface typeface1 = ResourcesCompat.getFont(mContext, R.font.montserrat_semibold);
                Typeface typeface2 = ResourcesCompat.getFont(mContext, R.font.montserrat_regular);
                holder.txt_valueTxtLineGraph.setTextColor(mContext.getResources().getColor(R.color.green));
                holder.txt_valueTxtLineGraph.setTypeface(typeface1);
                holder.txt_transactionTxtLineGraph.setTextColor(mContext.getResources().getColor(R.color.subtitle_label));
                holder.txt_transactionTxtLineGraph.setTypeface(typeface2);
                //LineGraphMethod("Values", lineChartModels, values);
                setPerformanceGraph(holder, item, "Values");
            }
        });*/


       /* holder.txt_transactionTxtLineGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.viewValue.setVisibility(View.INVISIBLE);
                holder.viewTranscation.setVisibility(View.VISIBLE);
                Typeface typeface1 = ResourcesCompat.getFont(mContext, R.font.montserrat_semibold);
                Typeface typeface2 = ResourcesCompat.getFont(mContext, R.font.montserrat_regular);
                holder.txt_valueTxtLineGraph.setTextColor(mContext.getResources().getColor(R.color.subtitle_label));
                holder.txt_valueTxtLineGraph.setTypeface(typeface2);
                holder.txt_transactionTxtLineGraph.setTextColor(mContext.getResources().getColor(R.color.green));
                holder.txt_transactionTxtLineGraph.setTypeface(typeface1);
                //LineGraphMethod("Transactions", lineChartModels, values);
                setPerformanceGraph(holder, item, "Transactions");
            }
        });*/


    }


    @Override
    public int getItemCount() {
        if (listdata != null) {
            return listdata.size();
        } else {
            return 0;
        }
    }

    private void LineGraphMethod(String selected, ArrayList<LineChartModel> lineChartModels, ArrayList<Entry> valuesAmt, ArrayList<Entry> valuesCount, final ViewHolder holder) {
        this.lineChartModels = lineChartModels;
        if (!this.lineChartModels.isEmpty()) {
            holder.linechart.clear();
            Util.setVisibility(holder.layout_no_performancedata, false);
            Util.setVisibility(holder.layout_performancedata, true);
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

                valuesAmt.add(new Entry(i, amt, ContextCompat.getDrawable(mContext, R.drawable.round_icon1)));
                valuesCount.add(new Entry(i, count, ContextCompat.getDrawable(mContext, R.drawable.round_icon2)));

                xAxisLabel.add("" + lineChartModels.get(i).getxAxisValue() + "");

                LoggerUtils.E("PERFORMANCE", "xAxisLabel:" + lineChartModels.get(i).getxAxisValue());
                LoggerUtils.E("PERFORMANCE", "values: X:" + valuesAmt.get(i).getX() + " Y:" + valuesAmt.get(i).getY() + "");
                LoggerUtils.E("PERFORMANCE", "values1: X:" + valuesCount.get(i).getX() + " Y:" + valuesCount.get(i).getY() + "");
            }

            holder.linechart.setBorderWidth(5f);
            holder.linechart.setTouchEnabled(true);
            holder.linechart.setDrawGridBackground(false);
            holder.linechart.getDescription().setEnabled(false);
            holder.linechart.setDoubleTapToZoomEnabled(false);
            holder.linechart.setExtraOffsets(10, 10, 10, 10);

            XAxis xAxis1 = holder.linechart.getXAxis();
            xAxis1.setDrawGridLines(false);
            xAxis1.setPosition(XAxis.XAxisPosition.BOTTOM);
            if (graphTheme.getSecondaryTextColor() != 0) {
                xAxis1.setTextColor(graphTheme.getSecondaryTextColor());
            } else {
                xAxis1.setTextColor(mContext.getResources().getColor(R.color.text_color));
            }
            xAxis1.setLabelCount(lineChartModels.size());

            xAxis1.setLabelRotationAngle(-90);

            if (lineChartModels.size() > 0) {
                xAxis1.setValueFormatter(new IndexAxisValueFormatter(xAxisLabel));
            }

            xAxis1.setTypeface(Typeface.DEFAULT_BOLD);

            xAxis1.disableGridDashedLine();
            xAxis1.disableAxisLineDashedLine();

            YAxis rightAxis1 = holder.linechart.getAxisRight();
            rightAxis1.setDrawGridLines(false);
            rightAxis1.setAxisMinimum(0f); // this replaces setStartAtZero(true)
            rightAxis1.setTextColor(Color.TRANSPARENT);
            rightAxis1.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
            rightAxis1.setZeroLineColor(Color.TRANSPARENT);
            rightAxis1.setEnabled(false);


            YAxis leftAxis1 = holder.linechart.getAxisLeft();
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
            leftAxis1.setTextColor(mContext.getResources().getColor(R.color.textwhite));
            Typeface typeface = ResourcesCompat.getFont(mContext, R.font.montserrat_semibold);
            leftAxis1.setTypeface(typeface);

            LineDataSet lineDataSetAmt = new LineDataSet(valuesAmt, "");
            LineDataSet lineDataSetCount = new LineDataSet(valuesCount, "");

            lineDataSetAmt.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
            lineDataSetCount.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
            lineDataSetAmt.setCircleColor(Color.parseColor("#F6F6F6"));
            lineDataSetAmt.setCircleHoleColor(mContext.getResources().getColor(R.color.graphline_color1));
            lineDataSetAmt.setCircleHoleRadius(4f);

            lineDataSetAmt.disableDashedLine();
            lineDataSetCount.disableDashedLine();

            lineDataSetAmt.setCircleRadius(7f);
            lineDataSetAmt.setColor(graphTheme.getLineColor1());
            lineDataSetAmt.setLineWidth(2f);
            lineDataSetAmt.setDrawValues(false);
            lineDataSetAmt.setDrawHighlightIndicators(true);
            lineDataSetAmt.setDrawFilled(true);
            lineDataSetAmt.setHighLightColor(mContext.getResources().getColor(R.color.graphline1_2));
            lineDataSetAmt.setDrawCircles(true);
            lineDataSetAmt.setDrawHorizontalHighlightIndicator(false);
            lineDataSetAmt.setDrawIcons(false);
            lineDataSetAmt.setAxisDependency(YAxis.AxisDependency.LEFT);

            lineDataSetCount.setCircleRadius(7f);
            lineDataSetCount.setColor(graphTheme.getLineColor2());
            lineDataSetCount.setLineWidth(2f);
            lineDataSetCount.setDrawValues(false);
            lineDataSetCount.setDrawHighlightIndicators(true);
            lineDataSetCount.setDrawFilled(true);
            lineDataSetCount.setHighLightColor(mContext.getResources().getColor(R.color.graphline2_2));
            lineDataSetCount.setDrawCircles(true);
            lineDataSetCount.setDrawHorizontalHighlightIndicator(false);
            lineDataSetCount.setDrawIcons(false);
            lineDataSetCount.setAxisDependency(YAxis.AxisDependency.RIGHT);
            lineDataSetCount.setCircleColor(Color.parseColor("#F6F6F6"));
            lineDataSetCount.setCircleHoleColor(ContextCompat.getColor(mContext, R.color.graphline_color2));
            lineDataSetCount.setCircleHoleRadius(4f);

            if (Utils.getSDKInt() >= 18) {
                // fill drawable only supported on api level 18 and above
                //Drawable drawable1 = /*ContextCompat.getDrawable(getContext(), R.drawable.fade_drawable1);*/ getLineColor1Drawable();
                //Drawable drawable2 = /*ContextCompat.getDrawable(getContext(), R.drawable.fade_drawable2);*/ getLineColor2Drawable();
                Drawable drawable1 = ContextCompat.getDrawable(mContext, R.drawable.fade_drawable1);
                Drawable drawable2 = ContextCompat.getDrawable(mContext, R.drawable.fade_drawable2);

                lineDataSetAmt.setFillDrawable(drawable1);
                lineDataSetCount.setFillDrawable(drawable2);
            } else {
                lineDataSetAmt.setFillColor(Color.BLACK);
                lineDataSetCount.setFillColor(Color.BLACK);
            }
            holder.linechart.setPinchZoom(true);
            List<ILineDataSet> dataSetList = new ArrayList<>();
            dataSetList.clear();
            dataSetList.add(lineDataSetCount);
            dataSetList.add(lineDataSetAmt);
            LineData lineData = new LineData(dataSetList);
            lineData.setValueTextColor(mContext.getResources().getColor(R.color.textwhite));
            lineData.setValueTextSize(10f);
            lineData.setValueTextColor(mContext.getResources().getColor(R.color.green));
            holder.linechart.setDrawMarkers(true);

            CustomMarkerView mv = new CustomMarkerView(mContext, R.layout.item_line_graph_marker, selected, lineChartModels);
            mv.setChartView(holder.linechart);
            holder.linechart.getLegend().setTextColor(mContext.getResources().getColor(R.color.text_color));
            holder.linechart.getLegend().setEnabled(false);
            holder.linechart.setMarker(mv);
            holder.linechart.getXAxis().setDrawAxisLine(true);
            holder.linechart.getAxisLeft().setDrawAxisLine(true);
            holder.linechart.getAxisRight().setDrawAxisLine(false);
            holder.linechart.getXAxis().setDrawGridLines(false);
            holder.linechart.setData(lineData);
            if (lineChartModels.size() > 15) {
                holder.linechart.setVisibleXRangeMaximum(15);
            } else {
                holder.linechart.fitScreen();
            }

            holder.linechart.invalidate(); // refresh

        } else {
            Util.setVisibility(holder.layout_performancedata, false);
            Util.setVisibility(holder.layout_no_performancedata, true);
        }

        //NewMethodHere
        holder.linechart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                /*List<Entry> data1 = linechart.getData().getDataSets().get(0).getEntriesForXValue(e.getX());
                ILineDataSet data2 = linechart.getData().getDataSets().get(1);*/
                int line1 = holder.linechart.getData().getDataSets().get(0).getEntryCount();
                for (int i = 0; i < line1; i++) {
                    if ((int) e.getX() == i) {
                        holder.linechart.getData().getDataSets().get(0).getEntriesForXValue(i).get(0).setIcon(ContextCompat.getDrawable(mContext, R.drawable.green_round_icon_fix));
                        holder.linechart.getData().getDataSets().get(1).getEntriesForXValue(i).get(0).setIcon(ContextCompat.getDrawable(mContext, R.drawable.blue_round_icon_fix));
                    } else {
                        holder.linechart.getData().getDataSets().get(0).getEntriesForXValue(i).get(0).setIcon(ContextCompat.getDrawable(mContext, R.drawable.round_icon1));
                        holder.linechart.getData().getDataSets().get(1).getEntriesForXValue(i).get(0).setIcon(ContextCompat.getDrawable(mContext, R.drawable.round_icon2));
                    }
                }
                holder.linechart.notifyDataSetChanged();
                holder.linechart.invalidate();
                //int line2 = linechart.getData().getDataSets().get(1).getEntryCount();
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }

    private boolean isViewVisible(View v) {
        return v.isShown();
    }

    private void setArrowRotation(View v, float f) {
        //v.animate().rotationX(f).setDuration(1000).start();
        v.animate().rotationX(f);
    }

   /* public static void expand(final View v) {
        int matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec(((View) v.getParent()).getWidth(), View.MeasureSpec.EXACTLY);
        int wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        v.measure(matchParentMeasureSpec, wrapContentMeasureSpec);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 3
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // Expansion speed of 1dp/ms
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }*/

    /*private void setVisibility(View v, boolean b) {
        if (b) {
            v.setVisibility(View.VISIBLE);
            expand(v);
        } else {
            v.setVisibility(View.GONE);
            collapse(v);
        }
    }*/

    /*public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // Collapse speed of 1dp/ms
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }*/
    public static List<Date> getDaysBetweenDates(String str_date, String end_date) {
        List<Date> dates = new ArrayList<Date>();


        DateFormat formatter;

        formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date startDate = null;
        Date endDate = null;
        try {
            startDate = formatter.parse(str_date);
            endDate = (Date) formatter.parse(end_date);
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
            Date lDate = (Date) dates.get(i);
            String ds = formatter.format(lDate);
            System.out.println(" Date is ..." + ds);
        }
        return dates;
    }

    private void setPerformanceGraph(ViewHolder holder, PerformanceListItem listitem, String type) {
        try {
            //2019-11-01
            SimpleDateFormat sdf1 = new SimpleDateFormat("dd", Locale.ENGLISH);
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            SimpleDateFormat sdf3 = new SimpleDateFormat("HH", Locale.ENGLISH);
            SimpleDateFormat sdf4 = new SimpleDateFormat("yyyy-MM", Locale.ENGLISH);
            List<SummaryListItem> summaryList = listitem.getSummaryList();

            if (summaryList != null && summaryList.size() > 0) {
                List<SummaryListItem> summaryListNew = new ArrayList<>();
                List<SummaryListItem> summaryListAll = new ArrayList<>();
                if (TextUtils.isEmpty(selectedDayType)) {
                    selectedDayType = "";
                }
                if (selectedDayType.equalsIgnoreCase("year")) {
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
                            SummaryListItem item = new SummaryListItem();
                            item.setDuration(sdf4.format(cals.get(i)));
                            item.setTotalamount("0.0");
                            item.setTotaltransaction("0");
                            summaryListAll.add(item);
                        }

                        for (int i = 0; i < summaryListAll.size(); i++) {
                            for (int j = 0; j < summaryList.size(); j++) {
                                if (summaryListAll.get(i).getDuration().equalsIgnoreCase(summaryList.get(j).getDuration())) {
                                    LoggerUtils.E(TAG, "MATCHING:" + summaryListAll.get(i).getDuration() + " | " + summaryList.get(j).getDuration());
                                    summaryListAll.get(i).setDuration(summaryList.get(j).getDuration());
                                    summaryListAll.get(i).setTotalamount(summaryList.get(j).getTotalamount());
                                    summaryListAll.get(i).setTotaltransaction(summaryList.get(j).getTotaltransaction());
                                }
                            }
                        }
                        Collections.sort(summaryListAll, new Comparator<SummaryListItem>() {
                            @Override
                            public int compare(SummaryListItem o1, SummaryListItem o2) {
                                return o1.getDuration().compareTo(o2.getDuration());
                            }
                        });
                    } catch (Exception e) {
                        LoggerUtils.E(TAG, "Exception 12:" + e.toString());
                    }
                } else {
                    if (startDate.equalsIgnoreCase(endDate)) {
                        selectedDayType = "day";
                        for (int i = 0; i < summaryList.size(); i++) {
                            int c = 0, d = 0;
                            int ipos = summaryList.get(i).getDuration().indexOf("-");
                            if (ipos != -1) {
                                c = Integer.parseInt(summaryList.get(i).getDuration().substring(0, ipos));
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

                            summaryList.get(i).setDuration(s1 + c + "-" + s2 + d);
                        }
                        SummaryListItem item1 = new SummaryListItem();
                        item1.setDuration("00-01");
                        item1.setTotalamount("0.0");
                        item1.setTotaltransaction("0");
                        summaryListNew.add(item1);
                        summaryListNew.addAll(summaryList);


                        int c = 0, d = 0;
                        int ipos = summaryListNew.get(0).getDuration().indexOf("-");
                        if (ipos != -1) {
                            c = Integer.parseInt(summaryListNew.get(0).getDuration().substring(0, ipos));
                        }
                        int ipos1 = summaryListNew.get(summaryListNew.size() - 1).getDuration().indexOf("-");
                        if (ipos1 != -1) {
                            d = Integer.parseInt(summaryListNew.get(summaryListNew.size() - 1).getDuration().substring(0, ipos));
                        }

                        LoggerUtils.E(TAG, "performance.size():" + summaryList.size());
                        LoggerUtils.E(TAG, "performanceNew.size():" + summaryListNew.size());
                        LoggerUtils.E(TAG, "c:" + c);
                        LoggerUtils.E(TAG, "d:" + d);
                        for (int i = c; i <= d; i++) {
                            SummaryListItem item = new SummaryListItem();
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
                            summaryListAll.add(item);
                        }

                        for (int i = 0; i < summaryListAll.size(); i++) {
                            for (int j = 0; j < summaryList.size(); j++) {
                                if (summaryListAll.get(i).getDuration().equalsIgnoreCase(summaryList.get(j).getDuration())) {
                                    LoggerUtils.E(TAG, "MATCHING:" + summaryListAll.get(i).getDuration() + " | " + summaryList.get(j).getDuration());
                                    summaryListAll.get(i).setDuration(summaryList.get(j).getDuration());
                                    summaryListAll.get(i).setTotalamount(summaryList.get(j).getTotalamount());
                                    summaryListAll.get(i).setTotaltransaction(summaryList.get(j).getTotaltransaction());
                                }
                            }
                        }

                        Collections.sort(summaryListAll, new Comparator<SummaryListItem>() {
                            @Override
                            public int compare(SummaryListItem o1, SummaryListItem o2) {
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
                            for (int j = 0; j < summaryList.size(); j++) {
                                present = ds.equals(summaryList.get(j).getDuration());
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
                            SummaryListItem item = new SummaryListItem();
                            item.setDuration(dateToBeAdded.get(i));
                            item.setTotalamount("0.0");
                            item.setTotaltransaction("0");
                            summaryListAll.add(item);
                        }
                        summaryListAll.addAll(summaryList);
                        Collections.sort(summaryListAll, new Comparator<SummaryListItem>() {
                            @Override
                            public int compare(SummaryListItem o1, SummaryListItem o2) {
                                return o1.getDuration().compareTo(o2.getDuration());
                            }
                        });

                    }

                }

                LoggerUtils.E(TAG, "summaryList:size():" + summaryList.size());
                LoggerUtils.E(TAG, "performanceAll.size():" + summaryListAll.size());

                ArrayList<LineChartModel> lineChartModels = new ArrayList<>();
                ArrayList<Entry> valuesAmt = new ArrayList<Entry>();
                ArrayList<Entry> valuesCount = new ArrayList<Entry>();

                double amt = 0.0;
                int transactionCount = 0;

                for (int i = 0; i < summaryListAll.size(); i++) {
                    SummaryListItem item = summaryListAll.get(i);
                    LineChartModel model = new LineChartModel();
                    if (item.getDuration() != null) {
                        try {
                            LoggerUtils.E(TAG, "selectedDayType:" + selectedDayType);

                            if (selectedDayType.equalsIgnoreCase("year")) {
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
//                                    SimpleDateFormat sdfTime1 = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
//                                    SimpleDateFormat sdfTime2 = new SimpleDateFormat("hh aa", Locale.ENGLISH);
//                                    String time1 = sdfTime2.format(sdfTime1.parse(date1 + ":00"));
//                                    LoggerUtils.E("refreshContent", "refreshContent:" + time1);
                                    model.setxAxisValue(date1);
                                } else {
                                    String date = getPerformanceDurationText(item.getDuration());
                                    model.setxAxisValue(date);
                                }
                            }
                            model.setDuration(item.getDuration());
                        } catch (Exception e) {
                            LoggerUtils.E(TAG, "Exception:" + e.toString());
                            model.setxAxisValue(item.getDuration());
                        }
                    } else {
                        model.setxAxisValue("NA");
                        model.setDuration("NA");
                    }

                    model.setTotalTransaction(item.getTotaltransaction());
                    model.setTotalAmount(item.getTotalamount());
                    lineChartModels.add(model);
                    transactionCount += (Integer.parseInt(item.getTotaltransaction()));
                    amt += (Double.valueOf(item.getTotalamount()));
                }

                String[] amounts = getAmounts(String.valueOf(amt));
                holder.tv_amount.setText("₹ " + convertToDefaultCurrencyFormatNew(amounts[0]) + "" + amounts[1]);
                holder.tv_transaction.setText(transactionCount + " " + mContext.getResources().getString(R.string.transactions));

                LineGraphMethod(type, lineChartModels, valuesAmt, valuesCount, holder);
                LineGraphMethod2(type, lineChartModels, valuesAmt, valuesCount, holder);

                holder.layout_performancedata.setVisibility(View.VISIBLE);
                holder.layout_no_performancedata.setVisibility(View.GONE);

                holder.LL_second_chart.setVisibility(View.VISIBLE);
                holder.tv_amount.setVisibility(View.VISIBLE);
                holder.tv_transaction.setVisibility(View.VISIBLE);
            } else {
                LoggerUtils.E(TAG, "summaryList:size():0");
                holder.layout_performancedata.setVisibility(View.GONE);
                holder.layout_no_performancedata.setVisibility(View.VISIBLE);

                holder.LL_second_chart.setVisibility(View.GONE);
                holder.tv_amount.setVisibility(View.GONE);
                holder.tv_transaction.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_location,tvAddress;
        //public TextView tv_amount1;
        //public TextView tv_amount2;
        public RelativeLayout RLArrowExpander;
        public ImageView arrowExpander;
        public ImageView iv_sale, iv_txn;
        public ImageView iv_empty_state_image;
        public ExpandableLayout iv_graph;
        public LineChart linechart, linechart2;
        public ExpandableLayout LLlinechart;
        public TextView txt_valueTxtLineGraph, txt_transactionTxtLineGraph;
        public View viewValue, viewTranscation;
        public TextView tv_amount;
        public TextView tv_transaction;
        public TextView tv_empty_state_text;
        public TextView tv_sale, tv_txn;
        public LinearLayout layout_performancedata, layout_no_performancedata, LL_second_chart;
        private final CardView card;


        public ViewHolder(View itemView) {
            super(itemView);
            this.tv_location = itemView.findViewById(R.id.tv_location);
            this.tvAddress = itemView.findViewById(R.id.tvAddress);
            this.tv_location.setSelected(true);
            //this.tv_amount1 = itemView.findViewById(R.id.tv_amount1);
            //this.tv_amount2 = itemView.findViewById(R.id.tv_amount2);
            this.RLArrowExpander = itemView.findViewById(R.id.RLArrowExpander);
            this.arrowExpander = itemView.findViewById(R.id.arrowExpander);
            this.linechart = itemView.findViewById(R.id.linechart);
            this.linechart2 = itemView.findViewById(R.id.linechart2);
            this.iv_graph = itemView.findViewById(R.id.iv_graph);
            this.LLlinechart = itemView.findViewById(R.id.LLlinechart);
            this.txt_valueTxtLineGraph = itemView.findViewById(R.id.valueTxtLineGraph);
            this.txt_transactionTxtLineGraph = itemView.findViewById(R.id.transactionTxtLineGraph);
            this.viewValue = itemView.findViewById(R.id.viewValue);
            this.viewTranscation = itemView.findViewById(R.id.viewTransaction);
            this.tv_amount = itemView.findViewById(R.id.tv_amount);
            this.tv_transaction = itemView.findViewById(R.id.tv_transaction);
            this.layout_performancedata = itemView.findViewById(R.id.layout_performancedata);
            this.layout_no_performancedata = itemView.findViewById(R.id.layout_no_performancedata);
            this.LL_second_chart = itemView.findViewById(R.id.LL_second_chart);
            this.tv_empty_state_text = itemView.findViewById(R.id.tv_empty_state_text);
            this.iv_empty_state_image = itemView.findViewById(R.id.iv_empty_state_image);
            this.tv_sale = itemView.findViewById(R.id.tv_sale);
            this.tv_txn = itemView.findViewById(R.id.tv_txn);
            this.iv_sale = itemView.findViewById(R.id.iv_sale);
            this.iv_txn = itemView.findViewById(R.id.iv_txn);
            this.card = itemView.findViewById(R.id.card);


            if (graphTheme.getPrimaryTextColor() != 0) {
                tv_location.setTextColor(graphTheme.getPrimaryTextColor());
            }
            if (graphTheme.getSecondaryTextColor() != 0) {
                tv_empty_state_text.setTextColor(graphTheme.getSecondaryTextColor());
                tv_sale.setTextColor(graphTheme.getSecondaryTextColor());
                tv_txn.setTextColor(graphTheme.getSecondaryTextColor());
                //tv_amount.setTextColor(graphTheme.getSecondaryTextColor());
                tv_transaction.setTextColor(graphTheme.getSecondaryTextColor());

                iv_sale.getDrawable().setTint(graphTheme.getSecondaryTextColor());
                iv_txn.getDrawable().setTint(graphTheme.getSecondaryTextColor());

                arrowExpander.getDrawable().setTint(graphTheme.getSecondaryTextColor());
            }
            if (graphTheme.getPrimaryTextFont() != null) {
                tv_location.setTypeface(graphTheme.getPrimaryTextFont());
            }
            if (graphTheme.getSecondaryTextFont() != null) {
                tv_empty_state_text.setTypeface(graphTheme.getSecondaryTextFont());
                tv_sale.setTypeface(graphTheme.getSecondaryTextFont());
                tv_txn.setTypeface(graphTheme.getSecondaryTextFont());
                tv_amount.setTypeface(graphTheme.getSecondaryTextFont());
                tv_transaction.setTypeface(graphTheme.getSecondaryTextFont());
            }
            if (graphTheme.getCardBackgroundColor() != 0) {
                card.setCardBackgroundColor(graphTheme.getCardBackgroundColor());
            }

            if (!TextUtils.isEmpty(graphTheme.getEmptyStateText())) {
                tv_empty_state_text.setText(graphTheme.getEmptyStateText());
            }
            if (graphTheme.getEmptyStateImage() != null) {
                iv_empty_state_image.setImageDrawable(graphTheme.getEmptyStateImage());
            }
            if (graphTheme.getLineColor1() != 0) {
                iv_sale.getDrawable().setTint(graphTheme.getLineColor1());
            }
            if (graphTheme.getLineColor2() != 0) {
                iv_txn.getDrawable().setTint(graphTheme.getLineColor2());
            }
        }
    }

    private void LineGraphMethod2(String selected, ArrayList<LineChartModel> lineChartModels, ArrayList<Entry> values, ArrayList<Entry> values1, ViewHolder
            holder) {
        //Line Chart
        this.lineChartModels = lineChartModels;
        holder.linechart2.clear();
        final ArrayList<String> xAxisLabel = new ArrayList<>();
        ArrayList<String> xVals = new ArrayList<>();
        xAxisLabel.clear();
        values.clear();
        values1.clear();

        double totalCount = 0.0;
        double totalAmt = 0.0;
        for (int i = 0; i < lineChartModels.size(); i++) {
            totalCount += (Double.parseDouble(lineChartModels.get(i).getTotalTransaction()));
            totalAmt += (Double.parseDouble(lineChartModels.get(i).getTotalAmount()));
        }

        for (int i = 0; i < lineChartModels.size(); i++) {

            float amt = Float.parseFloat(lineChartModels.get(i).getTotalAmount());
            float count = Float.parseFloat(lineChartModels.get(i).getTotalTransaction());

            values.add(new Entry(i, amt, ContextCompat.getDrawable(mContext, R.drawable.round_icon1)));
            values1.add(new Entry(i, count, ContextCompat.getDrawable(mContext, R.drawable.round_icon2)));
            xAxisLabel.add(lineChartModels.get(i).getxAxisValue());
        }

        holder.linechart2.setBorderWidth(5f);
        holder.linechart2.setTouchEnabled(false);
        holder.linechart2.setDrawGridBackground(false);
        holder.linechart2.getDescription().setEnabled(false);
        holder.linechart2.setDoubleTapToZoomEnabled(false);


        XAxis xAxis1 = holder.linechart2.getXAxis();
        xAxis1.setDrawGridLines(false);
        xAxis1.setPosition(XAxis.XAxisPosition.BOTTOM);
        if (graphTheme.getSecondaryTextColor() != 0) {
            xAxis1.setTextColor(graphTheme.getSecondaryTextColor());
        } else {
            xAxis1.setTextColor(mContext.getResources().getColor(R.color.text_color));
        }
        xAxis1.setLabelCount(lineChartModels.size(), true);
        if (lineChartModels.size() > 0) {
            xAxis1.setValueFormatter(new IndexAxisValueFormatter(xAxisLabel));
//                XAxis xAxis = linechart2.getXAxis();
//                xAxis1.setValueFormatter(new DateAxisFormatter(xAxis));
        }

        xAxis1.setTypeface(Typeface.DEFAULT_BOLD);

        xAxis1.disableGridDashedLine();
        xAxis1.disableAxisLineDashedLine();
        xAxis1.setEnabled(false);

        YAxis rightAxis1 = holder.linechart2.getAxisRight();
        rightAxis1.setDrawGridLines(false);
        rightAxis1.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        rightAxis1.setTextColor(Color.TRANSPARENT);
        rightAxis1.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        rightAxis1.setZeroLineColor(Color.TRANSPARENT);
        rightAxis1.setEnabled(false);


        YAxis leftAxis1 = holder.linechart2.getAxisLeft();
        leftAxis1.setDrawGridLines(false);
        leftAxis1.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        leftAxis1.setSpaceTop(20f);
        leftAxis1.setEnabled(true);
        leftAxis1.setDrawAxisLine(false);
        leftAxis1.setAxisLineColor(Color.TRANSPARENT);
        leftAxis1.setGridLineWidth(0.5f);
        leftAxis1.setGridColor(Color.parseColor("#ECECEC"));
        LoggerUtils.E(TAG, "LineGraphMethod: " + " : " + leftAxis1.getGridLineWidth());
        leftAxis1.setDrawLabels(false);
        leftAxis1.setValueFormatter(new MyValueFormatter(1200000f, leftAxis1));
        leftAxis1.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        leftAxis1.setTextColor(mContext.getResources().getColor(R.color.textwhite));
        Typeface typeface = ResourcesCompat.getFont(mContext, R.font.montserrat_semibold);
        leftAxis1.setTypeface(typeface);

        LineDataSet lineDataSet = new LineDataSet(values1, "");
        LineDataSet lineDataSet2 = new LineDataSet(values, "");

        lineDataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        lineDataSet2.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);


        lineDataSet.setCircleHoleColor(mContext.getResources().getColor(R.color.green));
        lineDataSet.setCircleHoleRadius(4f);

        lineDataSet.disableDashedLine();
        lineDataSet2.disableDashedLine();


        lineDataSet.setCircleRadius(7f);
        if (graphTheme.getLineColor2() != 0) {
            lineDataSet.setColor(graphTheme.getLineColor2());
        } else {
            lineDataSet.setColor(mContext.getResources().getColor(R.color.graphline2_1));
        }
        lineDataSet.setLineWidth(2f);
        lineDataSet.setDrawValues(false);
        lineDataSet.setDrawHighlightIndicators(false);
        lineDataSet.setDrawFilled(true);
        lineDataSet.setHighLightColor(mContext.getResources().getColor(R.color.graphline2_2));
        lineDataSet.setDrawCircles(false);
        lineDataSet.setDrawHorizontalHighlightIndicator(false);
        lineDataSet.setDrawIcons(false);
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        lineDataSet2.setCircleRadius(7f);
        if (graphTheme.getLineColor1() != 0) {
            lineDataSet2.setColor(graphTheme.getLineColor1());
        } else {
            lineDataSet2.setColor(mContext.getResources().getColor(R.color.graphline1_1));
        }

        lineDataSet2.setLineWidth(2f);
        lineDataSet2.setDrawValues(false);
        lineDataSet2.setDrawHighlightIndicators(false);
        lineDataSet2.setDrawFilled(true);
        lineDataSet2.setHighLightColor(mContext.getResources().getColor(R.color.graphline1_2));
        lineDataSet2.setDrawCircles(false);
        lineDataSet2.setDrawHorizontalHighlightIndicator(false);
        lineDataSet2.setDrawIcons(false);
        lineDataSet2.setAxisDependency(YAxis.AxisDependency.RIGHT);

        if (Utils.getSDKInt() >= 18) {
            // fill drawable only supported on api level 18 and above
            Drawable drawableGreen = ContextCompat.getDrawable(mContext, R.drawable.fade_drawable2);
            Drawable drawableBlue = ContextCompat.getDrawable(mContext, R.drawable.fade_drawable1);
            lineDataSet.setFillDrawable(drawableGreen);
            lineDataSet2.setFillDrawable(drawableBlue);
        } else {
            lineDataSet.setFillColor(Color.BLACK);
            lineDataSet2.setFillColor(Color.BLACK);
        }
        holder.linechart2.setPinchZoom(false);
        List<ILineDataSet> dataSetList = new ArrayList<>();
        dataSetList.clear();
        dataSetList.add(lineDataSet);
        dataSetList.add(lineDataSet2);
        LineData lineData = new LineData(dataSetList);
        lineData.setValueTextColor(mContext.getResources().getColor(R.color.textwhite));
        lineData.setValueTextSize(10f);
        lineData.setValueTextColor(mContext.getResources().getColor(R.color.green));
        holder.linechart2.setDrawMarkers(false);

        //MarkerView mv = ne w MarkerView(getContext(), R.layout.item_line_graph_marker);
        CustomMarkerView mv = new CustomMarkerView(mContext, R.layout.item_line_graph_marker, selected, this.lineChartModels);
        //mv.setOffset(-60, -110);
        //mv.setOffset(-mv.getWidth() / 2, -mv.getHeight());
        mv.setChartView(holder.linechart2);
        holder.linechart2.getLegend().setTextColor(mContext.getResources().getColor(R.color.text_color));
        holder.linechart2.getLegend().setEnabled(false);
        holder.linechart2.getXAxis().setDrawAxisLine(true);
        holder.linechart2.getAxisLeft().setDrawAxisLine(true);
        holder.linechart2.getAxisRight().setDrawAxisLine(false);
        holder.linechart2.getXAxis().setDrawGridLines(false);
        holder.linechart2.setData(lineData);
        if (lineChartModels.size() > 15) {
            holder.linechart.setVisibleXRangeMaximum(15);
        } else {
            holder.linechart.fitScreen();
        }
        holder.linechart2.invalidate(); // refresh

        //cancelProgressDialog();
    }

    private double getPercent(double value, double total) {
        String TAG = "populateChartList():";
        double p = (value / total) * 100;
        if (Double.isNaN(p)) {
            p = 0d;
        }
        String s = String.format(Locale.ENGLISH, "%.1f", p);
        double m = 0.0;
        try {
            m = Double.parseDouble(s);
        } catch (Exception e) {
            m = p;
        }
        //m = 100d;
        LoggerUtils.E(TAG, "value:" + value + "/" + total + "=" + m);
        //return Math.round(p);
        return m;
    }

    public class CustomMarkerView extends MarkerView {

        String selected;
        ImageView rupeeIcon, iv_markerAmt, iv_rupeeAmt, iv_markerCount;
        private final TextView tvContent;
        private final TextView tvContent11;
        private final TextView txt_total_transaction;
        private final TextView txt_date_time;
        private final ArrayList<LineChartModel> lineChartMod;
        LinearLayout LL_marker;

        public CustomMarkerView(Context context, int layoutResource, String selected, ArrayList<LineChartModel> lineChartMod) {
            super(context, layoutResource);
            // this markerview only displays a textview
            this.selected = selected;
            this.lineChartMod = lineChartMod;
            tvContent = findViewById(R.id.tvContent);
            tvContent11 = findViewById(R.id.tvContent1);
            LL_marker = findViewById(R.id.LL_marker);
            iv_markerCount = findViewById(R.id.iv_markerCount);
            rupeeIcon = findViewById(R.id.imageView);
            iv_markerAmt = findViewById(R.id.iv_markerAmt);
            iv_rupeeAmt = findViewById(R.id.iv_rupeeAmt);
            txt_total_transaction = findViewById(R.id.txt_total_transaction);
            txt_date_time = findViewById(R.id.txt_date_time);
            if (graphTheme.getSecondaryTextColor() != 0) {
                txt_date_time.setTextColor(graphTheme.getSecondaryTextColor());
                txt_total_transaction.setTextColor(graphTheme.getSecondaryTextColor());

            }
            if (graphTheme.getPrimaryTextColor() != 0) {
                tvContent.setTextColor(graphTheme.getPrimaryTextColor());
                tvContent11.setTextColor(graphTheme.getPrimaryTextColor());
                iv_rupeeAmt.getDrawable().setTint(graphTheme.getPrimaryTextColor());
            }

            if (graphTheme.getPrimaryTextFont() != null) {
                tvContent.setTypeface(graphTheme.getPrimaryTextFont());
                tvContent11.setTypeface(graphTheme.getPrimaryTextFont());
            }

            if (graphTheme.getSecondaryTextFont() != null) {
                txt_date_time.setTypeface(graphTheme.getSecondaryTextFont());
                txt_total_transaction.setTypeface(graphTheme.getSecondaryTextFont());
            }

            if (graphTheme.getLineColor1() != 0) {
                iv_markerAmt.getDrawable().setTint(graphTheme.getLineColor1());
            }
            if (graphTheme.getLineColor2() != 0) {
                iv_markerCount.getDrawable().setTint(graphTheme.getLineColor2());
            }

            if (graphTheme.getCardBackgroundColor() != 0) {
                LL_marker.getBackground().setTint(graphTheme.getCardBackgroundColor());
            }
        }

        // callbacks everytime the MarkerView is redrawn, can be used to update the
        // content (user-interface)
        @Override
        public void refreshContent(Entry e, Highlight highlight) {
//            LoggerUtils.E("Refresh", e.getY() + "");  formatK(e.getY())


            String stotal = String.valueOf(e.getY());
//            stotal = String.format("%.2f", e.getY());
            stotal = lineChartMod.get((int) e.getX()).getTotalAmount();
            LoggerUtils.E("refreshContent", "stotal:" + stotal);
            String void1 = "";
            String void2 = "";

            String[] amt = getAmounts(stotal);
            void1 = convertToDefaultCurrencyFormatNew(amt[0]);
            void2 = amt[1];
            /*if (stotal.contains(".")) {
                void1 = stotal.substring(0, stotal.length() - 3);
                void2 = "" + stotal.substring(stotal.length() - 3);
                void1 = Util.convertToDefaultCurrencyFormat1(void1);
            } else {
                void1 = stotal.substring(0, stotal.length() - 2);
                void2 = "" + stotal.substring(stotal.length() - 2);
                void1 = Util.convertToDefaultCurrencyFormat1(void1);
            }*/

            try {
                if (!TextUtils.isEmpty(selectedDayType) && selectedDayType.equalsIgnoreCase("day")) {
                    int ipos = lineChartMod.get((int) e.getX()).getDuration().indexOf("-");
                    String date = "NA";
                    if (ipos != -1) {
                        date = lineChartMod.get((int) e.getX()).getDuration().substring(0, ipos);
                    }
                    SimpleDateFormat sdf = new SimpleDateFormat("HH", Locale.ENGLISH);
                    SimpleDateFormat sdf2 = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);
                    String time1 = sdf2.format(sdf.parse(date + ":00"));
                    txt_date_time.setText(time1);
                    LoggerUtils.E("refreshContent", "refreshContent:" + time1);
                } else if (!TextUtils.isEmpty(selectedDayType) && (selectedDayType.equalsIgnoreCase("week") || selectedDayType.equalsIgnoreCase("month"))) {
                    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                    SimpleDateFormat sdf2 = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
                    String time1 = sdf2.format(sdf1.parse(lineChartMod.get((int) e.getX()).getDuration()));
                    txt_date_time.setText(time1);
                } else if (!TextUtils.isEmpty(selectedDayType) && selectedDayType.equalsIgnoreCase("year")) {
                    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM", Locale.ENGLISH);
                    SimpleDateFormat sdf2 = new SimpleDateFormat("MMM yyyy", Locale.ENGLISH);
                    String time1 = sdf2.format(sdf1.parse(lineChartMod.get((int) e.getX()).getDuration()));
                    txt_date_time.setText(time1);
                } else {
                    txt_date_time.setText(lineChartMod.get((int) e.getX()).getDuration());
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
                txt_date_time.setText(lineChartMod.get((int) e.getX()).getxAxisValue());
                e1.printStackTrace();
            }

            //txt_date_time.setText(lineChartMod.get((int) e.getX()).getxAxisValue());
            txt_total_transaction.setText(lineChartMod.get((int) e.getX()).getTotalTransaction() + " " + mContext.getResources().getString(R.string.transactions));
            tvContent.setText(void1);
            tvContent11.setText(void2);

            /*if (void1.equalsIgnoreCase("0")) {
                tvContent.setText(void1); // set the entry-value as the display text
                tvContent11.setText(void2);
                tvContent11.setVisibility(VISIBLE);

            } else {
                tvContent.setText(void1 + ""); // set the entry-value as the display text
                tvContent11.setVisibility(GONE);
            }

            if (selected.equalsIgnoreCase("Values")) {
                rupeeIcon.setVisibility(VISIBLE);
                tvContent11.setVisibility(VISIBLE);
            } else {
                rupeeIcon.setVisibility(GONE);
                tvContent11.setVisibility(GONE);
            }*/


            super.refreshContent(e, highlight);
        }

        @Override
        public MPPointF getOffset() {
            return new MPPointF(-(getWidth() / 2), -getHeight());
        }

    }

}