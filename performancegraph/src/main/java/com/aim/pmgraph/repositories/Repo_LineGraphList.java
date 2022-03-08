/*
 *
 *  Created by Pooran Kharol on 30/1/21 6:02 PM
 *  Copyright (c) 2021 . All rights reserved.
 *  Last modified 30/1/21 6:02 PM
 *
 */

package com.aim.pmgraph.repositories;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.aim.pmgraph.apiinterface.ApiInterface;
import com.mintoak.corelib.ApiClient;
import com.mintoak.corelib.LoggerUtils;
import com.mintoak.corelib.RXJavaCaller;
import com.mintoak.corelib.apiutil.ApiConstants;
import com.mintoak.corelib.apiutil.Encrypter;
import com.mintoak.corelib.builder.RequestParameters;

import org.json.JSONObject;

import io.reactivex.Observable;


public class Repo_LineGraphList {

    private static Repo_LineGraphList salesCounterListRepo;
    private MutableLiveData<String> performanceList;
    private static final String TAG = "Repo_LineGraphList";
    Context mContext;

    public static Repo_LineGraphList getInstance(Context context) {
        if (salesCounterListRepo == null) {
            salesCounterListRepo = new Repo_LineGraphList(context);
        }
        return salesCounterListRepo;
    }

    private ApiInterface apiInterface;

    public Repo_LineGraphList(Context context) {

        this.mContext = context;
    }

    public MutableLiveData<String> getPerformanceGraphViewAll(final JSONObject params, final RequestParameters requestParameters, final boolean isYearOrNot) {
        performanceList = new MutableLiveData<>();
        /*if (ApiConstants.publicKey == null) {
            try {
                apiInterface = getClientNew(mContext, requestParameters).create(ApiInterface.class);
                Observable<String> call = apiInterface.callAPIURL();
                RXJavaCaller.GetKey(call, new RXJavaCaller.OnKeyReceived() {
                    @Override
                    public void onKeyReceivedSuccess() {

                        if (isYearOrNot) {
                            getPerformanceGraphViewAll(params, requestParameters, true);
                        } else {
                            getPerformanceGraphViewAll(params, requestParameters, false);
                        }
                    }

                    @Override
                    public void onKeyReceivedError(String error) {
                        Log.e("FUNNEL_DATA", error);
                    }
                });
            } catch (Exception e) {
                LoggerUtils.E(TAG, "Exception3:" + e.toString());
                Util.cancelProgressDialog();
                Util.popUpDialog(mContext, "", "Something went wrong");
            }
        } else {*/


            JSONObject enc = Encrypter.getEncryptedJSON(params);
            String skey = enc.optString("SKEY", "");
            String iv = enc.optString("IV", "");
            JSONObject jsonObject1 = Encrypter.getFinalJSON(enc);
        LoggerUtils.E("RXJavaCaller", jsonObject1.toString());
            apiInterface = ApiClient.getClientNew(mContext, requestParameters).create(ApiInterface.class);

            Observable<String> call;
           /* if (isYearOrNot) {
                call = apiInterface.getPerformanceYearWise(jsonObject1.toString());
            } else {
                call = apiInterface.getPerformanceGraphViewAll(jsonObject1.toString());
            }*/
            call = apiInterface.getPerformanceGraphViewAllNewApi(jsonObject1.toString());
            RXJavaCaller.executeService(call, skey, iv, false, new RXJavaCaller.OnServiceExecuted() {
                @Override
                public void onSuccess(String result) {
                    performanceList.setValue(result);
                }

                @Override
                public void onError(String error) {
                    performanceList.setValue(error);
                    Log.e("onError", "PerformanceViewAll " + error);
                }

                @Override
                public void onKeyExpired() {
                    /*if (isYearOrNot) {
                        getPerformanceGraphViewAll(params, requestParameters, true);
                    } else {
                        getPerformanceGraphViewAll(params, requestParameters, false);
                    }*/
                    performanceList.setValue("E105");
                }

                @Override
                public void onSessionExpired() {
                    performanceList.setValue(ApiConstants.SESSION_EXPIRED);
                }
            });

        //}


        return performanceList;
    }

}
