package com.aim.pmgraph.model;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

@Keep
public class PerformanceListItem {

    @SerializedName("summaryList")
    private List<SummaryListItem> summaryList;

    @SerializedName("mid")
    private String mid;

    @SerializedName("area")
    private String area;
    private String pincode;
    private String address;

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    @SerializedName("tid")
    private String tid;

    public ArrayList<String> getTidList() {
        return tidList;
    }

    public void setTidList(ArrayList<String> tidList) {
        this.tidList = tidList;
    }

    private ArrayList<String> tidList = new ArrayList<>();

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    private boolean visible;

    public void setSummaryList(List<SummaryListItem> summaryList) {
        this.summaryList = summaryList;
    }

    public List<SummaryListItem> getSummaryList() {
        return summaryList;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getMid() {
        return mid;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getArea() {
        return area;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "PerformanceListItem{" +
                "summaryList=" + summaryList +
                ", mid='" + mid + '\'' +
                ", area='" + area + '\'' +
                ", pincode='" + pincode + '\'' +
                ", address='" + address + '\'' +
                ", tid='" + tid + '\'' +
                ", tidList=" + tidList +
                ", visible=" + visible +
                '}';
    }
}