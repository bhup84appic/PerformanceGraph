package com.aim.pmgraph.model;

/**
 * Created by Bilal Khan on 8/8/18.
 */
public class LineChartModel {
    String xAxisValue;
    String totalTransaction;
    String totalAmount;

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    String duration;

    public LineChartModel() {

    }

    public LineChartModel(String xAxisValue, String totalTransaction, String totalAmount, String duration) {
        this.xAxisValue = xAxisValue;
        this.totalTransaction = totalTransaction;
        this.totalAmount = totalAmount;
        this.duration = duration;
    }

    public String getxAxisValue() {
        return xAxisValue;
    }

    public void setxAxisValue(String xAxisValue) {
        this.xAxisValue = xAxisValue;
    }

    public String getTotalTransaction() {
        if (totalTransaction == null) {
            totalTransaction = "0";
        }
        return totalTransaction;
    }

    public void setTotalTransaction(String totalTransaction) {
        this.totalTransaction = totalTransaction;
    }

    public String getTotalAmount() {
        if (totalAmount == null) {
            totalAmount = "0";
        }
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }
}
