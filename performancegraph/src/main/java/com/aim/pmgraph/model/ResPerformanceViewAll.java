package com.aim.pmgraph.model;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@Keep
public class ResPerformanceViewAll {

	@SerializedName("message")
	private String message;

	@SerializedName("performanceList")
	private List<PerformanceListItem> performanceList;

	@SerializedName("status")
	private String status;

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}

	public void setPerformanceList(List<PerformanceListItem> performanceList){
		this.performanceList = performanceList;
	}

	public List<PerformanceListItem> getPerformanceList(){
		return performanceList;
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
			"ResPerformanceViewAll{" + 
			"message = '" + message + '\'' + 
			",performanceList = '" + performanceList + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}