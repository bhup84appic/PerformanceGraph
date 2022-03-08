package com.aim.performancegraph;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.aim.performancegraph.databinding.ActivityMainBinding;
import com.aim.pmgraph.intface.TwelveMonthViewCallBack;
import com.mintoak.corelib.Util;
import com.mintoak.corelib.apiutil.ApiConstants;
import com.mintoak.corelib.apiutil.ModuleConfiguration;
import com.mintoak.corelib.builder.RequestParameters;
import com.mintoak.corelib.calender_module.calender_filterutil.CalenderDateUI;
import com.mintoak.corelib.calender_module.listeners.CalenderDataToAppCallback;
import com.mintoak.corelib.enums.BaseUrl;
import com.mintoak.corelib.enums.ProjectType;
import com.mintoak.corelib.interfaces.SessionExpiredListener;
import com.mintoak.corelib.location_module.filter_utils.LocationFilterUI;
import com.mintoak.corelib.location_module.interfaces.LocationDataToAppCallback;
import com.mintoak.corelib.location_module.model.LocationData;
import com.mintoak.corelib.location_module.utils.LocationFilterUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationDataToAppCallback, CalenderDataToAppCallback, SessionExpiredListener, TwelveMonthViewCallBack {

        ActivityMainBinding binding;

    private String startDate = "2022-02-28", endDate = "2022-02-28";
    private RequestParameters requestParameters;
    private LocationData mLocationData;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    private boolean isTwelveMonthClicked= false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(MainActivity.this, R.layout.activity_main);

        startDate = sdf.format(new Date());
        endDate = sdf.format(new Date());

        binding.tvCalender.setText("S_Date : " + startDate + "  /  " + "E_Date : " + endDate);
        LocationFilterUtils.FILTER_JSON = "{\n" +
                "  \"status\": \"Success\",\n" +
                "  \"respMessage\": \"Success\",\n" +
                "  \"statusCode\": \"S101\",\n" +
                "  \"terminalInfo\": [\n" +
                "    {\n" +
                "      \"73000020\": {\n" +
                "        \"city\": \"Bangalore\",\n" +
                "        \"location\": \"Begihalli\",\n" +
                "        \"address\": \"Basaveshvar nagar,Bangalore, KARNATAKA, Bangalore, Karnataka-560076\",\n" +
                "        \"mid\": \"1404\",\n" +
                "        \"cid\": \"2\",\n" +
                "        \"dba\": \"Royal Challengers\",\n" +
                "        \"companyName\": \"Shagun and Bhavya\",\n" +
                "        \"accountNumber\": \"AQICAHjTA9t90rk+kDw094g+PNDSD80m3GMYeA+aHWHZwV62OQGUBJdBrpx/o9CSzolr2PKAAAAAbTBrBgkqhkiG9w0BBwagXjBcAgEAMFcGCSqGSIb3DQEHATAeBglghkgBZQMEAS4wEQQMP/uqBgqEeNZRmQ+2AgEQgComJvDy/qD0Zk8m/00OZR5aRE0rKHe/QvI73PMC8T4wGEaJ6RXKU9Onaos=\",\n" +
                "        \"role\": \"owner\",\n" +
                "        \"tidStatus\": \"Active\",\n" +
                "        \"bankProduct\": {\n" +
                "          \"insta\": false,\n" +
                "          \"jumbo\": false,\n" +
                "          \"le\": false,\n" +
                "          \"business_CreditCards\": false\n" +
                "        },\n" +
                "        \"pinCode\": \"560076\",\n" +
                "        \"segments\": [\n" +
                "          \"192\",\n" +
                "          \"102\",\n" +
                "          \"195\",\n" +
                "          \"201\",\n" +
                "          \"224\",\n" +
                "          \"166\",\n" +
                "          \"166\",\n" +
                "          \"231\"\n" +
                "        ],\n" +
                "        \"roleStatus\": \"Active\"\n" +
                "      },\n" +
                "      \"98202011\": {\n" +
                "        \"city\": \"kolkata\",\n" +
                "        \"location\": \"Diamond Plaza \",\n" +
                "        \"address\": \"68, Jessore Rd, Ward Number 23, Satgachi, Kolkata, West Bengal 700055, kolkata, West Bengal-700055\",\n" +
                "        \"mid\": \"6789\",\n" +
                "        \"cid\": \"2\",\n" +
                "        \"dba\": \"Royal Challengers\",\n" +
                "        \"companyName\": \"Shagun and Bhavya\",\n" +
                "        \"accountNumber\": \"AQICAHjTA9t90rk+kDw094g+PNDSD80m3GMYeA+aHWHZwV62OQFC4ICLb+ZP6HeP/97m69InAAAAbTBrBgkqhkiG9w0BBwagXjBcAgEAMFcGCSqGSIb3DQEHATAeBglghkgBZQMEAS4wEQQMjKxhEhRQ9o4vR33TAgEQgCqMdHOSw3+rYonq16UUBcrkAGNgjRgpSswtWRKHyyXXqGyO9gHZTFKJNBo=\",\n" +
                "        \"role\": \"owner\",\n" +
                "        \"tidStatus\": \"Active\",\n" +
                "        \"bankProduct\": {\n" +
                "          \"insta\": false,\n" +
                "          \"jumbo\": false,\n" +
                "          \"le\": false,\n" +
                "          \"business_CreditCards\": false\n" +
                "        },\n" +
                "        \"pinCode\": \"700055\",\n" +
                "        \"segments\": [\n" +
                "          \"192\",\n" +
                "          \"195\",\n" +
                "          \"201\",\n" +
                "          \"225\",\n" +
                "          \"226\",\n" +
                "          \"166\",\n" +
                "          \"166\"\n" +
                "        ],\n" +
                "        \"roleStatus\": \"Active\"\n" +
                "      },\n" +
                "      \"98202012\": {\n" +
                "        \"city\": \"Mumbai\",\n" +
                "        \"location\": \"Bandra \",\n" +
                "        \"address\": \"Mannat,mumbai, Mumbai, Maharashtra-420015\",\n" +
                "        \"mid\": \"1289\",\n" +
                "        \"cid\": \"2\",\n" +
                "        \"dba\": \"Royal Challengers\",\n" +
                "        \"companyName\": \"Shagun and Bhavya\",\n" +
                "        \"accountNumber\": \"AQICAHjTA9t90rk+kDw094g+PNDSD80m3GMYeA+aHWHZwV62OQHh8iFMtl8q3/RmXAVM1MQgAAAAbTBrBgkqhkiG9w0BBwagXjBcAgEAMFcGCSqGSIb3DQEHATAeBglghkgBZQMEAS4wEQQM5Hxr4QBuswQqDh4rAgEQgCr9tvSOhIyaNRvnilLDgWH+sbsL+QfSEa43/3mFB0zSE8YZIikedAX+Ga8=\",\n" +
                "        \"role\": \"owner\",\n" +
                "        \"tidStatus\": \"Active\",\n" +
                "        \"bankProduct\": {\n" +
                "          \"insta\": false,\n" +
                "          \"jumbo\": false,\n" +
                "          \"le\": false,\n" +
                "          \"business_CreditCards\": false\n" +
                "        },\n" +
                "        \"pinCode\": \"420015\",\n" +
                "        \"segments\": [\n" +
                "          \"192\",\n" +
                "          \"218\",\n" +
                "          \"195\",\n" +
                "          \"201\",\n" +
                "          \"166\",\n" +
                "          \"166\"\n" +
                "        ],\n" +
                "        \"roleStatus\": \"Active\"\n" +
                "      },\n" +
                "      \"98777221\": {\n" +
                "        \"city\": \"New Delhi\",\n" +
                "        \"location\": \"Red Fort\",\n" +
                "        \"address\": \"Netaji Subhash Marg, Lal Qila, Chandni Chowk, New Delhi, Delhi 110006, New Delhi, New Delhi-110006\",\n" +
                "        \"mid\": \"1510\",\n" +
                "        \"cid\": \"2\",\n" +
                "        \"dba\": \"Rashtrapati Sachivalaya\",\n" +
                "        \"companyName\": \"Shagun and Bhavya\",\n" +
                "        \"accountNumber\": \"AQICAHjTA9t90rk+kDw094g+PNDSD80m3GMYeA+aHWHZwV62OQHgo4PG1M515fdqAhArAlYQAAAAbTBrBgkqhkiG9w0BBwagXjBcAgEAMFcGCSqGSIb3DQEHATAeBglghkgBZQMEAS4wEQQME7tibb9D7HcS4FhcAgEQgCpmyDnGZbu3nelsz1l0VPXkPtdTk0rnSaZZSI99GPGHHPCReuNtY6JWeO0=\",\n" +
                "        \"role\": \"owner\",\n" +
                "        \"tidStatus\": \"Active\",\n" +
                "        \"bankProduct\": {\n" +
                "          \"insta\": false,\n" +
                "          \"jumbo\": false,\n" +
                "          \"le\": false,\n" +
                "          \"business_CreditCards\": false\n" +
                "        },\n" +
                "        \"pinCode\": \"110006\",\n" +
                "        \"segments\": [\n" +
                "          \"187\",\n" +
                "          \"219\",\n" +
                "          \"197\",\n" +
                "          \"198\",\n" +
                "          \"199\",\n" +
                "          \"201\",\n" +
                "          \"213\",\n" +
                "          \"166\",\n" +
                "          \"166\",\n" +
                "          \"228\"\n" +
                "        ],\n" +
                "        \"roleStatus\": \"Active\"\n" +
                "      },\n" +
                "      \"98777222\": {\n" +
                "        \"city\": \"Bangalore\",\n" +
                "        \"location\": \"Begihalli \",\n" +
                "        \"address\": \"Basaveshvar nagar,Bangalore, KARNATAKA, Bangalore, Karnataka-560076\",\n" +
                "        \"mid\": \"1404\",\n" +
                "        \"cid\": \"2\",\n" +
                "        \"dba\": \"Royal Challengers\",\n" +
                "        \"companyName\": \"Shagun and Bhavya\",\n" +
                "        \"accountNumber\": \"AQICAHjTA9t90rk+kDw094g+PNDSD80m3GMYeA+aHWHZwV62OQEVYmFZc15RzCDpCioYmW+yAAAAbTBrBgkqhkiG9w0BBwagXjBcAgEAMFcGCSqGSIb3DQEHATAeBglghkgBZQMEAS4wEQQM1NwdZfPf4w8BtLoLAgEQgCpvOG+AzILS/05kj5as9v6Zj2+8hKBAco+51CpCH78O7HXCS7KdXdPKPjw=\",\n" +
                "        \"role\": \"owner\",\n" +
                "        \"tidStatus\": \"Active\",\n" +
                "        \"bankProduct\": {\n" +
                "          \"insta\": false,\n" +
                "          \"jumbo\": false,\n" +
                "          \"le\": false,\n" +
                "          \"business_CreditCards\": false\n" +
                "        },\n" +
                "        \"pinCode\": \"560076\",\n" +
                "        \"segments\": [\n" +
                "          \"217\",\n" +
                "          \"219\",\n" +
                "          \"197\",\n" +
                "          \"199\",\n" +
                "          \"214\",\n" +
                "          \"201\",\n" +
                "          \"215\",\n" +
                "          \"212\",\n" +
                "          \"216\",\n" +
                "          \"225\",\n" +
                "          \"226\",\n" +
                "          \"166\",\n" +
                "          \"166\",\n" +
                "          \"231\"\n" +
                "        ],\n" +
                "        \"roleStatus\": \"Active\"\n" +
                "      },\n" +
                "      \"98777223\": {\n" +
                "        \"city\": \"Belgaum\",\n" +
                "        \"location\": \"Chennamma Circle \",\n" +
                "        \"address\": \"Kittur Rani Chennamma Circle, Khade Bazar, Raviwar Peth, Belagavi, Belgaum, Karnataka-590001\",\n" +
                "        \"mid\": \"1405\",\n" +
                "        \"cid\": \"2\",\n" +
                "        \"dba\": \"Royal Challengers\",\n" +
                "        \"companyName\": \"Shagun and Bhavya\",\n" +
                "        \"accountNumber\": \"AQICAHjTA9t90rk+kDw094g+PNDSD80m3GMYeA+aHWHZwV62OQErd0OnDA0L8LVxI+vixLs9AAAAbTBrBgkqhkiG9w0BBwagXjBcAgEAMFcGCSqGSIb3DQEHATAeBglghkgBZQMEAS4wEQQMQ4jiTEtJECTTwFvnAgEQgCo3R0vnHZb8cUxXBjvvvTgB3v4/yIThJjJfA7Dx8HlTr2fHdnjHFlBlscU=\",\n" +
                "        \"role\": \"owner\",\n" +
                "        \"tidStatus\": \"Active\",\n" +
                "        \"bankProduct\": {\n" +
                "          \"insta\": false,\n" +
                "          \"jumbo\": false,\n" +
                "          \"le\": false,\n" +
                "          \"business_CreditCards\": false\n" +
                "        },\n" +
                "        \"pinCode\": \"590001\",\n" +
                "        \"segments\": [\n" +
                "          \"73\",\n" +
                "          \"74\",\n" +
                "          \"99\",\n" +
                "          \"100\",\n" +
                "          \"101\",\n" +
                "          \"102\",\n" +
                "          \"107\",\n" +
                "          \"108\",\n" +
                "          \"201\",\n" +
                "          \"205\",\n" +
                "          \"224\",\n" +
                "          \"225\",\n" +
                "          \"226\",\n" +
                "          \"166\",\n" +
                "          \"166\",\n" +
                "          \"228\",\n" +
                "          \"231\"\n" +
                "        ],\n" +
                "        \"roleStatus\": \"Active\"\n" +
                "      },\n" +
                "      \"98777230\": {\n" +
                "        \"city\": \"New Delhi\",\n" +
                "        \"location\": \"Rashtrapati Bhavan\",\n" +
                "        \"address\": \"Rashtrapati Bhawan, President's Estate, New Delhi, Delhi 110004, New Delhi, New Delhi-110004\",\n" +
                "        \"mid\": \"31333\",\n" +
                "        \"cid\": \"2\",\n" +
                "        \"dba\": \"Rashtrapati Sachivalaya\",\n" +
                "        \"companyName\": \"Shagun and Bhavya\",\n" +
                "        \"accountNumber\": \"AQICAHjTA9t90rk+kDw094g+PNDSD80m3GMYeA+aHWHZwV62OQF4RpoQrtEfhrPgFEcfazrvAAAAbTBrBgkqhkiG9w0BBwagXjBcAgEAMFcGCSqGSIb3DQEHATAeBglghkgBZQMEAS4wEQQMjiGGBDJ705NtoKaBAgEQgCobsmdBAXYQNpjBWiILd3qATElKkgIeN0SfturccE2+6Lx84/k8dFLD+Ck=\",\n" +
                "        \"role\": \"owner\",\n" +
                "        \"tidStatus\": \"Active\",\n" +
                "        \"bankProduct\": {\n" +
                "          \"insta\": false,\n" +
                "          \"jumbo\": false,\n" +
                "          \"le\": false,\n" +
                "          \"business_CreditCards\": false\n" +
                "        },\n" +
                "        \"pinCode\": \"110004\",\n" +
                "        \"segments\": [\n" +
                "          \"201\",\n" +
                "          \"166\"\n" +
                "        ],\n" +
                "        \"roleStatus\": \"Active\"\n" +
                "      },\n" +
                "      \"98777231\": {\n" +
                "        \"city\": \"New Delhi\",\n" +
                "        \"location\": \"Rashtrapati Bhavan\",\n" +
                "        \"address\": \"Rashtrapati Bhawan, President's Estate, New Delhi, Delhi 110004, New Delhi, New Delhi-110004\",\n" +
                "        \"mid\": \"31333\",\n" +
                "        \"cid\": \"2\",\n" +
                "        \"dba\": \"Rashtrapati Sachivalaya\",\n" +
                "        \"companyName\": \"Shagun and Bhavya\",\n" +
                "        \"accountNumber\": \"AQICAHjTA9t90rk+kDw094g+PNDSD80m3GMYeA+aHWHZwV62OQESvEuwgmLoH4Pl8cRpBYqbAAAAbTBrBgkqhkiG9w0BBwagXjBcAgEAMFcGCSqGSIb3DQEHATAeBglghkgBZQMEAS4wEQQMvq1Wts9h6Go7cU2BAgEQgCo1TziyfoDNeumvKT0+0QvC4nxO9/N3hyHe+3HVdh+QS//40gjTaT0zsd0=\",\n" +
                "        \"role\": \"owner\",\n" +
                "        \"tidStatus\": \"Active\",\n" +
                "        \"bankProduct\": {\n" +
                "          \"insta\": false,\n" +
                "          \"jumbo\": false,\n" +
                "          \"le\": false,\n" +
                "          \"business_CreditCards\": false\n" +
                "        },\n" +
                "        \"pinCode\": \"110004\",\n" +
                "        \"segments\": [\n" +
                "          \"201\",\n" +
                "          \"166\",\n" +
                "          \"231\"\n" +
                "        ],\n" +
                "        \"roleStatus\": \"Active\"\n" +
                "      },\n" +
                "      \"9820181246\": {\n" +
                "        \"city\": \"Mumbai\",\n" +
                "        \"location\": \"JUHU\",\n" +
                "        \"address\": \"Mannat,mumbai, Mumbai, Maharashtra-420015\",\n" +
                "        \"mid\": \"1289\",\n" +
                "        \"cid\": \"2\",\n" +
                "        \"dba\": \"Royal Challengers\",\n" +
                "        \"companyName\": \"Shagun and Bhavya\",\n" +
                "        \"accountNumber\": \"AQICAHjTA9t90rk+kDw094g+PNDSD80m3GMYeA+aHWHZwV62OQHLzQigVhZZLA+sCQcumL16AAAAbTBrBgkqhkiG9w0BBwagXjBcAgEAMFcGCSqGSIb3DQEHATAeBglghkgBZQMEAS4wEQQMcGA9r91X9ETYykRhAgEQgCrUjkpwlRh/4r2gXq5WOywdXNo2MJmY/j5hC/eh7ViLMb+H4+9tVcVFLUM=\",\n" +
                "        \"role\": \"owner\",\n" +
                "        \"tidStatus\": \"Active\",\n" +
                "        \"bankProduct\": {\n" +
                "          \"insta\": false,\n" +
                "          \"jumbo\": false,\n" +
                "          \"le\": false,\n" +
                "          \"business_CreditCards\": false\n" +
                "        },\n" +
                "        \"pinCode\": \"420015\",\n" +
                "        \"segments\": [\n" +
                "          \"195\",\n" +
                "          \"201\",\n" +
                "          \"220\",\n" +
                "          \"225\",\n" +
                "          \"226\",\n" +
                "          \"166\",\n" +
                "          \"166\"\n" +
                "        ],\n" +
                "        \"roleStatus\": \"Active\"\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        requestParameters = new RequestParameters.Builder(MainActivity.this, ProjectType.PL).setBaseUrl(BaseUrl.URLMDEV4)
                .setTerminalId("").setFCMToken("").build();
        ModuleConfiguration.getInstance().setRequestParameters(requestParameters);
        Util.storeRequestValue(MainActivity.this, requestParameters);

        ApiConstants.SESSION_ID = "84d525d3-80df-47a0-9b01-066306f7341e";
        refreshSalesData();

        binding.btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocationFilterUI.openLocationFilterUI(MainActivity.this, MainActivity.this);
            }
        });

        binding.tvCalender.setOnClickListener(view -> CalenderDateUI.openCalenderView(MainActivity.this, selectedDate -> {
            startDate = selectedDate.getStartDate();
            endDate = selectedDate.getEndDate();
            binding.tvCalender.setText("S_Date : " + startDate + "  /  " + "E_Date : " + endDate);

            refreshSalesData();
        }));

        LocationFilterUI.getSelectedLocations(MainActivity.this);
    }

    private void refreshSalesData() {
        if(isTwelveMonthClicked){
            binding.performancegraph.getYearlyPerformance(startDate, endDate);
        }else {
            binding.performancegraph.setPerformanceData(requestParameters, startDate, endDate);
        }
        binding.performancegraph.setSaleCardLocationApplyCallback(this);
        binding.performancegraph.setSaleCardCalenderApplyCallback(this);
        binding.performancegraph.setMonthViewApplyCallback(this);
        binding.performancegraph.setApiSessionExpiredListener(MainActivity.this);

    }

    @Override
    public void onSessionExpired() {
        Toast.makeText(MainActivity.this, "onSessionExpired", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDateApplied(com.mintoak.corelib.calender_module.calender_filterutil.SelectedDate selectedDate) {
        startDate = selectedDate.getStartDate();
        endDate = selectedDate.getEndDate();
        binding.tvCalender.setText("S_Date : " + startDate + "  /  " + "E_Date : " + endDate);
        refreshSalesData();
//        Toast.makeText(MainActivity.this, "onDateApplied", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onLocationApplied(com.mintoak.corelib.location_module.model.LocationData locationData) {
        mLocationData = locationData;
        refreshSalesData();
        Toast.makeText(MainActivity.this, "onLocationApplied", Toast.LENGTH_SHORT).show();
        binding.btnLocation.setText(locationData.getCidLevelModel().getCompanyName() + " (" + locationData.getMidLevelModelList().size() + ")");
    }

    @Override
    public void onMonthViewChange(boolean isTMonthClicked) {
        this.isTwelveMonthClicked =isTMonthClicked;
        refreshSalesData();
    }
}
//    /"2022-02-28", "2022-02-28"