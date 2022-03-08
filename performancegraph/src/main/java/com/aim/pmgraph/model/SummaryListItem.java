package com.aim.pmgraph.model;


import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

@Keep
public class SummaryListItem {

    @SerializedName("duration")
    private String duration;

    @SerializedName("totalamount")
    private String totalamount;

    @SerializedName("totaltransaction")
    private String totaltransaction;

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDuration() {
        return duration;
    }

    public void setTotalamount(String totalamount) {
        this.totalamount = totalamount;
    }

    public String getTotalamount() {
        if (totalamount == null) {
            totalamount = "0";
        }
        return totalamount;
    }

    public void setTotaltransaction(String totaltransaction) {
        this.totaltransaction = totaltransaction;
    }

    public String getTotaltransaction() {
        return totaltransaction;
    }

    @Override
    public String toString() {
        return
                "SummaryListItem{" +
                        "duration = '" + duration + '\'' +
                        ",totalamount = '" + totalamount + '\'' +
                        ",totaltransaction = '" + totaltransaction + '\'' +
                        "}";
    }
}