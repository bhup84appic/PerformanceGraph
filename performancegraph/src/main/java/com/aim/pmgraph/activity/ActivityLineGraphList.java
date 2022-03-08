/*
 *
 *  Created by Pooran Kharol on 18/2/21 6:53 PM
 *  Copyright (c) 2021 . All rights reserved.
 *  Last modified 18/2/21 6:53 PM
 *
 */

package com.aim.pmgraph.activity;


import static com.mintoak.corelib.ApiClient.getClientNew;
import static com.mintoak.corelib.Util.convertToDefaultCurrencyFormatNew;
import static com.mintoak.corelib.Util.getAmounts;
import static com.mintoak.corelib.Util.isJSONValid;
import static com.mintoak.corelib.Util.isNetworkConnected;
import static com.mintoak.corelib.Util.popUpDialog;
import static com.mintoak.corelib.Util.setArrowRotation;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aim.pmgraph.MyValueFormatter;
import com.aim.pmgraph.R;
import com.aim.pmgraph.adapter.LineGraphListAdapter;
import com.aim.pmgraph.model.LineChartModel;
import com.aim.pmgraph.model.PerformanceItem;
import com.aim.pmgraph.model.PerformanceListItem;
import com.aim.pmgraph.model.ResPerformanceViewAll;
import com.aim.pmgraph.model.SummaryListItem;
import com.aim.pmgraph.monthutil.MonthViewUI;
import com.aim.pmgraph.theme.GraphTheme;
import com.aim.pmgraph.util.PerformanceCardUtils;
import com.aim.pmgraph.viewmodel.ViewModel_LineGraphList;
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
import com.mintoak.corelib.apiutil.ModuleConfiguration;
import com.mintoak.corelib.builder.RequestParameters;
import com.mintoak.corelib.calender_module.CustomCalendarFragment;
import com.mintoak.corelib.calender_module.calender_filterutil.CalenderDateUI;
import com.mintoak.corelib.calender_module.calender_filterutil.SelectedDate;
import com.mintoak.corelib.enums.BaseUrl;
import com.mintoak.corelib.enums.ProjectType;
import com.mintoak.corelib.location_module.filter_utils.LocationFilterUI;
import com.mintoak.corelib.location_module.fragment.BTM_SelectLocationFragment;
import com.mintoak.corelib.location_module.interfaces.LocationFilterApplyCallBack;
import com.mintoak.corelib.location_module.interfaces.LocationFilterCallback;
import com.mintoak.corelib.location_module.model.CidLevelModel;
import com.mintoak.corelib.location_module.model.CityLevelModel;
import com.mintoak.corelib.location_module.model.DbaLevelModel;
import com.mintoak.corelib.location_module.model.LocationData;
import com.mintoak.corelib.location_module.model.MidLevelModel;
import com.mintoak.corelib.location_module.model.TerminalLevelModel;
import com.mintoak.corelib.location_module.utils.LocationFilterUtils;
import com.mintoak.corelib.model.ColorSet;
import com.mintoak.corelib.model.FilterModel;
import com.mintoak.corelib.util.AppPreferences;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.jetbrains.annotations.Contract;
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


public class ActivityLineGraphList extends AppCompatActivity implements View.OnClickListener {
    private Context mContext;
    private Activity mActivity;
    private RecyclerView recycler;
    private ImageView img_back;
    private LineGraphListAdapter adapter;
    private RelativeLayout RLDayTab, RLWeekTab, RLMonthTab;
    private TextView tv_day, tv_week, tv_month;
    private Typeface typeface, typeface2;
    private AppPreferences appPreferences = new AppPreferences();
    private String selectedDayType;
    private LinearLayout LLShopLinear, linear_line_graph_parent;
    private TextView tv_shops;
    private static final String TAG = "ActivityLineGraphList";
    private TextView tvDataNotFound, txt_toolbar_title_single_store;
    private CardView cv_yearly_performance;
    private TextView tv_twelve_month;
    private LinearLayout linear_twelve;
    private GraphTheme graphTheme;
    private ImageView iv_twelve_month;

    ViewModel_LineGraphList viewModelLineGraphList;
    RequestParameters requestParameters;
    ImageView spinnerArrow;

    ColorSet colorSet;
    LocationFilterCallback locationFilterCallback;
    FilterModel filterModel;
    Toolbar line_graph_toolbar;

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
    private CardView card;
    private TextView calendarTv;
    String strDatefrom;
    String strDateto;
    private CardView all_graph_item;
    boolean isTwelveMonthClicked = false;

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

