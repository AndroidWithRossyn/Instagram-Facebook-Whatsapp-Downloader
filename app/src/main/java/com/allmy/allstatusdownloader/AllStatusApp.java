package com.allmy.allstatusdownloader;

import android.app.Application;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.onesignal.OneSignal;

import java.util.Arrays;
import java.util.List;

public class AllStatusApp extends Application {

    private static final String ONESIGNAL_APP_ID = "########-####-####-####-############";

    @Override
    public void onCreate() {
        super.onCreate();

        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this, initializationStatus -> {
        });

        List<String> testDeviceIds = Arrays.asList("33BE2250B43518CCDA7DE426D04EE231");
        RequestConfiguration configuration =
                new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
        MobileAds.setRequestConfiguration(configuration);

    }
}