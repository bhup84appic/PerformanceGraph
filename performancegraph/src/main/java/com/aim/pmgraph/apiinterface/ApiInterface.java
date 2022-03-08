package com.aim.pmgraph.apiinterface;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiInterface {
    //Sales counter View New API
    @Headers("Content-Type: application/json")
    @POST("HDFC360/V6/MerSaleCounterViewAll")
    Observable<String> getMerSaleCounterViewAllNewApi(@Body String param);

    //PerformancrGraphViewAll
    @Headers("Content-Type: application/json")
    @POST("HDFC360/V5/PerformanceViewAll")
    Observable<String> getPerformanceGraphViewAll(@Body String param);

    //PerformancrGraphViewAll new API 19 Mar 2021
    @Headers("Content-Type: application/json")
    @POST("HDFC360/V6/PerformanceViewAll")
    Observable<String> getPerformanceGraphViewAllNewApi(@Body String param);

    @Headers("Content-Type: application/json")
    @POST("HDFC360/PerformanceYearWise")
    Observable<String> getPerformanceYearWise(@Body String param);
}