    private void getLocations(final String strDatefrom, final String strDateto, final boolean isYear) {
        highlightTwelveMonth(isYear);
        LocationFilterUtils.getSelectedLocationFilter(new LocationFilterApplyCallBack() {
            @Override
            public void allSelectedData(CidLevelModel cidLevelModel, List<DbaLevelModel> dbaLevelModelList, List<CityLevelModel> cityLevelModelList, List<MidLevelModel> midLevelModelList, List<TerminalLevelModel> terminalLevelModelList) {
                CustomCalendarFragment.isDateChanged = 1;

                callService(cidLevelModel, dbaLevelModelList, cityLevelModelList, midLevelModelList, terminalLevelModelList, strDatefrom, strDateto, isYear);

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

    private ApiInterface apiInterface;

    private void callService(CidLevelModel cidLevelModel, List<DbaLevelModel> dbaLevelModelList, List<CityLevelModel> cityLevelModelList, List<MidLevelModel> midLevelModelList, List<TerminalLevelModel> terminalLevelModelList, String strDatefrom, String strDateto, boolean isYear) {
        ArrayList<String> terminals = new ArrayList<>();
        try {
            LoggerUtils.E("LocationFilterDebug", "cidLevelModel:" + cidLevelModel.getCid());
            LoggerUtils.E("LocationFilterDebug", "cidLevelModel.getDbaLevelModels:" + cidLevelModel.getDbaLevelModels().size());
            LoggerUtils.E("LocationFilterDebug", "midLevelModelList:" + midLevelModelList.size());
            terminals.clear();
            for (int i = 0; i < midLevelModelList.size(); i++) {
                MidLevelModel model = midLevelModelList.get(i);
                LoggerUtils.E("LocationFilterDebug", "mid:" + model.getMid() + " ===========>tidList:" + model.getTerminalLevelModels().size());
                for (int j = 0; j < model.getTerminalLevelModels().size(); j++) {
                    TerminalLevelModel model1 = model.getTerminalLevelModels().get(j);
                    LoggerUtils.E("LocationFilterDebug", "tid:" + model1.getTid());
                    terminals.add(model1.getTid());
                }
            }

        } catch (Exception e) {
            LoggerUtils.E("LocationFilterDebug", "Exception:" + e.toString());
        }
        /////////////////////////////// end debug purpose///////////////////////////////////
        ArrayList<String> stringArrayList = new ArrayList<>();
        for (int i = 0; i < midLevelModelList.size(); i++) {
            stringArrayList.add(midLevelModelList.get(i).getMid());
        }
        requestParameters = null;
        requestParameters = new RequestParameters.Builder(ActivityLineGraphList.this, ProjectType.PL)
                .setBaseUrl(BaseUrl.URLMDEV4).setTerminalId("").setFCMToken("").build();
//                .setBaseUrl(BaseUrl.URLMDEV4).setCid(cidLevelModel.getCid()).setMidList(stringArrayList).setType(Type.TYPE_MERC).build();
        Log.e("setRequestParameters", requestParameters.getBaseurl());
        ModuleConfiguration.getInstance().setRequestParameters(requestParameters);
        Util.storeRequestValue(this, requestParameters);

        JSONArray ja = null;
        JSONObject jsonObject = new JSONObject();

        try {
            //terminal ids
            ja = new JSONArray(terminals);
            if (isYear) {
                selectedDayType = "year";
              /*  jsonObject.put("midList", ja.toString());
                jsonObject.put("startDate", strDatefrom);
                jsonObject.put("endDate", strDateto);
                jsonObject.put("type", "merc");*/

                //new
                jsonObject.put("tidList", ja.toString());
                //jsonObject.put("startDate", strDatefrom);
                //jsonObject.put("endDate", strDateto);
                jsonObject.put("serviceType", "year");
            } else {
                selectedDayType = "";
              /*  jsonObject.put("startDate", strDatefrom);
                jsonObject.put("endDate", strDateto);
                jsonObject.put("id", ja.toString());*/

                //new
                jsonObject.put("tidList", ja.toString());
                jsonObject.put("startDate", strDatefrom);
                jsonObject.put("endDate", strDateto);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        call_getData(strDatefrom, strDateto, jsonObject, isYear, terminals);
        setCidMidName(cidLevelModel.getCompanyName(), midLevelModelList.size() + "");
    }

    private void LineGraphMethod2(String selected, String selectedDayType) {
        //Line Chart
        this.lineChartModels = lineChartModels;
        linechart2.clear();
        final ArrayList<String> xAxisLabel = new ArrayList<>();
        ArrayList<String> xVals = new ArrayList<>();
        xAxisLabel.clear();
        valuesAmt.clear();
        valuesCount.clear();

        double totalCount = 0.0;
        double totalAmt = 0.0;
        for (int i = 0; i < lineChartModels.size(); i++) {
            totalCount += (Double.parseDouble(lineChartModels.get(i).getTotalTransaction()));
            totalAmt += (Double.parseDouble(lineChartModels.get(i).getTotalAmount()));
        }

        for (int i = 0; i < lineChartModels.size(); i++) {

            float amt = Float.parseFloat(lineChartModels.get(i).getTotalAmount());
            float count = Float.parseFloat(lineChartModels.get(i).getTotalTransaction());

            valuesAmt.add(new Entry(i, amt, ContextCompat.getDrawable(mContext, R.drawable.round_icon1)));
            valuesCount.add(new Entry(i, count, ContextCompat.getDrawable(mContext, R.drawable.round_icon2)));
            xAxisLabel.add(lineChartModels.get(i).getxAxisValue());
        }

        linechart2.setBorderWidth(5f);
        linechart2.setTouchEnabled(false);
        linechart2.setDrawGridBackground(false);
        linechart2.getDescription().setEnabled(false);
        linechart2.setDoubleTapToZoomEnabled(false);


        XAxis xAxis1 = linechart2.getXAxis();
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

        YAxis rightAxis1 = linechart2.getAxisRight();
        rightAxis1.setDrawGridLines(false);
        rightAxis1.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        rightAxis1.setTextColor(Color.TRANSPARENT);
        rightAxis1.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        rightAxis1.setZeroLineColor(Color.TRANSPARENT);
        rightAxis1.setEnabled(false);


        YAxis leftAxis1 = linechart2.getAxisLeft();
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

        LineDataSet lineDataSet = new LineDataSet(valuesCount, "");
        LineDataSet lineDataSet2 = new LineDataSet(valuesAmt, "");


//        Gson gson = new GsonBuilder().create();
//        String myCustomArray = gson.toJson(lineDataSet2);
//        LoggerUtils.E("strValuesAmt",""+myCustomArray.toString());
//
//        String myCustomArrayvaluesCount = gson.toJson(lineDataSet);
//        LoggerUtils.E("strmyCustomArrayvaluesCount",""+myCustomArrayvaluesCount.toString());


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

        if (com.github.mikephil.charting.utils.Utils.getSDKInt() >= 18) {
            // fill drawable only supported on api level 18 and above
            Drawable drawableGreen = ContextCompat.getDrawable(mContext, R.drawable.fade_drawable2);
            Drawable drawableBlue = ContextCompat.getDrawable(mContext, R.drawable.fade_drawable1);
            lineDataSet.setFillDrawable(drawableGreen);
            lineDataSet2.setFillDrawable(drawableBlue);
        } else {
            lineDataSet.setFillColor(Color.BLACK);
            lineDataSet2.setFillColor(Color.BLACK);
        }
        linechart2.setPinchZoom(false);
        List<ILineDataSet> dataSetList = new ArrayList<>();
        dataSetList.clear();
        dataSetList.add(lineDataSet);
        dataSetList.add(lineDataSet2);
        LineData lineData = new LineData(dataSetList);
        lineData.setValueTextColor(mContext.getResources().getColor(R.color.textwhite));
        lineData.setValueTextSize(10f);
        lineData.setValueTextColor(mContext.getResources().getColor(R.color.green));
        linechart2.setDrawMarkers(false);

        //MarkerView mv = ne w MarkerView(getContext(), R.layout.item_line_graph_marker);
        CustomMarkerView mv = new CustomMarkerView(mContext, R.layout.item_line_graph_marker, selected, selectedDayType);
        //mv.setOffset(-60, -110);
        //mv.setOffset(-mv.getWidth() / 2, -mv.getHeight());
        mv.setChartView(linechart2);
        linechart2.getLegend().setTextColor(mContext.getResources().getColor(R.color.text_color));
        linechart2.getLegend().setEnabled(false);
        linechart2.getXAxis().setDrawAxisLine(true);
        linechart2.getAxisLeft().setDrawAxisLine(true);
        linechart2.getAxisRight().setDrawAxisLine(false);
        linechart2.getXAxis().setDrawGridLines(false);
        linechart2.setData(lineData);
        if (lineChartModels.size() > 15) {
            linechart.setVisibleXRangeMaximum(15);
        } else {
            linechart.fitScreen();
        }
        linechart2.invalidate(); // refresh

        //cancelProgressDialog();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph_list);
        mContext = ActivityLineGraphList.this;
        mActivity = ActivityLineGraphList.this;

        colorSet = ModuleConfiguration.getInstance().getColors();

        graphTheme = (GraphTheme) getIntent().getSerializableExtra("GraphTheme");
        appPreferences = new AppPreferences();
//        locationFilterCallback = ModuleConfiguration.getInstance().getLocationFilterCallback();
        viewModelLineGraphList = new ViewModelProvider(this).get(ViewModel_LineGraphList.class);

        typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.montserrat_semibold);
        typeface2 = ResourcesCompat.getFont(getApplicationContext(), R.font.montserrat_regular);

        recycler = findViewById(R.id.recycler);
        line_graph_toolbar = findViewById(R.id.toolbar);
        linear_line_graph_parent = findViewById(R.id.linear_line_graph_parent);
        spinnerArrow = findViewById(R.id.spinnerArrow);
        txt_toolbar_title_single_store = findViewById(R.id.toolbar_title);
        linear_twelve = findViewById(R.id.linear_twelve);
        img_back = findViewById(R.id.img_back);
        if (colorSet != null && colorSet.getToolbarTitleColor() != 0) {
            com.aim.pmgraph.util.Utils.setTintImageView(img_back, colorSet.getToolbarTitleColor());
        }
        RLDayTab = findViewById(R.id.RLDayTab);
        RLWeekTab = findViewById(R.id.RLWeekTab);
        RLMonthTab = findViewById(R.id.RLMonthTab);
        tv_day = findViewById(R.id.tv_day);
        tv_week = findViewById(R.id.tv_week);
        tv_month = findViewById(R.id.tv_month);
        LLShopLinear = findViewById(R.id.LLShopLinear);
        tv_shops = findViewById(R.id.tv_shops);
        all_graph_item = findViewById(R.id.all_graph_item);
        tv_shops.setSelected(true);
        tvDataNotFound = findViewById(R.id.tvDataNotFound);
        calendarTv = findViewById(R.id.calendarTv);
        tv_twelve_month = findViewById(R.id.tv_twelve_month);
        iv_twelve_month = findViewById(R.id.iv_twelve_month);
        cv_yearly_performance = findViewById(R.id.cv_yearly_performance);


        //this.tv_amount1 = itemView.findViewById(R.id.tv_amount1);
        //this.tv_amount2 = itemView.findViewById(R.id.tv_amount2);
        RLArrowExpander = findViewById(R.id.RLArrowExpander);
        arrowExpander = findViewById(R.id.arrowExpander);
        linechart = findViewById(R.id.linechart);
        linechart2 = findViewById(R.id.linechart2);
        iv_graph = findViewById(R.id.iv_graph);
        LLlinechart = findViewById(R.id.LLlinechart);
        txt_valueTxtLineGraph = findViewById(R.id.valueTxtLineGraph);
        txt_transactionTxtLineGraph = findViewById(R.id.transactionTxtLineGraph);
        viewValue = findViewById(R.id.viewValue);
        viewTranscation = findViewById(R.id.viewTransaction);
        tv_amount = findViewById(R.id.tv_amount);
        tv_transaction = findViewById(R.id.tv_transaction);
        layout_performancedata = findViewById(R.id.layout_performancedata);
        layout_no_performancedata = findViewById(R.id.layout_no_performancedata);
        LL_second_chart = findViewById(R.id.LL_second_chart);
        tv_empty_state_text = findViewById(R.id.tv_empty_state_text);
        iv_empty_state_image = findViewById(R.id.iv_empty_state_image);
        tv_sale = findViewById(R.id.tv_sale);
        tv_txn = findViewById(R.id.tv_txn);
        iv_sale = findViewById(R.id.iv_sale);
        iv_txn = findViewById(R.id.iv_txn);
        card = findViewById(R.id.card);

        img_back.setOnClickListener(this);
        // linear_twelve.setOnClickListener(this);

        RLDayTab.setOnClickListener(this);
        RLWeekTab.setOnClickListener(this);
        RLMonthTab.setOnClickListener(this);
        LLShopLinear.setOnClickListener(this);

        cv_yearly_performance.setOnClickListener(this);

        customiseColors();
        String startDate, endDate;

        startDate = getIntent().getStringExtra("startDate");
        endDate = getIntent().getStringExtra("endDate");
        isTwelveMonthClicked = getIntent().getBooleanExtra("isTwelveMonthClicked", false);

        strDatefrom = startDate;
        strDateto = endDate;
        if (!(LocationFilterUtils.globalCidLevel != null && LocationFilterUtils.globalCidLevel.size() > 0)) {
            LocationFilterUtils.getTerminalInfoList();
        }
        getLocations(strDatefrom, strDateto, isTwelveMonthClicked);

        filterModel = ModuleConfiguration.getInstance().getFilterModel();


        //call_getData("day", null, false);

        setCalendarData(strDatefrom, strDateto);


        calendarTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.avoidDoubleClicks(v);

//                Bundle bundle = new Bundle();
//                ApiNetworkUtils.captureEvent("performance-datefilter-icon-clicked", "Performance", "performance",
//                        "", "",
//                        "", bundle, ActivityLineGraphList.this);


                if (isNetworkConnected(mContext)) {
                    CustomCalendarFragment fragment = new CustomCalendarFragment().newInstance("", new ArrayList<String>());
                    fragment.setCalendarCallback(new CustomCalendarFragment.CalendarCallback() {
                        @Override
                        public void onCallCalendar(String fromDate, String toDate) {
                            LoggerUtils.D("Dates", fromDate + "------------" + toDate);
                            if (fromDate.equalsIgnoreCase("") && toDate.equalsIgnoreCase("")) {
                            }

                            strDatefrom = fromDate;
                            strDateto = toDate;
                            isTwelveMonthClicked = false;
                            setCalendarData(fromDate, toDate);
                            getLocations(strDatefrom, strDateto, false);
                            if (CalenderDateUI.calenderDataToAppCallback != null) {
                                SelectedDate selectedDate = new SelectedDate(strDatefrom, strDateto);
                                CalenderDateUI.calenderDataToAppCallback.onDateApplied(selectedDate);
                            }
                        }
                    });
                    fragment.show(getSupportFragmentManager(), "");
                } else {
                    popUpDialog(ActivityLineGraphList.this, "", getString(R.string.no_internet));
                }
            }
        });

        if (LLlinechart.isExpanded()) {
            //item.setVisible(false);
            setArrowRotation(arrowExpander, 0f);
            iv_graph.expand();
            LLlinechart.collapse();
        }

        RLArrowExpander.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (LLlinechart.isExpanded()) {
                    //item.setVisible(false);
                    setArrowRotation(arrowExpander, 0f);
                    iv_graph.expand();
                    LLlinechart.collapse();

//                    Bundle bundle = new Bundle();
//                    bundle.putString("location", "All Locations");
//                    ApiNetworkUtils.captureEvent("performance-collapse-icon-clicked", "Performance", "performance",
//                            "", "",
//                            "", bundle, ActivityLineGraphList.this);

                } else {
                    //item.setVisible(true);
                    setArrowRotation(arrowExpander, 180f);
                    LLlinechart.expand();
                    iv_graph.collapse();

//                    Bundle bundle = new Bundle();
//                    bundle.putString("location", "All Locations");
//                    ApiNetworkUtils.captureEvent("performance-expand-icon-clicked", "Performance", "performance",
//                            "", "",
//                            "", bundle, ActivityLineGraphList.this);
                }
            }
        });

//        Bundle bundle = new Bundle();
//        ApiNetworkUtils.captureEvent("performance-screen-viewed", "Performance", "performance", "screen", "",
//                "viewed", bundle, mContext);
    }

    private void getDuration(@NotNull String duration) {
        switch (duration) {
            case "day":
                setTypeFace(tv_day, tv_week, tv_month, tv_twelve_month);
                break;
            case "week":
                setTypeFace(tv_week, tv_day, tv_month, tv_twelve_month);
                break;
            case "month":
                setTypeFace(tv_month, tv_week, tv_day, tv_twelve_month);
                break;
            case "year":
                setTypeFace(tv_twelve_month, tv_week, tv_day, tv_month);
                break;
        }
    }

    private void setCidMidName(String cidName, String midSize) {
        tv_shops.setText(cidName + " (" + midSize + ")");

    }


    private void calculateGraph(List<PerformanceListItem> performanceListItemList) {


    }

    /*private void callService(CidLevelModel cidLevelModel, List<DbaLevelModel> dbaLevelModelList, List<CityLevelModel> cityLevelModelList, List<MidLevelModel> midLevelModelList, List<TerminalLevelModel> terminalLevelModelList, String strDatefrom, String strDateto, boolean isYear) {
        //ArrayList<String> terminals = new ArrayList<>();
        try {
            LoggerUtils.E("LocationFilterDebug", "cidLevelModel:" + cidLevelModel.getCid());
            LoggerUtils.E("LocationFilterDebug", "cidLevelModel.getDbaLevelModels:" + cidLevelModel.getDbaLevelModels().size());
            LoggerUtils.E("LocationFilterDebug", "midLevelModelList:" + midLevelModelList.size());
            //terminals.clear();
            for (int i = 0; i < midLevelModelList.size(); i++) {
                MidLevelModel model = midLevelModelList.get(i);
                LoggerUtils.E("LocationFilterDebug", "mid:" + model.getMid() + " ===========>tidList:" + model.getTerminalLevelModels().size());
                for (int j = 0; j < model.getTerminalLevelModels().size(); j++) {
                    TerminalLevelModel model1 = model.getTerminalLevelModels().get(j);
                    LoggerUtils.E("LocationFilterDebug", "tid:" + model1.getTid());
                    //terminals.add(model1.getTid());
                }

            }

        } catch (Exception e) {
            LoggerUtils.E("LocationFilterDebug", "Exception:" + e.toString());
        }
        /////////////////////////////// end debug purpose///////////////////////////////////
        ArrayList<String> stringArrayList = new ArrayList<>();
        for (int i = 0; i < midLevelModelList.size(); i++) {
            stringArrayList.add(midLevelModelList.get(i).getMid());
        }
        requestParameters = null;
        requestParameters = new RequestParameters.Builder(ActivityLineGraphList.this, ProjectType.TYPE_ONE_APP_BIZVIEW)
                .setBaseUrl(BaseUrl.URL_ONE_APP).setCid(cidLevelModel.getCid()).setMidList(stringArrayList).setType(Type.TYPE_MERC).build();
        Log.e("setRequestParameters", requestParameters.getBaseurl());
        ModuleConfiguration.getInstance().setRequestParameters(requestParameters);

        JSONArray ja = null;
        JSONObject jsonObject = new JSONObject();

        try {
            //terminal ids
            ja = new JSONArray(stringArrayList);
            if (isYear) {
                selectedDayType = "year";
                jsonObject.put("midList", ja.toString());
                jsonObject.put("startDate", strDatefrom);
                jsonObject.put("endDate", strDateto);
                jsonObject.put("type", "merc");

                //new
                /*jsonObject.put("tidList", ja.toString());
                jsonObject.put("startDate", strDatefrom);
                jsonObject.put("endDate", strDateto);
                jsonObject.put("serviceType", "year");*/
           /* } else {
                selectedDayType = "";
                jsonObject.put("startDate", strDatefrom);
                jsonObject.put("endDate", strDateto);
                jsonObject.put("id", ja.toString());

                //new
                /*jsonObject.put("tidList", ja.toString());
                jsonObject.put("startDate", strDatefrom);
                jsonObject.put("endDate", strDateto);*/
            /*}
        } catch (JSONException e) {
            e.printStackTrace();
        }
        call_getData(strDatefrom, strDateto, jsonObject, isYear);
        setCidMidName(cidLevelModel.getCompanyName(), midLevelModelList.size() + "");
    }*/

        private void call_getData(final String fromDate, final String toDate, final JSONObject jsonObject, final boolean isYearOrNot, final ArrayList<String> terminals) {

        if (isNetworkConnected(mActivity)) {

            requestParameters = ModuleConfiguration.getInstance().getRequestParameters();
            if (requestParameters == null) {
                requestParameters = Util.getRequestValue(this);
            }

            LoggerUtils.E("performanceListItemList", jsonObject.toString());
            Util.showProgressDialog(ActivityLineGraphList.this);
            if (ApiConstants.publicKey == null) {
                try {
                    apiInterface = getClientNew(mContext, requestParameters).create(ApiInterface.class);
                    Observable<String> call = apiInterface.callAPIURL();
                    RXJavaCaller.GetKey(call, new RXJavaCaller.OnKeyReceived() {
                        @Override
                        public void onKeyReceivedSuccess() {
                            Util.cancelProgressDialog();
                            call_getData(fromDate, toDate, jsonObject, isYearOrNot, terminals);
                        }

                        @Override
                        public void onKeyReceivedError(String error) {
                            Util.cancelProgressDialog();
                            Log.e("FUNNEL_DATA", error);
                        }
                    });
                } catch (Exception e) {
                    LoggerUtils.E(TAG, "Exception3:" + e.toString());
                    Util.cancelProgressDialog();
                    Util.popUpDialog(ActivityLineGraphList.this, "", "Something went wrong");
                }
            } else {
                viewModelLineGraphList.setListData(jsonObject, requestParameters, isYearOrNot);
                viewModelLineGraphList.getListData().observe(this, new Observer<String>() {
                    @Override
                    public void onChanged(String result) {
                        Log.e("PERFORMANCE_VIEW_ALL", "PerformanceViewAll " + result);
                        Util.cancelProgressDialog();
                        if (result.equals(ApiConstants.SESSION_EXPIRED)) {

                            if (PerformanceCardUtils.mPerCardApiSessionExpired != null) {
                                PerformanceCardUtils.mPerCardApiSessionExpired.onSessionExpired();
                            }
                        } else if (result.equals("E105")) {
                            call_getData(fromDate, toDate, jsonObject, isYearOrNot, terminals);
                        } else if (result.equalsIgnoreCase("Something went wrong") || result.equalsIgnoreCase("Connection error") || result.equalsIgnoreCase("Something went wrong. Please try again.")) {
                            Util.popUpDialog(ActivityLineGraphList.this, "", result);
                        } else if (isJSONValid(result)) {
                            //  mCustomerListView.stopProgress();
                            JSONObject payLaterResponse = null;
                            try {
                                payLaterResponse = new JSONObject(result);

                                Log.e("onSuccess", payLaterResponse.toString());

                            } catch (JSONException e) {
                                //payLaterResponse = new JSONObject();
                                LoggerUtils.E("Exception", e.toString());
                            }
                            GsonBuilder gsonBuilder = new GsonBuilder();
                            Gson gson = gsonBuilder.create();

                            ResPerformanceViewAll summary = gson.fromJson(payLaterResponse.toString(), ResPerformanceViewAll.class);


                            if (summary != null) {
                                recycler.setVisibility(View.VISIBLE);
                                if (summary.getPerformanceList() != null && summary.getPerformanceList().size() > 0) {
                                    if (isYearOrNot) {
                                        setTypeFace(tv_twelve_month, tv_month, tv_week, tv_day);
                                    } else {
                                        setTypeFace(tv_month, tv_week, tv_day, tv_twelve_month);
                                    }
                                    setTheAdapter(summary.getPerformanceList(), selectedDayType);
                                } else {
                                    setDummyTheAdapter(terminals);
                                    tvDataNotFound.setVisibility(View.GONE);

                                }
                            }
                        } else {
                            Util.popUpDialog(ActivityLineGraphList.this, "", result);
                        }


                    }
                });
            }


        } else {
            Util.cancelProgressDialog();
            //Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            Util.popUpDialog(ActivityLineGraphList.this, "", getString(R.string.no_internet));
        }

    }

    private ArrayList<String> getAreaAndMid(String tid) {
        ArrayList<String> resultArray = new ArrayList<>();

        if (LocationFilterUtils.globalCidLevel != null && LocationFilterUtils.globalCidLevel.size() > 0) {
            for (int i = 0; i < LocationFilterUtils.globalCidLevel.size(); i++) {
                //cid level
                CidLevelModel cidLevelModel = LocationFilterUtils.globalCidLevel.get(i);
                for (int j = 0; j < cidLevelModel.getDbaLevelModels().size(); j++) {
                    //dba level
                    DbaLevelModel dbaLevelModel = cidLevelModel.getDbaLevelModels().get(j);
                    for (int k = 0; k < dbaLevelModel.getCityLevelList().size(); k++) {
                        //city level
                        CityLevelModel cityLevelModel = dbaLevelModel.getCityLevelList().get(k);
                        for (int l = 0; l < cityLevelModel.getMidLevelModels().size(); l++) {
                            //mid level
                            MidLevelModel midLevelModel = cityLevelModel.getMidLevelModels().get(l);
                            for (int m = 0; m < midLevelModel.getTerminalLevelModels().size(); m++) {
                                //tid level
                                TerminalLevelModel model = midLevelModel.getTerminalLevelModels().get(m);
                                if (model.getTid().equals(tid)) {
                                    resultArray.add(midLevelModel.getMid());
                                    resultArray.add(midLevelModel.getLocation());
                                    resultArray.add(midLevelModel.getPinCode());
                                    resultArray.add(midLevelModel.getAddress());
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }


        return resultArray;
    }

    private void setDummyTheAdapter(@NotNull ArrayList<String> terminals) {

        List<PerformanceListItem> performanceListNew = new ArrayList<>();
        List<PerformanceListItem> performanceListFinal = new ArrayList<>();
        for (int i = 0; i < terminals.size(); i++) {
            String terminal = terminals.get(i);
            PerformanceListItem item = new PerformanceListItem();
            ArrayList<String> midAndArea = getAreaAndMid(terminal);
            if (midAndArea.size() != 0) {
                item.setMid(midAndArea.get(0));
                item.setArea(midAndArea.get(1));
                item.setPincode(midAndArea.get(2));
                item.setAddress(midAndArea.get(3));
                item.setTid(terminal);

            }
            performanceListNew.add(item);
        }

        //mid list
        ArrayList<String> allPinCodes = new ArrayList<>();

        for (int i = 0; i < performanceListNew.size(); i++) {
            PerformanceListItem list = performanceListNew.get(i);
            allPinCodes.add(list.getPincode());
        }

        LinkedHashSet<String> hashSet = new LinkedHashSet<>(allPinCodes);
        ArrayList<String> allPinCodesFinal = new ArrayList<>(hashSet);

        for (int i = 0; i < allPinCodesFinal.size(); i++) {
            String pincode = allPinCodesFinal.get(i);
            LoggerUtils.E("RESULT_ARRAY", " pincode:" + pincode);
            String area = "";
            String mid = "";
            String address = "";
            ArrayList<String> tidList = new ArrayList<>();
            boolean visible = false;

            for (int j = 0; j < performanceListNew.size(); j++) {
                PerformanceListItem list = performanceListNew.get(j);
                if (pincode.equals(list.getPincode())) {
                    LoggerUtils.E("RESULT_ARRAY", pincode + " == " + list.getPincode());
                    area = list.getArea();
                    mid = list.getMid();
                    address = list.getAddress();
                    tidList.add(list.getTid());
                    visible = list.isVisible();
                }

            }
            PerformanceListItem counterList = new PerformanceListItem();
            counterList.setMid(mid);
            counterList.setPincode(pincode);
            counterList.setAddress(address);
            counterList.setTidList(tidList);
            counterList.setArea(area);
            counterList.setVisible(visible);
            counterList.setSummaryList(null);

            performanceListFinal.add(counterList);
        }

        adapter = new LineGraphListAdapter(mContext, performanceListFinal, selectedDayType, graphTheme, strDatefrom, strDateto);
        //adapter.setHasStableIds(true);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(horizontalLayoutManager);
        recycler.setAdapter(adapter);
        recycler.setNestedScrollingEnabled(true);
        populateData(adapter, recycler);
        // all_graph_item.setVisibility(View.GONE);
        setOnAllLocationCard(performanceListNew);

    }

    private void setTheAdapter(List<PerformanceListItem> performanceList, String selectedDayType) {
        recycler.setVisibility(View.VISIBLE);
        tvDataNotFound.setVisibility(View.GONE);
        List<PerformanceListItem> performanceListNew = new ArrayList<>();
        List<PerformanceListItem> performanceListFinal = new ArrayList<>();
        for (int i = 0; i < performanceList.size(); i++) {
            PerformanceListItem item = performanceList.get(i);
            ArrayList<String> midAndArea = getAreaAndMid(item.getTid());
            if (midAndArea.size() != 0) {
                item.setMid(midAndArea.get(0));
                item.setArea(midAndArea.get(1));
                item.setPincode(midAndArea.get(2));
                item.setAddress(midAndArea.get(3));
            }

            performanceListNew.add(item);

        }

        //mid list
        ArrayList<String> allPinCodes = new ArrayList<>();

        for (int i = 0; i < performanceListNew.size(); i++) {
            PerformanceListItem list = performanceListNew.get(i);
            allPinCodes.add(list.getPincode());
        }

        LinkedHashSet<String> hashSetMIDs = new LinkedHashSet<>(allPinCodes);
        ArrayList<String> allPinCodesFinal = new ArrayList<>(hashSetMIDs);

        for (int i = 0; i < allPinCodesFinal.size(); i++) {
            String pincode = allPinCodesFinal.get(i);
            LoggerUtils.E("RESULT_ARRAY", " pincode:" + pincode);
            String area = "";
            String mid = "";
            String address = "";
            ArrayList<String> tidList = new ArrayList<>();
            List<SummaryListItem> summaryListItems = new ArrayList<>();
            List<SummaryListItem> summaryListItemsFinal = new ArrayList<>();
            boolean visible = false;

            for (int j = 0; j < performanceListNew.size(); j++) {
                PerformanceListItem list = performanceListNew.get(j);
                if (pincode.equals(list.getPincode())) {
                    LoggerUtils.E("RESULT_ARRAY", pincode + " == " + list.getPincode());
                    area = list.getArea();
                    mid = list.getMid();
                    address = list.getAddress();
                    tidList.add(list.getTid());
                    summaryListItems.addAll(list.getSummaryList());
                    visible = list.isVisible();
                }
            }

            ArrayList<String> listDurations = new ArrayList<>();

            for (int k = 0; k < summaryListItems.size(); k++) {
                SummaryListItem item1 = summaryListItems.get(k);
                listDurations.add(item1.getDuration());
            }

            for (int a = 0; a < listDurations.size(); a++) {
                LoggerUtils.E("RESULT_ARRAY", "listDurations:" + listDurations.get(a));
            }
            LinkedHashSet<String> hashSet = new LinkedHashSet<>(listDurations);

            ArrayList<String> listDurationsFinal = new ArrayList<>(hashSet);

            for (int b = 0; b < listDurationsFinal.size(); b++) {
                LoggerUtils.E("RESULT_ARRAY", " listDurationsFinal:" + listDurationsFinal.get(b));
            }

           /* Collections.sort(listDurationsFinal, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return o1.compareTo(o2);
                }
            });*/

            for (int m = 0; m < listDurationsFinal.size(); m++) {
                LoggerUtils.E("RESULT_ARRAY", " listDurationsFinal:sort:" + listDurationsFinal.get(m));
                double totalamount = 0D;
                int totaltransaction = 0;
                String duration = listDurationsFinal.get(m);
                for (int n = 0; n < summaryListItems.size(); n++) {
                    SummaryListItem item = summaryListItems.get(n);
                    if (item.getDuration().equals(duration)) {
                        totalamount = totalamount + Double.parseDouble(item.getTotalamount());
                        totaltransaction = totaltransaction + Integer.parseInt(item.getTotaltransaction());
                    }
                }
                if (totaltransaction > 0) {
                    SummaryListItem item = new SummaryListItem();
                    item.setDuration(duration);
                    item.setTotalamount(totalamount + "");
                    item.setTotaltransaction(totaltransaction + "");
                    summaryListItemsFinal.add(item);
                }
            }

            for (int c = 0; c < summaryListItemsFinal.size(); c++) {
                LoggerUtils.E("RESULT_ARRAY", " summaryListItemsFinal:" + summaryListItemsFinal.get(c).toString());
            }

            PerformanceListItem counterList = new PerformanceListItem();
            counterList.setMid(mid);
            counterList.setPincode(pincode);
            counterList.setAddress(address);
            counterList.setTidList(tidList);
            counterList.setArea(area);
            counterList.setVisible(visible);
            counterList.setSummaryList(summaryListItemsFinal);

            performanceListFinal.add(counterList);
        }

        adapter = new LineGraphListAdapter(mContext, performanceListFinal, selectedDayType, graphTheme, strDatefrom, strDateto);
        //adapter.setHasStableIds(true);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(horizontalLayoutManager);
        recycler.setAdapter(adapter);
        recycler.setNestedScrollingEnabled(true);
        populateData(adapter, recycler);

        setOnAllLocationCard(performanceListNew);
    }

    private void setOnAllLocationCard(List<PerformanceListItem> performanceListNew) {
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
                    if (strDatefrom.equals(strDateto)) {
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
                LoggerUtils.E("ALL_LOCATIONS", " durations:" + listDurationsFinal.get(i));
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
                String[] amounts = getAmounts(String.valueOf(transactionAmount));
                tv_amount.setText("₹ " + convertToDefaultCurrencyFormatNew(amounts[0]) + "" + amounts[1]);
                tv_transaction.setText(transactionCount + " " + mContext.getResources().getString(R.string.transactions));
                all_graph_item.setVisibility(View.VISIBLE);
                tv_amount.setVisibility(View.VISIBLE);
                tv_transaction.setVisibility(View.VISIBLE);
                LL_second_chart.setVisibility(View.VISIBLE);
                setPerformanceGraph(performanceItems, "PERFORMANCE_DATA", selectedDayType, strDatefrom, strDateto);
            } else {
                // all_graph_item.setVisibility(View.VISIBLE);

                tv_amount.setVisibility(View.GONE);
                tv_transaction.setVisibility(View.GONE);
                LL_second_chart.setVisibility(View.GONE);
                Util.setVisibility(layout_no_performancedata, true);
                Util.setVisibility(layout_performancedata, false);
            }
        } catch (Exception e) {
            LoggerUtils.E("ALL_CARD_EXCEPTION", e.toString());
        }
    }

    public ArrayList<LineChartModel> lineChartModels = new ArrayList<>();

    void customiseColors() {
        //line_graph_toolbar.setBackgroundColor(colorSet.getToolbarColor());
        if (colorSet != null && colorSet.getBackgroundColor() != 0) {
            //linear_line_graph_parent.setBackgroundColor(colorSet.getBackgroundColor());
        }
        if (colorSet != null && colorSet.getToolbarTitleColor() != 0) {
            txt_toolbar_title_single_store.setTextColor(colorSet.getToolbarTitleColor());
        }
        if (graphTheme.getPrimaryTextColor() != 0) {
            tv_shops.setTextColor(graphTheme.getPrimaryTextColor());
            spinnerArrow.setColorFilter(graphTheme.getPrimaryTextColor());
        }
    }

    private void setPerformanceGraph(@NotNull List<PerformanceItem> performance, String TAG, String selectedDayType, final String startDate, final String endDate) {
        LoggerUtils.E("PERFORMANCE_SELECTED_TYPE", selectedDayType);
        //2019-11-01
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd", Locale.ENGLISH);
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        SimpleDateFormat sdf3 = new SimpleDateFormat("HH", Locale.ENGLISH);
        SimpleDateFormat sdf4 = new SimpleDateFormat("yyyy-MM", Locale.ENGLISH);
        SimpleDateFormat sdf5 = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);
        LoggerUtils.E(TAG, "setPerformanceGraph:size():" + performance.size());
        if (TextUtils.isEmpty(selectedDayType)) {
            selectedDayType = "";
        }

        List<PerformanceItem> performanceAll = new ArrayList<>();
        List<PerformanceItem> performanceNew = new ArrayList<>();

        performanceAll.clear();

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

            } catch (Exception e) {
                LoggerUtils.E(TAG, "Exception 12:" + e.toString());
            }
        } else {

            if (startDate.equalsIgnoreCase(endDate)) {
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
//                            SimpleDateFormat sdfTime1 = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
//                            SimpleDateFormat sdfTime2 = new SimpleDateFormat("hh aa", Locale.ENGLISH);
//                            String time1 = sdfTime2.format(sdfTime1.parse(date1 + ":00"));
                            LoggerUtils.E("refreshContent", "refreshContent:" + date1);
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

        Collections.sort(lineChartModels, new Comparator<LineChartModel>() {
            @Override
            public int compare(LineChartModel lhs, LineChartModel rhs) {
                SimpleDateFormat sdfTime2;
                if (startDate.equals(endDate)) {
                    sdfTime2 = new SimpleDateFormat("hh aa", Locale.ENGLISH);
                } else {
                    sdfTime2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                }
                try {
                    return Objects.requireNonNull(sdfTime2.parse(lhs.getxAxisValue())).compareTo(sdfTime2.parse(rhs.getxAxisValue()));
                } catch (ParseException e) {
                    return lhs.getDuration().compareTo(rhs.getDuration());
                }


            }
        });

        LineGraphMethod("", selectedDayType);
        LineGraphMethod2("", selectedDayType);
    }

    ArrayList<Entry> valuesAmt = new ArrayList<Entry>();
    ArrayList<Entry> valuesCount = new ArrayList<Entry>();

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

                valuesAmt.add(new Entry(i, amt, ContextCompat.getDrawable(mContext, R.drawable.round_icon1)));
                valuesCount.add(new Entry(i, count, ContextCompat.getDrawable(mContext, R.drawable.round_icon2)));

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
            if (graphTheme.getSecondaryTextColor() != 0) {
                xAxis1.setTextColor(graphTheme.getSecondaryTextColor());
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
            Typeface typeface = ResourcesCompat.getFont(mContext, R.font.montserrat_semibold);
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
            lineDataSetAmt.setCircleHoleColor(ContextCompat.getColor(ActivityLineGraphList.this, R.color.graphline_color1));
            lineDataSetAmt.setCircleHoleRadius(4f);

            lineDataSetAmt.disableDashedLine();
            lineDataSetCount.disableDashedLine();

            lineDataSetAmt.setCircleRadius(7f);
            lineDataSetAmt.setColor(graphTheme.getLineColor1());
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
            lineDataSetCount.setCircleHoleColor(ContextCompat.getColor(ActivityLineGraphList.this, R.color.graphline_color2));
            lineDataSetCount.setCircleHoleRadius(4f);

            if (com.github.mikephil.charting.utils.Utils.getSDKInt() >= 18) {
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

            CustomMarkerView mv = new CustomMarkerView(mContext, R.layout.item_line_graph_marker, selected, selectedDayType);
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
                        linechart.getData().getDataSets().get(0).getEntriesForXValue(i).get(0).setIcon(ContextCompat.getDrawable(mContext, R.drawable.green_round_icon_fix));
                        linechart.getData().getDataSets().get(1).getEntriesForXValue(i).get(0).setIcon(ContextCompat.getDrawable(mContext, R.drawable.blue_round_icon_fix));
                    } else {
                        linechart.getData().getDataSets().get(0).getEntriesForXValue(i).get(0).setIcon(ContextCompat.getDrawable(mContext, R.drawable.round_icon1));
                        linechart.getData().getDataSets().get(1).getEntriesForXValue(i).get(0).setIcon(ContextCompat.getDrawable(mContext, R.drawable.round_icon2));
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
            stotal = lineChartModels.get((int) e.getX()).getTotalAmount();
            LoggerUtils.E("refreshContent", "stotal:" + stotal);
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
                } else if (!TextUtils.isEmpty(selectedDayType) && selectedDayType.equalsIgnoreCase("year")) {
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

    private void populateData(LineGraphListAdapter adapter, RecyclerView recycler) {
        adapter.notifyDataSetChanged();
        runLayoutAnimation(recycler);
    }

    private void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_recycler_top);

        recyclerView.setLayoutAnimation(controller);
        Objects.requireNonNull(recyclerView.getAdapter()).notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    @Override
    public void onClick(View v) {
        Util.avoidDoubleClicks(v);
        if (v == img_back) {
//            Bundle bundle = new Bundle();
//            ApiNetworkUtils.captureEvent("performance-back-icon-clicked", "Performance", "performance", "back", "icon",
//                    "clicked", bundle, ActivityLineGraphList.this);
            finish();
        }

        if (v == RLDayTab) {


            setTypeFace(tv_day, tv_week, tv_month, tv_twelve_month);
            //call_getData("day", null, false);
        }
        if (v == RLWeekTab) {
            setTypeFace(tv_week, tv_day, tv_month, tv_twelve_month);
            //call_getData("week", null, false);
        }
        if (v == RLMonthTab) {
            setTypeFace(tv_month, tv_week, tv_day, tv_twelve_month);
            //call_getData("month", null, false);
        }
    /*    if (v == linear_twelve) {
            setTypeFace(tv_month, tv_week, tv_day, tv_twelve_month);
            call_getData("", "", null,true);
        }*/

        if (v == cv_yearly_performance) {
            selectedDayType = "year";
            if (isNetworkConnected(mActivity)) {
                if (isTwelveMonthClicked) {
                    highlightTwelveMonth(false);
                    tv_twelve_month.setTypeface(typeface2);
                    isTwelveMonthClicked = false;
                    getLocations(strDatefrom, strDateto, false);
                    if (MonthViewUI.monthViewToAppCallback != null) {
                        MonthViewUI.monthViewToAppCallback.onMonthViewChange(isTwelveMonthClicked);
                    }
                } else {
                    isTwelveMonthClicked = true;
                    getLocations(strDatefrom, strDateto, true);
                    if (MonthViewUI.monthViewToAppCallback != null) {
                        MonthViewUI.monthViewToAppCallback.onMonthViewChange(isTwelveMonthClicked);
                    }
                }
            } else {
                popUpDialog(mActivity, "", getString(R.string.no_internet));
            }
        }

        if (v == LLShopLinear) {
            try {

                Bundle bundle = new Bundle();
//                ApiNetworkUtils.captureEvent("performance-locationfilter-icon-clicked", "Performance", "performance",
//                        "", "",
//                        "", bundle, ActivityLineGraphList.this);

                /*if (locationFilterCallback != null) {
                    locationFilterCallback.onLocationClick(SaleCounter.SALE_LIST);
                    finish();
                }*/
                BTM_SelectLocationFragment fragment = new BTM_SelectLocationFragment(new LocationFilterApplyCallBack() {
                    @Override
                    public void allSelectedData(CidLevelModel cidLevelModel, List<DbaLevelModel> dbaLevelModelList, List<CityLevelModel> cityLevelModelList, List<MidLevelModel> midLevelModelList, List<TerminalLevelModel> terminalLevelModelList) {
                        if (LocationFilterUI.mLocationDataToAppCallback != null) {
                            LocationData locationData = new LocationData(cidLevelModel, dbaLevelModelList, cityLevelModelList, midLevelModelList, terminalLevelModelList, LocationFilterUtils.FilterCidLevel, LocationFilterUtils.selectedCidLevel);
                            LocationFilterUI.mLocationDataToAppCallback.onLocationApplied(locationData);
                        }
                        callService(cidLevelModel, dbaLevelModelList, cityLevelModelList, midLevelModelList, terminalLevelModelList, strDatefrom, strDateto, false);
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
                fragment.show(getSupportFragmentManager(), "");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void highlightTwelveMonth(boolean b) {
        if (b) {
//            Bundle bundle = new Bundle();
//            ApiNetworkUtils.captureEvent("performance-12month-button-activated", "Performance", "performance",
//                    "", "",
//                    "", bundle, mContext);

            tv_twelve_month.setTextColor(getResources().getColor(R.color.text_color_month));
            iv_twelve_month.getDrawable().setTint(getResources().getColor(R.color.text_color_month));
            Typeface typeface = ResourcesCompat.getFont(ActivityLineGraphList.this, R.font.montserrat_semibold);
            tv_twelve_month.setTypeface(typeface);
        } else {
//            Bundle bundle = new Bundle();
//            ApiNetworkUtils.captureEvent("performance-12month-button-deactivated", "Performance", "performance",
//                    "", "",
//                    "", bundle, mContext);

            tv_twelve_month.setTextColor(getResources().getColor(R.color.subtitle_label));
            iv_twelve_month.getDrawable().setTint(getResources().getColor(R.color.subtitle_label));
            Typeface typeface2 = ResourcesCompat.getFont(ActivityLineGraphList.this, R.font.montserrat_regular);
            tv_twelve_month.setTypeface(typeface2);
        }
    }


    private void setTypeFace(TextView tv1, TextView tv2, TextView tv3, TextView tv4) {
        tv1.setTextColor(getResources().getColor(R.color.red_green));
        tv2.setTextColor(getResources().getColor(R.color.subtitle_label));
        tv3.setTextColor(getResources().getColor(R.color.subtitle_label));
        tv4.setTextColor(getResources().getColor(R.color.subtitle_label));
        tv1.setTypeface(typeface);
        tv2.setTypeface(typeface2);
        tv3.setTypeface(typeface2);
        tv4.setTypeface(typeface2);
        if (tv1.equals(tv_twelve_month)) {
            tv1.setTextColor(getResources().getColor(R.color.text_color_month));
            iv_twelve_month.getDrawable().setTint(getResources().getColor(R.color.text_color_month));
        } else {
            iv_twelve_month.getDrawable().setTint(getResources().getColor(R.color.subtitle_label));
        }
    }

    @Contract("null, _ -> false")
    private boolean checkPreSelectedDate(String strDatefrom, String strDateto) {
        return strDatefrom != null && !strDatefrom.equals("") && strDateto != null && !strDateto.equals("");
    }

    public void setCalendarData(String fromDate, String endDate) {
        if (checkPreSelectedDate(fromDate, endDate)) {
            if (fromDate.trim().equalsIgnoreCase(endDate.trim())) {
                if (fromDate.equalsIgnoreCase("") && endDate.equalsIgnoreCase("")) {
                    strDatefrom = getCurrentDate();
                    strDateto = getCurrentDate();
                } else {
                    SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                    Date newDate1 = null;
                    try {
                        newDate1 = format1.parse(fromDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    //format1 = new SimpleDateFormat("MMM dd,yyyy", Locale.ENGLISH);
                    //String from = format1.format(newDate1);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(newDate1);
                    calendarTv.setText(Util.getMonthTextName(this, getCurrentDateInSuffixFormat(cal)));

                }

            } else {

                SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                Date newDate1 = null;
                try {
                    newDate1 = format1.parse(fromDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                format1 = new SimpleDateFormat("MMM dd", Locale.ENGLISH);
                String from = format1.format(newDate1);

                SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                Date newDate2 = null;
                try {
                    newDate2 = format2.parse(endDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                format2 = new SimpleDateFormat("MMM dd,yyyy", Locale.ENGLISH);
                String to = format2.format(newDate2);


                calendarTv.setText(Util.getMonthTextName(this, from) + " - " + Util.getMonthTextName(this, to));
            }
        } else {
            calendarTv.setText(getString(R.string.today) + "," + Util.getMonthTextName(this, getCurrentDateInSuffixFormat(Calendar.getInstance())));
            strDatefrom = getCurrentDate();
            strDateto = getCurrentDate();
        }
    }

    public String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    private String getDayNumberSuffix(int day) {
        if (day >= 11 && day <= 13) {
            return "th";
        }
        switch (day % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

    public String getCurrentDateInSuffixFormat(Calendar currentCalDate) {
        String dayNumberSuffix = getDayNumberSuffix(currentCalDate.get(Calendar.DAY_OF_MONTH));
        DateFormat dateFormat = new SimpleDateFormat(" d'" + dayNumberSuffix + "' MMM yyyy", Locale.ENGLISH);
        return dateFormat.format(currentCalDate.getTime());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        Bundle bundle = new Bundle();
//        ApiNetworkUtils.captureEvent("performance-device-back-clicked", "Performance", "performance", "device",
//                "back", "clicked", bundle, ActivityLineGraphList.this);
    }
}
