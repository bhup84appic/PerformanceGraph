package com.aim.pmgraph.model;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@Keep
public class ResDashboardCards {


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

    public void setPerformance(List<PerformanceItem> performance){
        this.performance = performance;
    }

    public List<PerformanceItem> getPerformance(){
        return performance;
    }

    public void setNoMsisdnCount(int noMsisdnCount){
        this.noMsisdnCount = noMsisdnCount;
    }

    public int getNoMsisdnCount(){
        return noMsisdnCount;
    }

    public void setErrorCode(String errorCode){
        this.errorCode = errorCode;
    }

    public String getErrorCode(){
        return errorCode;
    }

    public void setMessage(String message){
        this.message = message;
    }

    public String getMessage(){
        return message;
    }

    public void setStatus(String status){
        this.status = status;
    }

    public String getStatus(){
        return status;
    }

    @Override
    public String toString(){
        return
                "ResDashboardCards{" +
                        "performance = '" + performance + '\'' +
                        ",noMsisdnCount = '" + noMsisdnCount + '\'' +
                        ",errorCode = '" + errorCode + '\'' +
                        ",message = '" + message + '\'' +
                        ",status = '" + status + '\'' +
                        "}";
    }
}