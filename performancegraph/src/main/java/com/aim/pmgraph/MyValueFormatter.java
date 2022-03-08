package com.aim.pmgraph;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.LargeValueFormatter;

public class MyValueFormatter extends LargeValueFormatter {

    public MyValueFormatter(float val, YAxis yaxis) {
        LargeValueFormatter largeValueFormatter = new LargeValueFormatter();
        largeValueFormatter.getFormattedValue(val, yaxis);
    }

}
