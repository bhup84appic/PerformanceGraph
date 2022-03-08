package com.aim.pmgraph.model;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@Keep
public class ResPerformaceDashboard {

    @SerializedName("performance")
    private List<PerformanceItem> performance;

    @SerializedName("noMsisdnCount")
    private int noMsisdnCount;

    @SerializedName("errorCode")
    private String errorCode;

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private String status;

    public List<PerformanceItem> getPerformance() {
        return performance;
    }

    public void setPerformance(List<PerformanceItem> performance) {
        this.performance = performance;
    }

    public int getNoMsisdnCount() {
        return noMsisdnCount;
    }

    public void setNoMsisdnCount(int noMsisdnCount) {
        this.noMsisdnCount = noMsisdnCount;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return
                "ResPerformaceDashboard{" +
                        "performance = '" + performance + '\'' +
                        ",noMsisdnCount = '" + noMsisdnCount + '\'' +
                        ",errorCode = '" + errorCode + '\'' +
                        ",message = '" + message + '\'' +
                        ",status = '" + status + '\'' +
                        "}";
    }
}