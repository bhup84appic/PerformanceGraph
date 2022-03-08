/*
 *
 *  Created by Pooran Kharol on 30/1/21 6:02 PM
 *  Copyright (c) 2021 . All rights reserved.
 *  Last modified 30/1/21 6:01 PM
 *
 */

package com.aim.pmgraph.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


import com.aim.pmgraph.repositories.Repo_LineGraphList;
import com.mintoak.corelib.builder.RequestParameters;

import org.json.JSONObject;

public class ViewModel_LineGraphList extends AndroidViewModel {


    private MutableLiveData<String> mutableLiveDataGetPerformanceList;

    Repo_LineGraphList repo_salesCounterList;

    public ViewModel_LineGraphList(@NonNull Application application) {
        super(application);

        repo_salesCounterList = Repo_LineGraphList.getInstance(application);
    }

    public void setListData(final JSONObject params, final RequestParameters requestParameters, boolean isYearOrNot) {
        mutableLiveDataGetPerformanceList = repo_salesCounterList.getPerformanceGraphViewAll(params, requestParameters,isYearOrNot);
    }

    public LiveData<String> getListData() {
        return mutableLiveDataGetPerformanceList;
    }

}
